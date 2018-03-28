package com.example.edp19.calchulator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    private Typeface fontOsrsBold;
    private Typeface fontOsrs;
    private OsrsTable table;
    private boolean updatedPrices;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("HOMEACTIVITY ONCREATE CALLED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fontOsrs = Typeface.createFromAsset(getAssets(), "fonts/osrs.ttf");
        fontOsrsBold = Typeface.createFromAsset(getAssets(), "fonts/osrs_bold.ttf");

        OsrsItem.typeface = fontOsrs;

        osrsItems = new HashMap<>();
        updatedPrices = false;

        //initialize widgets on screen
        table = new OsrsTable(
                this,
                osrsItems,
                (TableRow) findViewById(R.id.headerRow),
                (TableLayout)findViewById(R.id.tlGridTable)
        );

        System.out.println("Summary: " + getString(R.string.summaryJson));


        OsrsDB.getInstance(this).getWritableDatabase(new OsrsDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                HomeActivity.this.db = db;

                Cursor c = db.rawQuery("Select count(*) from Item", null);

                if(c.moveToNext()){
                    System.out.println("Loaded " + c.getString(0) + " items");
                }

                loadOsrsItems();

                if(!updatedPrices){
                    new FetchCurrentPricesTask().execute(getString(R.string.summaryJson));
                    table.reformat(new boolean[]{true, true, true, false, false, false});
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
        OsrsItem.CONTEXT = this;

        while(c.moveToNext()){
            int id = c.getInt(0);
            String name = c.getString(1);
            int highAlch = c.getInt(2);
            int currentPrice = c.getInt(3);
            int buyLimit = c.getInt(4);
            boolean isMembers = c.getInt(5) == 1;
            final boolean isFavorite = c.getInt(6) == 1;

            final OsrsItem item = new OsrsItem(id, name, highAlch, currentPrice, buyLimit, isMembers, isFavorite);

            osrsItems.put(item.getId(), item);
            table.addItem(item);

            item.getTvName().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(((TextView) view).getText().toString() + " clicked!");
                    Intent intent = new Intent(HomeActivity.this, ItemActivity.class);

                    //intent.putExtra("osrsItem", osrsItems.get(item.getId()));

                    //startActivity(intent);
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

            //item.setColumnWeights(1,4,2,2,0,0);
        }

        c.close();
        System.out.println("Closed cursor!!");
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


                            for(Integer id: osrsItems.keySet()){
                                int price = ((JSONObject) response.get(String.valueOf(id))).getInt("overall_average");
                                osrsItems.get(id).setPrice(price);

                                if(id == OsrsItem.NATURE_RUNE){
                                    OsrsItem.PRICE_NATURE_RUNE = price;
                                }
                            }

                            table.sortColumn("Profit");
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
