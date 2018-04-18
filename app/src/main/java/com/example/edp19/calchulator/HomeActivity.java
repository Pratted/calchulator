package com.example.edp19.calchulator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

    private HashMap<Integer, OsrsItem> osrsItems;
    private OsrsTable table;
    private boolean updatedPrices;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("HOMEACTIVITY ONCREATE CALLED");
        super.onCreate(savedInstanceState);

        //initialize resources (strings, fonts, colors, etc)
        new Osrs(this);;

        setContentView(R.layout.activity_home);

        osrsItems = new HashMap<>();
        updatedPrices = false;

        table = new OsrsTable(
                this,
                osrsItems,
                (TableRow) findViewById(R.id.headerRow),
                (TableLayout)findViewById(R.id.tlGridTable)
        );

        table.createSearchBar(
                (TextView) findViewById(R.id.tvStatus),
                (TextView) findViewById(R.id.tvSearchLabel),
                (EditText) findViewById(R.id.tvSearch)
        );


        if(table.needsPriceUpdate()){
            System.out.println("BEGIN FETCH PRICE UPDATE");
            new FetchCurrentPricesTask().execute(Osrs.strings.URL_CURRENT_PRICES);
        }
        else{
            System.out.println("Prices already updated");
        }
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
    }

    public void onButtonSearchClick(View v){
        System.out.println("Search button clicked!!");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");
    }

    private class FetchCurrentPricesTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            String url = urls[0];

            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            RequestQueue requestQueue = new RequestQueue(cache, network);
            requestQueue.start();

            try {
                System.out.println("OSRS ITEMS SIZE: " + osrsItems.size());

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("GOT A RESPONSE!!!!");
                        System.out.println(response.toString());
                        try {
                            System.out.println("Finished gathering response");


                            for(OsrsItem item: osrsItems.values()){
                                int price = ((JSONObject) response.get(String.valueOf(item.getId()))).getInt("overall_average");
                                item.setPrice(price);

                                if(item.getId().equals(OsrsItem.NATURE_RUNE)){
                                    OsrsItem.PRICE_NATURE_RUNE = price;
                                }
                            }

                            //mark time last updated for future runs..
                            Osrs.PRICES_LAST_UPDATED = Instant.now().toEpochMilli();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error response!!");
                        System.out.println(error.toString());
                    }
                });

                requestQueue.add(jsObjRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("The background task is about to end!!!");
            return new JSONObject();
        }
    }
}
