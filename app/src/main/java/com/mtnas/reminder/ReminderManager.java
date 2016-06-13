package com.mtnas.reminder;

/**
 * Created by Pineapple on 3/26/2015.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class ReminderManager {

    private static final String TAG = "ReminderManager";

    private Context mContext;
    private AlarmManager mAlarmManager;
    PendingIntent pi;

    public ReminderManager(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

    }

    public void setReminder(Long taskId, Calendar when) {

        Intent i = new Intent(mContext, OnAlarmReceiver.class);

        //Unique id for each class - unique pending intent
        final int _id = taskId.intValue();

        i.putExtra("mRowId", (long)taskId);

        pi = PendingIntent.getBroadcast(mContext, _id, i, PendingIntent.FLAG_UPDATE_CURRENT);

        //get seonds to poll from databse
        long time = 10000;

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), time, pi);

        Log.e(TAG, "Set reminder to go off every "+ time + "ms");
    }

    public void CancelReminder(String taskId) {

        Intent i = new Intent(mContext, OnAlarmReceiver.class);

        //Unique id for each class - unique pending intent
        final int _id = Integer.parseInt(taskId);

        pi = PendingIntent.getBroadcast(mContext, _id, i, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.cancel(pi);
        Log.e(TAG, "Reminder cancelled");
    }
}
