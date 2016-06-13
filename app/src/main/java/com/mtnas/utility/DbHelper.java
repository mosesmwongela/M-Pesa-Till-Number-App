package com.mtnas.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mwongela on 6/9/16.
 */
public class DbHelper {

    private static final String TAG = DbHelper.class.getSimpleName();
    
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "mtnas.db";

    // sms table configuration
    private static final String TABLE_NAME = "sms";
    public static final String KEY_ID = "id";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_THREAD_ID = "thread_id";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_DISPLAY_NAME = "dname";
    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "date";
    public static final String KEY_PROTOCOL = "protocol";
    public static final String KEY_READ = "read";
    public static final String KEY_STATUS = "status";
    public static final String KEY_TYPE = "type";
    public static final String KEY_BODY = "body";
    public static final String KEY_SERVICE_CENTER = "service_center";
    public static final String KEY_SMS_STATUS = "sts";
    
    private DatabaseOpenHelper openHelper;
    private SQLiteDatabase database;

    public DbHelper(Context aContext) {
        openHelper = new DatabaseOpenHelper(aContext);
        database = openHelper.getWritableDatabase();
    }

    public Cursor fetchDuplicateSms(String _id) throws SQLException {
        return database.query(true, TABLE_NAME, new String[] {KEY_ROWID}, KEY_ROWID + " = " + _id, null,null, null, null, null);
    }

    public void updateSmsStatus(String s) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SMS_STATUS, "1");
        database.update(TABLE_NAME, contentValues, KEY_ROWID + " = '" + s + "'", null);
    }

    public void  insertSms (String _id, String thread_id, String address, String name, String date,
                            String protocol, String read, String status, String type, String body,
                            String service_center, String sts) {
       ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ROWID, _id);
        contentValues.put(KEY_THREAD_ID, thread_id);
        contentValues.put(KEY_ADDRESS, address);
        contentValues.put(KEY_DISPLAY_NAME, name);
        contentValues.put(KEY_DATE, date);
        contentValues.put(KEY_PROTOCOL, protocol);
        contentValues.put(KEY_READ, read);
        contentValues.put(KEY_STATUS, status);
        contentValues.put(KEY_TYPE, type);
        contentValues.put(KEY_BODY, body);
        contentValues.put(KEY_SERVICE_CENTER, service_center);
        contentValues.put(KEY_SMS_STATUS, sts);
        database.insert(TABLE_NAME, null, contentValues);
    }

    public void clearInbox() {
        database.delete(TABLE_NAME, null, null);
    }

    public Cursor fetchAllSmsR(){
        String buildSQL = "SELECT * FROM " + TABLE_NAME+" ORDER BY " + KEY_DATE + " DESC;";
        return database.rawQuery(buildSQL, null);
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context aContext) {
            super(aContext, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            String buildSQL ="create table " + TABLE_NAME + " ("
                    + KEY_ID + " integer primary key autoincrement, "  //0
                    + KEY_ROWID + " text, " 						  //1
                    + KEY_THREAD_ID + " integer, " 					 //2
                    + KEY_ADDRESS + " text, " 					    //3
                    + KEY_DISPLAY_NAME + " text, " 				   //4
                    + KEY_NAME + " text, "						  //5
                    + KEY_DATE + " text, "					     //6
                    + KEY_PROTOCOL + " text, "		            //7
                    + KEY_READ + " text, "			           //8
                    + KEY_STATUS + " text,"					  //9
                    + KEY_TYPE + " text,"					 //10
                    + KEY_BODY + " text,"				    //11
                    + KEY_SERVICE_CENTER + " text,"		   //12
                    + KEY_SMS_STATUS + " text);";		  //13

            sqLiteDatabase.execSQL(buildSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            String buildSQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

            sqLiteDatabase.execSQL(buildSQL);
            onCreate(sqLiteDatabase);
        }
    }

    public DbHelper open() throws SQLException {
        database = openHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        openHelper.close();
    }

}
