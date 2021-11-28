
package com.example.timetosleep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;
/**
 *@author TAO Yiduo
 *@version 1.0
 * ESIGELEC-ISE-OC
 */
public class MainActivity extends AppCompatActivity {
    private TextView timeshow;
    private TextView limitationSet;
    private Button how2use;
    private KeyguardManager keyguardManager;
    private Timer timer;
    private int s = 0;
    private ProgressBar progressBar;
    private TimerTask myTimerTask = new TimerTask() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
        }
    };
    private int Limit = 60;
    private Button setting;
    private Button log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyguardManager= (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        how2use = findViewById(R.id.h2use_button);
        how2use.setOnClickListener(this::onClick);
        progressBar = findViewById(R.id.progressBar2);
        timeshow = findViewById(R.id.timeshow);
        limitationSet = findViewById(R.id.show_limit);
        //get limitation number,if file non exist, limit = 10 seconds.
        try {

            if(readData(this).length() > 0){
                Limit = Integer.parseInt(readData(this))*60;
                limitationSet.append("\t\t"+readData(this));
            }
            else{
                limitationSet.append("not defined, default is 1 minutes");
            }
        } catch (IOException e) {
            e.printStackTrace();
            limitationSet.append("not defined");
        }

        setting = findViewById(R.id.setting_button);
        setting.setOnClickListener(this::onClick);
        log = findViewById(R.id.logbutton);
        log.setOnClickListener(this::onClick);
        timer = new Timer();//start timer and send message to handler every second
        timer.schedule(myTimerTask, 1000, 1000);
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.setting_button:
                String string = String.valueOf(Limit);
                Intent intent2 = new Intent(MainActivity.this, Setting.class);
                intent2.putExtra("Number", string);
                startActivityForResult(intent2,1);
                break;
            case R.id.logbutton:
                Intent intent3 = new Intent(MainActivity.this, Log.class);
                startActivity(intent3);
                break;
            case  R.id.h2use_button:
                showHelpDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();//destroy timer when app is shut down
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if time limit is modified, refresh the data
        if(resultCode == 1){
            int newLimit = Integer.parseInt(data.getExtras().getString("Number"));
            Limit = newLimit;
            try {
                limitationSet.setText("Limitation (mins):");
                limitationSet.append("\t\t"+readData(this));
                Limit = Integer.parseInt(readData(this))*60;
            } catch (IOException e) {
                e.printStackTrace();
                limitationSet.append("not defined");
            }
        }
    }

    private String formatTime2(int seconds) {

        return String.format(" %02d:%02d", seconds / 60, seconds % 60);
    }
/*
*This is the handler for the timer
*Timer will be reset when screen is turned off
 */
    private Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @SuppressLint("HandlerLeak")
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
            if(!keyguardManager.isDeviceLocked()){
                s++;
                timeshow.setText(formatTime2(s));
                progressBar.setProgress(s*100/Limit);
            }else{
                s = 0;
            }

            if(s > Limit){
                showNotice();
                s = 0;
            }
        }

    };
    /*
    *show a notice to user to remind him
    * if sdk > 26 use notificationChannel
    * else use the old builder
     */
    private void showNotice(){
        //Intent intent = new Intent(MainActivity.this, MainActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,intent,0);

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= 26)
        {
            //when sdk > 26
            String id = "channel_1";
            String description = "143";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(id, description, importance);
            channel.enableLights(true);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(MainActivity.this, id)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Time to put down your phone")
                    .setContentText("and relax!")
                    .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .build();
            manager.notify(1, notification);
        }
        else
        {
            //when sdk < 26
            Notification notification = new NotificationCompat.Builder(MainActivity.this)
                    .setContentTitle("Time to put down your phone")
                    .setContentText("and relax")
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS)
                    .build();
            manager.notify(1,notification);
        }
    }
/*
*get setting from data.txt
 */
    private String readData(Context context) throws IOException {
        String data = "";
        try {
            InputStream inputStream = context.openFileInput("data.txt");
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            while ((line = reader.readLine())!=null){
                data = line;
            }
            reader.close();
            streamReader.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    public void saveDataToFile(Context context, String number){
        try {
            OutputStream stream = context.openFileOutput(
                    "data.txt",
                    Context.MODE_PRIVATE
            );
            OutputStreamWriter writer = new OutputStreamWriter(stream);

            writer.write(number);
            writer.flush();
            writer.close();
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    *Show help dialogue when user click "how to use"
     */
    private void showHelpDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.drawable.ic_launcher_foreground);
        normalDialog.setTitle("Help Message");
        normalDialog.setMessage("This is an APP to control your usage time." +
                "Please set a limit first." +
                "When limit time achieves, a notice will be shown." +
                "Please allow the APP for your usage data." +
                "for more info: email@xxx.fr");
        normalDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        normalDialog.setNegativeButton("CLOSE",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        normalDialog.show();
    }

}