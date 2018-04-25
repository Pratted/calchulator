package com.example.edp19.calchulator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

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

    private AlertDialog.Builder builder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Osrs.initialize(this);
        OsrsDB.initialize(this);

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

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
                    blockItem();
                }
            });

            btnHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setHidden(true);
                    long t = new Date().getTime();
                    System.out.println("STARTING THE TIMER AT: " + t);
                    item.setTimerStartTime(t);
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

        OsrsDB.save(item);
        editor.putBoolean("DataModified", true);

        editor.commit();
    }

    private void blockItem(){
        builder.setTitle("Are you sure you want to block this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        item.setBlocked(true);
                        Toast.makeText(ItemActivity.this, item.getName() + " has been blocked", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //gotta overload override this. -> do nothing and close dialog.
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
