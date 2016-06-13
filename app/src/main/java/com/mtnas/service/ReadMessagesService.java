package com.mtnas.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.mtnas.R;
import com.mtnas.utility.DbHelper;
import com.mtnas.utility.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mwongela on 6/9/16.
 */
public class ReadMessagesService extends Service {

    private DbHelper databaseHelper;
    private Cursor smsCursor;

    JSONParser jsonParser = new JSONParser();
    private static String SMS_URL = "";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MSG = "message";

    private static final String TAG = "ReadMessagesService";

    //sms cursor
    String[] projection = { "_id", "thread_id", "address", "date", "body"};
    String thread_id= "thread_id";
    String selection = "0==0) GROUP BY (" + thread_id;

    public ReadMessagesService(){
    }

    public IBinder onBind(Intent intent){
        throw new UnsupportedOperationException("Not yet implemented");
        //return null;
    }

    public void onCreate(){
    }

    public int onStartCommand(Intent intent, int flags, int StartId){

        Log.e(TAG, "onStartCommand");

        if (isMyServiceRunning(GetMessagesService.class)) {
            this.stopService(new Intent(this, GetMessagesService.class));
        }

        databaseHelper = new DbHelper(this);
        new ReadSms().execute();
        return Service.START_NOT_STICKY;
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

    class ReadSms extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            String message = null;
            try{
                smsCursor = databaseHelper.fetchAllSmsR();
                smsCursor.moveToFirst();
                int i =0, j=0;
                while(!smsCursor.isAfterLast()) {
                    String _id = smsCursor.getString(smsCursor.getColumnIndex("_id"));
                    String thread_id = smsCursor.getString(smsCursor.getColumnIndex("thread_id"));
                    String address = smsCursor.getString(smsCursor.getColumnIndex("address"));
                    String name = smsCursor.getString(smsCursor.getColumnIndex("dname"));
                    String date = smsCursor.getString(smsCursor.getColumnIndex("date"));
                    String protocol = smsCursor.getString(smsCursor.getColumnIndex("protocol"));
                    String read = smsCursor.getString(smsCursor.getColumnIndex("read"));
                    String status = smsCursor.getString(smsCursor.getColumnIndex("status"));
                    String type = smsCursor.getString(smsCursor.getColumnIndex("type"));
                    String body = smsCursor.getString(smsCursor.getColumnIndex("body"));
                    String service_center = smsCursor.getString(smsCursor.getColumnIndex("service_center"));
                    String sts = smsCursor.getString(smsCursor.getColumnIndex("sts"));

                    String e_code = null, e_date = null, e_amount = null, e_number = null, e_name = null, e_newbaance = null;
                    try {
                        String body_ = body;
                        e_code = body_.substring(0, body_.indexOf("Confirmed") - 1);
                        e_date = body_.substring(body_.indexOf("Confirmed.on") + 12, body_.indexOf("Ksh"));
                        e_amount = body_.substring(body_.indexOf("Ksh") + 3, body_.indexOf("received") - 1);
                        e_number = body_.substring(body_.indexOf("from") + 5, body_.indexOf("from") + 17);
                        e_name = body_.substring(body_.indexOf("from") + 17, body_.indexOf("New Account") - 1);
                        e_newbaance = body_.substring(body_.indexOf("is Ksh") + 6, body_.length());
                    }catch(Exception e){
                        e.printStackTrace();
                    }


                    if(sts.equalsIgnoreCase("0")){
                        int success=0;

                        try {
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("_id",_id));
                            params.add(new BasicNameValuePair("thread_id",thread_id));
                            params.add(new BasicNameValuePair("address",address));
                            params.add(new BasicNameValuePair("name",name));
                            params.add(new BasicNameValuePair("date",date));
                            params.add(new BasicNameValuePair("protocol",protocol));
                            params.add(new BasicNameValuePair("read",read));
                            params.add(new BasicNameValuePair("status",status));
                            params.add(new BasicNameValuePair("type",type));
                            params.add(new BasicNameValuePair("body",body));
                            params.add(new BasicNameValuePair("service_center",service_center));

                            params.add(new BasicNameValuePair("e_code",e_code));
                            params.add(new BasicNameValuePair("e_date",e_date));
                            params.add(new BasicNameValuePair("e_amount",e_amount));
                            params.add(new BasicNameValuePair("e_number",e_number));
                            params.add(new BasicNameValuePair("e_name",e_name));
                            params.add(new BasicNameValuePair("e_newbalance",e_newbaance));

                            SharedPreferences settings = getSharedPreferences(getString(R.string.prefs), 0);
                            SMS_URL = settings.getString(getString(R.string.prefs_server_url), null);

                            JSONObject json = jsonParser.makeHttpRequest(SMS_URL, "POST", params);

                            success = json.getInt(TAG_SUCCESS);

                            message = json.getString(TAG_MSG);

                            if (success == 1) {
                                i++;
                                databaseHelper.updateSmsStatus(_id);
                                Log.d(TAG, "******** SMS SENT ************ " + i);
                            }else{
                                j++;
                                Log.e(TAG, "******** SMS NOT SENT ****** " +  message + " "+ j);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    smsCursor.moveToNext();
                }

            }catch(Exception e){
                Log.e(TAG, "######## ERROR: SMS read FAILED ############");
                e.printStackTrace();
            }
            return message;
        }

        protected void onPostExecute(String file_url) {
            callReadService();
        }

    }

    public void callReadService(){
        Intent i = new Intent(this,GetMessagesService.class);
        this.startService(i);
    }

    public void onDestroy(){
        // Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
