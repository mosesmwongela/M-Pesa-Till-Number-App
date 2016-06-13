package com.mtnas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.mtnas.R;
import com.mtnas.utility.Font;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mwongela on 6/9/16.
 */
public class CustomInboxCursorAdapter extends CursorAdapter {
 
    @SuppressWarnings("deprecation")
	public CustomInboxCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }
 
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.single_inbox_sms_item, parent, false);
        return retView;
    }
 
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    	
    	TextView textViewStatus = (TextView) view.findViewById(R.id.status);
        int status = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(13)));
        if(status==0){
        	textViewStatus.setText("");
        }else if(status==1){
        	textViewStatus.setText("Processed");
        }

        Font.RALEWAY_LIGHT.apply(context, textViewStatus);

        TextView textViewNumber= (TextView) view.findViewById(R.id.number);
        String number = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3)));

        Font.RALEWAY_MEDIUM.apply(context, textViewNumber);
        
        textViewNumber.setText(number);
 
        TextView textViewMessage = (TextView) view.findViewById(R.id.message);
        String display1 = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(11)));
        textViewMessage.setText(display1+" ");

        Font.RALEWAY_LIGHT.apply(context, textViewMessage);
        
        TextView textViewTime = (TextView) view.findViewById(R.id.time);
        textViewTime.setText("");
        Font.RALEWAY_LIGHT.apply(context, textViewTime);
    }


    public static String Epoch2DateString(long epochSeconds, String formatString) {
        Date date = new Date(epochSeconds*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d H:mm", Locale.UK); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+3")); // give a timezone reference for formating (see comment at the bottom
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

}