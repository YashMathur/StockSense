package adapters;

import java.io.*;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.infigent.stocksense.Fragment4;
import com.infigent.stocksense.MainActivity;
import com.infigent.stocksense.R;

import android.app.*;
import android.content.Intent;
import android.graphics.Color;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class BackGround extends Service{
	double target, cur;
	private final IBinder mBinder = new MyBinder();
	
	@Override
	public IBinder onBind(Intent intent) {

		return mBinder;
	}
	
	public class MyBinder extends Binder {
		
		BackGround getService() {
			return BackGround.this;
		}
		
	}
	
	boolean isNowBetweenDateTime(final Date s, final Date e)
	{
	    final Date now = new Date();
	    return now.after(s) && now.before(e);
	}
	
	public static class NotificationID {
	    private static final AtomicInteger c = new AtomicInteger(0);
	    public static int getID() {
	        return c.incrementAndGet();
	    }
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("Service", "Started");		
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	    scheduler.scheduleAtFixedRate(new Runnable() {
	    	
	        @Override
	        public void run() {
	        	try {
					DatabaseHandler db = new DatabaseHandler(getApplicationContext());
					ArrayList<HashMap<String, String>> ll = new ArrayList<HashMap<String,String>>();
					
					List<alerts> alert = db.getAllContacts();
					
					for(alerts a:alert){
						String code = a.getCode();
						String type = a.getType();
						String num = a.getTar();
						String nameOfStock = a.getName();
						String f = a.getTodo();
						String st = a.getStat();
						if(st.contains("N")){
						
							HttpClient httpclient = new DefaultHttpClient();
							HttpGet httpGet = new HttpGet( getResources().getString(R.string.website) +
									"/stocks.php?f="+URLEncoder.encode(code, "UTF-8")+"&t="+type);
						
							try {
								HttpResponse httpResponse = httpclient.execute(httpGet);
								InputStream inputStream = httpResponse.getEntity()
										.getContent();
								InputStreamReader inputStreamReader = new InputStreamReader(
										inputStream);
								BufferedReader bufferedReader = new BufferedReader(
										inputStreamReader);
								StringBuilder stringBuilder = new StringBuilder();
								String bufferedStrChunk = null;
								
								while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
									stringBuilder.append(bufferedStrChunk);
								}
								String js = stringBuilder.toString();
								
								try {
									JSONObject jsonObj = new JSONObject(js);
									String v = jsonObj.getString("3");
									try{
										NumberFormat fmt = 
												NumberFormat.getNumberInstance(Locale.US);
										target = fmt.parse(num).doubleValue();
										cur = fmt.parse(v).doubleValue();
									}
									catch(NumberFormatException  nfe){
										Log.d("E", nfe.toString());
									}
									
									if(f.startsWith("G")){
									
										if(cur>=target){
											//Removing the alert

											a.setStat("Y");
											db.updateContact(a);
											//Log.d("SERVICE NEW", r.getString("c")+"-"+r.toString());
											PendingIntent rp;
											Intent it = new Intent(getApplicationContext(), 
													MainActivity.class);
											it.putExtra("name", nameOfStock);
											it.putExtra("q", code);
											it.putExtra("type", type);
                                            it.putExtra("eq", true);
                                            it.setAction("MyIntent");
                                            rp = PendingIntent.getActivity(
													getApplicationContext(), 0, it, 
													0);
											NotificationCompat.Builder mBuilder =
												    new NotificationCompat.Builder(
												    		getApplicationContext())
												    .setSmallIcon(R.drawable.ic_launcher)
												    .setContentTitle("StockSense")
												    .setContentIntent(rp)
												    .setVibrate(new long[]{1000,1000,1000})
												    .setLights(Color.RED, 3000, 3000)
                                                    .setAutoCancel(true)
												    .setContentText(nameOfStock + 
												    		" has reached the target price of Rs."+
												    		target);
										    NotificationManager notificationManager = 
										    		(NotificationManager) 
										    		getSystemService(NOTIFICATION_SERVICE);
										    
										    notificationManager.notify(NotificationID.getID(),
										    		mBuilder.build());
										    
										    
										    
										}
									}
									else{
										
										if(cur<=target){
											//Removing the alert
											a.setStat("Y");
											db.updateContact(a);
											//Log.d("SERVICE NEW", r.getString("c")+"-"+r.toString());
											PendingIntent rp;
											Intent it = new Intent(getApplicationContext(), 
													MainActivity.class);						
											it.putExtra("name", nameOfStock);
											it.putExtra("q", code);
											it.putExtra("type", type);
                                            it.putExtra("eq", true);
                                            it.setAction("MyIntent");
											rp = PendingIntent.getActivity(
													getApplicationContext(), 0, it, 
													0);
											NotificationCompat.Builder mBuilder =
												    new NotificationCompat.Builder(
												    		getApplicationContext())
												    .setSmallIcon(R.drawable.ic_launcher)
												    .setVibrate(new long[]{1000,1000,1000})
												    .setContentIntent(rp)
												    .setLights(Color.RED, 3000, 3000)
                                                    .setAutoCancel(true)
												    .setContentTitle("MarketSense")
												    .setContentText(nameOfStock + 
												    		" has gone below Rs."+ target);
										    NotificationManager notificationManager = 
										    		(NotificationManager) 
										    		getSystemService(NOTIFICATION_SERVICE);
										    
										    notificationManager.notify(001, mBuilder.build());
										    
										    
										    
										}
										
									}
									
									
								}catch(Exception e){}
								
							} catch (ClientProtocolException cpe) {
								Log.d("ex bk", cpe.toString());
							} catch (IOException ioe) {
								Log.d("ex bk", ioe.toString());						
							}
						
						}
					}
					//Log.d("Adding "+jsonArray.length(), "List: "+list.toString());
                
			    }catch(Exception e){
			    	Log.d("ex", e.toString());
			    }
	        }
	    }, 0, 5000, TimeUnit.MILLISECONDS);
		
		return Service.START_NOT_STICKY;
		
	}

}
