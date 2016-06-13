package com.mtnas.reminder;

/**
 * Created by Pineapple on 3/26/2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mtnas.service.GetMessagesService;

public class OnAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "OnAlarmReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "Alarm Received");

        long rowid = intent.getExtras().getLong("mRowId");

        WakeReminderIntentService.acquireStaticLock(context);

        Intent i = new Intent(context, GetMessagesService.class);
        i.putExtra("mRowId", rowid);
        context.startService(i);
    }
}