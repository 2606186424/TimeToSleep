package com.example.timetosleep;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 *@author TAO Yiduo
 *@version 1.0
 * This class shows the statistics of usage
 * ESIGELEC-ISE-OC
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Log extends AppCompatActivity {
    private TextView totalTime;
    private ListView lv;
    private long totalMs = 0;
    private ArrayList<Info> infos = new ArrayList<Info>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getLog(){
        Calendar beginCal = Calendar.getInstance();
        beginCal.add(Calendar.HOUR_OF_DAY, -1);
        Calendar endCal = Calendar.getInstance();
        UsageStatsManager manager=(UsageStatsManager)getApplicationContext().getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> stats=manager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,beginCal.getTimeInMillis(),endCal.getTimeInMillis());


        StringBuilder sb=new StringBuilder();
        for(UsageStats us:stats){
            try {
                PackageManager pm=getApplicationContext().getPackageManager();
                ApplicationInfo applicationInfo=pm.getApplicationInfo(us.getPackageName(),PackageManager.GET_META_DATA);
                if((applicationInfo.flags&applicationInfo.FLAG_SYSTEM)<=0){
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    String t=format.format(new Date(us.getLastTimeUsed()));
                    sb.append(pm.getApplicationLabel(applicationInfo)+"\t"+t+"\t"+ getMinutesFromLong(us.getTotalTimeInForeground())+"\n");
                    infos.add(new Info(pm.getApplicationLabel(applicationInfo).toString(), t, getMinutesFromLong(us.getTotalTimeInForeground()).toString()));
                    totalMs += us.getTotalTimeInForeground();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private Object getMinutesFromLong(long totalTimeInForeground) {
        long sec = totalTimeInForeground/1000;
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        getLog();
        InfoAdapter adapter = new InfoAdapter(Log.this, R.layout.info_item, infos);
        lv = findViewById(R.id.lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setAdapter(adapter);
        totalTime = findViewById(R.id.total);
        String totalMin = getMinutesFromLong(totalMs).toString();
        totalTime.append(totalMin);
    }
}