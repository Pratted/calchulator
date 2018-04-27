package com.example.edp19.calchulator;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import org.json.JSONObject;

/**
 * Created by eric on 4/19/18.
 */

public class OsrsNotificationService extends IntentService {

    //Need to declare a default constructor for IntelliJ
    public OsrsNotificationService() {
        super("DisplayNotification");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        System.out.println("Handling the intent in this service...");

        Osrs.initialize(this);
        OsrsDB.initialize(this);

        int id = intent.getIntExtra(Osrs.strings.KEY_ITEM, 0);
        boolean hasPriceUpdate = intent.getBooleanExtra(Osrs.strings.KEY_PRICES_LAST_UPDATED, false);
        String category = "Price update";
        String channelId;
        String message;
        String title;

        if(hasPriceUpdate) {
            try {
                JSONObject n = new OsrsPriceFetch(this).execute(getString(R.string.url_current_prices)).get();
            } catch (Exception e) {}

            title = "Price update Complete.";
            message = "The prices will update again in 4 hrs.";
            channelId = "Price Update";
        }
        else{
            category = "Item available";
            channelId = String.valueOf(id);
            OsrsItem item = OsrsDB.getItem(id);

            title = item.getName() + " is available again.";
            message = "The current market price is: " + item.getPrice();
            item.setHidden(false);
            OsrsDB.save(item);
        }

        displayNotification(category, channelId, title, message);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void displayNotification(String category, String id, String title, String message){
        //Notification Channel
        int importance = NotificationManager.IMPORTANCE_MAX;
        @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(id, category, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(notificationChannel);

        Intent next = new Intent(this, HomeActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , next,PendingIntent.FLAG_ONE_SHOT);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.high_alch);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.high_alch)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        notificationManager.notify(66, builder.build());
    }
}
