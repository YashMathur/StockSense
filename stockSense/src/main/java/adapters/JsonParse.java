package adapters;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class JsonParse {
	Context context;
	double current_latitude,current_longitude;
    public JsonParse(){}
    public JsonParse(double current_latitude,double current_longitude, Context context){
        this.current_latitude=current_latitude;
        this.current_longitude=current_longitude;
        this.context = context;
    }
    public List<SuggestGetSet> getParseJsonWCF(String s, Context context){
        List<SuggestGetSet> ListData = new ArrayList<SuggestGetSet>();
        try {
        	InputStream is = context.getAssets().open("json.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();
           String line =new String(buffer, "UTF-8");
           JSONObject jsonResponse = new JSONObject(line);
           JSONArray jsonArray = jsonResponse.getJSONArray("stock");
           for(int i = 0; i < jsonArray.length(); i++){
               JSONObject r = jsonArray.getJSONObject(i);
	               if(r.getString("name").toLowerCase(Locale.US).startsWith(s.toLowerCase())||
	            		   r.getString("code").toLowerCase().startsWith(s.toLowerCase()))
	            	   ListData.add(new SuggestGetSet(r.getString("name"),
	            		   r.getString("code"),r.getString("type")));

           }
       } catch (Exception e1) {
           e1.printStackTrace();
       }
        return ListData;
       }
}
