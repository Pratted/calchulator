package com.example.edp19.calchulator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by eric on 3/28/18.
 */

public class OsrsTable {
    private TextView tvStatus;
    private TableLayout table;
    private TableRow header;
    private ScrollView scrollView;
    private Context context;
    final private static boolean T = true;
    final private static boolean F = false;

    private View headers[] = new View[6];
    private HashMap<String, Boolean> sortedBy = new HashMap<>();
    private HashMap<Integer, OsrsTableItem> osrsItems;
    private String lastSortedBy;
    private boolean sortDesc;

    final public static int COLUMN_FAVORITE = 0;
    final public static int COLUMN_ITEM = 1;
    final public static int COLUMN_ALCH = 2;
    final public static int COLUMN_PRICE = 3;
    final public static int COLUMN_PROFIT = 4;
    final public static int COLUMN_LIMIT = 5;

    private OsrsPopupColumnSelector columnSelector;
    private OsrsNotificationReceiver notificationReceiver;

    public static boolean[] LAYOUT_CURRENT = new boolean[6];
    public static boolean[] LAYOUT_DEFAULT = new boolean[]{T,T,T,T,F,F};

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private LinearLayout layout;
    private PopupWindow window;
    private OsrsPriceFetch osrsPrices;
    private ImageButton scrollToTopButton;

    private boolean hideMems;
    private int hideProfitBelow;
    private String searchString = "";
    private boolean hideHiddenItems = true;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public OsrsTable(Context context, TextView tvStatus, TableRow header, ScrollView svTable, TableLayout table){
        this.tvStatus = tvStatus;
        this.table = table;
        this.header = header;
        this.context = context;
        this.scrollView = svTable;
        this.osrsItems = new HashMap<>();

        prefs = context.getSharedPreferences(Osrs.strings.PREFS_FILE, Context.MODE_PRIVATE);
        editor = prefs.edit(); //cant use prefs.edit().putString()

        Osrs.PRICES_LAST_UPDATED = prefs.getLong(Osrs.strings.KEY_PRICES_LAST_UPDATED, 0);
        System.out.println("LOADED TIME....");
        Osrs.PRICE_NATURE_RUNE = prefs.getInt(Osrs.strings.PREFS_PRICE_NATURE_RUNE, Osrs.PRICE_NATURE_RUNE);

        headers[COLUMN_FAVORITE] = createFavoriteHeader(Osrs.strings.NAME_FAVORITE_COLUMN);
        headers[COLUMN_ITEM] = createTextView(Osrs.strings.NAME_ITEM_COLUMN);
        headers[COLUMN_ALCH] = createTextView(Osrs.strings.NAME_ALCH_COLUMN);
        headers[COLUMN_PRICE] = createTextView(Osrs.strings.NAME_PRICE_COLUMN);
        headers[COLUMN_PROFIT] = createTextView(Osrs.strings.NAME_PROFIT_COLUMN);
        headers[COLUMN_LIMIT] = createTextView(Osrs.strings.NAME_LIMIT_COLUMN);

        LAYOUT_CURRENT = LAYOUT_DEFAULT.clone();

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) layoutInflater.inflate(R.layout.hide_item_menu, null);

        window = new PopupWindow(context);
        window.setContentView(layout);
        window.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);

        scrollView.fullScroll(ScrollView.FOCUS_UP);

        //initialize onClickListeners for all headers.
        for(View v: headers){
            final String column = (String) v.getTag();

            v.setOnClickListener(onHeaderItemClick(column));
            v.setOnLongClickListener(onHeaderItemLongClick());

            sortedBy.put(column, false);
            header.addView(v);
        }

        columnSelector = new OsrsPopupColumnSelector(context);
        columnSelector.setOnDismissListener(onColumnSelectorDismiss());

        //pre-select the visible columns in column selector
        columnSelector.selectColumns(LAYOUT_CURRENT);

        //all columns to the right of Alch are numeric and shall be right aligned.
        for(int i = COLUMN_ALCH; i < headers.length; i++){
            ((TextView) headers[i]).setGravity(Gravity.RIGHT);
        }

        notificationReceiver = new OsrsNotificationReceiver();

        tvStatus.setText(getPriceLastUpdateString());

        if(this.needsPriceUpdate()){
            System.out.println("Fetching prices...");
            tvStatus.setText("Updating prices...");
            this.fetchPrices();
        }
        else{
            System.out.println("Table does not need a price update.");
        }

        loadExistingTable();
        reload();
    }

    private void loadSharedPreferences(){
        Osrs.PRICES_LAST_UPDATED = prefs.getLong(Osrs.strings.KEY_PRICES_LAST_UPDATED, 0);
        Osrs.PRICE_NATURE_RUNE = prefs.getInt(Osrs.strings.PREFS_PRICE_NATURE_RUNE, Osrs.PRICE_NATURE_RUNE);
        Osrs.PRICE_UPDATE_INTERVAL = prefs.getInt(Osrs.strings.KEY_PRICE_UPDATE_INTERVAL, Osrs.PRICE_UPDATE_INTERVAL);
    }

    private TableRow row(int index){
        return (TableRow) table.getChildAt(index);
    }

    private int size(){
        return table.getChildCount();
    }

    private void addItem(OsrsTableItem item){
        table.addView(item.getTableRow());
    }

    private String getPriceLastUpdateString(){
        Date last = new Date(Osrs.PRICES_LAST_UPDATED);

        System.out.println("Last time: " + last.toLocaleString());
        tvStatus.setText(last.toLocaleString());

        int hours = (last.getHours() % 12) + 12;
        int min = last.getMinutes();
        boolean am = last.getHours() < 12;

        return "Prices last updated at " + hours + ":" + (min < 10 ? "0" + min : min)  + (am ? " am" : " pm");
    }

    //SettingsActivity may request settings to be restored, check prefs for answer.
    public boolean needsRestoreDefaults() {
        return prefs.getBoolean(Osrs.strings.RESTORE_DEFAULTS, false);
    }

    public void restoreDefaults(){
        editor.putBoolean(Osrs.strings.RESTORE_DEFAULTS, false);
        editor.apply();

        LAYOUT_CURRENT = LAYOUT_DEFAULT.clone();

        this.reload();

        //this.loadOsrsItems();
        //this.showSelectedColumns(LAYOUT_DEFAULT);
    }

    private TextView createTextView(String text){
        TextView tv = new TextView(context);

        tv.setTextColor(Osrs.colors.ORANGE);
        tv.setTypeface(Osrs.typefaces.FONT_REGULAR_BOLD);
        tv.setTextSize(Osrs.fonts.FONT_SIZE_LARGE);
        tv.setText(text);
        tv.setTag(text);

        return tv;
    }

    private void loadOsrsItems(){
        System.out.println("Loading all items...");
        osrsItems.clear();

        for(OsrsItem parent: OsrsDB.fetchAllItems().values()){
            OsrsTableItem item = new OsrsTableItem(context, parent);

            item.getTvName().setOnClickListener(onItemNameClick(item));
            item.getTvName().setOnLongClickListener(onItemNameLongClick(item));

            item.getIbFavorite().setOnClickListener(onFavoriteButtonClick(item));
            item.getTimer().setOnItemTimerFinishedListener(onItemTimerFinished());

            osrsItems.put(item.getId(), item);
            addItem(item);
        }
        System.out.println("Done loading all items...");
    }

    OsrsPopupColumnSelector.OnDismissListener onColumnSelectorDismiss(){
        return new OsrsPopupColumnSelector.OnDismissListener() {
            @Override
            public void onDismiss() {
                System.out.println("MY DISMISS CALLED!!!!!!");
                OsrsTable.this.showSelectedColumns(columnSelector.getSelectedColumns());

                hideHiddenItems = columnSelector.hideHiddenItems();

                System.out.println("We are " + (hideHiddenItems ? "" : "not") + " hiding hiddem items.");
                System.out.println("HIDE HIDDEN ITEMS -> " + hideHiddenItems);
                filter();
            }
        };
    }

    View.OnClickListener onItemNameClick(final OsrsItem item){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(((TextView) view).getText().toString() + " clicked!");
                Intent intent = new Intent(context, ItemActivity.class);

                intent.putExtra("item", item);
                context.startActivity(intent);
            }
        };
    }

    View.OnLongClickListener onItemNameLongClick(final OsrsTableItem item){
        return new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Point p = OsrsPopupColumnSelector.getPoint(v);

                TextView prompt = layout.findViewById(R.id.tvHideItemPrompt);
                TextView accept = layout.findViewById(R.id.tvHideItemAccept);
                TextView decline = layout.findViewById(R.id.tvHideItemDecline);

                prompt.setTypeface(Osrs.typefaces.FONT_REGULAR_BOLD);
                prompt.setTextSize(Osrs.fonts.FONT_SIZE_LARGE);
                prompt.setText("Hide/block " + item.getName() + "?");

                accept.setTypeface(Osrs.typefaces.FONT_REGULAR);
                accept.setTextSize(Osrs.fonts.FONT_SIZE_LARGE);

                decline.setTypeface(Osrs.typefaces.FONT_REGULAR);
                decline.setTextSize(Osrs.fonts.FONT_SIZE_LARGE);

                accept.setOnClickListener(onAcceptButtonClick(item));
                decline.setOnClickListener(onDeclineButtonClick());

                window.setBackgroundDrawable(new BitmapDrawable());
                window.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + -20, p.y + 50);

                return false;
            }
        };
    }


    View.OnClickListener onHeaderItemClick(final String column){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //header text is stored in tag
                sortColumn(column);

                ((ImageButton) headers[COLUMN_FAVORITE])
                        .setImageResource(sortedBy.get(Osrs.strings.NAME_FAVORITE_COLUMN) ?
                                android.R.drawable.star_on : android.R.drawable.star_off);
            }
        };
    }

    View.OnLongClickListener onHeaderItemLongClick(){
        return new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                columnSelector.show(view);
                return true;
            }
        };
    }

    View.OnClickListener onAcceptButtonClick(final OsrsTableItem item){
        return new View.OnClickListener() {
            public void onClick(View view) {
                window.dismiss();

                if (((RadioButton)layout.findViewById(R.id.rbBlock)).isChecked()) {
                    item.isBlocked = true;
                    item.getTableRow().setVisibility(View.GONE);
                    Toast.makeText(context, item.getName() + " is blocked", Toast.LENGTH_SHORT).show();
                } else { // rbHide is checked
                    item.isHidden = true;
                    item.startTimer();
                    OsrsTable.this.refresh();
//                    notificationReceiver.setAlarm(context, item.getId(), 10);
                }

                OsrsTable.this.paint();
                OsrsDB.save(item);
            }
        };
    }

    View.OnClickListener onDeclineButtonClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        };
    }

    View.OnClickListener onFavoriteButtonClick(final OsrsTableItem item){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.toggleFavorite();

                OsrsDB.save(item);
                Toast.makeText(context,
                        item.getName() + (item.getFavorite() ? " added" : " removed") + " to favorites",
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

    OsrsItemTimer.OnItemTimerFinishedListener onItemTimerFinished(){
        return new OsrsItemTimer.OnItemTimerFinishedListener() {
            @Override
            public void onItemTimerFinished(OsrsItem item) {
                osrsItems.get(item.getId()).setHidden(false);
                OsrsDB.save(osrsItems.get(item.getId()));
                OsrsTable.this.filter();
            }
        };
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortColumn(String columnName){
        final boolean sorted = sortedBy.get(columnName);
        sortDesc = !sorted;

        //clear previous sort criteria
        for(String s: sortedBy.keySet()){
            sortedBy.put(s, false);
        }

        ArrayList<Pair<String, Integer>> a1 = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> a2 = new ArrayList<>();

        //iterate through table to preserve existing sorts as much as possible.
        for(int i = 0; i < table.getChildCount(); i++){
            int id = table.getChildAt(i).getId();

            if(columnName.compareTo("Item") == 0)
                a1.add(new Pair<>(osrsItems.get(id).getString(columnName), id));
            else
                a2.add(new Pair<>(osrsItems.get(id).getInt(columnName), id));
        }


        a1.sort(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> stringIntegerPair, Pair<String, Integer> t1) {
                return sorted ? stringIntegerPair.first.compareTo(t1.first) :
                        t1.first.compareTo(stringIntegerPair.first);
            }
        });

        a2.sort(new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> integerIntegerPair, Pair<Integer, Integer> t1) {
                return sorted ? integerIntegerPair.first.compareTo(t1.first) :
                        t1.first.compareTo(integerIntegerPair.first);
            }
        });

        //clear all rows and put the items back into the table.
        table.removeAllViewsInLayout();

        for(Pair<String, Integer> p: a1){
            this.addItem(osrsItems.get(p.second));
        }

        for(Pair<Integer, Integer> p: a2){
            this.addItem(osrsItems.get(p.second));
        }

        //mark inverse of what we started with.
        sortedBy.put(columnName, !sorted);
        lastSortedBy = columnName;

        paint();
    }

    private void hideAllColumns(TableRow row){
        for(int i = 0; i < row.getChildCount(); i++){
            row.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private void setColumnWeights(TableRow row, TableRow.LayoutParams weights[]){
        for(int i = 0; i < weights.length; i++){
            row.getChildAt(i).setLayoutParams(weights[i]);

            if(weights[i].weight != 0){
                row.getChildAt(i).setVisibility(View.VISIBLE);
                row.setGravity(Gravity.CENTER_VERTICAL);
            }
        }
    }

    private TableRow.LayoutParams[] getColumnWeights(boolean[] cols){
        TableRow.LayoutParams weights[] = new TableRow.LayoutParams[cols.length];

        weights[COLUMN_FAVORITE] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_FAVORITE] ? 1 : 0);

        weights[COLUMN_ITEM] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_ITEM] ? 4 : 0);

        weights[COLUMN_ALCH] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_ALCH] ? 2 : 0);
        weights[COLUMN_PRICE] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_PRICE] ? 2 : 0);
        weights[COLUMN_PROFIT] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_PROFIT] ? 2 : 0);
        weights[COLUMN_LIMIT] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_LIMIT] ? 2 : 0);

        return weights;
    }

    public void showSelectedColumns(boolean[] cols){
        TableRow.LayoutParams weights[] = getColumnWeights(cols);

        hideAllColumns(this.header);

        setColumnWeights(this.header, weights);

        //pre-select all checkboxes in column selector
        for(int i = 0; i < cols.length; i++){
            columnSelector.checkboxes[i].setChecked(cols[i]);
        }

        for(int i = 0; i < this.size(); i++){
            hideAllColumns(this.row(i));
            this.setColumnWeights(this.row(i), weights);
        }

        LAYOUT_CURRENT = cols.clone();
    }


    public void paint(){
        for(int i = 0; i < size(); i++){
            row(i).setBackgroundColor(Osrs.colors.BROWN);
        }

        boolean isBrown = false;

        for(int i = 0; i < size(); i++){
            //ignore hidden rows for alternating colors.
            if(row(i).getVisibility() == View.GONE){
                continue;
            }

            if(isBrown)
                row(i).setBackgroundColor(Osrs.colors.LIGHT_BROWN);
            else
                row(i).setBackgroundColor(Osrs.colors.BROWN);

            isBrown = !isBrown;
        }
    }


    private void loadExistingTable(){
        //load the previously selected columns, (use default if NA).
        for(int i = 0; i < headers.length; i++){
            String name = (String) headers[i].getTag();

            LAYOUT_CURRENT[i] = prefs.getBoolean(name, LAYOUT_DEFAULT[i]);
        }

        lastSortedBy = prefs.getString("SortBy", "Profit");
        sortDesc = prefs.getBoolean("SortDesc", false);

        //mark as already sorted if necessary so sortColumn() sorts in descending order.
        sortedBy.put(lastSortedBy, sortDesc);

        sortColumn(lastSortedBy);
        showSelectedColumns(LAYOUT_CURRENT);
    }

    //imports the data from the database back into the table.
    public void reload() {
        table.removeAllViews();
        osrsItems.clear();

        loadOsrsItems();
        sortColumn(lastSortedBy);
        showSelectedColumns(LAYOUT_CURRENT);
        filter();

        editor.putBoolean(Osrs.strings.RELOAD_TABLE, false);
        editor.apply();
    }

    public void refresh() {
        hideMems = prefs.getBoolean(Osrs.strings.SWITCH_HIDE_MEMS_ITEMS, true);
        hideProfitBelow = prefs.getInt(Osrs.strings.PREF_MIN_PROFIT, 0);
        boolean removeAllFavs = prefs.getBoolean(Osrs.strings.PREFS_REMOVE_FAVS, false);

        //settings/item activity changed data. Reload it from DB but keep current table layout.
        if(hasDataBeenModified()){
            System.out.println("REFRESHED THE DATA FROM THE DB");
            OsrsDB.refreshOsrsTableItemsFromDB(osrsItems);
            editor.putBoolean("DataModified", false);
        }

        for(OsrsTableItem item : osrsItems.values()) {
            item.refreshProfit();

            //start a timer if not already running..
            if(item.getHidden() && !item.getTimer().isRunning()){
                System.out.println("RESTORING THE TIMER HAHAHHHAHAHAHAHAHAHAHAH");
                item.restoreTimer(item.getTimerStartTime());
            }
        }

        filter();
    }

    private void showAllItems() {
        for(OsrsTableItem item : osrsItems.values()) {
            item.show();
        }
    }

    public void filter() {
        showAllItems();

        for(OsrsTableItem item : osrsItems.values()) {
            if(item.getBlocked()) item.hide();
            if(hideMems && item.isMembers()) item.hide();
            if(item.getPrice() == 0 || item.getPrice() > 100000) item.hide();
            if(hideProfitBelow > 0 && item.getProfit() < hideProfitBelow) item.hide();
            if(searchString.length() > 0 && !item.getName().toLowerCase().contains(searchString)) item.hide();
            if(hideHiddenItems && item.getHidden()) item.hide();
        }

        paint();
    }

    // save information to shared prefs.
    public void save(){
        //save the selected columns
        for(int i = 0; i < headers.length; i++){
            String name = (String) headers[i].getTag();
            editor.putBoolean(name,LAYOUT_CURRENT[i]);
        }

        editor.putLong(Osrs.strings.KEY_PRICES_LAST_UPDATED, Osrs.PRICES_LAST_UPDATED);
        editor.putString("SortBy", lastSortedBy);
        editor.putBoolean("SortDesc", sortDesc);

        //use commit since this is called in onPause() and needs to be done immediately
        editor.commit();
    }

    public boolean needsPriceUpdate(){
        long interval = Osrs.PRICE_UPDATE_INTERVAL * 60 * 1000;
        long now = new Date().getTime();

        //interval amount of time has passed or prices havent been updated...
        return now - Osrs.PRICES_LAST_UPDATED > interval ||
                Osrs.PRICES_LAST_UPDATED == 0;
    }

    //fires off async task to update the prices
    public synchronized boolean fetchPrices() {
        if(osrsPrices != null) return false;
        else {
            osrsPrices = new OsrsPriceFetch(context);
            osrsPrices.setOnPricesReadyListner(new OsrsPriceFetch.OnPricesReady() {
                @Override
                public void onPricesReady() {
                    tvStatus.setText(getPriceLastUpdateString());
                    OsrsTable.this.reload();
                }
            });

            osrsPrices.execute(Osrs.strings.URL_CURRENT_PRICES);
            osrsPrices = null;
            return true;
        }
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public boolean hasDataBeenModified(){
        return prefs.getBoolean("DataModified", false);
    }
}