package com.example.edp19.calchulator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Date;

/**
 * Created by eric on 4/19/18.
 */

public class OsrsNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Action: " + intent.getAction());

        System.out.println("Osrs Notification Received!!!!");

        int item = intent.getIntExtra("item", 0);
        
        System.out.println("Recieved a " + item);

        Intent outgoing = new Intent(context, OsrsNotificationService.class);
        outgoing.putExtra("item", item);

        context.startService(outgoing);
    }

    public void setAlarm(Context context, int id, int seconds){
        context.registerReceiver( this, new IntentFilter("com.example.edp19.calchulator.OsrsNotificationService"));

        Intent i = new Intent("com.example.edp19.calchulator.OsrsNotificationService");
        i.putExtra("item", id);

        PendingIntent pintent = PendingIntent.getBroadcast( context, id, i, 0);
        AlarmManager manager = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));

        manager.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000* seconds, pintent);
    }
}
