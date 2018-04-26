package com.example.edp19.calchulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;

import java.util.Map;


/**
 * Created by eric on 3/28/18.
 */


//A global namespace to access resources from strings, colors, etc.
public class Osrs {

    public static class typefaces {
        public static Typeface FONT_REGULAR;
        public static Typeface FONT_REGULAR_BOLD;
    }

    public static class colors {
        public static int ORANGE;
        public static int BROWN;
        public static int LIGHT_BROWN;
        public static int LIGHT_GREEN;
        public static int LIGHT_RED;
        public static int RED;
    }

    public static class fonts{
        public static Integer FONT_SIZE_SMALL;
        public static Integer FONT_SIZE_MEDIUM;
        public static Integer FONT_SIZE_LARGE;
    }

    public static class strings {
        public static String PROMPT_SELECT_COLUMNS;
        public static String PROMPT_REMOVE_ALL_FAVORITES;
        public static String PROMPT_BLOCK_ITEM;

        public static String NAME_FAVORITE_COLUMN;
        public static String NAME_ITEM_COLUMN;
        public static String NAME_ALCH_COLUMN;
        public static String NAME_PRICE_COLUMN;
        public static String NAME_PROFIT_COLUMN;
        public static String NAME_LIMIT_COLUMN;
        public static String URL_CURRENT_PRICES;

        public static String YES;
        public static String NO;
        public static String NA;

        public static String KEY_PRICE_NATURE_RUNE;
        public static String KEY_RESTORE_DEFAULTS;
        public static String KEY_PRICES_LAST_UPDATED;
        public static String KEY_PRICE_UPDATE_INTERVAL;
        public static String KEY_RELOAD_TABLE;
        public static String KEY_REMOVE_ALL_FAVORITES;
        public static String KEY_HIDE_MEMBERS_ITEMS;
        public static String KEY_MIN_PROFIT;
        public static String KEY_SORT_BY;
        public static String KEY_SORT_DESCENDING;
        public static String KEY_HAS_DATA_BEEN_MODIFIED;
        public static String KEY_ITEM;
        public static String KEY_OSRS_ITEMS;
        public static String KEY_HIDE_HIDDEN_ITEMS;

        public static String DB_ATTRIBUTE_ID;
        public static String DB_ATTRIBUTE_NAME;
        public static String DB_ATTRIBUTE_ALCH;
        public static String DB_ATTRIBUTE_PRICE;
        public static String DB_ATTRIBUTE_IS_HIDDEN;
        public static String DB_ATTRIBUTE_IS_BLOCKED;
        public static String DB_ATTRIBUTE_IS_FAVORITE;
        public static String DB_ATTRIBUTE_IS_MEMBERS;
        public static String DB_ATTRIBUTE_BUY_LIMIT;
        public static String DB_ATTRIBUTE_TIMER_START_TIME;

        public static String TOAST_REMOVE_ALL_FAVORITES;
        public static String TOAST_BLOCK_ITEM;
        public static String TOAST_ADDED_TO_FAVORITES;
        public static String TOAST_REMOVED_FROM_FAVORITES;
        public static String TOAST_HIDE_ITEM;
        public static String TOAST_UNHIDE_ITEM;

        public static String LABEL_TV_REMOVE_FROM_FAVORITES;
        public static String LABEL_TV_ADD_TO_FAVORITES;
        public static String LABEL_BTN_HIDE;
        public static String LABEL_BTN_UNHIDE;

    }

    public static class files {
        public static String SHARED_PREFERENCES;
        public static String OSRS_DB;
        public static String OSRS_INITIALIZE_DB;
        public static String OSRS_FONT_REGULAR;
        public static String OSRS_FONT_REGULAR_BOLD;
    }

    //read variables from strings, colors, etc. and store into global variables for easy access
    public Osrs(Context context){
        Resources res = context.getResources();
        AssetManager assets  = context.getAssets();

        colors.ORANGE = res.getColor(R.color.osrsOrange);
        colors.BROWN = res.getColor(R.color.osrsBrown);
        colors.LIGHT_BROWN = res.getColor(R.color.osrsLightBrown);
        colors.LIGHT_GREEN = res.getColor(R.color.osrsLightGreen);
        colors.LIGHT_RED = res.getColor(R.color.osrsLightRed);
        colors.RED = res.getColor(R.color.osrsRed);

        strings.PROMPT_SELECT_COLUMNS = res.getString(R.string.prompt_select_columns);
        strings.PROMPT_REMOVE_ALL_FAVORITES = res.getString(R.string.prompt_remove_all_favorites);
        strings.PROMPT_BLOCK_ITEM = res.getString(R.string.prompt_block_item);

        strings.NAME_FAVORITE_COLUMN = res.getString(R.string.name_favorite_column);
        strings.NAME_ITEM_COLUMN = res.getString(R.string.name_item_column);
        strings.NAME_ALCH_COLUMN = res.getString(R.string.name_alch_column);
        strings.NAME_PRICE_COLUMN = res.getString(R.string.name_price_column);
        strings.NAME_PROFIT_COLUMN = res.getString(R.string.name_profit_column);
        strings.NAME_LIMIT_COLUMN = res.getString(R.string.name_limit_column);
        strings.URL_CURRENT_PRICES = res.getString(R.string.url_current_prices);

        strings.KEY_PRICE_NATURE_RUNE = res.getString(R.string.prefs_price_nat_rune);
        strings.KEY_HIDE_MEMBERS_ITEMS = res.getString(R.string.key_hide_members_items);
        strings.KEY_RESTORE_DEFAULTS = res.getString(R.string.key_restore_defaults);
        strings.KEY_PRICES_LAST_UPDATED = res.getString(R.string.key_price_last_updated);
        strings.KEY_MIN_PROFIT = res.getString(R.string.key_min_profit);
        strings.KEY_REMOVE_ALL_FAVORITES = res.getString(R.string.key_remove_favorites);
        strings.KEY_RELOAD_TABLE = res.getString(R.string.key_reload_table);
        strings.KEY_PRICE_UPDATE_INTERVAL = res.getString(R.string.key_price_update_interval);
        strings.KEY_SORT_BY = res.getString(R.string.key_sort_by);
        strings.KEY_SORT_DESCENDING = res.getString(R.string.key_sort_descending);
        strings.KEY_HAS_DATA_BEEN_MODIFIED = res.getString(R.string.key_has_data_been_modified);
        strings.KEY_ITEM = res.getString(R.string.key_item);
        strings.KEY_OSRS_ITEMS = res.getString(R.string.key_osrs_items);
        strings.KEY_HIDE_HIDDEN_ITEMS = res.getString(R.string.key_hide_hidden_items);

        strings.DB_ATTRIBUTE_ID = res.getString(R.string.db_attribute_id);
        strings.DB_ATTRIBUTE_NAME = res.getString(R.string.db_attribute_name);
        strings.DB_ATTRIBUTE_ALCH = res.getString(R.string.db_attribute_alch);
        strings.DB_ATTRIBUTE_PRICE = res.getString(R.string.db_attribute_price);
        strings.DB_ATTRIBUTE_IS_HIDDEN = res.getString(R.string.db_attribute_is_hidden);
        strings.DB_ATTRIBUTE_IS_BLOCKED = res.getString(R.string.db_attribute_is_blocked);
        strings.DB_ATTRIBUTE_IS_FAVORITE = res.getString(R.string.db_attribute_is_favorite);
        strings.DB_ATTRIBUTE_IS_MEMBERS = res.getString(R.string.db_attribute_is_members);
        strings.DB_ATTRIBUTE_BUY_LIMIT = res.getString(R.string.db_attribute_buy_limit);
        strings.DB_ATTRIBUTE_TIMER_START_TIME = res.getString(R.string.db_attribute_timer_start_time);

        strings.TOAST_ADDED_TO_FAVORITES = res.getString(R.string.toast_added_to_favorites);
        strings.TOAST_REMOVED_FROM_FAVORITES = res.getString(R.string.toast_removed_from_favorites);
        strings.TOAST_BLOCK_ITEM = res.getString(R.string.toast_block_item);
        strings.TOAST_REMOVE_ALL_FAVORITES = res.getString(R.string.toast_remove_all_favorites);
        strings.TOAST_HIDE_ITEM = res.getString(R.string.toast_hide_item);
        strings.TOAST_UNHIDE_ITEM = res.getString(R.string.toast_unhide_item);

        strings.LABEL_TV_ADD_TO_FAVORITES = res.getString(R.string.label_tv_add_to_favorites);
        strings.LABEL_TV_REMOVE_FROM_FAVORITES = res.getString(R.string.label_tv_remove_from_favorites);
        strings.LABEL_BTN_HIDE = res.getString(R.string.label_btn_hide);
        strings.LABEL_BTN_UNHIDE = res.getString(R.string.label_btn_unhide);

        strings.YES = res.getString(R.string.yes);
        strings.NO = res.getString(R.string.no);
        strings.NA = res.getString(R.string.na);

        fonts.FONT_SIZE_SMALL = res.getInteger(R.integer.font_size_small);
        fonts.FONT_SIZE_MEDIUM = res.getInteger(R.integer.font_size_medium);
        fonts.FONT_SIZE_LARGE = res.getInteger(R.integer.font_size_large);

        files.SHARED_PREFERENCES = res.getString(R.string.file_prefs);
        files.OSRS_DB = res.getString(R.string.file_database);
        files.OSRS_INITIALIZE_DB = res.getString(R.string.file_init_database);
        files.OSRS_FONT_REGULAR = res.getString(R.string.file_font_regular);
        files.OSRS_FONT_REGULAR_BOLD = res.getString(R.string.file_font_regular_bold);

        typefaces.FONT_REGULAR = Typeface.createFromAsset(assets, files.OSRS_FONT_REGULAR);
        typefaces.FONT_REGULAR_BOLD = Typeface.createFromAsset(assets, files.OSRS_FONT_REGULAR_BOLD);
    }

    private static boolean initialized  = false;

    public static long PRICES_LAST_UPDATED = 0;
    public static Integer PRICE_NATURE_RUNE = 210;


    public static Integer DEFAULT_TIMER = 30000;
    public static Integer PRICE_UPDATE_INTERVAL = 240;
    final public static Integer ID_NATURE_RUNE = 561;


    //initialize all globals from strings, colors, etc.
    public static void initialize(Context context){
        if(!initialized)
            new Osrs(context);

        initialized = true;
    }

    public static void printPrefKeys(SharedPreferences prefs){
        System.out.println("-------------------- Shared Prefs --------------------");
        for(Map.Entry<String,?>  entry : prefs.getAll().entrySet()){
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
