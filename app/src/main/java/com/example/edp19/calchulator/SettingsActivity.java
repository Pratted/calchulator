package com.example.edp19.calchulator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        Osrs.initialize(this);
        OsrsDB.initialize(this);

        Intent incoming = getIntent();
        builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setCancelable(true);

        prefs = this.getSharedPreferences(Osrs.files.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = prefs.edit();

        //use the osrs items from the Home activity if they're available, otherwise load from db.
        if(incoming != null && incoming.hasExtra(Osrs.strings.KEY_OSRS_ITEMS)){
            osrsItems = (HashMap<Integer, OsrsItem>) incoming.getSerializableExtra(Osrs.strings.KEY_OSRS_ITEMS);
        }
        else{
            System.out.println("Using DB items...");
            osrsItems = OsrsDB.fetchAllItems();
        }

        btnHiddenItems = findViewById(R.id.btnHiddenItems);
        btnBlockedItems = findViewById(R.id.btnBlockedItems);

        llBlockList = findViewById(R.id.llBlockList);
        llCheckBoxContainer = findViewById(R.id.llCheckboxContainer);
        svSettings = findViewById(R.id.svSettings);

        etPriceNat = findViewById(R.id.etPriceNat);
        etPriceNat.setHint(String.valueOf(Osrs.PRICE_NATURE_RUNE));
        etPriceNat.setText(String.valueOf(Osrs.PRICE_NATURE_RUNE));

        etMinProfit = findViewById(R.id.etMinProfit);
        etMinProfit.setText(String.valueOf(prefs.getInt(Osrs.strings.KEY_MIN_PROFIT, 0)));

        swHideMemsItems = findViewById(R.id.switch_hide_mems);
        swHideMemsItems.setChecked(prefs.getBoolean(Osrs.strings.KEY_HIDE_MEMBERS_ITEMS, false));

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
                svSettings.setVisibility(View.GONE);
                llBlockList.setVisibility(View.VISIBLE);

                ((TextView)llBlockList.findViewById(R.id.tvBlockListHeader))
                        .setText((blocked ? "Blocked " : "Hidden ") + " items");

                //remove any checkboxes from last time it was visible.
                llCheckBoxContainer.removeAllViews();

                System.out.println("Blocked -> " + blocked);
                System.out.println("Loaded " + osrsItems.size() + " items");

                int color = Osrs.colors.BROWN;

                //sort these bitches alphabetically
                ArrayList<OsrsItem> items = new ArrayList<>(osrsItems.values());
                Collections.sort(items, new Comparator<OsrsItem>() {
                    @Override
                    public int compare(OsrsItem o1, OsrsItem o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                for(final OsrsItem item: items){
                    //blocked -> show blocked items in blocked list, otherwise show hidden items.
                    if(blocked ? item.getBlocked() : item.getHidden()){
                        System.out.println("Adding " + item.getName() + " to list");
                        CheckBox cb = new CheckBox(SettingsActivity.this);
                        llCheckBoxContainer.addView(cb);

                        cb.setLayoutDirection(CheckBox.LAYOUT_DIRECTION_RTL);
                        cb.setTypeface(Osrs.typefaces.FONT_REGULAR);
                        cb.setTextColor(Color.WHITE);
                        cb.setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);
                        cb.setChecked(true);
                        cb.setBackgroundColor(color);
                        cb.setPadding(10,0,0,0);
                        cb.setText(item.getName());
                        cb.setId(item.getId());

                        if(color == Osrs.colors.LIGHT_BROWN)
                            color = Osrs.colors.BROWN;
                        else
                            color = Osrs.colors.LIGHT_BROWN;
                    }
                }

                llBlockList.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Saving...");

                        for(int i = 0; i < llCheckBoxContainer.getChildCount(); i++){
                            CheckBox cb = (CheckBox) llCheckBoxContainer.getChildAt(i);
                            int id = cb.getId();

                            if(blocked)
                                osrsItems.get(id).isBlocked = cb.isChecked();
                            else
                                osrsItems.get(id).isHidden = cb.isChecked();

                            OsrsDB.save(osrsItems.get(id));
                        }

                        llBlockList.setVisibility(View.GONE);
                        svSettings.setVisibility(View.VISIBLE);
                        editor.putBoolean(Osrs.strings.KEY_HAS_DATA_BEEN_MODIFIED, true);
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

    @Override
    public void onPause() {
        super.onPause();
        // not sure if onDestroy is called appropriately.
        if(etPriceNat.getText().toString().length() > 0) {
            Osrs.PRICE_NATURE_RUNE = Integer.valueOf(etPriceNat.getText().toString());
        }

        editor.putBoolean(Osrs.strings.KEY_REMOVE_ALL_FAVORITES, true);
        editor.putBoolean(Osrs.strings.KEY_HIDE_MEMBERS_ITEMS,
                swHideMemsItems.isChecked());
        editor.putInt(Osrs.strings.KEY_PRICE_NATURE_RUNE, Osrs.PRICE_NATURE_RUNE);
        editor.putInt(Osrs.strings.KEY_MIN_PROFIT,
                etMinProfit.getText().length() > 0?
                        Integer.valueOf(etMinProfit.getText().toString()) : 0);

        //commit because activity is about to be destroyed and HomeActivty needs this info ASAP
        editor.commit();
    }

    public void onRemoveFavoritesButtonClick(View view) {
        builder.setTitle(Osrs.strings.PROMPT_REMOVE_ALL_FAVORITES)
                .setPositiveButton(Osrs.strings.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (Integer id : osrsItems.keySet()) {
                            osrsItems.get(id).setFavorite(false);
                        }
                        OsrsDB.removeAllFavorites();
                        editor.putBoolean(Osrs.strings.KEY_REMOVE_ALL_FAVORITES, true);
                        editor.putBoolean(Osrs.strings.KEY_HAS_DATA_BEEN_MODIFIED, true);

                        Toast.makeText(SettingsActivity.this, Osrs.strings.TOAST_REMOVE_ALL_FAVORITES, Toast.LENGTH_SHORT).show();
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

    public void onRestoreDefaultsButtonClick(View view){
        builder.setTitle(Osrs.strings.PROMPT_RESTORE_DEFAULTS)
                .setPositiveButton(Osrs.strings.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putBoolean(Osrs.strings.KEY_RESTORE_DEFAULTS, true);
                        editor.apply();

                        Toast.makeText(SettingsActivity.this, Osrs.strings.TOAST_RESTORE_DEFAULTS, Toast.LENGTH_SHORT).show();
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
}