package com.example.acremote;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private String log_tag = "ir";
    private int status = 0;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Button b1, cool, fan;
    TextView info;
    long interval_in_min=15;
    Handler handler=new Handler();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(log_tag, "Main activity launched");

        settings = getApplicationContext().getSharedPreferences("com.example.acremote_preferences", 0);
        status = settings.getInt("status", 0);

        ConsumerIrManager irmanager = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
        boolean has_ir = irmanager.hasIrEmitter();

        if (!has_ir) {
            status = 2;
        }

        b1 = (Button) findViewById(R.id.b1);
        if (status==0) {
            b1.setText("start");
        } else if (status == 1){
            b1.setText("stop");
        } else {
            b1.setText("NO IR SENSOR");
        }

        info = (TextView) findViewById(R.id.info);
    }

    /*
    "Power On"			: "346,173,28,60,29,16,28,16,28,60,28,16,28,17,28,60,29,16,28,16,28,16,29,16,28,60,29,16,28,16,29,16,29,16,28,16,29,16,28,16,28,16,29,16,28,16,28,16,29,16,28,16,29,16,29,16,28,16,29,60,28,16,29,60,28,16,29,16,28,60,29,16,28,5000",
    "Power Off"			: "346,173,29,60,28,17,28,16,28,16,29,16,29,16,28,60,28,16,28,16,29,16,28,16,28,60,28,16,28,17,28,16,28,16,28,17,28,16,28,16,29,16,29,16,28,16,29,16,28,16,28,17,28,16,28,16,28,17,28,60,29,16,28,60,29,16,28,16,28,60,28,16,29,5000",
    "Cool"			    : "38000,346,173,28,60,29,16,28,16,28,60,28,16,28,17,28,60,29,16,28,16,28,16,29,16,28,60,29,16,28,16,29,16,29,16,28,16,29,16,28,16,28,16,29,16,28,16,28,16,29,16,28,16,29,16,29,16,28,16,29,60,28,16,29,60,28,16,29,16,28,60,29,16,28,5000",
    "Dry"			    : "38000,346,173,29,16,29,60,30,16,29,60,30,60,29,16,29,60,29,16,29,16,29,16,29,16,29,60,29,16,29,16,29,16,28,16,29,16,29,16,29,16,29,16,29,16,29,16,29,16,28,16,29,16,29,16,28,16,29,16,29,60,29,16,29,60,29,16,29,16,29,60,30,16,29,5000",
    "Fan"  		        : "38000,346,173,30,60,30,60,29,16,30,60,29,60,30,16,29,60,29,16,29,16,29,16,29,16,29,60,29,16,30,16,29,16,29,16,30,16,29,16,29,16,29,16,29,16,29,16,29,16,29,16,29,16,30,16,29,16,30,16,29,60,30,16,29,60,30,16,29,16,29,60,29,16,30,5000",
    "Heat"              : "38000,346,173,29,16,28,16,29,60,29,60,29,60,29,16,29,60,29,16,29,16,28,16,29,16,29,60,29,16,29,16,29,16,29,16,29,16,29,16,29,16,28,16,29,16,29,16,28,16,29,16,29,16,29,16,29,16,29,16,28,60,28,16,29,60,29,16,29,16,29,60,29,16,28,5000",
    "Auto"              : "38000,346,173,29,16,28,16,29,16,29,60,29,60,29,16,29,60,29,16,29,16,30,16,29,16,29,16,28,16,29,16,29,16,29,16,29,16,29,16,29,16,29,16,30,16,29,16,29,16,28,16,29,16,29,16,29,16,29,16,29,60,29,16,28,60,29,16,28,16,29,60,29,16,29,5000",
    "Fan Auto"          : "38000,346,173,29,60,28,17,28,16,28,60,28,16,28,17,28,60,29,16,29,16,28,16,28,17,28,60,28,16,28,16,28,17,28,16,28,16,29,16,28,16,28,16,29,16,28,16,28,17,28,16,28,16,28,17,28,16,28,17,28,60,29,16,28,60,29,16,28,16,28,60,28,16,28,5000",
    "Fan High"          : "38000,346,173",
    "Fan Medium"		: "38000,346,173,28,60,28,16,28,17,28,60,29,16,28,60,29,60,28,16,29,16,28,16,28,17,28,60,28,16,28,16,29,16,28,16,28,16,29,16,28,16,28,16,28,17,28,16,28,17,28,16,28,16,28,17,28,16,28,17,28,60,28,17,28,60,28,17,28,16,28,60,28,16,28,5000",
    "Fan Low"			: "346,173,28,60,28,16,28,16,29,60,28,60,28,16,29,60,28,16,28,17,28,16,28,17,28,60,29,16,28,16,28,16,29,16,28,16,28,16,29,16,28,16,28,17,28,16,28,16,29,16,28,16,28,17,28,16,28,16,28,60,28,16,28,60,28,17,28,16,28,60,28,16,28,5000",
    */

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/ //Removed yesterday

    public void onB1Clicked(View v) {

        if (status == 0) {
            Log.e(log_tag,"start button clicked");
            AlarmManager alarmManager=(AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, RemoteReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval_in_min * 60 * 1000,
                    pendingIntent);
            Log.e(log_tag, "set repeating alarm");
            Log.e(log_tag, "interval= " + interval_in_min);


            intent = new Intent(this, StartStopReceiver.class);
            intent.putExtra("type", false);
            pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
            Calendar now = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 6);
            calendar.set(Calendar.MINUTE, 30);
            long alarmMillis = calendar.getTimeInMillis();
            if (calendar.before(now)) alarmMillis+= 86400000L;
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmMillis, pendingIntent);

            status = 1;
            editor = settings.edit();
            editor.putInt("status", status);
            editor.apply();

            b1.setText("stop");

        } else if (status == 1) {
            Log.e(log_tag,"stop button clicked");
            AlarmManager alarmManager=(AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, RemoteReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            alarmManager.cancel(pendingIntent);
            Log.e(log_tag, "alarm cancelled");

            status = 0;
            editor = settings.edit();
            editor.putInt("status", status);
            editor.apply();

            b1.setText("start");
        } else if (status == 2) {

        }

    }

}

