package com.example.edp19.calchulator;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class HomeActivity extends AppCompatActivity {
    private OsrsSearchBar searchBar;
    private OsrsTable table;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("HOMEACTIVITY ONCREATE CALLED");
        super.onCreate(savedInstanceState);

        //initialize resources (strings, fonts, colors, etc)
        Osrs.initialize(this);
        OsrsDB.initialize(this);

        setContentView(R.layout.activity_home);

        table = new OsrsTable(
                this,
                (TextView) findViewById(R.id.tvStatus),
                (TableRow) findViewById(R.id.headerRow),
                (ScrollView) findViewById(R.id.svTable),
                (TableLayout) findViewById(R.id.tlGridTable)
        );

        searchBar = new OsrsSearchBar(table,
                (TextView) findViewById(R.id.tvStatus),
                (TextView) findViewById(R.id.tvSearchLabel),
                (EditText) findViewById(R.id.tvSearch)
        );
    }

    @Override
    public void onResume(){
        super.onResume();
        System.out.println("On resume called...");

        if(table.needsRestoreDefaults()){
            System.out.println("RESTORING DEFAULTS");
            table.restoreDefaults();
        }

        //apply new filters, changes, etc.
        table.refresh();
    }

    @Override
    public void onPause(){
        super.onPause();

        System.out.println("ON PAUSE CALLED!!!");
        table.save();

        System.out.println("Saved table");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        System.out.println("DESTROYEDDDD!!!!");
    }

    public void onButtonSettingsClick(View v){
        startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
    }
}
