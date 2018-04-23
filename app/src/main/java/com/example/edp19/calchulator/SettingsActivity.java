package com.example.edp19.calchulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity  {
    Button btnHiddenItems;
    Button btnBlockedItems;
    EditText etPriceNat;
    EditText etMinProfit;
    Switch swHideMemsItems;
    HashMap<Integer, OsrsItem> osrsItems;
    SQLiteDatabase db;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        osrsItems = OsrsDB.fetchAllItems(this);
        db = OsrsDB.getInstance(this).getReadableDatabase();
        btnHiddenItems = findViewById(R.id.btnHiddenItems);
        btnBlockedItems = findViewById(R.id.btnBlockedItems);
        prefs = this.getSharedPreferences(Osrs.strings.PREFS_FILE, Context.MODE_PRIVATE);
        editor = prefs.edit();

        findViewById(R.id.btnRemoveFavs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllFavorites();
            }
        });
        findViewById(R.id.btnRestoreDefaults).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllFavorites();
                restoreToDefault();
            }
        });

        etPriceNat = findViewById(R.id.etPriceNat);
        etPriceNat.setHint(String.valueOf(Osrs.PRICE_NATURE_RUNE));
        etPriceNat.setText(String.valueOf(Osrs.PRICE_NATURE_RUNE));
        etMinProfit = findViewById(R.id.etMinProfit);
        etMinProfit.setText(String.valueOf(prefs.getInt(Osrs.strings.PREF_MIN_PROFIT, 0)));
        swHideMemsItems = findViewById(R.id.switch_hide_mems);
        swHideMemsItems.setChecked(prefs.getBoolean(Osrs.strings.SWITCH_HIDE_MEMS_ITEMS, false));
    }

    @Override
    public void onResume(){
        super.onResume();
        etPriceNat.setHint(String.valueOf(Osrs.PRICE_NATURE_RUNE));
    }

    private void removeAllFavorites() {
        for (Integer id : osrsItems.keySet()) {
            osrsItems.get(id).setFavorite(false);
        }
        db.execSQL("update Item set isFavorite = 0;");
        editor.putBoolean(Osrs.strings.PREFS_REMOVE_FAVS, true);
    }

    private void restoreToDefault() {
        editor.putBoolean(Osrs.strings.RESTORE_DEFAULTS, true);
        editor.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        // not sure if onDestroy is called appropriately.
        if(etPriceNat.getText().toString().length() > 0) {
            Osrs.PRICE_NATURE_RUNE = Integer.valueOf(etPriceNat.getText().toString());
        }
        editor.putBoolean(Osrs.strings.SWITCH_HIDE_MEMS_ITEMS,
                swHideMemsItems.isChecked());
        editor.putInt(Osrs.strings.PREFS_PRICE_NATURE_RUNE, Osrs.PRICE_NATURE_RUNE);
        editor.putInt(Osrs.strings.PREF_MIN_PROFIT,
                etMinProfit.getText().length() > 0?
                        Integer.valueOf(etMinProfit.getText().toString()) : 0);
        editor.commit();
    }
}

/*
        OsrsDB.getInstance(this).getWritableDatabase(new OsrsDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                SettingsActivity.this.db = db;

                btnRemoveFavs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setCancelable(true)
                        .setTitle("Are you sure you want to remove all favorites?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeAllFavorites();

                                        Toast.makeText(SettingsActivity.this, "Removed all favorites", Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        });
        */