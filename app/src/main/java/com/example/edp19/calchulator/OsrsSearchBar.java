package com.example.edp19.calchulator;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by eric on 4/17/18.
 */

public class OsrsSearchBar {
    private EditText tvSearch;
    private TextView tvSearchLabel;
    private TextView tvStatus;
    private OsrsTable table;

    public OsrsSearchBar(final OsrsTable table, TextView tvStatus, TextView tvSearchLabel,
                         final EditText etSearch) {

        this.table = table;
        this.tvSearch = etSearch;
        this.tvSearchLabel = tvSearchLabel;
        this.tvStatus = tvStatus;

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

                table.showApplicableItems();
                table.filterItems(toSearch);
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
