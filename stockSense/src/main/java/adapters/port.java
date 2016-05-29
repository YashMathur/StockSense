package adapters;

import java.util.Date;

public class port {

    int _id;
    int _tid;
    String _name;
    String _code;
    String _type;
    int _com;
    int _qty;
    int _price;
    Date _date;
    String _bs;

    public port(){

    }

    public port(int id,int tid, String name, String code, String type, int com, int qty, int price, Date d, String bs){
        this._id = id;
        this._tid = tid;
        this._name = name;
        this._code = code;
        this._type = type;
        this._com = com;
        this._qty = qty;
        this._price = price;
        this._date = d;
        this._bs = bs;
    }

    public port(int id, String name, String code, String type, int com, int qty, int price, Date d, String bs){
        this._id = id;
        this._name = name;
        this._code = code;
        this._type = type;
        this._com = com;
        this._qty = qty;
        this._price = price;
        this._date = d;
        this._bs = bs;
    }

    public int getTID(){ return this._tid; }

    // getting ID
    public int getID(){
        return this._id;
    }

    // getting name
    public String getName(){
        return this._name;
    }

    // getting phone number
    public String getCode(){
        return this._code;
    }

    // getting phone number
    public String getType(){
        return this._type;
    }

    // getting ID
    public int getCom(){
        return this._com;
    }

    // getting name
    public int getQty(){
        return this._qty;
    }

    // getting phone number
    public int getPrice(){
        return this._price;
    }

    // getting phone number
    public Date getDate(){
        return this._date;
    }

    public String getBS(){
        return this._bs;
    }

    public void setID(int tar){this._id = tar; }

    public void setTID(int tar){
        this._tid = tar;
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

    public void setCom(int tar){
        this._com = tar;
    }

    public void setQty(int tar){
        this._qty = tar;
    }

    public void setPrice(int tar){
        this._price = tar;
    }

    public void setDate(Date tar){
        this._date = tar;
    }

    public void setBS(String tar){
        this._bs = tar;
    }

}
