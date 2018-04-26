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

    private TextView tvEquationAlch;
    private TextView tvEquationPrice;
    private TextView tvEquationNat;
    private TextView tvEquationProfit;
    private Button btnHide;

    private OsrsItem item;

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

        prefs = this.getSharedPreferences(Osrs.files.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = prefs.edit();

        System.out.println("onCreate called for ItemActivity!!!!");

        tvItemName = findViewById(R.id.tvItemName);
        tvCurrentPrice = findViewById(R.id.tvCurrentPrice);
        tvHighAlch = findViewById(R.id.tvHighAlch);
        ivItemImg = findViewById(R.id.ivItemImg);
        ibFavorite = findViewById(R.id.ibFavorite);
        tvFavorite = findViewById(R.id.tvFavorite);
        tvBuyLimit = findViewById(R.id.tvLimit);
        tvEquationAlch = findViewById(R.id.tvEquationHighAlch);
        tvEquationNat = findViewById(R.id.tvEquationNat);
        tvEquationPrice = findViewById(R.id.tvEquationPrice);
        tvEquationProfit = findViewById(R.id.tvEquationProfit);
        btnHide = findViewById(R.id.btnHide);

        Intent intent = getIntent();

        if(intent != null) {
            System.out.println("UPDATING THE ITEMNAME FIELD!");

            final OsrsItem item = intent.getParcelableExtra(Osrs.strings.KEY_ITEM);
            this.item = item;
        }

        tvItemName.setText(item.getName());
        tvCurrentPrice.setText(String.valueOf(item.getPrice()));
        tvBuyLimit.setText(item.getLimit() > 0 ? String.valueOf(item.getLimit()) : Osrs.strings.NA);
        tvHighAlch.setText(String.valueOf(item.getHighAlch()));
        tvEquationPrice.setText(String.valueOf(item.getPrice()));
        tvEquationAlch.setText(String.valueOf(item.getHighAlch()));
        tvEquationNat.setText(String.valueOf(Osrs.PRICE_NATURE_RUNE));
        tvEquationProfit.setText(String.valueOf(item.getProfit()));
        tvEquationProfit.setTextColor(item.getProfit() > 0 ? Osrs.colors.LIGHT_GREEN : Osrs.colors.RED);
        btnHide.setText(item.getHidden() ?  Osrs.strings.LABEL_BTN_UNHIDE : Osrs.strings.LABEL_BTN_HIDE);

        tvFavorite.setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);
        String toDisplay = (item.isFavorite ? Osrs.strings.LABEL_TV_REMOVE_FROM_FAVORITES : Osrs.strings.LABEL_TV_ADD_TO_FAVORITES);
        tvFavorite.setText(toDisplay);

        Drawable drawable = getResources().getDrawable(getResources().getIdentifier( "p" + item.getId() , "drawable", getPackageName()));

        ivItemImg.setImageDrawable(drawable);
        ivItemImg.setBackgroundDrawable(null);

        ibFavorite.setImageResource(item.isFavorite ? android.R.drawable.star_big_on : android.R.drawable.star_big_off);
    }


    @Override
    protected void onPause(){
        super.onPause();
        OsrsDB.save(item);

        //just assume data has been modified to make our life easier
        editor.putBoolean(Osrs.strings.KEY_HAS_DATA_BEEN_MODIFIED, true);
        editor.commit();
    }

    public void onBlockButtonClick(View view) {
        builder.setTitle(Osrs.strings.PROMPT_BLOCK_ITEM)
                .setPositiveButton(Osrs.strings.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        item.setBlocked(true);
                        Toast.makeText(ItemActivity.this, item.getName() + Osrs.strings.TOAST_BLOCK_ITEM, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(Osrs.strings.NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //gotta overload override this. -> do nothing and close dialog.
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onHideButtonClick(View view) {

        //item is not hidden, they clicked 'hide'
        if(!item.getHidden()){
            System.out.println("HIDE CLICKED");
            item.setTimerStartTime(new Date().getTime());
            Toast.makeText(this, item.getName() + Osrs.strings.TOAST_HIDE_ITEM, Toast.LENGTH_SHORT).show();
        }

        item.setHidden(!item.getHidden());
        btnHide.setText(item.getHidden() ?  Osrs.strings.LABEL_BTN_UNHIDE : Osrs.strings.LABEL_BTN_HIDE);
    }

    public void onFavoriteButtonClick(View view) {
        item.isFavorite = !item.isFavorite;
        ibFavorite.setImageResource(item.isFavorite ?
                android.R.drawable.star_big_on :
                android.R.drawable.star_big_off);

        tvFavorite.setText(item.isFavorite ?
                Osrs.strings.LABEL_TV_ADD_TO_FAVORITES :
                Osrs.strings.LABEL_TV_REMOVE_FROM_FAVORITES);
        editor.apply();
    }
}
