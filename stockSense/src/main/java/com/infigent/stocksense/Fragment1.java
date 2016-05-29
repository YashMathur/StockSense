package com.infigent.stocksense;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment1 extends Fragment {

	JSONArray losers = null, sensex = null;
    JSONArray data = null, nifty = null;
    JSONArray gainers = null;
    JSONArray gainersb = null;
    JSONArray losersb = null;    
    List<String> LIST = new ArrayList<String>();
    ArrayList<HashMap<String, String>> loserList = 
    		new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> gainerList = 
    		new ArrayList<HashMap<String, String>>(); 
    ArrayList<HashMap<String, String>> loserListB = 
    		new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> gainerListB = 
    		new ArrayList<HashMap<String, String>>(); 
    ArrayList<HashMap<String, String>> ploserList = 
    		new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> pgainerList = 
    		new ArrayList<HashMap<String, String>>(); 
    ArrayList<HashMap<String, String>> ploserListB = 
    		new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> pgainerListB = 
    		new ArrayList<HashMap<String, String>>();
    GainLose get = null;
    View view;
    boolean vis = false, visi=false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.home, null);
		Switch gainers = (Switch) view.findViewById(R.id.switch1);
		gainers.setChecked(true);
		gainers.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked){
					view.findViewById(R.id.listView1).setVisibility(View.VISIBLE);
	            	view.findViewById(R.id.bsegain).setVisibility(View.GONE);
				}else{
					view.findViewById(R.id.bsegain).setVisibility(View.VISIBLE);
	        		view.findViewById(R.id.listView1).setVisibility(View.GONE);
				}
				
			}
			
		});
        ImageButton ref = (ImageButton) view.findViewById(R.id.ref);

        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Doing Load", "Home");
                if (isNetworkAvailable()) {
                    get = new GainLose();
                    get.execute();
                    Log.d("Doing Load", "Home");
                } else {
                    Toast.makeText(getActivity(), "Internet not available", Toast.LENGTH_LONG).show();
                }
            }
        });

		Switch losers = (Switch) view.findViewById(R.id.switch2);
		losers.setChecked(true);
		losers.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked1) {
				
				if(isChecked1){
					view.findViewById(R.id.listView2).setVisibility(View.VISIBLE);
	        		view.findViewById(R.id.bselose).setVisibility(View.GONE);
				}else{
					view.findViewById(R.id.bselose).setVisibility(View.VISIBLE);
	        		view.findViewById(R.id.listView2).setVisibility(View.GONE);;
				}
				
			}
			
		});
		makeAl();
		makeP();
		makeN();

		return view;
	}
	
	public void makeAl(){
		try{
			InputStream is = getActivity().getAssets().open("alerts.json");
			int size = is.available();
			byte[] buffer = new byte[size];
		    is.read(buffer);
		    is.close();
		    String line =	new String(buffer, "UTF-8");
			File folder = new File(Environment.getExternalStorageDirectory() + "/StockSense");
			Boolean success = true;
			if(!folder.exists())
				success = folder.mkdir();
			
			if(success){
				File jsonFile = new File(folder, "alerts.json");
				if(!jsonFile.exists()){
					jsonFile.createNewFile();
					FileOutputStream  fos = new FileOutputStream(jsonFile);

		            fos.write(line.getBytes());
		            fos.close();
		            Log.d("CREATED", "ALERTS");
				}
				
				
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void makeP(){
		try{
			InputStream is = getActivity().getAssets().open("port.json");
			int size = is.available();
			byte[] buffer = new byte[size];
		    is.read(buffer);
		    is.close();
		    String line =	new String(buffer, "UTF-8");
			File folder = new File(Environment.getExternalStorageDirectory() + "/StockSense");
			Boolean success = true;
			if(!folder.exists())
				success = folder.mkdir();
			
			if(success){
				File jsonFile = new File(folder, "portfolio.json");
				if(!jsonFile.exists()){
					jsonFile.createNewFile();
					FileOutputStream  fos = new FileOutputStream(jsonFile);

		            fos.write(line.getBytes());
		            fos.close();
				}
				
				
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void makeN(){
		try{
			File folder = new File(Environment.getExternalStorageDirectory() + "/StockSense");
			Boolean success = true;
			if(!folder.exists())
				success = folder.mkdir();
			
			if(success){
				File jsonFile = new File(folder, "news.json");
				if(!jsonFile.exists()){
					jsonFile.createNewFile();
				}
				
				
			}
		}
		catch (IOException e) {
			e.printStackTrace();
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
	
	public void lData(){
		
		try {
			File folder = new File(Environment.getExternalStorageDirectory() + "/StockSense/");
			File jsonFile = new File(folder, "main.json");
			StringBuilder text = new StringBuilder(); 
			BufferedReader br = new BufferedReader(new FileReader(jsonFile));  
			String line;  

			while ((line = br.readLine()) != null) {  
				text.append(line);  
			}  
			JSONObject jsonObj = new JSONObject(text.toString()); //Your existing object

			if(!gainerList.isEmpty())
				gainerList.clear();
			if(!gainerListB.isEmpty())
				gainerListB.clear();
			if(!loserListB.isEmpty())
				loserListB.clear();
			if(!loserList.isEmpty())
				loserList.clear();
			
			gainers = jsonObj.getJSONArray("gain");
			for (int i = 0; i < gainers.length(); i++) {
				JSONObject t = gainers.getJSONObject(i);
				String symbol = t.getString("symbol");
				String ltp = t.getString("ltp");
				String np = t.getString("netPrice");
				if(symbol!="null"||np!="null"||ltp!="null"){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("symbol", symbol);
					map.put("ltp", ltp);
					map.put("change", np);
					gainerList.add(map);
				}
			}
			losers = jsonObj.getJSONArray("lose");
			for (int i = 0; i < losers.length(); i++) {
				JSONObject t = losers.getJSONObject(i);
				String symbol = t.getString("symbol");
				String ltp = t.getString("ltp");
				String np = t.getString("netPrice");
				if(symbol!="null"||np!="null"||ltp!="null"){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("symbol", symbol);
					map.put("ltp", ltp);
					map.put("change", np);
					loserList.add(map);
				}
			}
			gainersb = jsonObj.getJSONArray("gainb");
			for (int i = 0; i < gainersb.length(); i++) {
				JSONObject t = gainersb.getJSONObject(i);
				String symbol = t.getString("symbol");
				String ltp = t.getString("ltp");
				String np = t.getString("netPrice");
				
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("symbol", symbol);
				map.put("ltp", ltp);
				map.put("change", np);
				gainerListB.add(map);
			}
			losersb = jsonObj.getJSONArray("loseb");
			for (int i = 0; i < losersb.length(); i++) {
				JSONObject t = losersb.getJSONObject(i);
				String symbol = t.getString("symbol");
				String ltp = t.getString("ltp");
				String np = t.getString("netPrice");
			HashMap<String, String> map = new HashMap<String, String>();
				map.put("symbol", symbol);
				map.put("ltp", ltp);
				map.put("change", np);
				loserListB.add(map);
			}
			
			nifty = jsonObj.getJSONArray("nifty");
			String n_v = nifty.getJSONObject(0).getString("price");
			String n_c = nifty.getJSONObject(0).getString("change");
            if(n_c.trim().length()==0)
                n_c="N/A";
			String n_p = nifty.getJSONObject(0).getString("perc");
			
			sensex = jsonObj.getJSONArray("sensex");
			String s_v = sensex.getJSONObject(0).getString("price");
			String s_c = sensex.getJSONObject(0).getString("change");
            if(s_c.trim().length()==0)
                s_c="N/A";
			String s_p = sensex.getJSONObject(0).getString("perc");
			
			if(!loserListB.isEmpty()){
				ListView lv = (ListView) view.findViewById(R.id.listView2);
				ListView lvB = (ListView) view.findViewById(R.id.bsegain);
				ListView lB = (ListView) view.findViewById(R.id.bselose);
				ListView l = (ListView) view.findViewById(R.id.listView1);
				
				ListAdapter adapter = new SimpleAdapter(getActivity(),
						loserList, R.layout.listl, new String[] { "symbol",
								"ltp", "change" }, new int[] { R.id.sym, R.id.ltp,
								R.id.change });
	
				lv.setAdapter(adapter);
				setListViewHeightBasedOnChildren(lv);
				
				ListAdapter ad = new SimpleAdapter(getActivity(),
						gainerList, R.layout.listg, new String[] { "symbol",
								"ltp", "change" }, new int[] { R.id.symg, R.id.ltpg,
								R.id.changeg });
				l.setAdapter(ad);
				setListViewHeightBasedOnChildren(l);
				
				//BSE
				ListAdapter adapterG = new SimpleAdapter(getActivity(),
						gainerListB, R.layout.listgb, new String[] { "symbol",
								"ltp", "change" }, new int[] { R.id.symgb, R.id.ltpgb,
								R.id.changegb });
	
				lvB.setAdapter(adapterG);
				setListViewHeightBasedOnChildren(lvB);
				ListAdapter adB = new SimpleAdapter(getActivity(),
						loserListB, R.layout.listlb, new String[] { "symbol",
								"ltp", "change" }, new int[] { R.id.symbl, R.id.ltpbl,
								R.id.changebl });
	
				lB.setAdapter(adB);
				setListViewHeightBasedOnChildren(lB);
				
				TextView c_p = (TextView) view.findViewById(R.id.cnx);
				TextView c_v = (TextView) view.findViewById(R.id.cnx_c);
				TextView sp = (TextView) view.findViewById(R.id.sen);
				TextView sv = (TextView) view.findViewById(R.id.sen_c);
				LinearLayout nl = (LinearLayout) view.findViewById(R.id.niftl);
				LinearLayout ns = (LinearLayout) view.findViewById(R.id.senl);
				c_p.setText(n_v);
				c_v.setText(n_c+" "+n_p);
                if(n_c.contains("/")){
                    c_v.setTextColor(getResources().getColor(R.color.app_gray));
                }
                else if(n_c.startsWith("-")){
					c_v.setTextColor(getResources().getColor(R.color.app_gray));
					
				}
				else{
					c_v.setTextColor(getResources().getColor(R.color.app_gray));
				}
				sp.setText(s_v);
				sv.setText(s_c+" "+s_p);
                if(s_c.contains("/")){
                    sv.setTextColor(getResources().getColor(R.color.app_gray));
                }
                else if(s_c.startsWith("-")){
					sv.setTextColor(getResources().getColor(R.color.app_gray));
				}
				else{
					sv.setTextColor(getResources().getColor(R.color.app_gray));
				}
			}
		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
		} 
		catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
	
	@Override
	public void onResume(){
		super.onResume();
		Log.d("Check", "Home Resume");
		lData();
		if(get==null){
            if(isNetworkAvailable()) {
                get = new GainLose();
                get.execute();
            }else{
                Toast.makeText(getActivity(), "Internet not available", Toast.LENGTH_LONG).show();
            }
		}
	}

	@Override
	public void onPause(){
		super.onPause();
		Log.d("Check", "Home Pause");
		if(get!=null){
			get.cancel(true);
		}
	}
	
	class GainLose extends AsyncTask<String, Void, List<String>> {
        ImageButton ref = (ImageButton) view.findViewById(R.id.ref);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			view.findViewById(R.id.homeB).setVisibility(View.VISIBLE);
            ref.setEnabled(false);
		}

		@Override
		protected List<String> doInBackground(String... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(getResources().getString(R.string.website) +
					"/gainers.php");

			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
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
				try {
						JSONObject jsonObj = new JSONObject(stringBuilder.toString());
						
						if(!pgainerList.isEmpty())
							pgainerList.clear();
						if(!pgainerListB.isEmpty())
							pgainerListB.clear();
						if(!ploserList.isEmpty())
							ploserList.clear();
						if(!ploserListB.isEmpty())
							ploserListB.clear();
						gainers = jsonObj.getJSONArray("gain");
						for (int i = 0; i < gainers.length(); i++) {
							JSONObject t = gainers.getJSONObject(i);
							String symbol = t.getString("symbol");
							String ltp = t.getString("ltp");
							String np = t.getString("netPrice");
							if(symbol!="null"||np!="null"||ltp!="null"){
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("symbol", symbol);
								map.put("ltp", ltp);
								map.put("change", np);
								pgainerList.add(map);
							}
						}
						losers = jsonObj.getJSONArray("lose");
						for (int i = 0; i < losers.length(); i++) {
							JSONObject t = losers.getJSONObject(i);
							String symbol = t.getString("symbol");
							String ltp = t.getString("ltp");
							String np = t.getString("netPrice");
							if(symbol!="null"||np!="null"||ltp!="null"){
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("symbol", symbol);
								map.put("ltp", ltp);
								map.put("change", np);
								ploserList.add(map);
							}
						}
						gainersb = jsonObj.getJSONArray("gainb");
						for (int i = 0; i < gainersb.length(); i++) {
							JSONObject t = gainersb.getJSONObject(i);
							String symbol = t.getString("symbol");
							String ltp = t.getString("ltp");
							String np = t.getString("netPrice");
							
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("symbol", symbol);
							map.put("ltp", ltp);
							map.put("change", np);
							pgainerListB.add(map);
						}
						losersb = jsonObj.getJSONArray("loseb");
						for (int i = 0; i < losersb.length(); i++) {
							JSONObject t = losersb.getJSONObject(i);
							String symbol = t.getString("symbol");
							String ltp = t.getString("ltp");
							String np = t.getString("netPrice");
						HashMap<String, String> map = new HashMap<String, String>();
							map.put("symbol", symbol);
							map.put("ltp", ltp);
							map.put("change", np);
							ploserListB.add(map);
						}
						
						nifty = jsonObj.getJSONArray("nifty");
						String n_v = nifty.getJSONObject(0).getString("price");
						String n_c = nifty.getJSONObject(0).getString("change");
                        if(n_c.trim().length()==0)
                            n_c="N/A";
						String n_p = nifty.getJSONObject(0).getString("perc");
						
						sensex = jsonObj.getJSONArray("sensex");
						String s_v = sensex.getJSONObject(0).getString("price");
						String s_c = sensex.getJSONObject(0).getString("change");
                        if(s_c.trim().length()==0)
                            s_c = "N/A";
						String s_p = sensex.getJSONObject(0).getString("perc");
						
						LIST.clear();
						LIST.add(n_v);
						LIST.add(n_c+" "+n_p);
						LIST.add(s_v);
						LIST.add(s_c+" "+s_p);
						LIST.add(stringBuilder.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (ClientProtocolException cpe) {
				LIST.add("WI");
			} catch (IOException ioe) {
				LIST.add("WI");
			}
			return LIST;

		}

		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			view.findViewById(R.id.homeB).setVisibility(View.GONE);
            ref.setEnabled(true);
			if(!isCancelled()){
				if(!result.isEmpty()){
					if(result.size()<4){
						Toast.makeText(getActivity(), "Unable to reach servers. Try again later",
								Toast.LENGTH_LONG).show();
					}
					else{
						//NSE
						gainerList = new ArrayList<HashMap<String,String>>(pgainerList);
						gainerListB = new ArrayList<HashMap<String,String>>(pgainerListB);
						loserList = new ArrayList<HashMap<String,String>>(ploserList);
						loserListB = new ArrayList<HashMap<String,String>>(ploserListB);
						write("main.json", result.get(4));
						ListView lv = (ListView) view.findViewById(R.id.listView2);
						ListView lvB = (ListView) view.findViewById(R.id.bsegain);
						ListView lB = (ListView) view.findViewById(R.id.bselose);
						ListView l = (ListView) view.findViewById(R.id.listView1);
						
						ListAdapter adapter = new SimpleAdapter(getActivity(),
								loserList, R.layout.listl, new String[] { "symbol",
										"ltp", "change" }, new int[] { R.id.sym, R.id.ltp,
										R.id.change });
			
						lv.setAdapter(adapter);
						setListViewHeightBasedOnChildren(lv);
						
						ListAdapter ad = new SimpleAdapter(getActivity(),
								gainerList, R.layout.listg, new String[] { "symbol",
										"ltp", "change" }, new int[] { R.id.symg, R.id.ltpg,
										R.id.changeg });
						l.setAdapter(ad);
						setListViewHeightBasedOnChildren(l);
						
						//BSE
						ListAdapter adapterG = new SimpleAdapter(getActivity(),
								gainerListB, R.layout.listgb, new String[] { "symbol",
										"ltp", "change" }, new int[] { R.id.symgb, R.id.ltpgb,
										R.id.changegb });
			
						lvB.setAdapter(adapterG);
						setListViewHeightBasedOnChildren(lvB);
						ListAdapter adB = new SimpleAdapter(getActivity(),
								loserListB, R.layout.listlb, new String[] { "symbol",
										"ltp", "change" }, new int[] { R.id.symbl, R.id.ltpbl,
										R.id.changebl });
			
						lB.setAdapter(adB);
						setListViewHeightBasedOnChildren(lB);
						TextView c_p = (TextView) view.findViewById(R.id.cnx);
						TextView c_v = (TextView) view.findViewById(R.id.cnx_c);
						TextView s_p = (TextView) view.findViewById(R.id.sen);
						TextView s_v = (TextView) view.findViewById(R.id.sen_c);
						c_p.setText(result.get(0));
						c_v.setText(result.get(1));
						LinearLayout nl = (LinearLayout) view.findViewById(R.id.niftl);
						LinearLayout ns = (LinearLayout) view.findViewById(R.id.senl);
                        if(result.get(1).contains("/")){
                            c_v.setTextColor(getResources().getColor(R.color.app_gray));
                        }
                        else if(result.get(1).toString().startsWith("-"))
							c_v.setTextColor(getResources().getColor(R.color.app_red));
						else
							c_v.setTextColor(getResources().getColor(R.color.graphg));
						s_p.setText(result.get(2));
						s_v.setText(result.get(3));
                        if(result.get(3).contains("/")){
                            s_v.setTextColor(getResources().getColor(R.color.app_gray));
                        }
                        else if(result.get(3).toString().startsWith("-"))
							s_v.setTextColor(getResources().getColor(R.color.app_red));
						else
							s_v.setTextColor(getResources().getColor(R.color.graphg));
					}
				}
				else{
					Toast.makeText(getActivity(), "Unable to reach servers. Try again later.",
							Toast.LENGTH_LONG).show();
				}
			}
		}

		protected void onProgressUpdate(String... progress) {
			view.findViewById(R.id.homeB).setVisibility(View.VISIBLE);

		}
	}
	
}