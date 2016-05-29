package com.infigent.stocksense;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class BaseActivity extends SlidingFragmentActivity {
    AutoCompleteTextView search;

	private int mTitleRes;
	protected ListFragment mFrag;

	public BaseActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(mTitleRes);
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager()
					.beginTransaction();
			mFrag = new SampleListFragment();
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
		} else {
			mFrag = (ListFragment) this.getSupportFragmentManager()
					.findFragmentById(R.id.menu_frame);
		}

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setBehindOffset(165);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        
	}
	
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Dialog d = new Dialog(this);
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
	    case R.id.abtb:
	    	d.setTitle("About");
	    	d.setContentView(R.layout.about);
	    	d.setCancelable(true);
	    	d.show();
	        return true;
	        
	    case R.id.helpb:
	    	d.setTitle("Support");
	    	d.setContentView(R.layout.support);
	    	d.setCancelable(true);
	    	d.show();
	    	return true;
	    	
	    case R.id.tos:
	    	d.setTitle("Terms of Service");
	    	d.setContentView(R.layout.help);
	    	d.setCancelable(true);
	    	d.show();
	    	return true;
	    
	    case R.id.edal:
	    	d.setTitle("Alerts");
	    	d.setContentView(R.layout.ed);
	    	try{
	    		
		    	File folder = new File(Environment.getExternalStorageDirectory() + 
						"/StockSense/");
				File jsonFile = new File(folder, "alerts.json");
				StringBuilder text = new StringBuilder(); 
				BufferedReader br = new BufferedReader(new FileReader(jsonFile));  
				String line;  
	
				while ((line = br.readLine()) != null) {  
					text.append(line);  
				}  
				Log.d("TEXT=", text.toString());
				JSONObject jsonObjMain = new JSONObject(text.toString()); //Your existing object
				JSONArray jsonArray = jsonObjMain.getJSONArray("alerts");
				if(jsonArray.length()>0){
					ArrayList<HashMap<String, String>> l = new ArrayList<HashMap<String,String>>();
					for(int i = 0; i < jsonArray.length(); i++){
						HashMap<String, String> m = new HashMap<String, String>();
						JSONObject r = jsonArray.getJSONObject(i);
						String code = r.getString("code");
						String type = r.getString("type");
						String num = r.getString("val");
						String nameOfStock = r.getString("name");
						String f = r.getString("todo");
						m.put("name", nameOfStock);
						m.put("todo", f+" "+num);
						l.add(m);
					}
					ListView lv = (ListView) d.findViewById(R.id.edl);
					lv.setAdapter(new SimpleAdapter(getApplicationContext(), 
							l, R.layout.edlv, new String[]{"name", "todo"}, 
							new int[]{R.id.edN, R.id.edT}));
				}
				else{
					TextView tv = (TextView) d.findViewById(R.id.alcheck);
					tv.setVisibility(View.VISIBLE);
				}
	    	}catch(Exception e){
	    		
	    	}
	    	d.setCancelable(true);
	    	d.show();
	    	return true;
	    	
	    }
		return super.onOptionsItemSelected(item);
	}

}