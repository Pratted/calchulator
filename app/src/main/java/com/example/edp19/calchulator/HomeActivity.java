package com.example.edp19.calchulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
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

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private SQLiteDatabase db;

    private HashMap<Integer, OsrsItem> osrsItems;
    private OsrsTable table;
    private boolean updatedPrices;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

        System.out.println("Summary: " + Osrs.strings.URL_CURRENT_PRICES);


        OsrsDB.getInstance(this).getWritableDatabase(new OsrsDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                HomeActivity.this.db = db;

                loadOsrsItems();

                if(!updatedPrices){
                    new FetchCurrentPricesTask().execute(Osrs.strings.URL_CURRENT_PRICES);
                    table.reformat(OsrsTable.LAYOUT_DEFAULT);
                }

            }
        });


    }


    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();

        System.out.println("ON PAUSE CALLED!!!");
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



    public void loadOsrsItems(){
        osrsItems.clear();

        Cursor c = db.rawQuery("select * from Item", null);

        while(c.moveToNext()){
            int id = c.getInt(0);
            String name = c.getString(1);
            int highAlch = c.getInt(2);
            int currentPrice = c.getInt(3);
            int buyLimit = c.getInt(4);
            boolean isMembers = c.getInt(5) == 1;
            final boolean isFavorite = c.getInt(6) == 1;

            final OsrsItem item = new OsrsItem(id, name, highAlch, currentPrice, buyLimit, isMembers, isFavorite);
            item.setContext(this);

            osrsItems.put(item.getId(), item);
            table.addItem(item);

            item.getTvName().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(((TextView) view).getText().toString() + " clicked!");
                    Intent intent = new Intent(HomeActivity.this, ItemActivity.class);

                    intent.putExtra("osrsItem", item);

                    startActivity(intent);
                }
            });

            item.getIbFavorite().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.toggleFavorite();

                    db.execSQL("Update Item set isFavorite = " + (item.getFavorite() ? "1" : "0") + " where id = " + item.getId());
                    Toast.makeText(HomeActivity.this,
                            item.getName() + (item.getFavorite() ? " added" : " removed") + " to favorites",
                            Toast.LENGTH_SHORT).show();
                }
            });

            item.getTvName().setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View view) {
                    LayoutInflater inflater = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    PopupWindow pw = new PopupWindow(
                            inflater.inflate(R.layout.popup_header, null, false),
                            100,
                            100,
                            true);
                    // The code below assumes that the root container has an id called 'main'
                    pw.showAtLocation(item.getTvName(), Gravity.CENTER, 0, 0);
                    return false;
                }

                private void getSystemService(String layoutInflaterService) {
                }
            });
        }

        SharedPreferences prefs = getSharedPreferences("Hello", MODE_PRIVATE);
        String restoredText = prefs.getString("name", "failed to find name");

        System.out.println("Loaded: " + restoredText);

        c.close();
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

                            table.sortColumn(Osrs.strings.NAME_PROFIT_COLUMN);
                            updatedPrices = true;

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

        @Override
        protected void onPostExecute(JSONObject json) {

        }
    }
}
