package adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.infigent.stocksense.R;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Filter;

public class SuggestionsAdapter extends ArrayAdapter<String>{
	protected static final String TAG = "SuggestionAdapter";
    private List<String> suggestions;
    HashMap<String, String> map = new HashMap<String, String>();
    public SuggestionsAdapter(Activity context, String nameFilter) {
        super(context, R.layout.suggestion, R.id.s_name);
        suggestions = new ArrayList<String>();
        
    }
 
    @Override
    public int getCount() {
        return suggestions.size();
    }
 
    @Override
    public String getItem(int index) {
        return suggestions.get(index);
    }
 
    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                JsonParse jp=new JsonParse();
                if (constraint != null) {
                    List<SuggestGetSet> new_suggestions =jp.getParseJsonWCF(constraint.toString(), getContext());
                    suggestions.clear();
                    map.clear();
                    for (int i=0;i<new_suggestions.size();i++) {
                        //map.put("name",new_suggestions.get(i).getName());
                        //map.put("NSE", new_suggestions.get(i).getNse());
                        //map.put("BSE", new_suggestions.get(i).getBse());
                        // suggestions.add(map);
                    	suggestions.add(new_suggestions.get(i).getName()+
                    			"\n"+new_suggestions.get(i).getType()+
                    			": "+new_suggestions.get(i).getCode());
                    	/*if(new_suggestions.get(i).getBse() == "")
                    		suggestions.add(new_suggestions.get(i).getName()+
                    				"\nNSE: "+new_suggestions.get(i).getNse());
                    	if(new_suggestions.get(i).getNse() == "")
                    		suggestions.add(new_suggestions.get(i).getName()+
                    				"\nBSE: "+new_suggestions.get(i).getBse());
                    	if((new_suggestions.get(i).getBse() != "") && 
                    			(new_suggestions.get(i).getNse() != "")){
                    		suggestions.add(new_suggestions.get(i).getName()+
                    				"\nBSE: "+new_suggestions.get(i).getBse());
	                    	suggestions.add(new_suggestions.get(i).getName()+
	                				"\nNSE: "+new_suggestions.get(i).getNse());
                    	}*/
                    }

                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                }
                return filterResults;
            }
 
            @Override
            protected void publishResults(CharSequence contraint,
                    FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }
}
