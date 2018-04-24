package com.example.edp19.calchulator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity  {
    private Button btnHiddenItems;
    private Button btnBlockedItems;
    private EditText etPriceNat;
    private EditText etMinProfit;
    private Switch swHideMemsItems;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private LinearLayout llBlockList;
    private LinearLayout llCheckBoxContainer;
    private ScrollView svSettings;
    private HashMap<Integer, OsrsItem> osrsItems;

    private boolean BLOCKED = true;
    private boolean HIDDEN = false;

    private AlertDialog.Builder builder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent incoming = getIntent();
        builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setCancelable(true);


        //use the osrs items from the Home activity if they're available, otherwise load from db.
        if(incoming != null && incoming.hasExtra("osrsItems")){
            osrsItems = (HashMap<Integer, OsrsItem>) incoming.getSerializableExtra("osrsItems");
        }
        else{
            System.out.println("Using DB items...");
            osrsItems = OsrsDB.fetchAllItems(this);
        }

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
                restoreToDefault();
            }
        });

        llBlockList = findViewById(R.id.llBlockList);
        llCheckBoxContainer = findViewById(R.id.llCheckboxContainer);
        svSettings = findViewById(R.id.svSettings);

        etPriceNat = findViewById(R.id.etPriceNat);
        etPriceNat.setHint(String.valueOf(Osrs.PRICE_NATURE_RUNE));
        etPriceNat.setText(String.valueOf(Osrs.PRICE_NATURE_RUNE));

        etMinProfit = findViewById(R.id.etMinProfit);
        etMinProfit.setText(String.valueOf(prefs.getInt(Osrs.strings.PREF_MIN_PROFIT, 0)));

        swHideMemsItems = findViewById(R.id.switch_hide_mems);
        swHideMemsItems.setChecked(prefs.getBoolean(Osrs.strings.SWITCH_HIDE_MEMS_ITEMS, false));

        btnBlockedItems.setOnClickListener(onBlockedOrHiddenItemsClick(BLOCKED));
        btnHiddenItems.setOnClickListener(onBlockedOrHiddenItemsClick(HIDDEN));
    }

    //the same checkbox list is shown when either the block of hidden items buttons are clicked.
    //blocked == true -> the list will contain blocked items
    //blocked == false -> the list will contain hidden items
    private View.OnClickListener onBlockedOrHiddenItemsClick(final boolean blocked){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBlockList.setVisibility(View.VISIBLE);
                svSettings.setVisibility(View.GONE);

                ((TextView)llBlockList.findViewById(R.id.tvBlockListHeader))
                        .setText((blocked ? "Blocked " : "Hidden ") + " items");

                //remove any checkboxes from last time it was visible.
                llCheckBoxContainer.removeAllViews();

                System.out.println("Blocked -> " + blocked);
                System.out.println("Loaded " + osrsItems.size() + " items");

                for(OsrsItem item: osrsItems.values()){

                    //blocked -> show blocked items in blocked list, otherwise show hidden items.
                    if(blocked ? item.getBlocked() : item.getHidden()){
                        System.out.println("Adding " + item.getName() + " to list");
                        CheckBox cb = new CheckBox(SettingsActivity.this);
                        llCheckBoxContainer.addView(cb);
                        
                        cb.setText(item.getName());
                    }
                }

                llBlockList.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Saving...");
                        llBlockList.setVisibility(View.GONE);
                        svSettings.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
    }

    @Override
    public void onResume(){
        super.onResume();
        etPriceNat.setHint(String.valueOf(Osrs.PRICE_NATURE_RUNE));
    }

    private void removeAllFavorites() {
        builder.setTitle("Are you sure you want to remove all favorites?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (Integer id : osrsItems.keySet()) {
                        osrsItems.get(id).setFavorite(false);
                    }
                    OsrsDB.removeAllFavorites();
                    editor.putBoolean(Osrs.strings.PREFS_REMOVE_FAVS, true);

                    Toast.makeText(SettingsActivity.this, "Removed all favorites", Toast.LENGTH_SHORT).show();
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