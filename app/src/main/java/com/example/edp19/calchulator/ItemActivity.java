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
        tvHighAlch = findViewById(R.id.tvHighAlch);
        ivItemImg = findViewById(R.id.ivItemImg);

        //load the item from the database. fuck it.


        Intent intent = getIntent();

        if(intent != null){
            System.out.println("UPDATING THE ITEMNAME FIELD!");

            OsrsItem item = intent.getParcelableExtra("item");

            tvItemName.setText(item.getName());
            Drawable drawable = getResources().getDrawable(getResources().getIdentifier( "p" + item.getId() , "drawable", getPackageName()));

            ivItemImg.setImageDrawable(drawable);
            ivItemImg.setBackgroundDrawable(null);
            tvCurrentPrice.setText(String.valueOf(item.getPrice()));
            tvHighAlch.setText(String.valueOf(item.getHighAlch()));
        }
        else{
            System.out.println("savedInstanceState is null!!");
        }

    }

    @Override
    protected void onStart(){
        super.onStart();

    }
}
