package com.infigent.stocksense;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.google.android.gms.ads.InterstitialAd;

import adapters.SuggestionsAdapter;

public class Fragment3 extends Fragment {

	ArrayList<HashMap<String, String>> ports =
    		new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> p =
    		new ArrayList<HashMap<String, String>>();
	String x = null;
	ListView lv;
	Handler handler;
	TimerTask doAsynchronousTask;
	HttpGetAsyncTask load;
    Timer timer = new Timer();
    int s=0;
    TextView check, note;
    ImageView img;
    ProgressBar pb;
    LinearLayout ll;
    private InterstitialAd mInterstitialAd;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.portfolio, null);
        ImageButton add = (ImageButton) view.findViewById(R.id.watchAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d = new Dialog(getActivity());
                d.setContentView(R.layout.watch_add);
                d.setTitle("Add To Watchlist");
                final AutoCompleteTextView search = (AutoCompleteTextView)	d.findViewById(R.id.watchSearch);
                search.setThreshold(2);
                search.setAdapter(new SuggestionsAdapter(getActivity(), search.getText().toString()));
                d.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
                Button conf = (Button) d.findViewById(R.id.watch_add);
                final TextView aName, aCode, aType;
                aName = (TextView) d.findViewById(R.id.watchName);
                aCode= (TextView) d.findViewById(R.id.watchCode);
                aType = (TextView) d.findViewById(R.id.watchType);

                search.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        String term = parent.getItemAtPosition(pos).toString();
                        String[] lines = term.split("\\r?\\n");
                        String name = lines[0];
                        String code = lines[1].substring(lines[1].indexOf(":")+1, lines[1].length()).trim();
                        String type = lines[1].substring(0, lines[1].indexOf(":"));
                        search.setText(name);
                        aName.setText(name);
                        aCode.setText(code);
                        aType.setText(type);
                    }
                });

                conf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(aName.getText().toString().trim().length()!=0){
                            try {
                                addToWatch(aName.getText().toString(),
                                        aType.getText().toString(), aCode.getText().toString());
                                aName.setText("");
                                aCode.setText("");
                                aType.setText("");
                                if(getS()!=0){
                                    check.setVisibility(View.GONE);
                                    img.setVisibility(View.GONE);
                                    note.setVisibility(View.VISIBLE);
                                    ll.setVisibility(View.VISIBLE);
                                    lData();
                                    callIt();
                                }
                                else{
                                    check.setVisibility(View.VISIBLE);
                                    img.setVisibility(View.VISIBLE);
                                    note.setVisibility(View.GONE);
                                    ll.setVisibility(View.GONE);
                                    lv.setVisibility(View.GONE);
                                }
                                lData();
                                callIt();
                            } catch (Exception e) {
                                Log.d("Error 3", e.toString());
                            }
                        }else{
                            Toast.makeText(getActivity(), "Please select a stock", Toast.LENGTH_SHORT).show();
                        }
                        d.hide();
                    }
                });
                d.show();

            }
        });
		img = (ImageView) view.findViewById(R.id.pnone);
        check = (TextView) view.findViewById(R.id.check);
        lv = (ListView) view.findViewById(R.id.port);
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                    int position, long arg3) {

                final TextView id = (TextView) v.findViewById(R.id.portc);
                AlertDialog.Builder alert = new AlertDialog.Builder(
				        getActivity());

				alert.setTitle("Delete");
				alert.setMessage("Do you want delete this item?");
				alert.setPositiveButton("YES", new OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {

				    	try {
							deleteAlert(id.getText().toString().substring(5
									, id.getText().toString().length()));
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

				    }
				});
				alert.setNegativeButton("CANCEL", new OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        dialog.dismiss();
				    }
				});

				alert.show();



                return true;
            }
     });
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				if(load!=null)
					load.cancel(true);
				timer.cancel();
				TextView tv = (TextView)view.findViewById(R.id.portc);
				TextView nn = (TextView)view.findViewById(R.id.portn);
				String nm = nn.getText().toString();
				String tp = tv.getText().toString().substring(0, 3);
				String cd = tv.getText().toString().substring(5, tv.getText().toString().length());
				Log.d("Type", tp);
				Log.d("Code", cd);
				Bundle data = new Bundle();
		        data.putString("q", cd);
		        data.putString("type", tp);
		        data.putString("name", nm);
		        data.putString("f", "3");
				android.support.v4.app.FragmentTransaction transaction =
						getActivity().getSupportFragmentManager().beginTransaction();
				Fragment4 equity = new Fragment4();
				equity.setArguments(data);
				switchFragment(equity);
			}
		});

        pb=(ProgressBar) view.findViewById(R.id.portpb);
        ll = (LinearLayout) view.findViewById(R.id.ll);
        note = (TextView) view.findViewById(R.id.note);
        pb.setVisibility(View.INVISIBLE);


		return view;
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

            Toast.makeText(getActivity(), "Added", Toast.LENGTH_LONG).show();

        }else{

            Toast.makeText(getActivity(), "This stock has already been added", Toast.LENGTH_LONG).show();

        }
    }


	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchContent(fragment);
		}

	}

	public void deleteAlert(String code)
								throws JSONException, IOException{

		JSONObject jsonObjMain, jsonObjTemp;
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
        if(getS()!=0){

			timer.cancel();
			if(doAsynchronousTask!=null)
				doAsynchronousTask.cancel();
			if(load!=null)
				load.cancel(true);
			Log.d("Ports resume", "r");
			check.setVisibility(View.GONE);
			img.setVisibility(View.GONE);
			note.setVisibility(View.VISIBLE);
			ll.setVisibility(View.VISIBLE);
			lData();
			callIt();
		}
		else{
			Log.d("Ports resume", "r");
			check.setVisibility(View.VISIBLE);
			img.setVisibility(View.VISIBLE);
			note.setVisibility(View.GONE);
			ll.setVisibility(View.GONE);
			lv.setVisibility(View.GONE);
		}
        Toast.makeText(getActivity(), "Removed", Toast.LENGTH_LONG).show();


	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public int getS(){

		int s = 0;
        try {
        	File folder = new File(Environment.getExternalStorageDirectory() + "/StockSense/");
    		File jsonFile = new File(folder, "portfolio.json");
    		StringBuilder text = new StringBuilder();
    		BufferedReader br = new BufferedReader(new FileReader(jsonFile));
            String line;
			while ((line = br.readLine()) != null) {
			    text.append(line);
			}
			JSONObject jsonObjMain = new JSONObject(text.toString()); //Your existing object
	        JSONArray jsonArray = jsonObjMain.getJSONArray("portfolio");
	        s = jsonArray.length();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return s;

	}

	@Override
	public void onPause() {
		super.onPause();
		if(getS()!=0){
			timer.cancel();
			if(doAsynchronousTask!=null)
				doAsynchronousTask.cancel();
			if(load!=null)
				load.cancel(true);
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		if(getS()!=0){
			Log.d("Ports resume", "r");
			check.setVisibility(View.GONE);
			img.setVisibility(View.GONE);
			note.setVisibility(View.VISIBLE);
			ll.setVisibility(View.VISIBLE);
			lData();
			callIt();
		}
		else{
			Log.d("Ports resume", "r");
			check.setVisibility(View.VISIBLE);
			img.setVisibility(View.VISIBLE);
			note.setVisibility(View.GONE);
			ll.setVisibility(View.GONE);
		}

	}

	public void callIt(){
		fresh();
        if(getS()!=0){
            timer.cancel();
            if(doAsynchronousTask!=null)
                doAsynchronousTask.cancel();
            if(load!=null)
                load.cancel(true);
        }
        handler = new Handler();
	    doAsynchronousTask = new TimerTask() {
	        @Override
	        public void run() {
	            handler.post(new Runnable() {
	                public void run() {
	                    try {
	                    	if(isNetworkAvailable()){
		                    	if(load!=null){
	                    			load.cancel(true);
	                    		}
		                    	//lData();
	                    		load = new HttpGetAsyncTask();
		                        load.execute();
	                    	}
	                    	else{
	                    		Log.d("IE", "IE");
	                    		lData();
	                    	}
	                    } catch (Exception e) {
	                    	Log.d("Ex Port", e.toString());
	                    }
	                }
	            });
	        }
	    };
	    timer = new Timer();
	    timer.schedule(doAsynchronousTask, 0, 30000);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager =
				(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}


	public void lData(){

		try {
			File folder = new File(Environment.getExternalStorageDirectory() + "/StockSense/");
			File jsonFile = new File(folder, "portfolio.json");
			StringBuilder text = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(jsonFile));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
			}
			JSONObject jsonObjMain = new JSONObject(text.toString()); //Your existing object
			JSONArray jsonArray = jsonObjMain.getJSONArray("portfolio");

			if(!ports.isEmpty())
				ports.clear();
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject r = jsonArray.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				String name = r.getString("name");
				String code = r.getString("code");
				String type = r.getString("type");
				map.put("name", name);
				map.put("code", type+": "+code);
				map.put("type", "");
				ports.add(map);
			}

			SimpleAdapter adapter = new SimpleAdapter(getActivity(),
					ports, R.layout.playout, new String[] { "name",
				"code", "type" }, new int[] { R.id.portn, R.id.portc,
				R.id.portv });
			adapter.notifyDataSetChanged();
			lv.setAdapter(adapter);

			Log.d("lData", "lData");

		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void fresh(){

		int start = 9; // let's take your failing example: 21-07
		int end = 16;
		int hours = (end - start) % 24; // here hours will be 14

		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("IST"));
		// set calendar to TODAY 21:00:00.000
		cal.set(Calendar.HOUR_OF_DAY, start);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		long shm = cal.getTimeInMillis();

		// add 14 hours = TOMORROW 07:00:00.000
		cal.add(Calendar.HOUR_OF_DAY, hours);
		long ehm = cal.getTimeInMillis();
		long chm = System.currentTimeMillis();
		if(chm<ehm && chm>shm)
			Log.d("START:END:CURRENT", shm+":"+chm+":"+ehm);
		else
			Log.d("START:END:CURRENT NO", shm+":"+chm+":"+ehm);

	}

	public void write(String sFileName, String sBody){
	    try
	    {
	        File root = new File(Environment.getExternalStorageDirectory(), "StockSense");
	        if (!root.exists()) {
	            root.mkdirs();
	        }
	        File gpxfile = new File(root, sFileName);
	        FileWriter writer = new FileWriter(gpxfile);
	        writer.append(sBody);
	        writer.flush();
	        writer.close();
	    }
	    catch(IOException e)
	    {
	         e.printStackTrace();
	    }
	   }

	class HttpGetAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
            Log.d("doing", "yes");
            pb.setVisibility(View.VISIBLE);

		}

		@Override
		protected void onCancelled() {
	        super.onCancelled();
			Log.d("PORTS Cancelled","cancelled");

	    }
		@Override
		protected String doInBackground(String... q) {

			String v = null;
			for(int countervar=0;countervar<1;countervar++){
				if(load.isCancelled())
					break;

			try {
				File folder = new File(Environment.getExternalStorageDirectory() + "/StockSense/");
				File jsonFile = new File(folder, "portfolio.json");
				StringBuilder text = new StringBuilder();
				BufferedReader br = new BufferedReader(new FileReader(jsonFile));
	            String line;

	            while ((line = br.readLine()) != null) {
	                text.append(line);
	            }
	            JSONObject jsonObjMain = new JSONObject(text.toString()); //Your existing object
	            JSONArray jsonArray = jsonObjMain.getJSONArray("portfolio");

	            HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(getResources().getString(R.string.website) +
                        "/port.php?j="+ URLEncoder.encode(text.toString(), "UTF-8"));
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

                JSONObject jsonObj = new JSONObject(stringBuilder.toString());
				if(!p.isEmpty())
					p.clear();

		        for(int i = 0; i < jsonArray.length(); i++){
		        	JSONObject r = jsonArray.getJSONObject(i);
		            HashMap<String, String> map = new HashMap<String, String>();
		            String name = r.getString("name");
		            String code = r.getString("code");
		            String type = r.getString("type");

                    JSONObject jA = jsonObj.getJSONObject(code+"_"+type);

					v = stringBuilder.toString();
					String string = jA.getString("2");
		            map.put("name", name);
		            map.put("code", type+": "+code);
		            map.put("type", string);
		            map.put("t", jA.getString("3")+" "+jA.getString("1"));
		            p.add(map);
	           }

			} catch (ClientProtocolException cpe) {
				cpe.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}catch (JSONException e) {
				e.printStackTrace();
			}
			}

			return v;

		}

		protected void onPostExecute(String result) {

			pb.setVisibility(View.INVISIBLE);
			if(isCancelled()||!isAdded()){

			}
			else{
				if(result!=null){
					ports = new ArrayList<HashMap<String,String>>(p);
					if(ports.isEmpty()){
						Log.d("ports", "empty");
						lData();
					}
					else{
						SimpleAdapter adapter = new SimpleAdapter(getActivity(),
							ports, R.layout.playout, new String[] { "name",
						"code", "type", "t" }, new int[] { R.id.portn, R.id.portc,
						R.id.portv, R.id.portperc });

						SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
							@Override
							public boolean setViewValue(View view, Object object, String value) {

						    	View p = (View)view.getParent();
						    	TextView bk = (TextView) p.findViewById(R.id.portv);
						    	TextView bm = (TextView) view.findViewById(R.id.portperc);
						    	Boolean b = true;
						    	if (view.equals((TextView) view.findViewById(R.id.portperc))){
                                    if(value.contains("/")){
                                        bk.setTextColor(getResources().getColor(R.color.White));
                                        bm.setTextColor(getResources().getColor(R.color.White));
                                    }
					                else if(value.contains("-")){
						                bk.setTextColor(getResources().getColor(R.color.app_red));
						                bm.setTextColor(getResources().getColor(R.color.app_red));
					                }
					                else{
					                	bk.setTextColor(getResources().getColor(R.color.graphg));
					                	bm.setTextColor(getResources().getColor(R.color.graphg));
					                }
					            }

				                return false;
					        }
						 };
						 adapter.setViewBinder(binder);

						adapter.notifyDataSetChanged();
						lv.setAdapter(adapter);
						write("port.json", result);
						Log.d("Port Async", "Done");
					}
				}
				else{
					lData();
				}
			}
		}

		protected void onProgressUpdate(String... progress) {

			pb.setVisibility(View.VISIBLE);

		}

	}

}