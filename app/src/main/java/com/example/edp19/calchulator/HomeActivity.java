package com.example.edp19.calchulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    TableLayout table;
    TableRow headerRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //initialize widgets on screen
        table = findViewById(R.id.tlGridTable);
        headerRow = findViewById(R.id.headerRow);
    }

    @Override
    protected void onStart(){
        super.onStart();

        addTableHeaders();

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
