package adapters;

public class alerts {
    
    int _id;
    String _name;
    String _code;
    String _type;
    String _todo;
    String _tar;
    String _stat;
     
    public alerts(){
         
    }
    public alerts(int id, String name, String code, String type, String todo, String tar, String stat){
        this._id = id;
        this._name = name;
        this._code = code;
        this._type = type;
        this._todo = todo;
        this._tar = tar;
        this._stat = stat;
    }
    
    public alerts(String name, String code, String type, String todo, String tar, String stat){
        this._name = name;
        this._code = code;
        this._type = type;
        this._todo = todo;
        this._tar = tar;
        this._stat = stat;
    }
    
    public int getID(){
        return this._id;
    }
    public String getName(){
        return this._name;
    }
    public String getCode(){
        return this._code;
    }
    public String getType(){
        return this._type;
    }
    public String getTodo(){
        return this._todo;
    }
    public String getTar(){
        return this._tar;
    }
    public String getStat(){
        return this._stat;
    }
    public void setID(int tar){
        this._id = tar;
    }
    public void setName(String tar){
        this._name = tar;
    }
    public void setCode(String tar){
        this._code = tar;
    }
    public void setType(String tar){
        this._type = tar;
    }
    public void setTar(String tar){
        this._tar = tar;
    }
    public void setStat(String stat){
        this._stat = stat;
    }
    public void setTodo(String todo){
        this._todo = todo;
    }
     
}