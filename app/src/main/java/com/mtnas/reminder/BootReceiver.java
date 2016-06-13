package com.mtnas.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by mwongela on 11/3/15.
 */
public class BootReceiver extends BroadcastReceiver
{
    // Boot intent action name
    private static final String BOOT_ACTION_NAME = "android.intent.action.BOOT_COMPLETED";
    private static String TAG = "BootReceiver";

    private Calendar mCalendar;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (BOOT_ACTION_NAME.equals(intent.getAction()))
        {
            //start alarm again
            setReminderToPoll(false, context);
            Log.e(TAG, "On boot receiver, start alarm again");
        }
    }

    private void setReminderToPoll(boolean reset, Context context){
        mCalendar = Calendar.getInstance();
        setDate();
        long taskId= 1234;
        if(reset){
            new ReminderManager(context).CancelReminder(taskId + "");
        }
        new ReminderManager(context).setReminder(taskId, mCalendar);
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

}
