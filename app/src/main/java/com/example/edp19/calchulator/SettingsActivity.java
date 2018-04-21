package com.example.edp19.calchulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity  {
    Button btnHiddenItems;
    Button btnBlockedItems;
    Button btnRemoveFavs;
    Button btnRestoreDefaults;
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
    }

    @Override
    public void onResume(){
        super.onResume();

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
    }

    private void removeAllFavorites() {
        for (Integer id : osrsItems.keySet()) {
            osrsItems.get(id).setFavorite(false);
        }
        db.execSQL("update Item set isFavorite = 0;");
    }

    private void restoreToDefault() {
        editor.putBoolean(Osrs.strings.NAME_FAVORITE_COLUMN, OsrsTable.LAYOUT_DEFAULT[OsrsTable.COLUMN_FAVORITE]);
        editor.putBoolean(Osrs.strings.NAME_ALCH_COLUMN, OsrsTable.LAYOUT_DEFAULT[OsrsTable.COLUMN_ALCH]);
        editor.putBoolean(Osrs.strings.NAME_ITEM_COLUMN, OsrsTable.LAYOUT_DEFAULT[OsrsTable.COLUMN_ITEM]);
        editor.putBoolean(Osrs.strings.NAME_LIMIT_COLUMN, OsrsTable.LAYOUT_DEFAULT[OsrsTable.COLUMN_LIMIT]);
        editor.putBoolean(Osrs.strings.NAME_PRICE_COLUMN, OsrsTable.LAYOUT_DEFAULT[OsrsTable.COLUMN_PRICE]);
        editor.putBoolean(Osrs.strings.NAME_PROFIT_COLUMN, OsrsTable.LAYOUT_DEFAULT[OsrsTable.COLUMN_PROFIT]);
        editor.putBoolean("ReloadTable", true);
        editor.commit();
    }
}