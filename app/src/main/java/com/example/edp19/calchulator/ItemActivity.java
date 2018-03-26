package com.example.edp19.calchulator;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
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

import java.io.InputStream;
import java.net.URL;

public class ItemActivity extends AppCompatActivity {

    TextView tvItemName;
    TextView tvCurrentPrice;
    TextView tvHighAlch;
    TextView tvBuyLimit;
    ImageView ivItemImg;


    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        if(Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        System.out.println("onCreate called for ItemActivity!!!!");

        tvItemName = findViewById(R.id.tvItemName);
        tvCurrentPrice = findViewById(R.id.tvCurrentPrice);
        ivItemImg = findViewById(R.id.ivItemImg);

        Intent intent = getIntent();

        if(intent != null){
            System.out.println("UPDATING THE ITEMNAME FIELD!");

            OsrsItem item = (OsrsItem) intent.getParcelableExtra("osrsItem");

            tvItemName.setText(item.getName());
            Drawable drawable = getResources().getDrawable(getResources().getIdentifier( "p" + item.getId() , "drawable", getPackageName()));

            ivItemImg.setImageDrawable(drawable);

            //new FetchCurrentPricesTask().execute("https://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + String.valueOf(item.getId()));
        }
        else{
            System.out.println("savedInstanceState is null!!");
        }

    }


    public static Drawable loadImageFromWeb(String url){
        try{
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    private class FetchCurrentPricesTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            String url = urls[0];

            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();

            try{

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("GOT A RESPONSE!!!!");
                        System.out.println(response.toString());
                        try{
                            tvCurrentPrice.setText("");
                            tvCurrentPrice.setText("Current Price: " + String.valueOf(response.get("overall")));

                            System.out.println("Finished gathering response");
                        } catch (Exception e){
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
            } catch (Exception e){
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
