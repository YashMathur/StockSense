package adapters;

public class SuggestGetSet {
	String name,code,type;
    public SuggestGetSet(String name, String code, String type){
        this.setType(type);
        this.setName(name);
        this.setCode(code);
    }
    private void setType(String nse2) {
    	this.type = nse2;
		
	}
    public String getCode() {
        return code;
    }
    public String getType() {
        return type;
    }
    
 
    public void setCode(String id) {
        this.code = id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
}
