package com.infigent.stocksense;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import adapters.DatabaseHandler;
import adapters.port;

public class PortView extends Fragment {
    View view;
    int PORTFOLIO_ID;
    final int CONTEXT_MENU_EDIT =1;
    final int CONTEXT_MENU_DELETE =2;
    String DELETE_CODE, DELETE_TYPE;
    ImageButton refresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.portfolio_view, null);
        Bundle extras = getArguments();
        final int id = extras.getInt("id");
        PORTFOLIO_ID = id;
        final String name = extras.getString("name");
        refresh = (ImageButton) view.findViewById(R.id.refresh);
        ImageButton add = (ImageButton) view.findViewById(R.id.addfromport);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Add a = new Add();
                Bundle ex = new Bundle();
                ex.putString("id", id+"");
                ex.putString("name", name);
                a.setArguments(ex);
                switchFragment(a);
            }
        });

        final ListView lv = (ListView) view.findViewById(R.id.portfolio_view_list);
        registerForContextMenu(lv);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setup s = new setup();
                s.execute(id);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {

                TextView cd = (TextView) v.findViewById(R.id.stock_code);
                String type = cd.getText().toString().substring(0, cd.getText().toString().indexOf(":"));
                String code = cd.getText().toString().substring(cd.getText().toString().indexOf(":")+1,
                                                        cd.getText().toString().length());
                DatabaseHandler db = new DatabaseHandler(getActivity());
                List<port> trans = db.getAllTrans(PORTFOLIO_ID, code, type);
                ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                for(int j=0;j<trans.size();++j){
                    HashMap<String, String> map = new HashMap<String, String>();
                    port p = trans.get(j);
                    map.put("q", String.valueOf(p.getQty()));
                    map.put("p", String.valueOf(p.getPrice()));
                    map.put("c", String.valueOf(p.getCom()));
                    map.put("t", p.getBS().startsWith("B")?"Buy":"Sell");
                    list.add(map);
                }

                SimpleAdapter ad = new SimpleAdapter(getActivity(), list, R.layout.trans,
                        new String[]{"t", "q", "p", "c"}, new int[]{R.id.tt, R.id.tq, R.id.tp, R.id.tc});
                Dialog d = new Dialog(getActivity());
                d.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
                d.setTitle(cd.getText()+" Transactions");
                d.setContentView(R.layout.transview);

                ListView lView = (ListView) d.findViewById(R.id.transList);
                lView.setAdapter(ad);
                d.show();

            }
        });

        TextView nameView = (TextView) view.findViewById(R.id.fName);
        nameView.setText(name);
        Log.d("NEW", name+id);
        setup s = new setup();
        s.execute(id);
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,
                                    View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuInfo;
        RelativeLayout s = (RelativeLayout) info.targetView;
        TextView codeView = (TextView) s.findViewById(R.id.stock_code);
        DELETE_TYPE = codeView.getText().toString().substring(0, codeView.getText().toString().indexOf(":"));
        DELETE_CODE = codeView.getText().toString().substring(codeView.getText().toString().indexOf(":")+1,
                                            codeView.getText().length());

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
                alert.setMessage("Do you want delete this transaction?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseHandler db = new DatabaseHandler(getActivity());
                        Log.d("VALS", String.valueOf(PORTFOLIO_ID)+ DELETE_CODE+ DELETE_TYPE);
                        db.deleteTrans(String.valueOf(PORTFOLIO_ID), DELETE_CODE, DELETE_TYPE);
                        setup s = new setup();
                        s.execute(PORTFOLIO_ID);
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

    public class setup extends AsyncTask<Integer, Integer, String[]> {

        ArrayList<HashMap<String, String>> pAdd = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> listAdd = new ArrayList<HashMap<String, String>>();
        DecimalFormat nf = new DecimalFormat("#.00");
        ProgressBar fvp = (ProgressBar) view.findViewById(R.id.fvp);
        int errEx = 0;
        /*
        0: okay
        1: error
        2: no transactions
        */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fvp.setVisibility(View.VISIBLE);
            refresh.setEnabled(false);
        }

        @Override
        protected String[] doInBackground(Integer... ints) {
            String a[] = new String[5];
            int id = ints[0];
            DatabaseHandler port = new DatabaseHandler(getActivity());

            try {
                List<port> p =  port.getAllPort(id);

                Log.d("ID HERE", ""+p.size());
                JSONObject jObj = new JSONObject();
                JSONArray arr = new JSONArray();
                for(int i=0;i<p.size();++i){

                    port po = p.get(i);
                    JSONObject jArr = new JSONObject();
                    jArr.put("name", po.getName());
                    jArr.put("code", po.getCode());
                    jArr.put("type", po.getType());
                    arr.put(jArr);


                }
                jObj.put("portfolio", arr);
                if(p.size()>0) {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(getResources().getString(R.string.website) +
                            "/php/port.php?j=" + URLEncoder.encode(jObj.toString(), "UTF-8"));
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
                    Log.d("here", "DATA: "+stringBuilder.toString());
                    JSONObject jsonObj = new JSONObject(stringBuilder.toString());

                    double mktVal = 0, gl = 0, pl, orig = 0, dp=0, day = 0;

                    ArrayList<String> stocks = new ArrayList<String>();
                    boolean err = false, derr=true;

                    for (int j = 0; j < p.size(); ++j) {
                        HashMap<String, String> m = new HashMap<String, String>();
                        port po = p.get(j);
                        boolean eq = false;
                        for(int l =0;l<stocks.size();++l)
                            if (stocks.get(l).equals(po.getCode() + "_" + po.getType()))
                                eq = true;

                        if(!eq) {
                            stocks.add(po.getCode() + "_" + po.getType());
                            JSONObject JO = jsonObj.getJSONObject(po.getCode() + "_" + po.getType());
                            if (JO.getString("3").equals("N/A")) {
                                Log.d("Error", "N/A error trueee!!!!");
                                err = true;
                                derr=true;
                                //break;
                            }
                            if (!err) {
                                NumberFormat format = NumberFormat.getInstance(Locale.US);
                                Number number = format.parse(JO.getString("2").replace("&nbsp;&nbsp;&nbsp;&nbsp;-", "0"));
                                Double cVal = number.doubleValue();
                                Number oVal = format.parse(JO.getString("4").replace("&nbsp;&nbsp;&nbsp;&nbsp;-", "0").
                                        replace("\n", ""));
                                Number dll = format.parse(JO.getString("3").
                                        replace("+", "").replace("&nbsp;&nbsp;&nbsp;&nbsp;-", "0"));
                                Double ol = oVal.doubleValue();
                                double spl = 0, dpl = 0;
                                int shares = 0;
                                double den = 0;
                                day = 0;
                                double smtVal = 0;

                                for (int l = j; l < p.size(); ++l) {
                                    port portfolio = p.get(l);
                                    if ((portfolio.getCode() + "_" +
                                            portfolio.getType()).equals(po.getCode() + "_" + po.getType())) {
                                        if (portfolio.getBS().startsWith("B")) {
                                            spl += (cVal - portfolio.getPrice()) * portfolio.getQty() - portfolio.getCom();
                                            dpl += dll.doubleValue() * portfolio.getQty();
                                            mktVal += cVal * portfolio.getQty();
                                            smtVal += cVal * portfolio.getQty();
                                            shares += portfolio.getQty();
                                        } else {
                                            spl += (cVal - portfolio.getPrice()) *
                                                    (-portfolio.getQty()) - portfolio.getCom();
                                            dpl += dll.doubleValue() * -portfolio.getQty();
                                            mktVal += cVal * -portfolio.getQty();
                                            smtVal += cVal * -portfolio.getQty();
                                            shares -= portfolio.getQty();
                                            Log.d("DPL", "Price-->" + (cVal - Double.parseDouble(JO.getString("3"))));
                                        }
                                        orig += portfolio.getPrice() * portfolio.getQty();
                                        den += portfolio.getPrice() * portfolio.getQty();
                                        day += (cVal - Double.parseDouble(JO.getString("3"))) * portfolio.getQty();

                                    }
                                }


                                gl += spl;
                                dp += dpl;
                                double perc = spl * 100 / den;
                                m.put("name", po.getName());
                                m.put("code", po.getType() + ":" + po.getCode());
                                m.put("shares", shares + "");
                                m.put("val", cVal + "");
                                m.put("change", JO.getString("3") + " " + JO.getString("1"));
                                m.put("mkt", nf.format(smtVal));
                                m.put("pl", nf.format(spl));
                                m.put("pl_perc", nf.format(perc) + "%");
                                pAdd.add(m);
                            }
                            else{

                                /*
                                *STARTS HERE
                                 */
                                NumberFormat format = NumberFormat.getInstance(Locale.US);
                                Number number = format.parse(JO.getString("2").replace("&nbsp;&nbsp;&nbsp;&nbsp;-", "0"));
                                Double cVal = number.doubleValue();
                                Number oVal = format.parse(JO.getString("4").replace("&nbsp;&nbsp;&nbsp;&nbsp;-", "0").
                                        replace("\n", ""));
                                double spl = 0;
                                int shares = 0;
                                double den = 0;
                                day = 0;
                                double smtVal = 0;

                                for (int l = j; l < p.size(); ++l) {
                                    port portfolio = p.get(l);
                                    if ((portfolio.getCode() + "_" +
                                            portfolio.getType()).equals(po.getCode() + "_" + po.getType())) {
                                        if (portfolio.getBS().startsWith("B")) {
                                            spl += (cVal - portfolio.getPrice()) * portfolio.getQty() - portfolio.getCom();
                                            mktVal += cVal * portfolio.getQty();
                                            smtVal += cVal * portfolio.getQty();
                                            shares += portfolio.getQty();
                                        } else {
                                            spl += (cVal - portfolio.getPrice()) *
                                                    (-portfolio.getQty()) - portfolio.getCom();
                                            mktVal += cVal * -portfolio.getQty();
                                            smtVal += cVal * -portfolio.getQty();
                                            shares -= portfolio.getQty();
                                        }
                                        orig += portfolio.getPrice() * portfolio.getQty();
                                        den += portfolio.getPrice() * portfolio.getQty();

                                    }
                                }


                                gl += spl;
                                double perc = spl * 100 / den;
                                m.put("name", po.getName().length()>23?po.getName().substring(0, 23)+"..":po.getName());
                                m.put("code", po.getType() + ":" + po.getCode());
                                m.put("shares", shares + "");
                                m.put("val", cVal + "");
                                m.put("change", JO.getString("3") + " " + JO.getString("1"));
                                m.put("mkt", nf.format(smtVal));
                                m.put("pl", nf.format(spl));
                                m.put("pl_perc", nf.format(perc) + "%");
                                pAdd.add(m);

                            }
                        }
                    }

                    Log.d("DATA: ", "CUR = "+mktVal+", ORIGINAL = "+orig);

                    if(err){
                        pl = (gl / orig) * 100;
                        Double dplp = (dp / day) * 100;
                        a[0] = nf.format(gl);
                        a[1] = nf.format(pl) + "%";
                        a[2] = nf.format(mktVal);
                        a[3] = "N/A";
                        a[4] = "N/A";
                    }else{
                        pl = (gl / orig) * 100;
                        Double dplp = (dp / day) * 100;
                        a[0] = nf.format(gl);
                        a[1] = nf.format(pl) + "%";
                        a[2] = nf.format(mktVal);
                        a[3] = nf.format(dp);
                        a[4] = nf.format(dplp)+"%";
                    }

                }
                else{
                    pAdd = null;
                    a[0] = "N/A";
                    a[1] = "N/A";
                    a[2] = "N/A";
                    a[3] = "N/A";
                    a[4] = "N/A";
                    errEx = 2;
                }


            } catch (Exception e) {
                Log.d("error", e.toString());
            }
            return a;
        }

        @Override
        protected void onPostExecute(String e[]) {
            if(isAdded()&&!isCancelled()) {
                listAdd = pAdd;

                TextView plT = (TextView) view.findViewById(R.id.pl);
                TextView plT_perc = (TextView) view.findViewById(R.id.pl_perc);
                TextView dpl = (TextView) view.findViewById(R.id.dpl);
                TextView dpl_perc = (TextView) view.findViewById(R.id.dpl_perc);
                TextView mk = (TextView) view.findViewById(R.id.mktval);
                plT.setText(e[0]);
                plT_perc.setText(e[1]);
                if(!e[1].contains("-")){
                    plT.setTextColor(getResources().getColor(R.color.graphg));
                    plT_perc.setTextColor(getResources().getColor(R.color.graphg));
                }else{
                    plT.setTextColor(getResources().getColor(R.color.app_red));
                    plT_perc.setTextColor(getResources().getColor(R.color.app_red));
                }
                mk.setText(e[2]);
                dpl.setText(e[3]);
                dpl_perc.setText(e[4]);
                if(!e[4].contains("-")){
                    dpl.setTextColor(getResources().getColor(R.color.graphg));
                    dpl_perc.setTextColor(getResources().getColor(R.color.graphg));
                }else{
                    dpl.setTextColor(getResources().getColor(R.color.app_red));
                    dpl_perc.setTextColor(getResources().getColor(R.color.app_red));
                }

                TextView message = (TextView) view.findViewById(R.id.message);
                Log.d("EXERROR", "Value="+errEx);
                ListView lv = (ListView) view.findViewById(R.id.portfolio_view_list);
                SimpleAdapter ad;
                if(errEx==0) {
                    ad = new SimpleAdapter(getActivity(), listAdd, R.layout.portfolioview,
                            new String[]{"name", "code", "shares", "val", "change", "mkt", "pl", "pl_perc"},
                            new int[]{R.id.stock_name, R.id.stock_code, R.id.stock_shares,
                                    R.id.stock_price, R.id.stock_change, R.id.stock_mktVal
                                    , R.id.stock_pl, R.id.stock_pl_perc});

                    SimpleAdapter.ViewBinder binder = new SimpleAdapter.ViewBinder() {
                        @Override
                        public boolean setViewValue(View view, Object object, String value) {

                            View p = (View) view.getParent();
                            TextView bk = (TextView) p.findViewById(R.id.stock_price);
                            TextView bm = (TextView) view.findViewById(R.id.stock_change);
                            TextView pl = (TextView) p.findViewById(R.id.stock_pl);
                            TextView plp = (TextView) view.findViewById(R.id.stock_pl_perc);
                            if (view.equals((TextView) view.findViewById(R.id.stock_change))) {
                                if(value.contains("/")){
                                    bk.setTextColor(getResources().getColor(R.color.White));
                                    bm.setTextColor(getResources().getColor(R.color.White));
                                }
                                else if (value.contains("-")) {
                                    bk.setTextColor(getResources().getColor(R.color.app_red));
                                    bm.setTextColor(getResources().getColor(R.color.app_red));
                                } else {
                                    bk.setTextColor(getResources().getColor(R.color.graphg));
                                    bm.setTextColor(getResources().getColor(R.color.graphg));
                                }
                            }
                            if (view.equals((TextView) view.findViewById(R.id.stock_pl_perc))) {
                                if(value.contains("/")){
                                    bk.setTextColor(getResources().getColor(R.color.White));
                                    bm.setTextColor(getResources().getColor(R.color.White));
                                }
                                else if (value.contains("-")) {
                                    pl.setTextColor(getResources().getColor(R.color.app_red));
                                    plp.setTextColor(getResources().getColor(R.color.app_red));
                                } else {
                                    pl.setTextColor(getResources().getColor(R.color.graphg));
                                    plp.setTextColor(getResources().getColor(R.color.graphg));
                                }
                            }

                            return false;
                        }
                    };

                    message.setVisibility(View.GONE);
                    ad.setViewBinder(binder);

                    lv.setAdapter(ad);
                }else if(errEx==1){
                    message.setVisibility(View.VISIBLE);
                    message.setText("Data cannot be retrieved. Please try again later.");
                }else if(errEx==2){
                    ad = null;
                    lv.setAdapter(ad);
                    message.setVisibility(View.VISIBLE);
                    message.setText("No transactions");
                }
                fvp.setVisibility(View.INVISIBLE);
                refresh.setEnabled(true);
            }
        }
    }


}
