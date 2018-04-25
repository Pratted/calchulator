package com.example.edp19.calchulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {

    private TextView tvItemName;
    private TextView tvCurrentPrice;
    private TextView tvHighAlch;
    private TextView tvBuyLimit;
    private ImageView ivItemImg;
    private ImageButton ibFavorite;
    private TextView tvFavorite;

    private Button btnHide;
    private Button btnBlock;

    private OsrsItem item;
    private OsrsItem orignalItem;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Osrs.initialize(this);
        OsrsDB.initialize(this);

        prefs = this.getSharedPreferences(Osrs.strings.PREFS_FILE, Context.MODE_PRIVATE);
        editor = prefs.edit();

        System.out.println("onCreate called for ItemActivity!!!!");

        tvItemName = findViewById(R.id.tvItemName);
        tvCurrentPrice = findViewById(R.id.tvCurrentPrice);
        tvHighAlch = findViewById(R.id.tvHighAlch);
        ivItemImg = findViewById(R.id.ivItemImg);
        ibFavorite = findViewById(R.id.ibFavorite);
        tvFavorite = findViewById(R.id.tvFavorite);
        tvBuyLimit = findViewById(R.id.tvLimit);
        btnHide = findViewById(R.id.btnHide);
        btnBlock = findViewById(R.id.btnBlock);

        Intent intent = getIntent();

        if(intent != null) {
            System.out.println("UPDATING THE ITEMNAME FIELD!");

            final OsrsItem item = intent.getParcelableExtra("item");
            this.item = item;
        }

            orignalItem = new OsrsItem(item);

            tvItemName.setText(item.getName());
            Drawable drawable = getResources().getDrawable(getResources().getIdentifier( "p" + item.getId() , "drawable", getPackageName()));

            ivItemImg.setImageDrawable(drawable);
            ivItemImg.setBackgroundDrawable(null);
            tvCurrentPrice.setText(String.valueOf(item.getPrice()));

            tvBuyLimit.setText(item.getLimit() > 0 ? String.valueOf(item.getLimit()) : "N/A");
            tvHighAlch.setText(String.valueOf(item.getHighAlch()));

            ibFavorite.setImageResource(item.isFavorite ? android.R.drawable.star_big_on : android.R.drawable.star_big_off);
            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.isFavorite = !item.isFavorite;
                    ibFavorite.setImageResource(item.isFavorite ? android.R.drawable.star_big_on : android.R.drawable.star_big_off);
                    tvFavorite.setText(item.isFavorite ? "Remove from favorites" : "Add to favorites");
                    editor.apply();
                }
            });

            btnBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setBlocked(true);
                }
            });

            btnHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setHidden(true);
                }
            });

            tvFavorite.setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);

            String toDisplay = (item.isFavorite ? "Remove from favorites" : "Add to favorites");
            tvFavorite.setText(toDisplay);
    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    protected void onPause(){
        super.onPause();

        if(!orignalItem.equals(item)){
            OsrsDB.save(item);
            editor.putBoolean("DataModified", true);
        }


        editor.commit();
    }
}
