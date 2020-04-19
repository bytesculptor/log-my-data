package com.bytesculptor.logmydata;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by r0 on 22.02.18.
 */

class LogbookListAdapter extends BaseAdapter {

    private static final String TAG = LogbookListAdapter.class.getSimpleName();

    private ArrayList<String> localLogbookList;
    private Activity context;


    public LogbookListAdapter(Activity context, ArrayList<String> logbookList) {
        super();
        this.context = context;
        this.localLogbookList = logbookList;
    }


    private class ViewHolder {
        TextView tvLogbookName;
    }


    public int getCount() {
        if (localLogbookList != null) {
            return localLogbookList.size();
        } else
            return 0;
    }


    public Object getItem(int position) {
        return localLogbookList.get(position);
    }


    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.view_single_logbook, null);
            holder = new ViewHolder();

            holder.tvLogbookName = convertView.findViewById(R.id.tvBlockName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvLogbookName.setText(localLogbookList.get(position));

        return convertView;
    }

    public void updateList(ArrayList<String> logbookList) {
        localLogbookList = logbookList;
        notifyDataSetChanged();
    }

}