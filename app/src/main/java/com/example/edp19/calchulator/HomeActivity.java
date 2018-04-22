package com.example.edp19.calchulator;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
    private SQLiteDatabase db;
    private OsrsSearchBar searchBar;
    private OsrsTable table;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("HOMEACTIVITY ONCREATE CALLED");
        super.onCreate(savedInstanceState);

        //initialize resources (strings, fonts, colors, etc)
        new Osrs(this);

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

        if(table.needsUpdate()){
            System.out.println("Reloading the table...");
            table.reload();
        }
        else{
            System.out.println("Table does not need an update.");
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        System.out.println("ON PAUSE CALLED!!!");
        table.save();

        System.out.println("Saved table");
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



    public void onButtonSettingsClick(View v){
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);

        startActivity(intent);

        getCacheDir();
    }
}
