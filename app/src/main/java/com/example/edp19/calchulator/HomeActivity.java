package com.example.edp19.calchulator;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.time.Instant;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private OsrsSearchBar searchBar;
    private HashMap<Integer, OsrsItem> osrsItems;
    private OsrsTable table;
    private boolean updatedPrices;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("HOMEACTIVITY ONCREATE CALLED");
        super.onCreate(savedInstanceState);

        //initialize resources (strings, fonts, colors, etc)
        new Osrs(this);

        setContentView(R.layout.activity_home);

        osrsItems = new HashMap<>();
        updatedPrices = false;

        table = new OsrsTable(
                this,
                osrsItems,
                (TableRow) findViewById(R.id.headerRow),
                (TableLayout) findViewById(R.id.tlGridTable)
        );

        searchBar = new OsrsSearchBar(table, osrsItems,
                (TextView) findViewById(R.id.tvStatus),
                (TextView) findViewById(R.id.tvSearchLabel),
                (EditText) findViewById(R.id.tvSearch)
        );

        System.out.println("BEGIN FETCH PRICE UPDATE");

        //table.fetchPrices();
        System.out.println("PRICES UPDATED BITCHHHHHH");

//        if (table.needsPriceUpdate()) {
//            System.out.println("BEGIN FETCH PRICE UPDATE");
//            table.fetchPrices();
//            System.out.println("PRICES UPDATED BITCHHHHHH");
//        } else {
//            System.out.println("Prices already updated");
//        }
    }

    @Override
    public void onResume(){
        super.onResume();

        for(OsrsItem item: osrsItems.values()){
            item.setContext(this);
        }



        System.out.println("On resume called...");
    }

    @Override
    public void onPause(){
        super.onPause();

        System.out.println("ON PAUSE CALLED!!!");
        table.save();

        System.out.println("Saved table");
    }

    @Override
    public void onRestart(){
        super.onRestart();

        System.out.println("ONRESTARTED CALLED!!!");
    }

    @Override
    protected void onStart(){
        super.onStart();

        System.out.println("ON HOMEACTIVITY_ONSTART CALLED!!!!");
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);

        System.out.println("ON HOMEACTIVITY_SAVEINSTANCESTATE CALLED!!!!");
    }

    @Override
    public void onStop(){
        super.onStop();

        System.out.println("HOME ONSTOP CALLED!!!");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        System.out.println("DESTROYEDDDD!!!!");
    }



    public void onButtonSettingsClick(View v){
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        intent.putExtra("osrsItems", osrsItems);

        startActivity(intent);

        getCacheDir();
    }
}
