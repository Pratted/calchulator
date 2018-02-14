package com.example.edp19.calchulator;

import android.annotation.SuppressLint;
import android.content.Context;
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

        osrsItems = new HashMap<>();

        //initialize widgets on screen
        table = findViewById(R.id.tlGridTable);
        headerRow = findViewById(R.id.headerRow);

        loadOsrsItems("items.csv");
        //String json = loadJsonFromFile(this, "items.json");
    }

    @Override
    protected void onStart(){
        super.onStart();

        addTableHeaders();

        /*
        for(int i = 0; i < 5; i++){
            TableRow tr = new TableRow(this);
            TextView tv1 = new TextView(this);
            TextView tv2 = new TextView(this);

            tv1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tv2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));

            tv1.setText("Eric");
            tv2.setText("Pratt");

            tr.addView(tv1);
            tr.addView(tv2);

            table.addView(tr);
        }
        */
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
                System.out.println(name);
            }

            System.out.println("Added all items");


            int i = 0;

            for(Integer id: osrsItems.keySet()){
                if(i > 10) break;

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
