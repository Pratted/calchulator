package com.example.edp19.calchulator;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity implements HomeActivity.SettingsDialog.SettingsDialogListener {
    Button buttonRemoveFavs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        buttonRemoveFavs = findViewById(R.id.buttonRemoveFavs);

        /******************** Callback ********************/
        buttonRemoveFavs.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onPositiveClick();
            }
        });
    }

    /******************** Override interface method ********************/
    @Override
    public void onPositiveClick() {
        HomeActivity.SettingsDialog d = new HomeActivity.SettingsDialog();
        d.show(getFragmentManager(), "");
    }
}
