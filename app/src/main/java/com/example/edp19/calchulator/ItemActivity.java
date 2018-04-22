package com.example.edp19.calchulator;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {

    TextView tvItemName;
    TextView tvCurrentPrice;
    TextView tvHighAlch;
    TextView tvBuyLimit;
    ImageView ivItemImg;
    ImageButton ibFavorite;
    TextView tvFavorite;


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
        ibFavorite = findViewById(R.id.ibFavorite);
        tvFavorite = findViewById(R.id.tvFavorite);

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


            ibFavorite.setImageResource(item.isFavorite ? android.R.drawable.star_big_on : android.R.drawable.star_big_off);
            tvFavorite.setTextSize(12);

            String toDisplay = (item.isFavorite ? this.getString(R.string.items_page_msg_part_1) :
                                                this.getString(R.string.items_page_msg_part_2)) +
                                                item.getName() +
                                                this.getString(R.string.items_page_msg_part_3);
            tvFavorite.setText(toDisplay);
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
