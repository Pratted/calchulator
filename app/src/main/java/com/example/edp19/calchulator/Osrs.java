package com.example.edp19.calchulator;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;

import java.io.FileOutputStream;


/**
 * Created by eric on 3/28/18.
 */

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
        public static String PROMPT_SELECT_COLUMNS;
        public static String NAME_FAVORITE_COLUMN;
        public static String NAME_ITEM_COLUMN;
        public static String NAME_ALCH_COLUMN;
        public static String NAME_PRICE_COLUMN;
        public static String NAME_PROFIT_COLUMN;
        public static String NAME_LIMIT_COLUMN;
        public static String URL_CURRENT_PRICES;
        public static String PREFS_FILE;
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
        strings.PREFS_FILE = "osrs.prefs";

        fonts.FONT_SIZE_SMALL = res.getInteger(R.integer.font_size_small);
        fonts.FONT_SIZE_MEDIUM = res.getInteger(R.integer.font_size_medium);
        fonts.FONT_SIZE_LARGE = res.getInteger(R.integer.font_size_large);

        AssetFileDescriptor fd;



    }

    public static long PRICES_LAST_UPDATED = 0;



}
