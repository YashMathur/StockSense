package com.infigent.stocksense;

import com.actionbarsherlock.app.ActionBar;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import adapters.BackGround;
import adapters.SuggestionsAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends BaseActivity {

	private Fragment mContent;
	ImageButton sb;
	TextView title;
	InputMethodManager ime;
	public MainActivity() {
		super(R.string.app_name);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(this, BackGround.class));
		// setSlidingActionBarEnabled(true);
        Intent n = getIntent();
        if(getIntent()!=null) {
            Bundle bundle = n.getExtras();
            if (bundle != null) {
                Log.e("ARG", "EXIST");
                if (bundle.containsKey("eq")) {
                    Log.e("EQ", "EXIST");
                    if (bundle.getBoolean("eq")) {
                        Log.e("eq", "true");
                        Bundle data = new Bundle();
                        Fragment4 eq = new Fragment4();
                        mContent = eq;
                        data.putString("q", bundle.getString("q"));
                        data.putString("type", bundle.getString("type"));
                        data.putString("name", bundle.getString("name"));
                        data.putString("f",gId());
                        eq.setArguments(data);
                        switchContent(eq);
                    }
                }
            }
        }

		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new Fragment1();

        ScrollView scrollView = new ScrollView(this);
        LinearLayout l = new LinearLayout(this);
        TextView one = new TextView(this);
        one.setText(getResources().getString(R.string.intro));
        one.setPadding(0, 0, 0, 20);
        TextView two = new TextView(this);
        two.setText("Limited License");
        two.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
        TextView three = new TextView(this);
        three.setText(getResources().getString(R.string.l1));
        TextView four = new TextView(this);
        four.setText(getResources().getString(R.string.l2));
        four.setPadding(0, 0, 0, 20);
        TextView five = new TextView(this);
        five.setText("Disclaimer");
        five.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
        TextView six = new TextView(this);
        six.setText(getResources().getString(R.string.disclaimer1));
        TextView seven = new TextView(this);
        seven.setText(getResources().getString(R.string.disclaimer2));
        TextView eight = new TextView(this);
        eight.setText(getResources().getString(R.string.disclaimer3));
        eight.setPadding(0, 0, 0, 20);
        TextView nine = new TextView(this);
        nine.setText("Liability For Our Services");
        nine.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
        TextView ten = new TextView(this);
        ten.setText(getResources().getString(R.string.liability1));
        TextView eleven = new TextView(this);
        eleven.setText(getResources().getString(R.string.liability2));
        eleven.setPadding(0, 0, 0, 20);
        TextView twelve = new TextView(this);
        twelve.setText("Maintenance And Support");
        twelve.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
        TextView thirteen = new TextView(this);
        thirteen.setText(getResources().getString(R.string.ms));
        thirteen.setPadding(0, 0, 0, 20);
        TextView fourteen = new TextView(this);
        fourteen.setText("Links To Third Party Website");
        fourteen.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
        TextView fifteen = new TextView(this);
        fifteen.setText(getResources().getString(R.string.p3));
        l.addView(one);
        l.addView(two);
        l.addView(three);
        l.addView(four);
        l.addView(five);
        l.addView(six);
        l.addView(seven);
        l.addView(eight);
        l.addView(nine);
        l.addView(ten);
        l.addView(eleven);
        l.addView(twelve);
        l.addView(thirteen);
        l.addView(fourteen);
        l.addView(fifteen);
        one.setTextColor(getResources().getColor(R.color.White));
        two.setTextColor(getResources().getColor(R.color.White));
        three.setTextColor(getResources().getColor(R.color.White));
        four.setTextColor(getResources().getColor(R.color.White));
        five.setTextColor(getResources().getColor(R.color.White));
        six.setTextColor(getResources().getColor(R.color.White));
        seven.setTextColor(getResources().getColor(R.color.White));
        eight.setTextColor(getResources().getColor(R.color.White));
        nine.setTextColor(getResources().getColor(R.color.White));
        ten.setTextColor(getResources().getColor(R.color.White));
        eleven.setTextColor(getResources().getColor(R.color.White));
        twelve.setTextColor(getResources().getColor(R.color.White));
        thirteen.setTextColor(getResources().getColor(R.color.White));
        fourteen.setTextColor(getResources().getColor(R.color.White));
        fifteen.setTextColor(getResources().getColor(R.color.White));
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(getResources().getColor(R.color.bg));
        l.setPadding(10, 10, 10, 10);
        scrollView.addView(l);
		boolean firstboot = 
				getSharedPreferences("BOOT_PREF", MODE_PRIVATE).getBoolean("firstboot", true);
	    if (firstboot){

            LayoutInflater inflater = getLayoutInflater();
            View vvv=inflater.inflate(R.layout.alerttitle, null);
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setTitle("Terms of Service")
            .setView(scrollView)
            .setCustomTitle(vvv)
            .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    getSharedPreferences("BOOT_PREF", MODE_PRIVATE)
                            .edit()
                            .putBoolean("firstboot", false)
                            .commit();
                }
            })
            .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Close the activity as they have declined the EULA
                    MainActivity.this.finish();
                }

            })
            .setCancelable(false);
	    	builder.create().show();
	    }
		ime = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		ActionBar actionBar = getSupportActionBar();
		getSupportActionBar().setCustomView(R.layout.search);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		sb = (ImageButton) actionBar.getCustomView().findViewById(R.id.sb);
		title = (TextView)actionBar.getCustomView().findViewById(R.id.title);
		sb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// only will trigger it if no physical keyboard is open
				search.setVisibility(View.VISIBLE);
				search.requestFocus();
				ime.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
				search.setSelection(search.getText().length());
				sb.setVisibility(View.GONE);
				title.setVisibility(View.GONE);
				
			}
		});
		
		search = (AutoCompleteTextView)	actionBar.getCustomView().findViewById(R.id.et);
        search.setThreshold(2);
        search.setAdapter(new SuggestionsAdapter(this, search.getText().toString()));
        search.setSelectAllOnFocus(true);
        search.clearFocus();
		
        
        search.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int pos,
					long id) {
				String term = parent.getItemAtPosition(pos).toString();
				String[] lines = term.split("\\r?\\n");
				String name = lines[0];
				String code = lines[1].substring(lines[1].indexOf(":")+1, lines[1].length()).trim();
				String type = lines[1].substring(0, lines[1].indexOf(":"));
				Log.d("DATA", name+" "+type+":"+code);
                search.setText("");
				if(isNetworkAvailable()){
					if(term.replace(" ", "")!=null){
						Bundle data = new Bundle();	
				        data.putString("q", code);
				        data.putString("type", type);
				        data.putString("name", name);
				        data.putString("f", gId());
				        Fragment4 eq = new Fragment4();
				        eq.setArguments(data);		
				        Log.d("C", gId()+" frag");
				        mContent = eq;
				        getSupportFragmentManager().beginTransaction()
							.replace(R.id.content_frame, eq).commit();
				        getSlidingMenu().showContent();
				        search.setVisibility(View.GONE);
						sb.setVisibility(View.VISIBLE);
						title.setVisibility(View.VISIBLE);
						ime.hideSoftInputFromWindow(search.getApplicationWindowToken(), 0);
					}
					else{
						Toast.makeText(getApplicationContext(), "Enter a search term!", 
								Toast.LENGTH_LONG).show();
					}
				}
				else
					Toast.makeText(getApplicationContext(), "Internet not available", 
							Toast.LENGTH_LONG).show();
			}
		});
        
        search.setOnEditorActionListener(new OnEditorActionListener() {
        	@Override
        	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		        return false;
        	}
        });
		
		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();
        AdView mAdView;
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SampleListFragment()).commit();
	}

    public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment, fragment.getClass().toString()).commit();
		getSlidingMenu().showContent();
	}
	
	public String gId(){
		
		String c = mContent.getClass().toString();
		if(c.endsWith("1")){
			Log.d("C1", c);
			return "1";
		}
		else if(c.endsWith("2")){
			Log.d("C2", c);
			return "2";
		}
		else if(c.endsWith("3")){
			Log.d("C3", c);
			return "3";
		}
        else if(c.equals("PortView")){
            return "PortView";
        }
		else{
			Log.d("C", c);
			return "0";
		}
	}
	
	@Override
	public void onBackPressed() {
		
		Log.d("Back", "Pressed");
		String c = mContent.getClass().toString();
		String r =Fragment4.class.toString();
		Log.d("Current", c);
		Log.d("F4", r);
		if(search.getVisibility()==View.VISIBLE){
			
			search.setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			sb.setVisibility(View.VISIBLE);
			ime.hideSoftInputFromWindow(search.getApplicationWindowToken(), 0);
			return;
		}else if(r.equals(c)){
			
			String from = Fragment4.getFrom();
			Log.d("From", from+"st Act");

            if(from.equals("1"))
                switchContent(new Fragment1());
            else if(from.equals("2"))
                switchContent(new Fragment2());
            else if(from.equals("3"))
                switchContent(new Fragment3());
            else if(from.equals("6"))
                switchContent(new Fragment6());
            else if(from.equals("PortView"))
                switchContent(new PortView());
            /*switch(from){
			case "1":
				switchContent(new Fragment1());
				break;
			case "2":
				switchContent(new Fragment2());
				break;
			case "3":
				switchContent(new Fragment3());
				break;
			case "0":
				break;
            case "PortView":
                switchContent(new PortView());
                break;
			default:
				break;
			}*/
			
		}else if(c.equals(PortView.class.toString())){

            switchContent(new Fragment6());

        }else if(c.equals(Add.class.toString())){
            Add a = new Add();
            Bundle b = new Bundle();
            b.putString("id", a.gID());
            b.putString("name", a.gName());
            a.setArguments(b);
            switchContent(new Fragment6());
        }
        else{
			MainActivity.this.finish();
		}

			
		
	}
	
}
