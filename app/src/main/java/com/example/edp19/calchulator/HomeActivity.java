package com.example.edp19.calchulator;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

        System.out.println("Home on create Called!!!!!!");
        osrsItems = new HashMap<>();

        //initialize widgets on screen
        table = findViewById(R.id.tlGridTable);
        headerRow = findViewById(R.id.headerRow);

        loadOsrsItems("items.csv");
        addTableHeaders();
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
                if(i > 100) break;

                TableRow tr = new TableRow(this);

                TextView tvName = new TextView(this);
                TextView tvBuy = new TextView(this);
                TextView tvLimit = new TextView(this);

                tvName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
                tvBuy.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                tvLimit.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                tvName.setText(osrsItems.get(id).name);
                tvBuy.setText(String.valueOf(osrsItems.get(id).id));
                tvLimit.setText(osrsItems.get(id).isMembers ? "1" : "0");

                tr.addView(tvName);
                tr.addView(tvBuy);
                tr.addView(tvLimit);

                tvName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println(((TextView) view).getText().toString() + " clicked!");
                        Intent intent = new Intent(HomeActivity.this, ItemActivity.class);

                        intent.putExtra("osrsItem", osrsItems.get(id));

                        startActivity(intent);
                    }
                });

                table.addView(tr);
                i++;
            }


        } catch (IOException e){
            e.printStackTrace();
        }
    }


    public void addTableHeaders(){

        TextView tvItem = new TextView(this);
        TextView tvBuy = new TextView(this);
        TextView tvHighAlch = new TextView(this);

        tvItem.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        tvBuy.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tvHighAlch.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

        tvItem.setText("Item");
        tvBuy.setText("Buy");
        tvHighAlch.setText("High Alch");

        headerRow.addView(tvItem);
        headerRow.addView(tvBuy);
        headerRow.addView(tvHighAlch);
    }

}
