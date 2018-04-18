package com.example.edp19.calchulator;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton ibSearch;
    private HashMap<Integer, OsrsItem> osrsItems;
    private OsrsTable table;

    public OsrsSearchBar(final OsrsTable table, HashMap<Integer, OsrsItem> osrsItems,
                         TextView tvStatus, TextView tvSearchLabel, final EditText etSearch) {

        this.table = table;
        this.osrsItems = osrsItems;
        this.tvSearch = etSearch;
        this.tvSearchLabel = tvSearchLabel;
        this.tvStatus = tvStatus;
        this.ibSearch = ibSearch;

        configureTextView(etSearch);
        configureTextView(tvSearchLabel);
        configureTextView(tvStatus);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("Current text: " + charSequence.toString());

                String toSearch = charSequence.toString().toLowerCase();
                if(toSearch.equals("")) table.resetSearch();
                else table.filterItems(toSearch);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void configureTextView(TextView tv){
        tv.setTypeface(Osrs.typefaces.FONT_REGULAR);
        tv.setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);
    }

}
