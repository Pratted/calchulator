package com.example.edp19.calchulator;

import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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


        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        if(intent != null){
            System.out.println("UPDATING THE ITEMNAME FIELD!");

            OsrsItem item = (OsrsItem) intent.getParcelableExtra("osrsItem");

            tvItemName.setText(item.name);

            try{
                //String url = "https://rsbuddy.com/exchange/summary.json";
                String url = "https://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + String.valueOf(item.id);


                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("GOT A RESPONSE!!!!");
                        System.out.println(response.toString());
                        try{
                            tvCurrentPrice.setText("");
                            tvCurrentPrice.setText("Current Price: " + String.valueOf(response.get("overall")));


                        } catch (Exception e){
                            System.out.println("fuck");
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error response!!");
                        System.out.println(error.toString());
                        //
                    }
                });

                requestQueue.add(jsObjRequest);
                ivItemImg.setImageDrawable(loadImageFromWeb("https://services.runescape.com/m=itemdb_oldschool/obj_big.gif?id=" + String.valueOf(item.id)));

            } catch (Exception e){
                System.out.println("Something went wrong lol!");
                System.out.println(e.toString());
                e.printStackTrace();
            }
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

}
