package com.example.edp19.calchulator;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by eric on 3/28/18.
 */

public class OsrsTable {
    private TableLayout table;
    private TableRow header;
    private Context context;
    final private static boolean T = true;
    final private static boolean F = false;

    private View headers[] = new View[6];
    private HashMap<String, Boolean> sortedBy = new HashMap<>();
    private HashMap<Integer, OsrsItem> osrsItems;
    private String lastSortedBy;
    private boolean sortDesc;

    final public static int COLUMN_FAVORITE = 0;
    final public static int COLUMN_ITEM = 1;
    final public static int COLUMN_ALCH = 2;
    final public static int COLUMN_PRICE = 3;
    final public static int COLUMN_PROFIT = 4;
    final public static int COLUMN_LIMIT = 5;

    private OsrsPopupColumnSelector columnSelector;

    public static boolean[] LAYOUT_CURRENT = new boolean[6];
    public static boolean[] LAYOUT_DEFAULT = new boolean[]{T,T,T,T,F,F};

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private SQLiteDatabase db;
    private LinearLayout layout;
    private PopupWindow window;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public OsrsTable(Context context, HashMap<Integer, OsrsItem> osrsItems, TableRow header, final TableLayout table){
        this.table = table;
        this.header = header;
        this.context = context;
        this.osrsItems = osrsItems;

        prefs = context.getSharedPreferences(Osrs.strings.PREFS_FILE, Context.MODE_PRIVATE);
        editor = prefs.edit(); //cant use prefs.edit().putString()

        headers[COLUMN_FAVORITE] = createFavoriteHeader(Osrs.strings.NAME_FAVORITE_COLUMN);
        headers[COLUMN_ITEM] = createTextView(Osrs.strings.NAME_ITEM_COLUMN);
        headers[COLUMN_ALCH] = createTextView(Osrs.strings.NAME_ALCH_COLUMN);
        headers[COLUMN_PRICE] = createTextView(Osrs.strings.NAME_PRICE_COLUMN);
        headers[COLUMN_PROFIT] = createTextView(Osrs.strings.NAME_PROFIT_COLUMN);
        headers[COLUMN_LIMIT] = createTextView(Osrs.strings.NAME_LIMIT_COLUMN);

        LAYOUT_CURRENT = LAYOUT_DEFAULT;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) layoutInflater.inflate(R.layout.hide_item_menu, null);

        window = new PopupWindow(context);
        window.setContentView(layout);
        window.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);

        //initialize onClickListeners for all headers.
        for(View v: headers){
            final String text = (String) v.getTag();

            v.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //header text is stored in tag
                    sortColumn(text);
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
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

        //all columns to the right of Alch are numeric and shall be right aligned.
        for(int i = COLUMN_ALCH; i < headers.length; i++){
            ((TextView) headers[i]).setGravity(Gravity.RIGHT);
        }

        //pre-select the visible columns in column selector
        columnSelector.selectColumns(LAYOUT_CURRENT);

        Osrs.PRICES_LAST_UPDATED = prefs.getLong("PriceUpdate", Instant.now().toEpochMilli());

        restoreTable();

        OsrsDB.getInstance(context).getWritableDatabase(new OsrsDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase db) {
                OsrsTable.this.db = db;
                loadOsrsItems();
                sortColumn(lastSortedBy); //restore previous sort...
                refresh();
            }
        });

        System.out.println("Last updated: " + Time.from(Instant.ofEpochSecond(Osrs.PRICES_LAST_UPDATED)).toGMTString());
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

    private void loadOsrsItems(){
        osrsItems.clear();

        Cursor c = db.rawQuery("select * from Item", null);

        while(c.moveToNext()){
            int id = c.getInt(0);
            final String name = c.getString(1);
            int highAlch = c.getInt(2);
            int currentPrice = c.getInt(3);
            int buyLimit = c.getInt(4);
            boolean isMembers = c.getInt(5) == 1;
            final boolean isFavorite = c.getInt(6) == 1;

            final OsrsItem item = new OsrsItem(id, name, highAlch, currentPrice, buyLimit, isMembers, isFavorite);
            item.setContext(context);


            osrsItems.put(item.getId(), item);
            addItem(item);

            item.getTvName().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(((TextView) view).getText().toString() + " clicked!");
                    Intent intent = new Intent(context, ItemActivity.class);

                    intent.putExtra("osrsItem", item);

                    context.startActivity(intent);
                }
            });

            item.getTvName().setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {
                    Point p = OsrsPopupColumnSelector.getPoint(v);

                    TextView prompt = layout.findViewById(R.id.tvHideItemPrompt);
                    TextView accept = layout.findViewById(R.id.tvHideItemAccept);
                    TextView decline = layout.findViewById(R.id.tvHideItemDecline);

                    prompt.setTypeface(Osrs.typefaces.FONT_REGULAR_BOLD);
                    prompt.setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);
                    prompt.setText("Hide " + name + " for 4 hours?");

                    accept.setTypeface(Osrs.typefaces.FONT_REGULAR);
                    accept.setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);

                    final TextView tis = (TextView) v;

                    accept.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            window.dismiss();
                            System.out.println("Accepted!!!");
                            item.getTableRow().setVisibility(View.GONE);
                            OsrsTable.this.paint();
                            setAlarm(item);
                        }
                    });

                    decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            window.dismiss();
                        }
                    });


                    decline.setTypeface(Osrs.typefaces.FONT_REGULAR);
                    decline.setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);

                    int OFFSET_X = -20;
                    int OFFSET_Y = 50;

                    window.setBackgroundDrawable(new BitmapDrawable());
                    window.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

                    return false;
                }
            });

            item.getIbFavorite().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.toggleFavorite();

                    db.execSQL("Update Item set isFavorite = " + (item.getFavorite() ? "1" : "0") + " where id = " + item.getId());
                    Toast.makeText(context,
                            item.getName() + (item.getFavorite() ? " added" : " removed") + " to favorites",
                            Toast.LENGTH_SHORT).show();
                }
            });

        }

        c.close();
    }

    public void setAlarm(final OsrsItem hiddenItem) {
        final NotificationManager nManager = buildNotificationManager();
        final NotificationCompat.Builder builder = buildNotificationBuilder(hiddenItem);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override public void onReceive( Context context, Intent intent) {
                System.out.println("ON FUCKING RECEIVE");
                context.unregisterReceiver(this); // this == BroadcastReceiver, not Activity
                hiddenItem.getTableRow().setVisibility(View.VISIBLE);
                paint();
                nManager.notify(hiddenItem.getId(), builder.build());
            }
        };

        context.registerReceiver( receiver, new IntentFilter("This doesn't matter"));

        PendingIntent pintent = PendingIntent.getBroadcast( context, 0, new Intent("This doesn't matter"), 0);
        AlarmManager manager = (AlarmManager)(context.getSystemService( Context.ALARM_SERVICE ));

        // set alarm to fire 5 sec (1000*5) from now (SystemClock.elapsedRealtime())
        manager.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000*5, pintent);
    }

    private NotificationManager buildNotificationManager(){
        String NOTIFICATION_CHANNEL_ID = "4655";
        //Notification Channel
        int importance = NotificationManager.IMPORTANCE_MAX;
        @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "PLZ", importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannel(notificationChannel);
        return notificationManager;
    }

    private NotificationCompat.Builder buildNotificationBuilder(OsrsItem item) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra("osrsItems", osrsItems);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent,PendingIntent.FLAG_ONE_SHOT);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.high_alch);

        String NOTIFICATION_CHANNEL_ID = "4655";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.high_alch)
                .setContentTitle("Item available again.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(item.getName() + " available again.")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);
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
    public void sortColumn(String columnName){
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
                a1.add(new Pair<String, Integer>(osrsItems.get(id).getString(columnName), id));
            else
                a2.add(new Pair<Integer, Integer>(osrsItems.get(id).getInt(columnName), id));
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

    public void restoreDefaults(){
        this.reformat(LAYOUT_DEFAULT);
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

        weights[COLUMN_FAVORITE] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_FAVORITE] ? 1 : 0);

        weights[COLUMN_ITEM] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_ITEM] ? 4 : 0);

        weights[COLUMN_ALCH] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_ALCH] ? 2 : 0);
        weights[COLUMN_PRICE] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_PRICE] ? 2 : 0);
        weights[COLUMN_PROFIT] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_PROFIT] ? 2 : 0);
        weights[COLUMN_LIMIT] = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, cols[COLUMN_LIMIT] ? 2 : 0);

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

    public void addItem(OsrsItem item){
        table.addView(item.getTableRow());
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


    private void restoreTable(){
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
        this.reformat(LAYOUT_CURRENT);
    }

    public void refresh(){
        System.out.println("LAYOUT " + LAYOUT_CURRENT.toString());
        reformat(LAYOUT_CURRENT);

        paint();
    }

    public void save(){
        //save the selected columns
        for(int i = 0; i < headers.length; i++){
            String name = (String) headers[i].getTag();
            System.out.println("Saving '" + name + "'");

            editor.putBoolean(name,LAYOUT_CURRENT[i]);
        }

        editor.putLong("PriceUpdate", Osrs.PRICES_LAST_UPDATED);
        editor.putString("SortBy", lastSortedBy);
        editor.putBoolean("SortDesc", sortDesc);

        //use commit since this is called in onPause() and needs to be done immediately
        editor.commit();
    }

    public boolean needsPriceUpdate(){
        // updated within last 4 hrs ? false : true
        return Osrs.PRICES_LAST_UPDATED - 4 * 3600 * 1000 > 0;
    }

    public void filterItems(String toSearch) {
        for (OsrsItem item : osrsItems.values()) {
            if(!item.getName().toLowerCase().contains(toSearch.toLowerCase())) {
                item.getTableRow().setVisibility(View.GONE);
            }
        }

        paint();
    }

    public void resetSearch() {
        for(OsrsItem item : osrsItems.values()){
            item.getTableRow().setVisibility(View.VISIBLE);
        }

        paint();
    }
}

