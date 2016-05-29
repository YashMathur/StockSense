package adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
 
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "stocksense";

    private static final String TABLE_CONTACTS = "alerts";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CODE = "code";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TODO = "todo";
    private static final String KEY_TARGET = "target";
    private static final String KEY_STAT = "status";
    private static final String TABLE_MAIN = "portM";
    private static final String TABLE_SUB = "portS";
    private static final String KEY_TID = "tid";
    private static final String KEY_PRICE = "price";
    private static final String KEY_QTY = "qty";
    private static final String KEY_DATE = "date";
    private static final String KEY_COM = "commission";
    private static final String KEY_BS = "bs";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_CODE + " TEXT," + KEY_TYPE + " TEXT,"
                + KEY_TODO + " TEXT," + KEY_TARGET + " TEXT, "+ KEY_STAT + " TEXT" + ")";


        String CREATE_MAIN_TABLE = "CREATE TABLE " + TABLE_MAIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT)";


        String CREATE_SUB_TABLE = "CREATE TABLE " + TABLE_SUB + "("
                + KEY_TID + " INTEGER PRIMARY KEY," + KEY_ID + " INTEGER ," +  KEY_NAME + " TEXT,"
                + KEY_CODE + " TEXT," + KEY_TYPE + " TEXT,"+ KEY_BS +" TEXT, "+KEY_PRICE+" INTEGER, "
                +KEY_DATE+" DATE, "+KEY_QTY+" INTEGER, "+KEY_COM+" INTEGER)";

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_MAIN_TABLE);
        db.execSQL(CREATE_SUB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUB);

        onCreate(db);
    }

    // Adding new contact
    public void addContact(alerts contact) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); 
        values.put(KEY_CODE, contact.getCode()); 
        values.put(KEY_TYPE, contact.getType()); 
        values.put(KEY_TODO, contact.getTodo()); 
        values.put(KEY_TARGET, contact.getTar()); 
        values.put(KEY_STAT, contact.getStat()); 
 
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single contact
    public alerts getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
                KEY_NAME, KEY_CODE, KEY_TYPE, KEY_TODO, KEY_TARGET, KEY_STAT}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        alerts contact = new alerts(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5), cursor.getString(6));
        // return contact
        db.close();
        return contact;
    }
     
    // Getting All Contacts
    public List<alerts> getAllContacts() {
        List<alerts> contactList = new ArrayList<alerts>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        if (cursor.moveToFirst()) {
            do {
            	alerts contact = new alerts();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setCode(cursor.getString(2));
                contact.setType(cursor.getString(3));
                contact.setTodo(cursor.getString(4));
                contact.setTar(cursor.getString(5));
                contact.setStat(cursor.getString(6));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return contactList;
    }
 
    // Updating single contact
    public int updateContact(alerts contact) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_CODE, contact.getCode());
        values.put(KEY_TYPE, contact.getType());
        values.put(KEY_TODO, contact.getTodo());
        values.put(KEY_TARGET, contact.getTar());
        values.put(KEY_STAT, contact.getStat());

        if(db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) })!=0) {
            db.close();
            return 1;
        }
        else {
            db.close();
            return 0;
        }

    }
 
    // Deleting single contact
    public void deleteContact(alerts contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();
        db.close();
        cursor.close();
        return c;
    }

    public void addFolder(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);

        db.insert(TABLE_MAIN, null, values);
        db.close();
    }

    public void deleteFolder(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MAIN, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.delete(TABLE_SUB, KEY_ID +" = ?",
                new String[]{String.valueOf(id) });
        db.close();
    }

    public void deleteTrans(String id, String code, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUB, KEY_ID +" =? AND "+KEY_CODE+" =? AND "+KEY_TYPE+" =?",
                new String[]{id, code, type});
        db.close();
    }

    public void addPort(port contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, contact.getID());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_CODE, contact.getCode());
        values.put(KEY_TYPE, contact.getType());
        values.put(KEY_PRICE, contact.getPrice());
        values.put(KEY_DATE, contact.getDate().toString());
        values.put(KEY_QTY, contact.getQty());
        values.put(KEY_COM, contact.getCom());
        values.put(KEY_BS, contact.getBS());
        Log.d(KEY_BS, contact.getBS());
        db.insert(TABLE_SUB, null, values);
        db.close(); // Closing database connection
    }

    public int updatePort(port contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_CODE, contact.getCode());
        values.put(KEY_TYPE, contact.getType());
        values.put(KEY_COM, contact.getCom());
        values.put(KEY_PRICE, contact.getPrice());
        values.put(KEY_DATE, contact.getDate().toString());
        values.put(KEY_BS, contact.getBS());

        // Inserting Row
        return db.update(TABLE_SUB, values, KEY_TID + " = ?",
                new String[] { String.valueOf(contact.getTID()) });
    }

    public List<port> getAllPort(int id) {
        List<port> contactList = new ArrayList<port>();
        String selectQuery = "SELECT  * FROM " + TABLE_SUB+" WHERE `id` = "+id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                port contact = new port();
                contact.setID(Integer.parseInt(cursor.getString(1)));
                contact.setName(cursor.getString(2));
                contact.setCode(cursor.getString(3));
                contact.setType(cursor.getString(4));
                contact.setPrice(Integer.parseInt(cursor.getString(6)));
                contact.setDate(new Date());
                contact.setQty(Integer.parseInt(cursor.getString(8)));
                contact.setBS(cursor.getString(5));
                contact.setCom(Integer.parseInt(cursor.getString(9)));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return contactList;
    }

    public List<port> getAllTrans(int id, String code, String type) {
        List<port> contactList = new ArrayList<port>();
        String selectQuery =
                "SELECT  * FROM " + TABLE_SUB+" WHERE `id` = "+id+" AND `code` = '"+code+"' AND `type` = '"+type+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                port contact = new port();
                contact.setID(Integer.parseInt(cursor.getString(1)));
                contact.setName(cursor.getString(2));
                contact.setCode(cursor.getString(3));
                contact.setType(cursor.getString(4));
                contact.setPrice(Integer.parseInt(cursor.getString(6)));
                contact.setDate(new Date());
                contact.setQty(Integer.parseInt(cursor.getString(8)));
                contact.setBS(cursor.getString(5));
                contact.setCom(Integer.parseInt(cursor.getString(9)));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }


        db.close();
        cursor.close();
        return contactList;
    }

    public List<HashMap<String, String>> getAllFolders() {
        List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_MAIN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("name", cursor.getString(1));
                map.put("id", cursor.getString(0));
                contactList.add(map);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return contactList;
    }

    public int getFolders() {
        String countQuery = "SELECT  * FROM " + TABLE_MAIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int c = cursor.getCount();
        db.close();
        cursor.close();

        return c;
    }

    public boolean checkPort(int id, String code, String type){

        String countQuery = "SELECT  * FROM " + TABLE_SUB + " WHERE `id`="+id+" AND `code`="+code+" AND `type`="+type;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();
        db.close();
        return c != 0;

    }

    public int getPorts() {
        String countQuery = "SELECT  * FROM " + TABLE_SUB;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int c = cursor.getCount();
        cursor.close();
        db.close();
        return c;
    }
 
}