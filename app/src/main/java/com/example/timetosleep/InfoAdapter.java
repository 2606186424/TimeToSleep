package com.example.timetosleep;

import android.content.Context;
import android.icu.text.IDNA;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 *  *@author TAO Yiduo
 *  *@version 1.0
 *  * ESIGELEC-ISE-OC
 *  this class is adapter for listview
 */
public class InfoAdapter extends ArrayAdapter<Info> {
    private int resourceId;
    public InfoAdapter(Context context, int textViewResourceId, ArrayList<Info> info){
        super(context,textViewResourceId,info);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Info info = getItem(position);
        String[] str = info.getDuration().split(":");
        int progress = Integer.parseInt(str[0])*100 / 180;
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView appName = (TextView)view.findViewById(R.id.app_name);
        TextView lastTime = (TextView)view.findViewById(R.id.last_time);
        TextView duration = (TextView)view.findViewById(R.id.duration);
        ProgressBar pro = (ProgressBar)view.findViewById(R.id.progressBar);
        appName.setText(info.getAppName());
        lastTime.setText(info.getStartTime());
        duration.setText(info.getDuration());
        //pro.set
        pro.setProgress(progress);
        return view;
    }
}
