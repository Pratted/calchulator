package com.example.edp19.calchulator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by edp19 on 3/22/18.
 */

public class OsrsDB extends SQLiteOpenHelper {

    interface OnDBReadyListener {
        void onDBReady(SQLiteDatabase db);
    }

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "osrs.db";

    private static String SQL_DESTROY_DB = "DROP TABLE IF EXISTS Item;";
    private static OsrsDB db;

    //only ever needed when onCreate() is called
    private static WeakReference<Context> context;

    private OsrsDB(Context c) {
        super(c.getApplicationContext(),DATABASE_NAME,null,DATABASE_VERSION);
        context = new WeakReference<>(c);
    }

    public static synchronized OsrsDB getInstance(Context context) {
        if (db == null) {
            db = new OsrsDB(context);
        }
        return db;
    }

    public static void initialize(Context context){
        if(db == null){
            db = new OsrsDB(context);
        }
    }

    public static OsrsItem getItem(int id){
        Cursor c = db.getReadableDatabase().rawQuery("select * from Item where id = " + id, null);
        return getItemFromCursor(c);
    }

    public static void removeAllFavorites(){
        db.getWritableDatabase().execSQL("Update item set isFavorite = 0");
    }

    public static OsrsItem getItemFromCursor(Cursor c){
        int id = c.getInt(0);
        final String name = c.getString(1);
        int highAlch = c.getInt(2);
        int currentPrice = c.getInt(3);
        int buyLimit = c.getInt(4);
        boolean isMembers = c.getInt(5) == 1;
        boolean isFavorite = c.getInt(6) == 1;
        boolean isHidden = c.getInt(7) == 1;
        boolean isBlocked = c.getInt(8) == 1;

        return new OsrsItem(id, name, highAlch, currentPrice, buyLimit, isMembers, isFavorite, isHidden, isBlocked);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("Initializing Database...");

        try{
            Scanner s = new Scanner(context.get().getAssets().open("setup.sql"));
            String script = "";

            //load SQL script into string
            while(s.hasNext()){
                script += s.nextLine() + "\n";
            }

            int i = 0;

            //tokenize SQL script. Can only execute one SQL statement at a time
            for(String cmd: script.split(";")){
                i++;

                //only execute non-empty commands.
                if(cmd.trim().length() > 0){
                    db.execSQL(cmd + ";");
                }
            }

            System.out.println("INSERTED " + i + " items");

        } catch (Exception e){
            System.out.println("Failed to read file!");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DESTROY_DB);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void getWritableDatabase(OnDBReadyListener listener) {
        new OpenDbAsyncTask().execute(listener);
    }

    public static HashMap<Integer, OsrsItem> fetchAllItems(){
        HashMap<Integer, OsrsItem> items = new HashMap<>();
        System.out.println("FectchAllItems");
        Cursor c = db.getReadableDatabase().rawQuery("select * from Item", null);
        System.out.println("AFTER CURSOR");

        while(c.moveToNext()){
            OsrsItem item = OsrsDB.getItemFromCursor(c);
            items.put(item.getId(), item);
        }

        return items;
    }

    public static void save(OsrsItem item){
        ContentValues cv = new ContentValues();
        cv.put("currentPrice", item.getPrice());
        cv.put("isFavorite", item.getFavorite());
        cv.put("isBlocked", item.getBlocked());
        cv.put("isHidden", item.getHidden());

        System.out.println("INSIDE SAVE");
        System.out.println(item.getName() + " " + item.getHidden());
        System.out.println(item.getName() + " " + item.getBlocked());


        db.getWritableDatabase()
                .update("Item", cv, "id = ?", new String[]{String.valueOf(item.getId())});
    }

    //refresh the current table from the DB without redrawing it.
    public static void refreshOsrsTableItemsFromDB(HashMap<Integer, OsrsTableItem> items){
        HashMap<Integer, OsrsItem> newItems = fetchAllItems();

        for(OsrsTableItem old: items.values()){
            int id = old.getId();
            OsrsItem curr = newItems.get(id);

            old.setFavorite(curr.getFavorite());
            old.setHidden(curr.getHidden());
            old.setBlocked(curr.getBlocked());
            old.setPrice(curr.getPrice());
        }
    }

    private static class OpenDbAsyncTask extends AsyncTask<OnDBReadyListener,Void,SQLiteDatabase> {
        OnDBReadyListener listener;

        @Override
        protected SQLiteDatabase doInBackground(OnDBReadyListener... params){
            listener = params[0];
            return OsrsDB.db.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase db) {
            listener.onDBReady(db);
        }
    }
}
