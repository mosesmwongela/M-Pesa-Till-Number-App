package com.mtnas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mtnas.adapter.CustomInboxCursorAdapter;
import com.mtnas.reminder.ReminderManager;
import com.mtnas.utility.DbHelper;

import java.util.Calendar;

/**
 * Created by mwongela on 6/9/16.
 */
public class InboxActivity extends ActionBarActivity {

    final Context context = this;
    private CustomInboxCursorAdapter customAdapter;
    private DbHelper databasehelper;
    private ListView listView;

    String ID;
    private Button addButton;

    private Calendar mCalendar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setSubtitle("Mpesa Till Number App Server");
        }

        SharedPreferences settings = getSharedPreferences(getString(R.string.prefs), 0);
        String SMS_URL = settings.getString(getString(R.string.prefs_server_url), null);
        
        if(SMS_URL==null)
            setURLDialog(false);

        databasehelper = new DbHelper(this);

        listView = (ListView) findViewById(R.id.list_inbox);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                customAdapter = new CustomInboxCursorAdapter(InboxActivity.this, databasehelper.fetchAllSmsR());
                listView.setAdapter(customAdapter);
            }
        });

        registerForContextMenu(listView);

        setReminderToPoll(false);
    }

    private void setReminderToPoll(boolean reset){
        mCalendar = Calendar.getInstance();
        setDate();
        long taskId= 1234;
        if(reset){
            new ReminderManager(this).CancelReminder(taskId + "");
        }
        new ReminderManager(this).setReminder(taskId, mCalendar);
    }


    private void setDate() {
        String day="Monday";
        if(day.equalsIgnoreCase("Sunday")){
            mCalendar.set(2013, 8, 22);
        }
        if(day.equalsIgnoreCase("Monday")){
            mCalendar.set(2013, 8, 23);
        }
        if(day.equalsIgnoreCase("Tuesday")){
            mCalendar.set(2013, 8, 24);
        }
        if(day.equalsIgnoreCase("Wednesday")){
            mCalendar.set(2013, 8, 25);
        }
        if(day.equalsIgnoreCase("Thursday")){
            mCalendar.set(2013, 8, 26);
        }
        if(day.equalsIgnoreCase("Friday")){
            mCalendar.set(2013, 8, 27);
        }
        if(day.equalsIgnoreCase("Saturday")){
            mCalendar.set(2013, 8, 28);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setURL:
                setURLDialog(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Select level
    public void setURLDialog(boolean error) {

        LayoutInflater li = LayoutInflater.from(InboxActivity.this);
        View promptsView = li.inflate(R.layout.set_url_layout, null);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        TextView prompt_instruction = (TextView) promptsView.findViewById(R.id.level_prompt_instruction);
        final EditText time_edittext = (EditText) promptsView.findViewById(R.id.etURL);


        if(error) {
            prompt_instruction.setText("Please enter a valid URL");
        }else{
            prompt_instruction.setText("Set Server URL");
        }

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                try {
                                    String strOutboxURL = time_edittext.getText().toString();

                                    if ((strOutboxURL.equalsIgnoreCase("") || (strOutboxURL == null)) ) {
                                        setURLDialog(true);
                                    }
                                    else if (!(URLUtil.isValidUrl(strOutboxURL))) {
                                        setURLDialog(true);
                                    }

                                    //store URL
                                    SharedPreferences settings = getSharedPreferences(getString(R.string.prefs), 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString(getString(R.string.prefs_server_url), strOutboxURL);
                                    editor.commit();
                                    ;
                                    Log.e("InboxActivity", "Server URL: " + strOutboxURL);
                            }catch(Exception e){
                                    e.printStackTrace();
                                    setURLDialog(true);
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Log.e("InboxActivity", "Canceled");
                            }
                        });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}