package com.mtnas.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import com.mtnas.utility.DbHelper;

/**
 * Created by mwongela on 6/9/16.
 */
public class GetMessagesService extends Service {

    private DbHelper databaseHelper;
    private Cursor smsCursor;
    private Cursor smsExist;
    private static final Uri INBOX_URI = Uri.parse("content://sms/inbox");

    private static final String TAG = "GetMessagesService";

    //sms cursor
    String[] projection = { "_id", "thread_id", "address", "date", "body"};
    String thread_id= "thread_id";
    String selection = "0==0) GROUP BY (" + thread_id;

    public GetMessagesService(){
    }

    public IBinder onBind(Intent intent){
        throw new UnsupportedOperationException("Not yet implemented");
        //return null;
    }

    public void onCreate(){
    }

    public int onStartCommand(Intent intent, int flags, int StartId){

        Log.e(TAG, "onStartCommand");

        databaseHelper = new DbHelper(this);

        if (isMyServiceRunning(ReadMessagesService.class)) {
            this.stopService(new Intent(this, ReadMessagesService.class));
        }

        new GetInbox().execute();
        return Service.START_NOT_STICKY;
    }

    public void onDestroy(){
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    class GetInbox extends AsyncTask<String, String, String> {

        protected String doInBackground(String... args) {

            try{
                smsCursor = getContentResolver().query(INBOX_URI, null, null, null, null);
                smsCursor.moveToFirst();
                int i = 0;
                while(!smsCursor.isAfterLast()) {

                    String _id = smsCursor.getString(smsCursor.getColumnIndex("_id"));

                    smsExist = databaseHelper.fetchDuplicateSms(_id);

                    String thread_id = smsCursor.getString(smsCursor.getColumnIndex("thread_id"));

                    String address = smsCursor.getString(smsCursor.getColumnIndex("address"));
                    address = address.trim();
                    address = address.replaceAll("\\s+", "");
                    if(address.length()<13){
                        address = address.replaceFirst("0", "+254");
                    }
                    String name = getDisplayNameByNumber(address);

                    if(address.substring(0, 1).equalsIgnoreCase("+")){
                        address = address.replaceFirst("\\+", "");
                    }

                    String date = smsCursor.getString(smsCursor.getColumnIndex("date"));
                    String protocol = smsCursor.getString(smsCursor.getColumnIndex("protocol"));
                    String read = smsCursor.getString(smsCursor.getColumnIndex("read"));
                    String status = smsCursor.getString(smsCursor.getColumnIndex("status"));
                    String type = "inbox";
                    String body = smsCursor.getString(smsCursor.getColumnIndex("body"));
                    String service_center = smsCursor.getString(smsCursor.getColumnIndex("service_center"));

                    if(name.equalsIgnoreCase("MPESA")) {
                        if (smsExist.getCount() == 0) {
                            i++;
                            databaseHelper.insertSms(_id, thread_id, address, name, date, protocol, read, status, type, body, service_center, "0");
                        }
                    }
                    smsCursor.moveToNext();
                }

                Log.e(TAG, "######## SUCCESS ############");

            } catch(Exception e) {
                Log.e(TAG, "######## ERROR: INBOX SMS IMPORT FAILED ############");
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            callReadService();
        }
    }

    public void callReadService(){
        Intent i = new Intent(this,ReadMessagesService.class);
        this.startService(i);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private String getDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        Cursor contactLookup = this.getContentResolver().query(uri, new String[] {ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        int indexName = contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);

        try {
            if (contactLookup != null && contactLookup.moveToNext()) {
                number = contactLookup.getString(indexName);
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }
        // Log.d("DISPLAY NAME IMPORT", number);
        return number;
    }

}
