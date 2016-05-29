package com.infigent.stocksense;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import adapters.DatabaseHandler;
import adapters.alerts;
import adapters.port;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class Fragment4 extends Fragment {
	String js;
	Button  sv;
	ImageButton add, al, watch;
	List<String> LIST = new ArrayList<String>();
	List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
	String[] label_eq = new String[]{"", "", "", "", "", "", "Range",
			 "52 Week", "Open", "Volume", "Market cap.", "P/E","Div/Yield", "EPS", "Shares", 
			 "Beta", "Inst. own"};
	 
	String[] label_st = new String[]{"", "", "", "", "", "", "Range", "52 Week",
			 "Open", "Volume"};
	ArrayList<HashMap<String, String>> nList;
	ArrayList<HashMap<String, String>> pList = 
    		new ArrayList<HashMap<String, String>>(); 
	
	private static final AtomicLong LAST_TIME_MS = new AtomicLong();
	String[] from = {"r", "c"};
	int[] to = {R.id.i_key, R.id.i_value};
	ListView l;
	TextView tv, t, tttt;
	String equityName;
	//getNews newsGet;
	ProgressBar p;
	TimerTask doAsynchronousTask;
	SimpleAdapter a;
	String type,eq_Name;
	Timer timer;
    graph g;
	HttpGetAsyncTask load;
	Handler handler;
	Dialog dialog;
    View view;
    String u;
	String[] xax;
	XYMultipleSeriesDataset mDataSet = new XYMultipleSeriesDataset();
    TimeSeries mCurrent = new TimeSeries("Visits");
    static String ff;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.eq, null);
		Bundle extras = getArguments();
        equityName = extras.get("q").toString();
        eq_Name = extras.get("name").toString();
        type = extras.get("type").toString();
        
        Log.d("Received", eq_Name+" "+type+":"+equityName);

        ff = extras.getString("f");

        Log.d("FROM::: ", ""+ff);
        l = (ListView) view.findViewById(R.id.equityL);
        p = (ProgressBar) view.findViewById(R.id.progressBar1);
        TextView eq_name = (TextView) view.findViewById(R.id.eq);
        eq_name.setText(eq_Name);
        TextView sc = (TextView) view.findViewById(R.id.type_code);
        sc.setText(type+":"+equityName);
        t = (TextView) view.findViewById(R.id.val);		
		tv = (TextView) view.findViewById(R.id.eq_change);
		tv.setText("");
        watch = (ImageButton) view.findViewById(R.id.watchlist);
        watch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addToWatch(eq_Name, type, equityName);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
		add = (ImageButton) view.findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					addToPort(eq_Name, type, equityName);
			}
		});
		dialog = new Dialog(getActivity());
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
        dialog.setContentView(R.layout.alerts);
        dialog.setTitle("Add Alert");
		sv = (Button) dialog.findViewById(R.id.save);
		tttt = (TextView) dialog.findViewById(R.id.eVal);
        final InputMethodManager ime = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

		al = (ImageButton) view.findViewById(R.id.alert);
		al.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
                dialog.show();
                ime.showSoftInput(tttt, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		
		
		
		sv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.rg1);
				int sId = rg.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) dialog.findViewById(sId);
				String strr = tttt.getText().toString();
				addToAl(eq_Name, type, equityName, strr, rb.getText().toString());
				dialog.dismiss();
                ime.hideSoftInputFromWindow(tttt.getWindowToken(), 0);
			}
		});
		
		eq_name.setText(eq_Name);
        load = new HttpGetAsyncTask();
        if(check(equityName)){
        	watch.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_important));
        }
        else{
        	watch.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_not_important));
        }
        
		return view;
	}
	
	public static String getFrom(){

        Log.d("FROM::: ", ""+ff);
		return ff;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager =
				(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public void callIt(){
        handler = new Handler();
	    doAsynchronousTask = new TimerTask() {       
	        @Override
	        public void run() {
	            handler.post(new Runnable() {
	                public void run() {       
	                    try {
	                    		if(isNetworkAvailable()){
		                    		
		                            g = new graph();
		                            g.execute(equityName, type);
		                    		load = new HttpGetAsyncTask();
			                        load.execute(equityName, type);
			                        //Log.d("doingEQ", "yes");	
	                    		}
	                    		else{
	                    			Toast.makeText(getActivity(), "No Connection!",
	                    					Toast.LENGTH_LONG).show();
	                    		}
	                    } catch (Exception e) {
	                    	Log.d("doingEQ", "NO!! " +e);
	                    }
	                }
	            });
	        }
	    };
	    timer = new Timer();
	    timer.schedule(doAsynchronousTask, 0, 5000);
	} 
	
	@Override
	public void onResume(){
		super.onResume();
		//Log.d("Check", "EQ Resume");
		callIt();
	}

	@Override
	public void onPause(){
		super.onPause();
		//Log.d("Check", "EQ Pause");
		timer.cancel();
		if(load!=null)
			load.cancel(true);
	}

	public void addToWatch(String name, String type, String code) throws JSONException, IOException {
		JSONObject jsonObjMain, jsonObjTemp;
		JSONObject jO = new JSONObject();
		JSONArray jsonArray_stampi;
		File jsonFile;
		
		File folder = new File(Environment.getExternalStorageDirectory() + "/StockSense");
		jsonFile = new File(folder, "portfolio.json");
		StringBuilder text = new StringBuilder(); 
		BufferedReader br = new BufferedReader(new FileReader(jsonFile));  
        String line;  

        while ((line = br.readLine()) != null) {  
            text.append(line);  
        }  
        jsonObjMain = new JSONObject(text.toString());
        jsonArray_stampi = jsonObjMain.getJSONArray("portfolio"); //Array where you wish to append
	
		
            
		if(!check(code)){
		  //Add data
            jO.put("name",name);
            jO.put("code",code);
            jO.put("type",type);

            //Append
            jsonArray_stampi.put(jO);
            JSONObject jsonO = new JSONObject();
            jsonO.put("portfolio", jsonArray_stampi);
            FileOutputStream  fos = new FileOutputStream(jsonFile);
            fos.write(jsonO.toString().getBytes());
            fos.close();
        	watch.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_important));

            Toast.makeText(getActivity(), "Added", Toast.LENGTH_LONG).show();
  
		}else{
			JSONArray list = new JSONArray(); 
			for(int i=0;i<jsonArray_stampi.length();i++){
				JSONObject r = jsonArray_stampi.getJSONObject(i);
				jsonObjTemp = new JSONObject();
				if(!r.getString("code").equals(code)){
					jsonObjTemp.put("name", r.getString("name"));
					jsonObjTemp.put("code", r.getString("code"));
					jsonObjTemp.put("type", r.getString("type"));
					list.put(jsonObjTemp);
				}
			}
			JSONObject jo = new JSONObject();
			jo.put("portfolio", list);
			FileOutputStream  fos = new FileOutputStream(jsonFile);
            fos.write(jo.toString().getBytes());
            fos.close();
        	watch.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_not_important));
            Toast.makeText(getActivity(), "Removed", Toast.LENGTH_LONG).show();
		}
	}

    public void addToPort(final String name, final String type, final String code){

        final DatabaseHandler db = new DatabaseHandler(getActivity());
        ArrayList<HashMap<String, String>> f = (ArrayList<HashMap<String, String>>) db.getAllFolders();
        final Dialog d = new Dialog(getActivity());
        d.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
        d.setContentView(R.layout.portadd);
        final ListView portAdd = (ListView) d.findViewById(R.id.portList);
        d.setTitle("Select Portfolio");
        Button cNew = (Button) d.findViewById(R.id.cNew);
        cNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Title");

                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.addFolder(input.getText().toString());
                        ArrayList<HashMap<String, String>> f = (ArrayList<HashMap<String, String>>) db.getAllFolders();
                        SimpleAdapter simple = new SimpleAdapter(getActivity(), f, R.layout.portadd_sub
                                , new String[]{"id", "name"}, new int[]{R.id.portaddid, R.id.portaddname});
                        portAdd.setAdapter(simple);



                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        portAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewL, int i, long l) {

                final TextView IDView = (TextView) viewL.findViewById(R.id.portaddid);
                TextView portaddName = (TextView) viewL.findViewById(R.id.portaddname);
                final Dialog di = new Dialog(getActivity());
                di.setTitle("Add to "+portaddName.getText().toString().trim());
                di.setContentView(R.layout.portcond);
                di.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
                final Button submit = (Button) di.findViewById(R.id.sub);
                final TextView price = (TextView) di.findViewById(R.id.price);
                final TextView shares = (TextView) di.findViewById(R.id.shares);

                submit.setEnabled(price.getText().toString().trim().length() == 0 ||
                        shares.getText().toString().trim().length() == 0 ? false : true);
                price.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        submit.setEnabled(price.getText().toString().trim().length() == 0 ||
                                shares.getText().toString().trim().length() == 0 ? false : true);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                final InputMethodManager ime = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                final TextView broker = (TextView) di.findViewById(R.id.broker);
                final Switch bs = (Switch) di.findViewById(R.id.type);
                submit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int p = Integer.parseInt(price.getText().toString());
                        int s = Integer.parseInt(shares.getText().toString());
                        int b;
                        if(broker.getText().toString().trim().equals(""))
                            b = 0;
                        else
                            b = Integer.parseInt(broker.getText().toString());
                        String buy;
                        if (bs.isChecked())
                            buy = "S";
                        else
                            buy = "B";
                        int ID = Integer.parseInt(IDView.getText().toString());
                        Log.d("BUY", buy + s);

                        port pMan = new port();

                        pMan.setQty(s);
                        pMan.setDate(new Date());
                        pMan.setPrice(p);
                        pMan.setName(name);
                        pMan.setCode(code);
                        pMan.setType(type);
                        pMan.setCom(b);
                        pMan.setID(ID);
                        pMan.setBS(buy);
                        if (name != null)
                            db.addPort(pMan);
                        di.hide();
                        d.hide();
                        ime.hideSoftInputFromWindow(price.getWindowToken(), 0);
                    }
            });
            di.show();

            }

        });

        SimpleAdapter simple = new SimpleAdapter(getActivity(), f, R.layout.portadd_sub
                        , new String[]{"id", "name"}, new int[]{R.id.portaddid, R.id.portaddname});
        portAdd.setAdapter(simple);
        d.show();
    }
	
	public void addToAl(String name, String type, String code, String value, String todo){
		
		DatabaseHandler db = new DatabaseHandler(getActivity());
		db.addContact(new alerts(name, code, type, todo, value, "N"));
		Toast.makeText(getActivity(), "Alert created", Toast.LENGTH_LONG).show();
	}
	
	public boolean check(String id){
		boolean eb = false;
		try {
			File folder = new File(Environment.getExternalStorageDirectory() + "/StockSense");
			File jsonFile = new File(folder, "portfolio.json");
			StringBuilder text = new StringBuilder(); 
			BufferedReader br = new BufferedReader(new FileReader(jsonFile));  
            String line;  

            while ((line = br.readLine()) != null) {  
                text.append(line);  
            }  
            JSONObject jsonObjMain = new JSONObject(text.toString()); //Your existing object
            JSONArray jsonArray_stampi = jsonObjMain.getJSONArray("portfolio"); //Array where you wish to append
            
            for(int i = 0;i<jsonArray_stampi.length();i++){
            	
            	JSONObject r = jsonArray_stampi.getJSONObject(i);
            	if(r.getString("code").equals(id)){
            		Log.d(r.getString("code"), id);
            		eb = true;
            		break;
            	}
            	else
            		Log.d(r.getString("code"), id);
            }
  
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
				e.printStackTrace();
			}
        return eb;
		
	}
	
	public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter(); 
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	class HttpGetAsyncTask extends AsyncTask<String, Void, List<String>> {
		String cN, cT;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}
		
		@Override
		protected void onCancelled() {
	    }

		@Override
		protected List<String> doInBackground(String... q) {
				
			cN=q[0];
			cT=q[1];
			String v = null, c = null;
			
			try {
			String url = getResources().getString(R.string.website) +
				"/stocks.php?f="+URLEncoder.encode(q[0], "UTF-8")+"&t="+q[1];
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
			
				HttpResponse httpResponse = httpClient.execute(httpGet);
				InputStream inputStream = httpResponse.getEntity()
						.getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk;
				
				while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
					stringBuilder.append(bufferedStrChunk);
				}
				js = stringBuilder.toString();
				try {
					JSONObject jsonObj = new JSONObject(js);
					
					String type = jsonObj.getString("2");
					v = jsonObj.getString("3");
					c = jsonObj.getString("4")+" "+jsonObj.getString("1");
					if(type.equals("EQ")){
						
						
						String[] n = new String[17];
						for(int i = 6;i<=16;i++){
							n[i] = jsonObj.getString("" + i);
						}
						if(!fillMaps.isEmpty())
							fillMaps.clear();
						for(int i=6;i<=14;i++){
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("r", label_eq[i]);
							map.put("c", n[i].replace("&nbsp;&nbsp;&nbsp;&nbsp;", "").replace("\n", ""));
							fillMaps.add(map);
						}
						
					}
					else{
						
						String[] n = new String[10];
						for(int i = 6;i<=9;i++){
							n[i] = jsonObj.getString(""+i);
							Log.d("HELLO"+i, n[i]);
						}
						if(!fillMaps.isEmpty())
							fillMaps.clear();
						for(int i=6;i<=9;i++){
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("r", label_st[i]);
							map.put("c", n[i].replace("&nbsp;&nbsp;&nbsp;&nbsp;", "").replace("\n", ""));
							fillMaps.add(map);
						}
					}
						
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (ClientProtocolException cpe) {
				cpe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			LIST.clear();
			LIST.add(v);
			LIST.add(c);
			return LIST;
			
		}

		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			p.setVisibility(View.INVISIBLE);
			if(isAdded()){
				if(result.size()==2){
					if(result.get(1).startsWith("-"))
						tv.setTextColor(getResources().getColor(R.color.Red));
					else
						tv.setTextColor(getResources().getColor(R.color.DarkGreen));
                    if (!isCancelled() && isAdded()) {
                        a = new SimpleAdapter(getActivity(), fillMaps,R.layout.eqlist , from, to);
                        a.notifyDataSetChanged();
                        l.setAdapter(a);
                        setListViewHeightBasedOnChildren(l);
                        t.setText(result.get(0));
                        tv.setText(result.get(1));
                        load.cancel(true);
                    }
                }
				else{
					Toast.makeText(getActivity(), "Unable to connect to servers.",
							Toast.LENGTH_LONG).show();
				}
			}
		}

	}
	
	class graph extends AsyncTask<String, Void, String> {
		int l=1, c=0;
	    String n[] = new String[188];
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... q) {
			
			String type = q[1];
			if(type.equals("BSE")){
				type = "BOM";
			}
			
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(
					"http://www.google.com/finance/getprices?q="+URLEncoder.encode(q[0], "UTF-8")+"&x="+type+"&i=120&p=1d&f=d,c,h,l,o,v");

				HttpResponse httpResponse = httpClient.execute(httpGet);
				InputStream inputStream = httpResponse.getEntity()
						.getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk;
				
				n = new String[188];
				xax = new String[188];
				while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
					stringBuilder.append(bufferedStrChunk);
					if(l==8){
						u = bufferedStrChunk.split(",")[0];
					}
					if(l>=8){
						n[c] = bufferedStrChunk.split(",")[1];
						//xax[c] = bufferedStrChunk.split(",")[0];
						c++;
					}
					l++;
				}	
				
				
			} catch(Exception e){
				Log.d("Error", e.toString());
			}
			return null;
			
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(isAdded()){
				if(u!=null){
					if(result==null){

						LinearLayout gd = (LinearLayout) view.findViewById(R.id.gd);
						gd.setVisibility(View.VISIBLE);
						TimeSeries series = new TimeSeries("Visits");
						for(int i=0;i<c;++i){
							try {
								String allexam_end_time = "09:15:00";
							    SimpleDateFormat sdfkk = new SimpleDateFormat("hh:mm");
							    Date reqEndtimekk;
								reqEndtimekk = sdfkk.parse(allexam_end_time);
								Calendar instance = GregorianCalendar.getInstance();
							    instance.setTime(reqEndtimekk);
							    instance.add(GregorianCalendar.MINUTE, (i*2));
							    try{
								    Double d = Double.parseDouble(n[i]);
								    series.add(instance.getTime(), d);
							    }catch(Exception e){
							    	Log.d("E", e.toString());
							    }
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						mDataSet.removeSeries(mCurrent);
						mCurrent = series;
				        mDataSet.addSeries(mCurrent);
				        XYSeriesRenderer viewsRenderer = new XYSeriesRenderer();
				        //viewsRenderer.setPointStyle(PointStyle.CIRCLE);
				        viewsRenderer.setFillPoints(true);
				        viewsRenderer.setLineWidth(2);
				        //viewsRenderer.setDisplayChartValues(true);
				        viewsRenderer.setColor(Color.WHITE);
				        
				        // Creating a XYMultipleSeriesRenderer to customize the whole chart
				        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
				        multiRenderer.setApplyBackgroundColor(true);
				        multiRenderer.setBackgroundColor(getResources().getColor(R.color.bg));
				        multiRenderer.setAxesColor(Color.GRAY);
				        multiRenderer.setXLabelsColor(Color.WHITE);
				        multiRenderer.setYLabelsAlign(Align.RIGHT);
				        multiRenderer.setYLabelsColor(0, Color.WHITE);
				        multiRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
				        multiRenderer.setShowGrid(true);
				        multiRenderer.setScale(1000);
				        //multiRenderer.setZoomInLimitY(ymax);
				        multiRenderer.setPanEnabled(false, false);
				        multiRenderer.setZoomEnabled(false, false);
				        multiRenderer.setGridColor(Color.GRAY);
				        multiRenderer.setZoomButtonsVisible(false);
				        multiRenderer.addSeriesRenderer(viewsRenderer);
				        multiRenderer.setShowLegend(false);
						final GraphicalView mChart =
                                ChartFactory.getTimeChartView(getActivity().getBaseContext(),
                                mDataSet, multiRenderer,"hh:mm");
						mChart.setBackgroundColor(getResources().getColor(R.color.bg));
						gd.addView(mChart);
						
					}
					else{
						view.findViewById(R.id.eM).setVisibility(View.VISIBLE);
					}
				}
				else{
					view.findViewById(R.id.eM).setVisibility(View.VISIBLE);
				}
				
			}
		}
		
	}
	
	/*class getNews extends AsyncTask<String, String, String>{
		
		@Override
		protected String doInBackground(String... q) {
			String xml = null;
			String type = q[1];
			if(type.equals("BSE")){
				type = "BOM";
			}
		    try {
		    	String url = getResources().getString(R.string.website) +"/comp.php?f="+q[0]+"&t="+type;
		    	XMLParser parser = new XMLParser();
			    xml = parser.getXmlFromUrl(url); //get XML
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource is = new InputSource(new StringReader(xml.trim()));
				Document doc = builder.parse(is); // get DOM elem.
			    NodeList nl = doc.getElementsByTagName("item");
			    //loop
			    int len=nl.getLength();

			    if(!pList.isEmpty())
					pList.clear();
			    for (int i=0; i< len; i++){
			        HashMap<String, String> map = new HashMap<String, String>();
			        Element e = (Element) nl.item(i);
			        //add to map

			        NodeList title = e.getElementsByTagName("title");
		            Element tline = (Element) title.item(0);
		            NodeList dn = e.getElementsByTagName("link");
		            Element dline = (Element) dn.item(0);
		            NodeList da = e.getElementsByTagName("pubdate");
		            Element daline = (Element) da.item(0);
			        map.put("name", tline.getTextContent().trim());
			        map.put("date", ""+daline.getTextContent());
			        map.put("link", ""+dline.getTextContent());
			        // hash => list
			        pList.add(map);
			    }
				Log.d("XML "+len, pList.size()+"-sized "+url);

				
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			} catch (SAXException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    
			
			return xml;
			
		}
		
		@Override
		protected void onPostExecute(String xml) {
			
			super.onPostExecute(xml);
			if(xml.equals("WI")){
				Toast.makeText(getActivity().getApplicationContext(), 
						"Weak Internet. Try again later", Toast.LENGTH_LONG).show();
			}
			else{
				
				nList = new ArrayList<HashMap<String,String>>(pList);
				SimpleAdapter adapter = new SimpleAdapter(getActivity(), nList, R.layout.comp, 
						new String[]{"name", "date", "link"}, 
						new int[]{R.id.ctitle, R.id.cdate, R.id.clink});
				ListView newsL = (ListView) view.findViewById(R.id.cNews);
				newsL.setAdapter(adapter);
				newsL.setOnTouchListener(new OnTouchListener() {
				    // Setting on Touch Listener for handling the touch inside ScrollView
				    @Override
				    public boolean onTouch(View v, MotionEvent event) {
				    // Disallow the touch request for parent scroll on touch of child view
				    v.getParent().requestDisallowInterceptTouchEvent(true);
				    return false;
				    }
				});
			}
		}
		
		@Override
		protected void onPreExecute(){
		}		
	}*/
}