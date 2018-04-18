package com.example.edp19.calchulator;

import android.widget.EditText;
import android.widget.TextView;

import com.example.edp19.calchulator.Osrs;
import com.example.edp19.calchulator.OsrsItem;
import com.example.edp19.calchulator.OsrsTable;

import java.util.HashMap;

/**
 * Created by eric on 4/17/18.
 */

public class OsrsSearchBar {
    private EditText tvSearch;
    private TextView tvSearchLabel;
    private TextView tvStatus;

    private HashMap<Integer, OsrsItem> osrsItems;
    private OsrsTable table;

    public OsrsSearchBar(OsrsTable table, HashMap<Integer, OsrsItem> osrsItems,
                         TextView tvStatus, TextView tvSearchLabel, EditText tvSearch) {
        this.table = table;
        this.osrsItems = osrsItems;
        this.tvSearch = tvSearch;
        this.tvSearchLabel = tvSearchLabel;
        this.tvStatus = tvStatus;

        configureTextView(tvSearch);
        configureTextView(tvSearchLabel);
        configureTextView(tvStatus);
    }

    private void configureTextView(TextView tv){
        tv.setTypeface(Osrs.typefaces.FONT_REGULAR);
        tv.setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);
    }

}
