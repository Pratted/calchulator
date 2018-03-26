package com.example.edp19.calchulator;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private SQLiteDatabase db;

    private TableLayout table;
    private TableRow headerRow;
    private HashMap<Integer, OsrsItem> osrsItems;
    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/osrs.ttf");

        osrsItems = new HashMap<>();

        //initialize widgets on screen
        table = findViewById(R.id.tlGridTable);
        headerRow = findViewById(R.id.headerRow);

        System.out.println("Summary: " + getString(R.string.summaryJson));

        //addTableHeaders();
    }

    @Override
    public void onResume(){
        super.onResume();

        OsrsDB.getInstance(this).getWritableDatabase(new OsrsDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                HomeActivity.this.db = db;

                Cursor c = db.rawQuery("Select count(*) from Item", null);

                if(c.moveToNext()){
                    System.out.println("Loaded " + c.getString(0) + " items");
                }

                loadOsrsItems();
            }
        });



        //new FetchCurrentPricesTask().execute(getString(R.string.summaryJson));
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
        table.removeAllViews();

        Cursor c = db.rawQuery("select * from Item", null);

        OsrsItem.CONTEXT = this;

        while(c.moveToNext()){
            int id = c.getInt(0);
            String name = c.getString(1);
            int highAlch = c.getInt(2);
            int currentPrice = c.getInt(3);
            int buyLimit = c.getInt(4);
            boolean isMembers = c.getInt(5) == 1;
            boolean isFavorite = c.getInt(6) == 1;

            System.out.println("About to create item..");
            final OsrsItem item = new OsrsItem(id, name, highAlch, currentPrice, buyLimit, isMembers, isFavorite);

            osrsItems.put(item.getId(), item);

            if(item.getHighAlch() >= 200){
                table.addView(item.getTableRow());
                //addItemToTable(item.getId());
            }

            item.getTvName().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(((TextView) view).getText().toString() + " clicked!");
                    Intent intent = new Intent(HomeActivity.this, ItemActivity.class);

                    intent.putExtra("osrsItem", osrsItems.get(item.getId()));

                    startActivity(intent);
                }
            });
        }
        System.out.println("Finished loading items!");
        //sortItemName();
        //sortItemName();

        c.close();
        System.out.println("Closed cursor!!");
    }


    /*
    private void addItemToTable(final int id){
        TableRow tr = new TableRow(this);

        TextView tvName = new TextView(this);
        TextView tvBuy = new TextView(this);
        TextView tvLimit = new TextView(this);

        tvName.setTypeface(typeface);
        tvBuy.setTypeface(typeface);
        tvLimit.setTypeface(typeface);

        tvName.setTextSize(18);

        tvName.setTextColor(Color.WHITE);
        tvBuy.setTextColor(Color.WHITE);
        tvLimit.setTextColor(Color.WHITE);

        tvName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
        tvBuy.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        tvLimit.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

        tvName.setText(osrsItems.get(id).name);
        tvBuy.setText(String.valueOf(osrsItems.get(id).highAlch));
        tvLimit.setText(String.valueOf(osrsItems.get(id).limit).compareTo("0") == 0 ? "N/A" : String.valueOf(osrsItems.get(id).limit));

        final ImageButton ibFavorite = new ImageButton(this);

        ibFavorite.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

        if(osrsItems.get(id).isFavorite){
            ibFavorite.setImageResource(android.R.drawable.star_on);
            ibFavorite.setId((int)android.R.drawable.star_on);
        }
        else{
            ibFavorite.setImageResource(android.R.drawable.star_off);
            ibFavorite.setId((int)android.R.drawable.star_off);
        }

        tr.addView(ibFavorite);
        tr.addView(tvName);
        tr.addView(tvBuy);
        tr.addView(tvLimit);
        tr.setId(id);

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(((TextView) view).getText().toString() + " clicked!");
                Intent intent = new Intent(HomeActivity.this, ItemActivity.class);

                intent.putExtra("osrsItem", osrsItems.get(id));

                startActivity(intent);
            }
        });


        ibFavorite.setPadding(0,-8,0,0);

        ibFavorite.setBackgroundDrawable(null);
        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cv = new ContentValues();

                if(ibFavorite.getId() == (int) android.R.drawable.star_off){
                    cv.put("isFavorite", 1);
                    System.out.println("TURNING ON!!!!");
                    ibFavorite.setImageResource(android.R.drawable.star_on);
                    ibFavorite.setId((int) android.R.drawable.star_on);

                    Toast.makeText(HomeActivity.this, osrsItems.get(id).name + " added to favorites", Toast.LENGTH_SHORT).show();
                    osrsItems.get(id).isFavorite = true;

                    db.update("Item", cv, "id = ?", new String[]{String.valueOf(id)});
                }
                else{
                    cv.put("isFavorite", 0);
                    System.out.println("TURNING OFF!!!!");
                    ibFavorite.setImageResource(android.R.drawable.star_off);
                    ibFavorite.setId((int) android.R.drawable.star_off);
                    Toast.makeText(HomeActivity.this, osrsItems.get(id).name + " removed from favorites", Toast.LENGTH_SHORT).show();
                    osrsItems.get(id).isFavorite = false;

                    db.update("Item", cv, "id = ?", new String[]{String.valueOf(id)});
                }
            }
        });

        table.addView(tr);
    }
    */

    public void onButtonSettingsClick(View v){
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        intent.putExtra("osrsItems", osrsItems);

        startActivity(intent);
    }

    /*
    public void sortItemName(){
        ArrayList<Pair<String, OsrsItem>> strArr = new ArrayList<> ();

        for(int i = 0; i < table.getChildCount(); i++){
            int itemId = table.getChildAt(i).getId();

            OsrsItem item = osrsItems.get(itemId);
            strArr.add(new Pair(item.name, osrsItems.get(itemId)));
        }

        if(strArr.get(0).first.compareTo(strArr.get(strArr.size()-1).first) < 0 ){
            Collections.sort(strArr, new Comparator<Pair<String, OsrsItem>>() {
                @Override
                public int compare(final Pair<String, OsrsItem> o1, final Pair<String, OsrsItem> o2) {
                    return o2.first.compareTo(o1.first);
                }
            });
        }
        else{
            Collections.sort(strArr, new Comparator<Pair<String, OsrsItem>>() {
                @Override
                public int compare(final Pair<String, OsrsItem> o1, final Pair<String, OsrsItem> o2) {
                    return o1.first.compareTo(o2.first);
                }
            });
        }

        table.removeAllViewsInLayout();

        for(int i = 0; i < strArr.size(); i++){
            addItemToTable(strArr.get(i).second.id);
        }
    }

    public void sortFavorite(){
        ArrayList<Pair<Integer, OsrsItem>> intArr = new ArrayList<> ();

        for(int i = 0; i < table.getChildCount(); i++){
            int itemId = table.getChildAt(i).getId();

            OsrsItem item = osrsItems.get(itemId);

            if(item.isFavorite)
                intArr.add(new Pair(item.isFavorite ? 1 : 0, osrsItems.get(itemId)));
        }

        if(intArr.get(0).first.compareTo(intArr.get(intArr.size()-1).first) > 0 ){
            Collections.sort(intArr, new Comparator<Pair<Integer, OsrsItem>>() {
                @Override
                public int compare(final Pair<Integer, OsrsItem> o1, final Pair<Integer, OsrsItem> o2) {
                    return o1.first.compareTo(o2.first);
                }
            });
        }
        else{
            Collections.sort(intArr, new Comparator<Pair<Integer, OsrsItem>>() {
                @Override
                public int compare(final Pair<Integer, OsrsItem> o1, final Pair<Integer, OsrsItem> o2) {
                    return o2.first.compareTo(o1.first);
                }
            });
        }

        table.removeAllViewsInLayout();

        for(int i = 0; i < intArr.size(); i++){
            addItemToTable(intArr.get(i).second.id);
        }
    }

    public void sortByHeader(String header) {
        ArrayList<Pair<Integer, OsrsItem>> intArr = new ArrayList<> ();

        for(int i = 0; i < table.getChildCount(); i++){
            int itemId = table.getChildAt(i).getId();

            OsrsItem item = osrsItems.get(itemId);

            if(header.compareTo("Buy") == 0)
                intArr.add(new Pair(item.highAlch, osrsItems.get(itemId)));
            if(header.compareTo("High Alch") == 0)
                intArr.add(new Pair(item.limit, osrsItems.get(itemId)));
        }

        if(intArr.get(0).first.compareTo(intArr.get(intArr.size()-1).first) > 0 ){
            Collections.sort(intArr, new Comparator<Pair<Integer, OsrsItem>>() {
                @Override
                public int compare(final Pair<Integer, OsrsItem> o1, final Pair<Integer, OsrsItem> o2) {
                    return o1.first.compareTo(o2.first);
                }
            });
        }
        else{
            Collections.sort(intArr, new Comparator<Pair<Integer, OsrsItem>>() {
                @Override
                public int compare(final Pair<Integer, OsrsItem> o1, final Pair<Integer, OsrsItem> o2) {
                    return o2.first.compareTo(o1.first);
                }
            });
        }

        table.removeAllViewsInLayout();

        for(int i = 0; i < intArr.size(); i++){
            addItemToTable(intArr.get(i).second.id);
        }
    }
    */

    @SuppressLint("ResourceAsColor")
    public void addTableHeaders(){

        TextView tvItem = new TextView(this);
        TextView tvBuy = new TextView(this);
        TextView tvHighAlch = new TextView(this);

        tvItem.setTextColor(getResources().getColor(R.color.osrsOrange));
        tvItem.setTypeface(typeface);
        tvItem.setTextSize(18);

        tvBuy.setTextColor(getResources().getColor(R.color.osrsOrange));
        tvBuy.setTypeface(typeface);
        tvBuy.setTextSize(18);

        tvHighAlch.setTextColor(getResources().getColor(R.color.osrsOrange));
        tvHighAlch.setTypeface(typeface);
        tvHighAlch.setTextSize(18);

        final ImageButton ibFavorite = new ImageButton(this);

        ibFavorite.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        ibFavorite.setImageResource(android.R.drawable.star_off);
        //ibFavorite.setImageResource(R.drawable.high_alch);
        ibFavorite.setId((int)android.R.drawable.star_off);
        ibFavorite.setPadding(0,-5,0,0);
        ibFavorite.setBackgroundDrawable(null);

        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ibFavorite.getId() == (int) android.R.drawable.star_off){
                    System.out.println("TURNING ON!!!!");
                    ibFavorite.setImageResource(android.R.drawable.star_on);
                    ibFavorite.setId((int) android.R.drawable.star_on);
                }
                else{
                    System.out.println("TURNING OFF!!!!");
                    ibFavorite.setImageResource(android.R.drawable.star_off);
                    ibFavorite.setId((int) android.R.drawable.star_off);
                }
            }
        });
        ibFavorite.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tvItem.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
        tvBuy.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        tvHighAlch.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ortFavorite();
            }
        });

        tvItem.setText("Item");
        tvItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //sortItemName();
            }
        });

        tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sortByHeader("Buy");
            }
        });

        tvHighAlch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sortByHeader("High Alch");
            }
        });

        tvBuy.setText("Buy");
        tvHighAlch.setText("High Alch");

        headerRow.addView(ibFavorite);
        headerRow.addView(tvItem);
        headerRow.addView(tvBuy);
        headerRow.addView(tvHighAlch);
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

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("GOT A RESPONSE!!!!");
                        System.out.println(response.toString());
                        try {
                            System.out.println("Finished gathering response");

                            for(Integer id: osrsItems.keySet()){
                                //osrsItems.get(id). = ((JSONObject) response.get(String.valueOf(id))).getInt("overall_average");
                            }


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
