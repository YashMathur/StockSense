package com.infigent.stocksense;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SampleListFragment extends ListFragment {
	public String[] menus = { "Markets", "News", "Watchlist", "Portfolio", "Manage Alerts",
			"Terms of Service", "About"};
	ListView lv;
	SimpleAdapter simple;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		SampleAdapter adapter = new SampleAdapter(getActivity());

		
		adapter.add(new SampleItem(menus[0], R.drawable.market));
		adapter.add(new SampleItem(menus[1], R.drawable.news));
        adapter.add(new SampleItem(menus[2], R.drawable.ic_action_not_important));
        adapter.add(new SampleItem(menus[3], R.drawable.portfolio));
		adapter.add(new SampleItem(menus[4], R.drawable.alerts));
		adapter.add(new SampleItem(menus[5], R.drawable.tos));
		adapter.add(new SampleItem(menus[6], R.drawable.about));
		
		
		setListAdapter(adapter);

	}
	
	public void lvh(ListView listView) {
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
	public void onListItemClick(ListView l, View v, int position, long id) {

		Fragment newContent = null;
		final Dialog d = new Dialog(getActivity());
        d.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
		switch (position) {
		case 0:
			newContent = new Fragment1();
			break;
		case 1:
			newContent = new Fragment2();
			break;
		case 2:
			newContent = new Fragment3();
			break;
        case 3:
            newContent = new Fragment6();
            break;
		case 4:
			newContent = new Fragment5();
			break;
		case 5:
			d.setTitle("Terms of Service");
	    	d.setContentView(R.layout.help);
	    	d.setCancelable(true);
	    	d.show();
	    	break;
		case 6:
			d.setTitle("About");
	    	d.setContentView(R.layout.about);
	    	d.setCancelable(true);
	    	d.show();
	    	break;
		}
		if (newContent != null)
			switchFragment(newContent);
	}

	private class SampleItem {
		public String tag;
		public int iconRes;

		public SampleItem(String tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
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
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo me){
		if (v.getId() == R.id.edl) {
		    ListView lv = (ListView) v;
		    AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) me;
	
		    menu.add("Edit");
		    menu.add("Delete");
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
	            .getMenuInfo();
	    int menuItemIndex = item.getItemId();
	    // check for selected option
	    if (menuItemIndex == 0) {
	    	
	    }else{
	    	
	    }

	    return true;
	}
	
	public boolean lData(){
		
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
			JSONObject jsonObjMain = new JSONObject(text.toString()); //Your existing object
			JSONArray jsonArray = jsonObjMain.getJSONArray("alerts");
			ArrayList<HashMap<String, String>> ll = new ArrayList<HashMap<String,String>>();
			if(jsonArray.length()>0){
				for(int i = 0; i < jsonArray.length(); i++){
					HashMap<String, String> m = new HashMap<String, String>();
					boolean sid = true;
					JSONObject r = jsonArray.getJSONObject(i);
					String num = r.getString("val");
					String nameOfStock = r.getString("name");
					String f = r.getString("todo");
					m.put("name", nameOfStock);
					m.put("todo", f+" Rs."+num);
					String disp = null;
					if(r.getString("c").contains("N")){
						 disp = "Pending";
					}else if(r.getString("c").contains("Y")){
						 disp = "Reached";
					}else{
						 disp = "Deleted";
						 sid=false;
					}
					m.put("s", disp);
					m.put("id", r.getString("id"));
					if(sid)
						ll.add(m);
				}
				if(ll.size()>0){
					Log.d("List ll: "+jsonArray.length(), ll.toString());
					simple = new SimpleAdapter(getActivity().getApplicationContext(), 
							ll, R.layout.edlv, new String[]{"name", "todo", "s", "id"}, 
							new int[]{R.id.edN, R.id.edT, R.id.edS, R.id.edID});
						return true;
					}else{
						Log.d("List ll: ", ll.toString());
						simple = new SimpleAdapter(getActivity().getApplicationContext(), 
								ll, R.layout.edlv, new String[]{"name", "todo", "s", "id"}, 
								new int[]{R.id.edN, R.id.edT, R.id.edS, R.id.edID});
						return false;
				}
			}
			else{
				Log.d("List ll: ", ll.toString());
				simple = new SimpleAdapter(getActivity().getApplicationContext(), 
						ll, R.layout.edlv, new String[]{"name", "todo", "s", "id"}, 
						new int[]{R.id.edN, R.id.edT, R.id.edS, R.id.edID});
				return false;
			}
    	}catch(Exception e){
    		
    	}
		return false;
		
	}

	public void deleteAlert(String id, Dialog d) throws JSONException, 
															IOException{
		
		File folder = new File(Environment.getExternalStorageDirectory() + "/StockSense");
		File jsonFile = new File(folder, "alerts.json");
		StringBuilder text = new StringBuilder(); 
		BufferedReader br = new BufferedReader(new FileReader(jsonFile));  
        String line;  

        while ((line = br.readLine()) != null) {  
            text.append(line);  
        }  
        JSONObject jsonObjMain = new JSONObject(text.toString());
        JSONArray jsonArray = jsonObjMain.getJSONArray("alerts");
        JSONArray list = new JSONArray(); 
        for(int i=0;i<jsonArray.length();++i){
        	
        	JSONObject r = jsonArray.getJSONObject(i);
        	Log.d("COMPARE", r.getString("id")+" : "+id);
        	if(r.getString("id").equals(id)){
				r.remove("c");
				r.put("c", "Deleted");
        	}
        	list.put(r);
        	
        }
        JSONObject jo = new JSONObject();
		jo.put("alerts", list);
		FileOutputStream  fos = new FileOutputStream(jsonFile);
        fos.write(jo.toString().getBytes());
        fos.close();
        Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_LONG).show();
        if(lData()){
    		lv.setAdapter(simple);
    	}else{
    		lv.setAdapter(simple);
			TextView tv = (TextView) d.findViewById(R.id.alcheck);
			tv.setVisibility(View.VISIBLE);
    	}
		
	}
	
	
	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(final int position, View view, ViewGroup parent) {
			if (view == null) {
				view = LayoutInflater.from(getContext()).inflate(R.layout.row,
						null);
			}
			ImageView icon = (ImageView) view.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			final TextView title = (TextView) view.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			return view;
		}
	}
}