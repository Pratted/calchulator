package com.example.edp19.calchulator;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HomeActivity extends AppCompatActivity {
    private SQLiteDatabase db;

    private HashMap<Integer, OsrsItem> osrsItems;
    private Typeface typeface;
    private OsrsTable table;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/osrs.ttf");

        osrsItems = new HashMap<>();

        //initialize widgets on screen
        table = new OsrsTable(
                (TableRow) findViewById(R.id.headerRow),
                (TableLayout)findViewById(R.id.tlGridTable)
        );
        
        System.out.println("Summary: " + getString(R.string.summaryJson));
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
                new FetchCurrentPricesTask().execute(getString(R.string.summaryJson));
            }
        });


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

            item.setColumnWeights(1,4,2,3,0,0);
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

    public class OsrsTable {
        private TableLayout table;
        private TableRow header;
        private Context context;

        private ImageButton ibFavorite;
        private TextView tvItem;
        private TextView tvHighAlch;
        private TextView tvPrice;
        private TextView tvProfit;
        private TextView tvLimit;

        final private int HEADER_TEXT_SIZE = 18;

        private View headers[] = new View[6];
        private HashMap<String, Boolean> sortedBy = new HashMap<>();

        public OsrsTable(TableRow header, TableLayout table){
            this.table = table;
            this.header = header;
            context = HomeActivity.this;

            headers[0] = ibFavorite = createFavoriteHeader("Favorite");
            headers[1] = tvItem = createTextView("Item");
            headers[2] = tvHighAlch = createTextView("Alch");
            headers[3] = tvPrice = createTextView("Price");
            headers[4] = tvProfit = createTextView("Profit");
            headers[5] = tvLimit = createTextView("Limit");

            //initialize onClickListeners for all headers.
            for(View v: headers){
                final String text = (String) v.getTag();

                v.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        //header text is stored in tag
                        System.out.println("Sorting by " + (String) (v.getTag()));
                        sortColumn(text);
                    }
                });

                sortedBy.put(text, false);
            }

            ibFavorite.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tvItem.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
            tvHighAlch.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
            tvPrice.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
            tvProfit.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0f));
            tvLimit.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0f));

            tvProfit.setVisibility(View.GONE);
            tvLimit.setVisibility(View.GONE);

            header.addView(ibFavorite);
            header.addView(tvItem);
            header.addView(tvHighAlch);
            header.addView(tvPrice);
            header.addView(tvProfit);
            header.addView(tvLimit);
        }

        private TextView createTextView(String text){
            TextView tv = new TextView(context);

            tv.setTextColor(getResources().getColor(R.color.osrsOrange));
            tv.setTypeface(typeface);
            tv.setTextSize(HEADER_TEXT_SIZE);
            tv.setText(text);
            tv.setTag(text);

            return tv;
        }

        private View getHeader(String header){
            if(header.compareTo("Item") == 0) return tvItem;
            if(header.compareTo("Alch") == 0) return tvHighAlch;
            if(header.compareTo("Price") == 0) return tvPrice;
            if(header.compareTo("Profit") == 0) return tvProfit;
            if(header.compareTo("Limit") == 0) return tvLimit;
            if(header.compareTo("Favorite") == 0) return ibFavorite;

            return null;
        }

        private ImageButton createFavoriteHeader(String tag){
            ImageButton ib = new ImageButton(context);

            ib.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            ib.setImageResource(android.R.drawable.star_off);
            ib.setPadding(0,-5,0,0);
            ib.setBackgroundDrawable(null);
            ib.setTag(tag);

            return ib;
        }

        public void sortColumn(String columnName){
            Map<Integer, Integer> mInt = new TreeMap<>();
            Map<String, Integer> mStr = new TreeMap<>();

            boolean sorted = sortedBy.get(columnName);

            //sort descending if already sorted (ascending)
            if(sorted){
                mInt = new TreeMap<>(Collections.<Integer>reverseOrder());
                mStr = new TreeMap<>(Collections.<String>reverseOrder());
            }

            //clear previous sort criteria
            for(String s: sortedBy.keySet()){
                sortedBy.put(s, false);
            }

            //iterate through table to preserve existing sorts as much as possible.
            for(int i = 0; i < table.getChildCount(); i++){
                int id = table.getChildAt(i).getId();

                if(columnName.compareTo("Item") == 0)
                    mStr.put(osrsItems.get(id).getString(columnName), id);
                else
                    mInt.put(osrsItems.get(id).getInt(columnName), id);
            }

            //clear all rows and put the items back into the table.
            table.removeAllViewsInLayout();

            for(Integer id: mInt.keySet()){
                this.addItem(osrsItems.get(mInt.get(id)));
            }

            for(String id: mStr.keySet()){
                this.addItem(osrsItems.get(mStr.get(id)));
            }

            //mark inverse of what we started with.
            sortedBy.put(columnName, !sorted);
        }

        public void hideColumn(String columnName){
            View v = getHeader(columnName);
            v.setVisibility(View.GONE);
        }

        public void showColumn(String columnName){
            View v = getHeader(columnName);
            v.setVisibility(View.VISIBLE);
        }

        public void addItem(OsrsItem item){
            table.addView(item.getTableRow());
        }

    }
}
