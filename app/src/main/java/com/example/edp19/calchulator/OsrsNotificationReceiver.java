package com.example.edp19.calchulator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by eric on 4/19/18.
 */

public class OsrsNotificationReceiver extends WakefulBroadcastReceiver {


    // table.setSomeAlarm -> (wait n seconds) -> onReceive -> StartsService -> onHandleIntent

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Action: " + intent.getAction());

        System.out.println("Osrs Notification Received!!!!");

        int item = intent.getIntExtra("item", 0);
        boolean priceUpdate = intent.getBooleanExtra(Osrs.strings.KEY_PRICES_LAST_UPDATED, false);

        Intent outgoing = new Intent(context, OsrsNotificationService.class);

        if(priceUpdate) {
            System.out.println("We are going to update the prices...");

            outgoing.putExtra(Osrs.strings.KEY_PRICES_LAST_UPDATED, true);
            context.startService(outgoing);
        } else {
            System.out.println("Recieved a " + item);

            outgoing.putExtra(Osrs.strings.KEY_ITEM, item);
            context.startService(outgoing);
        }
    }

    public void setAlarm(Context context, int id){
        context.registerReceiver( this, new IntentFilter("com.example.edp19.calchulator.OsrsNotificationService"));

        Intent i = new Intent("com.example.edp19.calchulator.OsrsNotificationService");
        i.putExtra(Osrs.strings.KEY_ITEM, id);

        PendingIntent pintent = PendingIntent.getBroadcast( context, id, i, 0);
        AlarmManager manager = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));

        manager.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + Osrs.DEFAULT_TIMER, pintent);
    }

    public void setPriceUpdateAlarm(Context context, int seconds) {
        context.registerReceiver( this, new IntentFilter("com.example.edp19.calchulator.OsrsNotificationService"));

        Intent i = new Intent("com.example.edp19.calchulator.OsrsNotificationService");
        i.putExtra(Osrs.strings.KEY_PRICES_LAST_UPDATED, true);

        PendingIntent pintent = PendingIntent.getBroadcast( context, 1, i, 0);
        AlarmManager manager = (AlarmManager)(context.getSystemService(Context.ALARM_SERVICE));

        System.out.println("PRICE UPDATE ALARM SET");

        manager.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + Osrs.DEFAULT_TIMER, pintent);
    }
}
