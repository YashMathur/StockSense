package com.infigent.stocksense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapters.DatabaseHandler;
import adapters.alerts;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Fragment5 extends Fragment{
	final int CONTEXT_MENU_EDIT =1;
	final int CONTEXT_MENU_DELETE =2;
	ListView lv;
	SimpleAdapter simple;
	String idT;
	ImageView sho;
	TextView tv;
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.ed, null);
        tv = (TextView) view.findViewById(R.id.alcheck);
		lv = (ListView) view.findViewById(R.id.edl);
		sho = (ImageView) view.findViewById(R.id.alt);
    	registerForContextMenu(lv);
		return view;
	}

    public void callIt(){
        /*handler = new Handler();
        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {*/
                        try {
                            if(lData())
                                lv.setAdapter(simple);
                            else{
                                lv.setAdapter(simple);
                                tv.setVisibility(View.VISIBLE);
                                sho.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            Log.d("doingEQ", "NO!! " +e);
                        }
                    /*}
                });
            }
        };
        timer = new Timer();
        timer.schedule(doAsynchronousTask, 0, 3000);*/
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,
                                    View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuInfo;
        LinearLayout s = ((LinearLayout) info.targetView);
        TextView id = (TextView) s.findViewById(R.id.edID);
        TextView ch = (TextView) s.findViewById(R.id.edS);
        //selectedWordId = info.id;
        idT = id.getText().toString();
        if(ch.getText().toString().startsWith("P"))
        	menu.add(Menu.NONE, CONTEXT_MENU_EDIT, Menu.NONE, "Edit");
        
        menu.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE, "Delete");
                
        OnMenuItemClickListener listener = new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onContextItemSelected(item);
                return true;
            }
        };

        for (int i = 0, n = menu.size(); i < n; i++)
            menu.getItem(i).setOnMenuItemClickListener(listener);
        
        Log.d("Selected", "HERE: "+idT);
    }
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
		Log.d("Clicked", "Menu");
		switch(item.getItemId()){
		case CONTEXT_MENU_EDIT:
            final Dialog d = new Dialog(getActivity());
            d.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
            d.show();
            d.setTitle("Edit Alert");

            final InputMethodManager ime = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            d.setContentView(R.layout.edal);
            final EditText editT = (EditText) d.findViewById(R.id.editVal);
            ime.showSoftInput(editT, InputMethodManager.SHOW_IMPLICIT);
            Button editB = (Button) d.findViewById(R.id.editB);
            editB.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if(editT.getText().length()>0){
                        try{
                            RadioGroup rg = (RadioGroup) d.findViewById(R.id.rge);
                            int sId = rg.getCheckedRadioButtonId();
                            RadioButton rb = (RadioButton) d.findViewById(sId);
                            DatabaseHandler db = new DatabaseHandler(getActivity());
                            alerts alert = db.getContact(Integer.parseInt(idT));
                            alert.setTar(editT.getText().toString());
                            alert.setTodo(rb.getText().toString());
                            db.updateContact(alert);
                            ime.hideSoftInputFromWindow(editT.getWindowToken(), 0);
                            d.dismiss();

                        }catch(Exception e){
                                Log.d("Error 5", e.toString());
                        }
                    }
                    if(lData()){
                        lv.setAdapter(simple);
                    }
                    else{
                        lv.setAdapter(simple);
                        tv.setVisibility(View.VISIBLE);
                        sho.setVisibility(View.VISIBLE);
                    }
                }
            });
            break;
		case CONTEXT_MENU_DELETE:
            	AlertDialog.Builder alert = new AlertDialog.Builder(
				        getActivity());

				alert.setTitle("Delete");
				alert.setMessage("Do you want delete this item?");
				alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				            
						deleteAlert(idT);

				    }
				});
				alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        // TODO Auto-generated method stub
				        dialog.dismiss();
				        lData();
				    }
				});

				alert.show();
                break;
        }
		return super.onContextItemSelected(item);
        
    }

    @Override
    public void onResume(){
        super.onResume();
        callIt();
    }

    @Override
    public void onPause(){
        super.onPause();
    }
	
	public boolean lData(){

        SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object object, String value) {

                View p = (View)view.getParent();
                TextView bk = (TextView) p.findViewById(R.id.edS);
                if (view.equals(view.findViewById(R.id.edS))){
                    if(value.startsWith("P")){
                        bk.setTextColor(getResources().getColor(R.color.app_red));
                    }
                    else{
                        bk.setTextColor(getResources().getColor(R.color.graphg));
                    }
                }

                return false;
            }
        };
		DatabaseHandler db = new DatabaseHandler(getActivity());
		ArrayList<HashMap<String, String>> ll = new ArrayList<HashMap<String,String>>();
		if(db.getContactsCount()>0){
			List<alerts> alert = db.getAllContacts();
			for(alerts a:alert){
				
				boolean sid = true;
				String disp="";
				HashMap<String, String> m = new HashMap<String, String>();
				m.put("id", ""+a.getID());
				m.put("name", a.getName());
				m.put("ct", a.getType()+":"+a.getCode());
				m.put("todo", a.getTodo()+" Rs."+a.getTar());
				if(a.getStat().contains("N")){
					sid=true;
					disp="Pending";
				}else if(a.getStat().contains("Y")){
					sid=true;
					disp="Reached";
				}else{
					sid=false;
				}
				m.put("s", disp);
				if(sid)
					ll.add(m);
			}

            //simple.setViewBinder(binder);
			if(ll.size()>0){
				simple = new SimpleAdapter(getActivity().getApplicationContext(), 
						ll, R.layout.edlv, new String[]{"name", "todo", "s", "id", "ct"},
						new int[]{R.id.edN, R.id.edT, R.id.edS, R.id.edID, R.id.edCT});
                simple.setViewBinder(binder);
				return true;
			}
			else{
				simple = new SimpleAdapter(getActivity().getApplicationContext(), 
						ll, R.layout.edlv, new String[]{"name", "todo", "s", "id", "ct"},
						new int[]{R.id.edN, R.id.edT, R.id.edS, R.id.edID, R.id.edCT});
                simple.setViewBinder(binder);
				return false;
			
				}
		}else{
			simple = new SimpleAdapter(getActivity().getApplicationContext(), 
					ll, R.layout.edlv, new String[]{"name", "todo", "s", "id", "ct"},
					new int[]{R.id.edN, R.id.edT, R.id.edS, R.id.edID, R.id.edCT});
            simple.setViewBinder(binder);
			return false;
		}
		
	}

	public void deleteAlert(String id) {
		DatabaseHandler db = new DatabaseHandler(getActivity());
		alerts alert = db.getContact(Integer.parseInt(id));
		db.deleteContact(alert);
		if(lData()){
			lv.setAdapter(simple);
		}
		else{
			lv.setAdapter(simple);
			tv.setVisibility(View.VISIBLE);
			sho.setVisibility(View.VISIBLE);
		}
	}
	
	
}
