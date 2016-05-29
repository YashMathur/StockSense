package com.infigent.stocksense;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import adapters.DatabaseHandler;
import adapters.alerts;
import adapters.port;


public class Fragment6 extends Fragment {
    DatabaseHandler pMan;
    View view;
    ArrayList<HashMap<String, String>> listAdd;
    final int CONTEXT_MENU_EDIT =1;
    final int CONTEXT_MENU_DELETE =2;
    String idT;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.portmain, null);
        loadFolders("Loading");
        pMan = new DatabaseHandler(getActivity());
        if(pMan.getFolders()<1){
            view.findViewById(R.id.portfolioNone).setVisibility(View.VISIBLE);
            view.findViewById(R.id.portfolioCheck).setVisibility(View.VISIBLE);
        }else{
            view.findViewById(R.id.portfolioNone).setVisibility(View.GONE);
            view.findViewById(R.id.portfolioCheck).setVisibility(View.GONE);
        }
        ListView lv = (ListView)view.findViewById(R.id.port0);
        registerForContextMenu(lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                if(isNetworkAvailable()) {
                    TextView idView = (TextView) v.findViewById(R.id.folderID);
                    int id = Integer.parseInt(idView.getText().toString());

                    TextView nView = (TextView) v.findViewById(R.id.folderName);
                    String name = nView.getText().toString();

                    Bundle data = new Bundle();
                    data.putString("name", name);
                    data.putInt("id", id);
                    android.support.v4.app.FragmentTransaction transaction =
                            getActivity().getSupportFragmentManager().beginTransaction();
                    PortView pv = new PortView();
                    pv.setArguments(data);
                    switchFragment(pv);
                }else{
                    Toast.makeText(getActivity(), "Internet not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageButton add = (ImageButton) view.findViewById(R.id.addfolder);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View viewButton) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter Portfolio's Title");

                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder.setView(input);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHandler db = new DatabaseHandler(getActivity());
                        db.addFolder(input.getText().toString());
                        loadFolders("Loading");
                        pMan = new DatabaseHandler(getActivity());
                        if(pMan.getFolders()<1){
                            view.findViewById(R.id.portfolioNone).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.portfolioCheck).setVisibility(View.VISIBLE);
                        }else{
                            view.findViewById(R.id.portfolioNone).setVisibility(View.GONE);
                            view.findViewById(R.id.portfolioCheck).setVisibility(View.GONE);
                        }
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

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,
                                    View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuInfo;
        LinearLayout s = ((LinearLayout) info.targetView);
        TextView id = (TextView) s.findViewById(R.id.folderID);
        TextView ch = (TextView) s.findViewById(R.id.folderName);

        idT = id.getText().toString();
        //menu.add(Menu.NONE, CONTEXT_MENU_EDIT, Menu.NONE, "Edit");
        menu.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE, "Delete");

        MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d("Clicked", "Menu");
        switch(item.getItemId()){
            case CONTEXT_MENU_EDIT:
                break;
            case CONTEXT_MENU_DELETE:
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getActivity());

                alert.setTitle("Delete");
                alert.setMessage("Do you want delete this item?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHandler db = new DatabaseHandler(getActivity());
                        db.deleteFolder(idT);
                        loadFolders("Loading");

                        pMan = new DatabaseHandler(getActivity());
                        if(pMan.getFolders()<1){
                            view.findViewById(R.id.portfolioNone).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.portfolioCheck).setVisibility(View.VISIBLE);
                        }else{
                            view.findViewById(R.id.portfolioNone).setVisibility(View.GONE);
                            view.findViewById(R.id.portfolioCheck).setVisibility(View.GONE);
                        }
                    }
                });
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
                break;
        }
        return super.onContextItemSelected(item);

    }

    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof MainActivity) {
            MainActivity fca = (MainActivity) getActivity();
            fca.switchContent(fragment);
        }

    }

    public void loadFolders(String str){

        pMan = new DatabaseHandler(getActivity());
        List<HashMap<String, String>> folders = pMan.getAllFolders(), nameList = new ArrayList<HashMap<String, String>>();
        ArrayList<List<port>> mList = new ArrayList<List<port>>();

        if(mList != null)
            mList.clear();
        if(nameList != null)
            nameList.clear();
        for(int i=0;i<folders.size();++i){
            HashMap<String, String> map = new HashMap<String, String>();
            int id = Integer.parseInt(folders.get(i).get("id"));
            Log.d("ID",  id+"");
            List<port> p = pMan.getAllPort(id);
            map.put("id", folders.get(i).get("id"));
            map.put("name", folders.get(i).get("name"));
            nameList.add(map);
            mList.add(p);
        }

        ListView lv = (ListView)view.findViewById(R.id.port0);
        SimpleAdapter ad = new SimpleAdapter(getActivity(), folders, R.layout.portfoliofolder,
                new String[]{"id", "name"}, new int[]{R.id.folderID, R.id.folderName});
        lv.setAdapter(ad);
        if(isNetworkAvailable()) {
            setup s = new setup();
            s.execute(folders, mList, nameList);
        }else{
            loadDash("-");
            Toast.makeText(getActivity(), "Internet not available", Toast.LENGTH_SHORT).show();
        }

    }

    public void loadDash(String str){

        pMan = new DatabaseHandler(getActivity());
        List<HashMap<String, String>> folders = pMan.getAllFolders(), nameList = new ArrayList<HashMap<String, String>>();
        ArrayList<List<port>> mList = new ArrayList<List<port>>();

        if(mList != null)
            mList.clear();
        if(nameList != null)
            nameList.clear();
        for(int i=0;i<folders.size();++i){
            HashMap<String, String> map = new HashMap<String, String>();
            int id = Integer.parseInt(folders.get(i).get("id"));
            Log.d("ID",  id+"");
            List<port> p = pMan.getAllPort(id);
            map.put("id", folders.get(i).get("id"));
            map.put("name", folders.get(i).get("name"));
            map.put("market", str);
            map.put("pl", "-");
            map.put("plp", "-");
            nameList.add(map);
            mList.add(p);
        }

        ListView lv = (ListView)view.findViewById(R.id.port0);
        SimpleAdapter ad = new SimpleAdapter(getActivity(), folders, R.layout.portfoliofolder,
                new String[]{"id", "name", "market", "pl", "plp"}, new int[]{R.id.folderID, R.id.folderName,
                R.id.folderVal, R.id.gl, R.id.gp});
        lv.setAdapter(ad);

    }

    public class setup extends AsyncTask<Object, Integer, String> {
        ArrayList<HashMap<String, String>> pAdd = new ArrayList<HashMap<String, String>>();
        ProgressBar prog = (ProgressBar) view.findViewById(R.id.portfolioProg);
        @Override
        protected String doInBackground(Object... objects) {

            if(!pAdd.isEmpty())
                pAdd.clear();

            try {
                List<HashMap<String, String>> f = (List<HashMap<String, String>>) objects[0];
                ArrayList<List<port>> p = (ArrayList<List<port>>) objects[1];
                ArrayList<HashMap<String, String>> nameList = (ArrayList<HashMap<String, String>>) objects[2];
                JSONObject jObj = new JSONObject();
                JSONArray arr;

                for(int i=0;i<p.size();++i){
                    HashMap<String, String> map = new HashMap<String, String>();
                    arr = new JSONArray();
                    List<port> listP = p.get(i);

                    String fName = nameList.get(i).get("name");
                    String fId = nameList.get(i).get("id");

                    for(int j=0;j<listP.size();++j){

                        port po = listP.get(j);
                        JSONObject jArr = new JSONObject();
                        jArr.put("name", po.getName());
                        jArr.put("code", po.getCode());
                        jArr.put("type", po.getType());
                        arr.put(jArr);

                    }
                    jObj.put("portfolio", arr);
                    if(listP.size()>0) {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(getResources().getString(R.string.website) +
                                "/port.php?j=" + URLEncoder.encode(jObj.toString(), "UTF-8"));
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

                        double mktVal = 0, gl = 0, pl = 0, orig = 0;

                        for (int j = 0; j < listP.size(); ++j) {
                            port po = listP.get(j);
                            JSONObject JO = jsonObj.getJSONObject(po.getCode() + "_" + po.getType());
                            NumberFormat format = NumberFormat.getInstance(Locale.US);
                            Number number = format.parse(JO.getString("2"));
                            Double cVal = number.doubleValue();
                            int shares = po.getQty();
                            double com = po.getCom();
                            double pBought = po.getPrice();
                            if(po.getBS().startsWith("B")) {
                                gl += (cVal - pBought)*shares - com;
                                mktVal += cVal * shares;
                            }
                            else {
                                gl += (cVal - pBought)*-shares - com;
                                mktVal += cVal * -shares;
                            }
                            orig += shares * po.getPrice();

                        }

                        Log.d("DATA: ", "CUR = "+mktVal+", ORIGINAL = "+orig);
                        DecimalFormat nf = new DecimalFormat("#.00");
                        pl = (gl/orig)*100;
                        map.put("val", nf.format(mktVal));
                        map.put("gl", nf.format(gl));
                        map.put("perc", nf.format(pl) + "%");
                        map.put("id", fId);
                        map.put("name", fName);
                        pAdd.add(map);

                    }
                    else{
                        map.put("val", "N/A");
                        map.put("gl", "N/A");
                        map.put("perc", "N/A");
                        map.put("id", fId);
                        map.put("name", fName);
                        pAdd.add(map);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            if (isCancelled() || !isAdded()) {

            } else {
                listAdd = pAdd;

                ListView lv = (ListView)view.findViewById(R.id.port0);
                SimpleAdapter ad = new SimpleAdapter(getActivity(), listAdd, R.layout.portfoliofolder,
                        new String[]{"id", "name", "val", "gl", "perc"},
                        new int[]{R.id.folderID, R.id.folderName, R.id.folderVal, R.id.gl, R.id.gp});

                SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Object object, String value) {

                        View p = (View)view.getParent();
                        TextView bk = (TextView) p.findViewById(R.id.gl);
                        TextView bm = (TextView) view.findViewById(R.id.gp);
                        if (view.equals((TextView)view.findViewById(R.id.gp))){
                            if(value.contains("-")){
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
                ad.setViewBinder(binder);
                lv.setAdapter(ad);
                prog.setVisibility(View.INVISIBLE);

            }
        }
    }
}