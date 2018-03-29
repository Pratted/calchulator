package com.example.edp19.calchulator;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by eric on 3/28/18.
 */

public class OsrsTable {
    private TableLayout table;
    private TableRow header;
    private Context context;
    private static boolean T = true;
    private static boolean F = false;
    private boolean lastRowBrown = false;

    private View headers[] = new View[6];
    private HashMap<String, Boolean> sortedBy = new HashMap<>();
    private HashMap<Integer, OsrsItem> osrsItems;

    final public static int COLUMN_FAVORITE = 0;
    final public static int COLUMN_ITEM = 1;
    final public static int COLUMN_ALCH = 2;
    final public static int COLUMN_PRICE = 3;
    final public static int COLUMN_PROFIT = 4;
    final public static int COLUMN_LIMIT = 5;

    public OsrsPopupColumnSelector columnSelector;

    public static boolean[] LAYOUT_DEFAULT = new boolean[]{T,T,T,T,F,F};

    public OsrsTable(Context context, HashMap<Integer, OsrsItem> osrsItems, TableRow header, final TableLayout table){
        this.table = table;
        this.header = header;
        this.context = context;
        this.osrsItems = osrsItems;

        headers[COLUMN_FAVORITE] = createFavoriteHeader(Osrs.strings.NAME_FAVORITE_COLUMN);
        headers[COLUMN_ITEM] = createTextView(Osrs.strings.NAME_ITEM_COLUMN);
        headers[COLUMN_ALCH] = createTextView(Osrs.strings.NAME_ALCH_COLUMN);
        headers[COLUMN_PRICE] = createTextView(Osrs.strings.NAME_PRICE_COLUMN);
        headers[COLUMN_PROFIT] = createTextView(Osrs.strings.NAME_PROFIT_COLUMN);
        headers[COLUMN_LIMIT] = createTextView(Osrs.strings.NAME_LIMIT_COLUMN);

        header.setPadding(0,50,0,0);

        //initialize onClickListeners for all headers.
        for(View v: headers){
            final String text = (String) v.getTag();

            v.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //header text is stored in tag
                    System.out.println("Sorting by " + (String) (v.getTag()));
                    sortColumn(text);
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    System.out.println(((String) view.getTag()) + " long clicked!!");

                    columnSelector.show(view);
                    return true;
                }
            });

            sortedBy.put(text, false);
            header.addView(v);
        }

        columnSelector = new OsrsPopupColumnSelector(context);

        columnSelector.setOnDismissListener(new OsrsPopupColumnSelector.OnDismissListener() {
            @Override
            public void onDismiss() {
                System.out.println("MY DISMISS CALLED!!!!!!");
                OsrsTable.this.reformat(columnSelector.getSelectedColumns());
            }
        });

        for(int i = COLUMN_ALCH; i < headers.length; i++){
            ((TextView) headers[i]).setGravity(Gravity.RIGHT);
        }
        

    }

    private TextView createTextView(String text){
        TextView tv = new TextView(context);

        tv.setTextColor(Osrs.colors.ORANGE);
        tv.setTypeface(Osrs.typefaces.FONT_REGULAR_BOLD);
        tv.setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);
        tv.setText(text);
        tv.setTag(text);

        return tv;
    }

    private ImageButton createFavoriteHeader(String tag){
        ImageButton ib = new ImageButton(context);

        ib.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        ib.setImageResource(android.R.drawable.star_off);
        ib.setPadding(0,-5,0,0);
        ib.setBackgroundDrawable(null);
        ib.setTag(tag);

        return ib;
    }

    public void sortColumn(String columnName){
        Map<Integer, Integer> mInt = new TreeMap<>();
        Map<String, Integer> mStr = new TreeMap<>();

        boolean sorted = sortedBy.get(columnName);

        //sort descending if already sorted (ascending)
        if(sorted){
            mInt = new TreeMap<>(Collections.<Integer>reverseOrder());
            mStr = new TreeMap<>(Collections.<String>reverseOrder());
        }

        //clear previous sort criteria
        for(String s: sortedBy.keySet()){
            sortedBy.put(s, false);
        }

        //iterate through table to preserve existing sorts as much as possible.
        for(int i = 0; i < table.getChildCount(); i++){
            int id = table.getChildAt(i).getId();

            if(columnName.compareTo("Item") == 0)
                mStr.put(osrsItems.get(id).getString(columnName), id);
            else
                mInt.put(osrsItems.get(id).getInt(columnName), id);
        }

        //clear all rows and put the items back into the table.
        table.removeAllViewsInLayout();

        for(Integer id: mInt.keySet()){
            this.addItem(osrsItems.get(mInt.get(id)));
        }

        for(String id: mStr.keySet()){
            this.addItem(osrsItems.get(mStr.get(id)));
        }

        //mark inverse of what we started with.
        sortedBy.put(columnName, !sorted);
    }

    private void reset(TableRow row){
        for(int i = 0; i < row.getChildCount(); i++){
            row.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private void setColumnWeights(TableRow row, TableRow.LayoutParams weights[]){
        for(int i = 0; i < row.getChildCount(); i++){
            row.getChildAt(i).setLayoutParams(weights[i]);

            if(weights[i].weight != 0){
                row.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }
    }

    public TableRow row(int index){
        return (TableRow) table.getChildAt(index);
    }

    public int size(){
        return table.getChildCount();
    }

    public void reformat(boolean[] cols){
        TableRow.LayoutParams weights[] = new TableRow.LayoutParams[cols.length];

        //Favorite
        weights[COLUMN_FAVORITE] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_FAVORITE] ? 1 : 0);

        //Item
        weights[COLUMN_ITEM] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_ITEM] ? 4 : 0);

        //Alch, Price, Profit, Limit
        weights[COLUMN_ALCH] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_ALCH] ? 2 : 0);
        weights[COLUMN_PRICE] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_PRICE] ? 2 : 0);
        weights[COLUMN_PROFIT] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_PROFIT] ? 2 : 0);
        weights[COLUMN_LIMIT] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_LIMIT] ? 2 : 0);

        reset(this.header);

        setColumnWeights(this.header, weights);

        for(int i = 0; i < this.size(); i++){
            reset(this.row(i));
            this.setColumnWeights(this.row(i), weights);
        }
    }

    public void addItem(OsrsItem item){
        item.getTableRow().setBackgroundColor(lastRowBrown ? Osrs.colors.LIGHT_BROWN : Osrs.colors.BROWN);
        table.addView(item.getTableRow());

        //alternating row color...
        lastRowBrown = !lastRowBrown;
    }
}

