package com.example.edp19.calchulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        table = findViewById(R.id.tlGridTable);


        for(int i = 0; i < 5; i++){
            TableRow tr = new TableRow(this);

            TextView tv1 = new TextView(this);
            TextView tv2 = new TextView(this);

            tv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));

            tv1.setText("Eric");
            tv2.setText("Pratt");

            tr.addView(tv1);
            tr.addView(tv2);

            table.addView(tr);
        }

        //addTableHeaders();
    }


    public void addTableHeaders(View v){

        TableRow headerRow = new TableRow(this);

        TextView tvItem = new TextView(this);
        TextView tvBuy = new TextView(this);
        TextView tvHighAlch = new TextView(this);

        tvItem.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
        tvBuy.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
        tvHighAlch.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));

        tvItem.setText("Item");
        tvBuy.setText("Buy");
        tvHighAlch.setText("High Alch");

        headerRow.addView(tvItem);
        headerRow.addView(tvBuy);
        headerRow.addView(tvHighAlch);

        table.addView(headerRow);
    }

}
