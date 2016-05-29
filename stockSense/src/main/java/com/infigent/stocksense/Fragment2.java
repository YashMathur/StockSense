package com.infigent.stocksense;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import adapters.XMLParser;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Fragment2 extends Fragment {

	List<HashMap<String, String>> dat;
	ListView lv;
	String[] from = {"title", "pubDate", "description"};
	int[] to = {R.id.news_title, R.id.pdate, R.id.descn};
	getNews news;
	ListAdapter adapter;
	ArrayList<HashMap<String, String>> nList;
	ArrayList<HashMap<String, String>> p = 
    		new ArrayList<HashMap<String, String>>(); 
	static final String URL = "http://www.indiainfoline.com/rss/latestnews.xml";
	// XML node keys
	static final String KEY_ITEM = "item"; // parent node
	static final String KEY_NAME = "title";
	static final String KEY_DATE = "pubDate";
	static final String KEY_DESC = "description";
	ProgressBar nprog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news, null);
		final Dialog d;
    	/*d= new Dialog(getActivity());
    	d.setContentView(R.layout.newsview);
        final TextView nTitle = (TextView) d.findViewById(R.id.nTitle);
    	final TextView nTime = (TextView) d.findViewById(R.id.nTime);
    	final TextView nBody = (TextView) d.findViewById(R.id.nBody);*/
		dat = new ArrayList<HashMap<String, String>>();
        nList = new ArrayList<HashMap<String, String>>();
    	nprog = (ProgressBar) view.findViewById(R.id.nProg);
    	lv = (ListView) view.findViewById(R.id.lv1);
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
			
				TextView u = (TextView) v.findViewById(R.id.descn);
				String url = u.getText().toString();
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
						Uri.parse(url.trim()));
				startActivity(browserIntent);
				/*TextView t1 = (TextView) v.findViewById(R.id.news_title);
				TextView t2 = (TextView) v.findViewById(R.id.nd);
				TextView t3 = (TextView) v.findViewById(R.id.pdate);
				nTitle.setText(t1.getText().toString());
				nTime.setText(t3.getText().toString());
				nBody.setText(t2.getText().toString());
				d.show();*/
				
			}
		});

		return view;
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
	
	public void lData(){
		
		try {
			File folder = new File(Environment.getExternalStorageDirectory() + "/StockSense/");
			File jsonFile = new File(folder, "news.json");
			StringBuilder text = new StringBuilder(); 
			BufferedReader br = new BufferedReader(new FileReader(jsonFile));  
			String line;  

			while ((line = br.readLine()) != null) {  
				text.append(line);  
			}  
		try{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(text.toString().trim()));
			Document doc = builder.parse(is); // get DOM elem.
		 
		    NodeList nl = doc.getElementsByTagName("item");
		    //loop
		    int len=nl.getLength();
		    if(nl.getLength()>30){
		    	len = 30;
		    }
		    
		    for (int i=0; i< len; i++){
		        HashMap<String, String> map = new HashMap<String, String>();
		        Element e = (Element) nl.item(i);
		        //add to map

		        NodeList title = e.getElementsByTagName("title");
	            Element tline = (Element) title.item(0);
	            NodeList dn = e.getElementsByTagName("link");
	            Element dline = (Element) dn.item(0);
	            NodeList da = e.getElementsByTagName("pubDate");
	            Element daline = (Element) da.item(0);
		        map.put(KEY_NAME, getD(tline));
		        map.put(KEY_DATE, getD(daline).trim());
		        map.put(KEY_DESC, getD(dline));
		        // hash => list
		        nList.add(map);
		    }
		    if(!nList.isEmpty()){
				adapter = new SimpleAdapter(getActivity(), nList, R.layout.news_list, from, to);
				lv.setAdapter(adapter);
				Log.d("lData", "lData");
			}
			
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
		}  catch (IOException e) {
			e.printStackTrace();
		} 
	
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.d("Check", "News Resume");
		lData();
		if(news==null){
            if(isNetworkAvailable()) {
                news = new getNews();
                news.execute();
            }else{
                Toast.makeText(getActivity(), "Unable to reach servers. Try again later", Toast.LENGTH_LONG).show();
            }
		}
	}

	@Override
	public void onPause(){
		super.onPause();
		Log.d("Check", "News Pause");
		if(news!=null){
			news.cancel(true);
		}
	}
	
	public static String getD(Element f) {

        NodeList list = f.getChildNodes();
        String data;

        for(int index = 0; index < list.getLength(); index++){
            if(list.item(index) instanceof CharacterData){
                CharacterData child  = (CharacterData) list.item(index);
                data = child.getData();

                if(data != null && data.trim().length() > 0)
                   return child.getData();
            }
        }
        return "";
}
	
	class getNews extends AsyncTask<Void, Void, String>{
		

		@Override
		protected String doInBackground(Void... params) {
			String xml = null;
		    try {
		    	XMLParser parser = new XMLParser();
			    xml = parser.getXmlFromUrl(URL); //get XML
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource is = new InputSource(new StringReader(xml.trim()));
				Document doc = builder.parse(is); // get DOM elem.
			 
			    NodeList nl = doc.getElementsByTagName("item");
			    //loop
			    int len=nl.getLength();
			    if(nl.getLength()>30){
			    	len = 30;
			    }
			    
			    if(!p.isEmpty())
					p.clear();
			    for (int i=0; i< len; i++){
			        HashMap<String, String> map = new HashMap<String, String>();
			        Element e = (Element) nl.item(i);
			        //add to map

			        NodeList title = e.getElementsByTagName("title");
		            Element tline = (Element) title.item(0);
		            NodeList dn = e.getElementsByTagName("link");
		            Element dline = (Element) dn.item(0);
		            NodeList da = e.getElementsByTagName("pubDate");
		            Element daline = (Element) da.item(0);
			        map.put(KEY_NAME, getD(tline));
			        map.put(KEY_DATE, getD(daline).trim());
			        map.put(KEY_DESC, getD(dline));
			 
			        // hash => list
			        p.add(map);
			    }
				
				
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    
			
			return xml;
			
		}
		
		@Override
		protected void onPostExecute(String xml) {
			
			super.onPostExecute(xml);
			nprog.setVisibility(View.GONE);
			if(xml.equals("WI")){
				Toast.makeText(getActivity().getApplicationContext(), 
						"Weak Internet. Try again later", Toast.LENGTH_LONG).show();
			}
			else{
				nList = new ArrayList<HashMap<String,String>>(p);
				adapter = new SimpleAdapter(getActivity(), nList, R.layout.news_list, from, to);
				lv.setAdapter(adapter);
				write("news.json", xml);
				Log.d("News", "loaded");
			}
		}
		
		@Override
		protected void onPreExecute(){
			Log.d("starting", "y");
			
			nprog.setVisibility(View.VISIBLE);
		}
		
	}

}