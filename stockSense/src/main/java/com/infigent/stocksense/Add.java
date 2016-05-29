package com.infigent.stocksense;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Date;

import adapters.DatabaseHandler;
import adapters.SuggestionsAdapter;
import adapters.port;

public class Add extends Fragment {
    View view;
    String NN;
    String Sid;
    TextView NAME, CODE, TYPE;
    EditText  s, p, c;
    Button add;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.addport, null);
        final AutoCompleteTextView search;
        search = (AutoCompleteTextView)	view.findViewById(R.id.searchBox);
        search.setThreshold(2);
        search.setAdapter(new SuggestionsAdapter(getActivity(), search.getText().toString()));
        Bundle bundle = getArguments();
        Sid = bundle.getString("id");
        NN = bundle.getString("name");

        NAME = (TextView) view.findViewById(R.id.nameto);
        CODE = (TextView) view.findViewById(R.id.codeto);
        TYPE = (TextView) view.findViewById(R.id.typeto);
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                String term = parent.getItemAtPosition(pos).toString();
                String[] lines = term.split("\\r?\\n");
                String name = lines[0];
                String code = lines[1].substring(lines[1].indexOf(":")+1, lines[1].length()).trim();
                String type = lines[1].substring(0, lines[1].indexOf(":"));
                search.setText(name);
                NAME.setText(name);
                CODE.setText(code);
                TYPE.setText(type);
                show show = new show();
                show.execute(code, type);
                check();
            }
        });
        s = (EditText) view.findViewById(R.id.sharesto);
        p = (EditText) view.findViewById(R.id.priceto);
        c = (EditText) view.findViewById(R.id.cometo);
        s.addTextChangedListener(watch);
        p.addTextChangedListener(watch);
        c.addTextChangedListener(watch);
        final Switch BS = (Switch) view.findViewById(R.id.switch3);
        final InputMethodManager ime = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        add = (Button) view.findViewById(R.id.addto);
        check();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = Integer.parseInt(Sid);
                String name = NAME.getText().toString();
                String code = CODE.getText().toString();
                String type = TYPE.getText().toString();
                int shares = Integer.parseInt(s.getText().toString());
                int price = Integer.parseInt(p.getText().toString());
                int com;
                if(c.getText().toString().trim().equals(""))
                    com = 0;
                else
                    com = Integer.parseInt(c.getText().toString());
                String B;
                if (BS.isChecked()) {
                    B = "S";
                } else {
                    B = "B";
                }
                port p = new port();
                p.setID(id);
                p.setName(name);
                p.setCode(code);
                p.setType(type);
                p.setQty(shares);
                p.setPrice(price);
                p.setCom(com);
                p.setBS(B);
                p.setDate(new Date());
                DatabaseHandler db = new DatabaseHandler(getActivity());
                if (name != null && shares > 0 && price > 0)
                    db.addPort(p);
                Bundle data = new Bundle();
                data.putString("name", NN);
                data.putInt("id", id);
                ime.hideSoftInputFromWindow(s.getWindowToken(), 0);
                android.support.v4.app.FragmentTransaction transaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                PortView pv = new PortView();
                pv.setArguments(data);
                switchFragment(pv);
            }
        });
        return view;
    }


    private final TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            check();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void check(){
        if(NAME.getText().toString().trim().length()==0||CODE.getText().toString().trim().length()==0||
                TYPE.getText().toString().trim().length()==0||s.getText().toString().trim().length()==0||
                p.getText().toString().trim().length()==0)
            add.setEnabled(false);
        else
            add.setEnabled(true);
    }

    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof MainActivity) {
            MainActivity fca = (MainActivity) getActivity();
            fca.switchContent(fragment);
        }

    }

    public String gName(){
        return NN;
    }
    public String gID(){
        return Sid;
    }


    class show extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onCancelled() {
        }

        @Override
        protected String doInBackground(String... q) {

            String v="";
            try {
                String url = getResources().getString(R.string.website) +
                        "/stocks.php?f="+ URLEncoder.encode(q[0], "UTF-8")+"&t="+q[1];
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
                String js = stringBuilder.toString();
                try {
                    JSONObject jsonObj = new JSONObject(js);
                    v = jsonObj.getString("3");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (ClientProtocolException cpe) {
                cpe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            return v;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            TextView val = (TextView) view.findViewById(R.id.value);
            val.setText(result);

        }

    }


}
