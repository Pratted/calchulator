package com.example.edp19.calchulator;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;


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
    }

    public static class fonts{
        public static Integer FONT_SIZE_SMALL;
        public static Integer FONT_SIZE_MEDIUM;
        public static Integer FONT_SIZE_LARGE;
    }

    public static class strings {
        public static String MSG_PRICES_UPDATED;
        public static String PREF_MIN_PROF_BOOL;
        public static String PREF_MIN_PROFIT;
        public static String PREFS_PRICE_NATURE_RUNE;
        public static String SWITCH_HIDE_MEMS_ITEMS;
        public static String PREFS_REMOVE_FAVS;
        public static String RESTORE_DEFAULTS;
        public static String RELOAD_TABLE;
        public static String PROMPT_SELECT_COLUMNS;
        public static String NAME_FAVORITE_COLUMN;
        public static String NAME_ITEM_COLUMN;
        public static String NAME_ALCH_COLUMN;
        public static String NAME_PRICE_COLUMN;
        public static String NAME_PROFIT_COLUMN;
        public static String NAME_LIMIT_COLUMN;
        public static String URL_CURRENT_PRICES;
        public static String PREFS_FILE;

        public static String KEY_PRICES_LAST_UPDATED;
        public static String KEY_PRICE_UPDATE_INTERVAL;
        public static String KEY_RESTORE_DEFAULTS;
        public static String KEY_RELOAD_TABLE;
        public static String KEY_REMOVE_ALL_FAVORITES;
        public static String KEY_HIDE_MEMBERS_ITEMS;
        public static String KEY_MIN_PROFIT;

    }

    public Osrs(Context context){
        Resources res = context.getResources();
        AssetManager assets  = context.getAssets();

        typefaces.FONT_REGULAR = Typeface.createFromAsset(assets, "fonts/osrs.ttf");
        typefaces.FONT_REGULAR_BOLD = Typeface.createFromAsset(assets, "fonts/osrs_bold.ttf");

        colors.ORANGE = res.getColor(R.color.osrsOrange);
        colors.BROWN = res.getColor(R.color.osrsBrown);
        colors.LIGHT_BROWN = res.getColor(R.color.osrsLightBrown);

        strings.PROMPT_SELECT_COLUMNS = res.getString(R.string.prompt_select_columns);
        strings.NAME_FAVORITE_COLUMN = res.getString(R.string.name_favorite_column);
        strings.NAME_ITEM_COLUMN = res.getString(R.string.name_item_column);
        strings.NAME_ALCH_COLUMN = res.getString(R.string.name_alch_column);
        strings.NAME_PRICE_COLUMN = res.getString(R.string.name_price_column);
        strings.NAME_PROFIT_COLUMN = res.getString(R.string.name_profit_column);
        strings.NAME_LIMIT_COLUMN = res.getString(R.string.name_limit_column);
        strings.URL_CURRENT_PRICES = res.getString(R.string.url_current_prices);
        strings.PREFS_FILE = res.getString(R.string.file_prefs);
        strings.MSG_PRICES_UPDATED = context.getString(R.string.pricesUpdated);
        strings.KEY_PRICES_LAST_UPDATED = res.getString(R.string.key_price_last_updated);
        strings.RELOAD_TABLE = res.getString(R.string.reloadTable);
        strings.RESTORE_DEFAULTS = res.getString(R.string.restoreDefs);
        strings.SWITCH_HIDE_MEMS_ITEMS = res.getString(R.string.swtich_show_mems_checked);
        strings.PREFS_REMOVE_FAVS = res.getString(R.string.prefs_remove_favs);
        strings.PREFS_PRICE_NATURE_RUNE = res.getString(R.string.prefs_price_nat_rune);
        strings.PREF_MIN_PROFIT = res.getString(R.string.pref_min_profit);
        strings.PREF_MIN_PROF_BOOL = res.getString(R.string.pref_min_profit_boolean);
        strings.KEY_PRICE_UPDATE_INTERVAL = res.getString(R.string.key_price_update_interval);

        fonts.FONT_SIZE_SMALL = res.getInteger(R.integer.font_size_small);
        fonts.FONT_SIZE_MEDIUM = res.getInteger(R.integer.font_size_medium);
        fonts.FONT_SIZE_LARGE = res.getInteger(R.integer.font_size_large);
    }

    private static boolean initialized  = false;

    public static long PRICES_LAST_UPDATED = 0;
    public static Integer PRICE_NATURE_RUNE = 210;
    public static Integer PRICE_UPDATE_INTERVAL = 240;
    final public static Integer ID_NATURE_RUNE = 561;

    public static void initialize(Context context){
        if(!initialized)
            new Osrs(context);

        initialized = true;
    }

}
