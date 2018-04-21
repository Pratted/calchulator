package com.example.edp19.calchulator;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity  {
    Button buttonRemoveFavs;
    HashMap<Integer, OsrsItem> osrsItems;
    SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //buttonRemoveFavs = findViewById(R.id.buttonRemoveFavs);

        System.out.println("ABout to unpakc");
        osrsItems = (HashMap<Integer, OsrsItem> ) getIntent().getSerializableExtra("osrsItems");

        OsrsDB.getInstance(this).getWritableDatabase(new OsrsDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                SettingsActivity.this.db = db;

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

                buttonRemoveFavs.setOnClickListener(new View.OnClickListener() {
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

    /*
    public void removeAllFavorites() {
        for (Integer id : osrsItems.keySet()) {
            osrsItems.get(id).setFavorite(false);
        }

        SettingsActivity.this.db.execSQL("update Item set isFavorite = 0;");
    }
    */
}
