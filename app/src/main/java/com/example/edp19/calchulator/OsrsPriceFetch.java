package com.example.edp19.calchulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

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

/**
 * Created by ask5430 on 4/19/18.
 */

public class OsrsPriceFetch extends AsyncTask<String, Void, JSONObject> {

    private Context context;
    private static HashMap<Integer, Integer> prices = new HashMap<>();

    public OsrsPriceFetch( Context context) {
        this.context = context;
    }

    public interface OnPricesReady{
        void onPricesReady();
    }

    OnPricesReady listner;

    public void setOnPricesReadyListner(OnPricesReady listner){
        this.listner = listner;
    }

    @Override
    protected JSONObject doInBackground(String... urls) {
        String url = urls[0];

        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        try {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("GOT A RESPONSE!!!!");
                    System.out.println(response.toString());

                    OsrsDB db = OsrsDB.getInstance(context);


                    Cursor c = db.getReadableDatabase().rawQuery("Select id from item", null);
                    try {
                        System.out.println("Finished gathering response");
                        final StringBuilder updatePrices = new StringBuilder();
                        updatePrices.append("update item set currentPrice = case\n");

                        while(c.moveToNext()) {
                            int id = c.getInt(0);
                            Integer price = ((JSONObject) response.get(String.valueOf(id))).getInt("overall_average");

                            if(price > 0 && price < 100000 && price != null) {
                                prices.put(id, price);
                            }


                            updatePrices.append("\twhen id = " + id + " then " + price + "\n");
                        }

                        updatePrices.append("\nend;");
                        Integer price = ((JSONObject) response.get(String.valueOf(Osrs.ID_NATURE_RUNE))).getInt("overall_average");

                        if(price == Osrs.ID_NATURE_RUNE){
                            Osrs.PRICE_NATURE_RUNE = price;
                        }

                        OsrsDB.getInstance(context).getWritableDatabase(new OsrsDB.OnDBReadyListener() {
                            @Override
                            public void onDBReady(SQLiteDatabase db) {
                                db.execSQL(updatePrices.toString());

                                //db.execSQL("update item set currentPrice = null where currentPrice = 0");

                                //mark time last updated for future runs..
                                Osrs.PRICES_LAST_UPDATED = Instant.now().toEpochMilli();
                                SharedPreferences sp = context.getSharedPreferences(Osrs.strings.PREFS_FILE, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putLong(Osrs.strings.PREFS_PRICE_UPDATE, Osrs.PRICES_LAST_UPDATED);

                                System.out.println("Finished updating the database..");
                                listner.onPricesReady();
                            }
                        });



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

        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        System.out.println("On post execute called.");
    }
}
