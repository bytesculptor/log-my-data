package com.bytesculptor.logmydata.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.bytesculptor.logmydata.R;

import java.text.DateFormat;
import java.util.Date;

public class LogbookContentViewAdapter extends CursorAdapter {

    private static final String TAG = LogbookContentViewAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private int ciValue, ciComment, ciTimestamp;

    public LogbookContentViewAdapter(Context context, Cursor c) {
        super(context, c);

        inflater = LayoutInflater.from(context);
        ciValue = c.getColumnIndex(DbHandler.TYPE_NUMBER);
        ciComment = c.getColumnIndex(DbHandler.COMMENT);
        ciTimestamp = c.getColumnIndex(DbHandler.TIMESTAMP);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String dateFormatPlayed = (cursor.getLong(ciTimestamp) > 0) ? DateFormat.getDateTimeInstance().format(new Date(cursor.getLong(ciTimestamp))) : "-";

        TextView val = (TextView) view.findViewById(R.id.tvCardValue);
        TextView com = (TextView) view.findViewById(R.id.tvCardComment);
        TextView time = (TextView) view.findViewById(R.id.tvCardTimestamp);

        val.setText(cursor.getString(ciValue));
        com.setText(cursor.getString(ciComment));
        time.setText(dateFormatPlayed);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.view_single_entry, null);
    }
}