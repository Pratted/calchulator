package com.example.edp19.calchulator;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

public class HomeActivity extends AppCompatActivity {

    TableLayout table;
    TableRow headerRow;
    HashMap<Integer, OsrsItem> osrsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        osrsItems = new HashMap<>();

        //initialize widgets on screen
        table = findViewById(R.id.tlGridTable);
        headerRow = findViewById(R.id.headerRow);

        addTableHeaders();
        loadOsrsItems("items.csv");

    }

    @SuppressLint("ValidFragment")
    public static class SettingsDialog extends DialogFragment {

        /******************** Interface ********************/
        public interface SettingsDialogListener {
            public void onPositiveClick();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Are you sure you want to remove all favorites?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

            return builder.create();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        System.out.println("ON PAUSE CALLED!!!");
    }

    @Override
    public void onRestart(){
        super.onRestart();

        System.out.println("ONRESTARTED CALLED!!!");
    }

    @Override
    protected void onStart(){
        super.onStart();

        System.out.println("ON HOMEACTIVITY_ONSTART CALLED!!!!");
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);

        System.out.println("ON HOMEACTIVITY_SAVEINSTANCESTATE CALLED!!!!");
    }

    @Override
    public void onStop(){
        super.onStop();

        System.out.println("HOME ONSTOP CALLED!!!");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        System.out.println("DESTROYEDDDD!!!!");
    }


    public void loadOsrsItems(String filename){

        try {
            Scanner s = new Scanner(getAssets().open("items.csv"));

            while (s.hasNext()) {
                String[] attributes = s.nextLine().split(",");

                int id = Integer.valueOf(attributes[0]);
                String name = attributes[1];
                boolean isMembers = Integer.valueOf(attributes[2]) != 0;

                osrsItems.put(id, new OsrsItem(id, name, isMembers));
                //System.out.println(name);
            }

            System.out.println("Added all items");
            int i = 0;

            for(final Integer id: osrsItems.keySet()){
                if(i++ > 100) break;

                addItemToTable(id);
            }

            sortByHeader("Item");
            sortByHeader("Item");

        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addItemToTable(final int id){
        TableRow tr = new TableRow(this);

        TextView tvName = new TextView(this);
        TextView tvBuy = new TextView(this);
        TextView tvLimit = new TextView(this);

        tvName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
        tvBuy.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        tvLimit.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

        tvName.setText(osrsItems.get(id).name);
        tvBuy.setText(String.valueOf(osrsItems.get(id).id));
        tvLimit.setText(osrsItems.get(id).isMembers ? "1" : "0");

        final ImageButton ibFavorite = new ImageButton(this);

        ibFavorite.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        //ibFavorite.setImageDrawable(n);
        ibFavorite.setImageResource(android.R.drawable.star_off);
        ibFavorite.setId((int)android.R.drawable.star_off);


        tr.addView(ibFavorite);
        tr.addView(tvName);
        tr.addView(tvBuy);
        tr.addView(tvLimit);
        tr.setId(id);

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(((TextView) view).getText().toString() + " clicked!");
                Intent intent = new Intent(HomeActivity.this, ItemActivity.class);

                intent.putExtra("osrsItem", osrsItems.get(id));

                startActivity(intent);
            }
        });

        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(ibFavorite.getResources().getResourceName(ibFavorite.getItemId()).split("\\/")

                if(ibFavorite.getId() == (int) android.R.drawable.star_off){
                    System.out.println("TURNING ON!!!!");
                    ibFavorite.setImageResource(android.R.drawable.star_on);
                    ibFavorite.setId((int) android.R.drawable.star_on);

                    Toast.makeText(HomeActivity.this, osrsItems.get(id).name + " added to favorites", Toast.LENGTH_SHORT).show();
                }
                else{
                    System.out.println("TURNING OFF!!!!");
                    ibFavorite.setImageResource(android.R.drawable.star_off);
                    ibFavorite.setId((int) android.R.drawable.star_off);
                    Toast.makeText(HomeActivity.this, osrsItems.get(id).name + " removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        table.addView(tr);
    }

    public void onButtonSettingsClick(View v){
        startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sortByHeader(String header) {
        if(header.compareTo("Item") == 0){
            ArrayList<Pair<String, OsrsItem>> strArr = new ArrayList<> ();

            for(int i = 0; i < table.getChildCount(); i++){
                int itemId = table.getChildAt(i).getId();

                OsrsItem item = osrsItems.get(itemId);
                strArr.add(new Pair(item.name, osrsItems.get(itemId)));
            }

            if(strArr.get(0).first.compareTo(strArr.get(strArr.size()-1).first) > 0 ){
                Collections.sort(strArr, new Comparator<Pair<String, OsrsItem>>() {
                    @Override
                    public int compare(final Pair<String, OsrsItem> o1, final Pair<String, OsrsItem> o2) {
                        return o1.first.compareTo(o2.first);
                    }
                });
            }
            else{
                Collections.sort(strArr, new Comparator<Pair<String, OsrsItem>>() {
                    @Override
                    public int compare(final Pair<String, OsrsItem> o1, final Pair<String, OsrsItem> o2) {
                        return o2.first.compareTo(o1.first);
                    }
                });
            }

            table.removeAllViewsInLayout();

            for(int i = 0; i < strArr.size(); i++){
                addItemToTable(strArr.get(i).second.id);
            }
        }
        else{
            ArrayList<Pair<Integer, OsrsItem>> intArr = new ArrayList<> ();

            for(int i = 0; i < table.getChildCount(); i++){
                int itemId = table.getChildAt(i).getId();

                OsrsItem item = osrsItems.get(itemId);

                if(header.compareTo("Buy") == 0)
                    intArr.add(new Pair(item.id, osrsItems.get(itemId)));
                if(header.compareTo("High Alch") == 0)
                    intArr.add(new Pair(item.isMembers ? 1 : 0, osrsItems.get(itemId)));
            }

            if(intArr.get(0).first.compareTo(intArr.get(intArr.size()-1).first) > 0 ){
                Collections.sort(intArr, new Comparator<Pair<Integer, OsrsItem>>() {
                    @Override
                    public int compare(final Pair<Integer, OsrsItem> o1, final Pair<Integer, OsrsItem> o2) {
                        return o1.first.compareTo(o2.first);
                    }
                });
            }
            else{
                Collections.sort(intArr, new Comparator<Pair<Integer, OsrsItem>>() {
                    @Override
                    public int compare(final Pair<Integer, OsrsItem> o1, final Pair<Integer, OsrsItem> o2) {
                        return o2.first.compareTo(o1.first);
                    }
                });
            }

            table.removeAllViewsInLayout();

            for(int i = 0; i < intArr.size(); i++){
                addItemToTable(intArr.get(i).second.id);
            }
        }

    }

    public void addTableHeaders(){

        TextView tvItem = new TextView(this);
        TextView tvBuy = new TextView(this);
        TextView tvHighAlch = new TextView(this);
        TextView temp = new TextView(this);

        temp.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tvItem.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
        tvBuy.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        tvHighAlch.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

        tvItem.setText("Item");
        tvItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sortByHeader("Item");
            }
        });

        tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByHeader("Buy");
            }
        });

        tvHighAlch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByHeader("High Alch");
            }
        });

        tvBuy.setText("Buy");
        tvHighAlch.setText("High Alch");

        headerRow.addView(tvItem);
        headerRow.addView(tvBuy);
        headerRow.addView(tvHighAlch);
    }

}
