package com.example.edp19.calchulator;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;

/**
 * Created by eric on 2/13/18.
 *
 * Basic class to hold attributes related to an OSRS Item.
 *
 */

public class OsrsItem implements Parcelable{
    private int id;
    private String name;
    private int highAlch = 1;
    private int price = 1;
    private int limit;
    private boolean isMembers;
    private boolean isFavorite = false;

    public static Integer PRICE_NATURE_RUNE = 210;
    final public static Integer NATURE_RUNE = 561;
    private Context context;

    public OsrsItem(int id, String name, int highAlch, int price, int limit, boolean isMembers, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.highAlch = highAlch;
        this.price = price;
        this.limit = limit;
        this.isMembers = isMembers;
        this.isFavorite = isFavorite;
    }

    public void setContext(Context context){
        this.context = context;

        row = new TableRow(context);

        tvName = new TextView(context);
        tvPrice = new TextView(context);
        tvHighAlch = new TextView(context);
        tvLimit = new TextView(context);
        tvProfit = new TextView(context);
        ibFavorite = new ImageButton(context);
        tvProfit = new TextView(context);

        tvPrice.setGravity(Gravity.RIGHT);
        tvHighAlch.setGravity(Gravity.RIGHT);
        tvProfit.setGravity(Gravity.RIGHT);
        tvLimit.setGravity(Gravity.RIGHT);

        ibFavorite.setBackgroundDrawable(null);
        ibFavorite.setPadding(0,-8,0,0);
        tvName.setTextSize(18);

        setId(id);
        setName(name);
        setHighAlch(highAlch);
        setPrice(price);
        setLimit(limit);
        setMembers(isMembers);
        setFavorite(isFavorite);

        formatTextViews(tvName, tvPrice, tvHighAlch, tvLimit, tvPrice);

        row.addView(ibFavorite);
        row.addView(tvName);
        row.addView(tvHighAlch);
        row.addView(tvPrice);
        row.addView(tvProfit);
        row.addView(tvLimit);
        row.setId(id);
    }

    private TextView tvLimit;
    private TextView tvName;
    private ImageButton ibFavorite;
    private TextView tvPrice;
    private TextView tvHighAlch;
    private TextView tvProfit;

    public TableRow getTableRow() {
        return row;
    }

    public void setTableRow(TableRow row){
        this.row = row;
    }

    private TableRow row;

    private void formatTextViews(TextView... views){
        for(TextView view: views){
            view.setTypeface(Osrs.typefaces.FONT_REGULAR);
            view.setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);
            view.setTextColor(Color.WHITE);
        }
    }

    public int getInt(String name){
        if(name.compareTo(Osrs.strings.NAME_ALCH_COLUMN) == 0) return highAlch;
        if(name.compareTo(Osrs.strings.NAME_PRICE_COLUMN) == 0) return price;
        if(name.compareTo(Osrs.strings.NAME_PROFIT_COLUMN) == 0) return getProfit();
        if(name.compareTo(Osrs.strings.NAME_LIMIT_COLUMN) == 0) return limit;
        if(name.compareTo(Osrs.strings.NAME_FAVORITE_COLUMN) == 0) return isFavorite ? 1 : 0;

        return 0;
    }

    public String getString(String name){
        if(name.compareTo(Osrs.strings.NAME_ITEM_COLUMN) == 0) return this.name;

        return "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        tvName.setText(name);
    }

    public void setTvName(TextView tv){
        tvName = tv;
        setName(name);
    }

    public TextView getTvName(){
        return tvName;
    }

    public void setPrice(int price){
        this.price = price;

        if(tvPrice != null){
            tvPrice.setText(String.valueOf(price));
        }
    }

    public void setTvPrice(TextView tv){
        tvPrice = tv;
        tv.setText(String.valueOf(price));
    }

    public TextView getTvPrice(){
        return tvPrice;
    }

    public Integer getPrice() {
        return price;
    }

    public void setFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
        ibFavorite.setImageResource(isFavorite ?
                android.R.drawable.star_on :
                android.R.drawable.star_off);
    }

    public void setIbFavorite(ImageButton ib){
        ibFavorite = ib;
        setFavorite(isFavorite);
    }

    public void toggleFavorite(){
        setFavorite(!isFavorite);
    }

    public boolean getFavorite(){
        return isFavorite;
    }

    public ImageButton getIbFavorite() {
        return ibFavorite;
    }

    public int getHighAlch() {
        return highAlch;
    }

    public void setHighAlch(int highAlch) {
        this.highAlch = highAlch;

        tvHighAlch.setText(String.valueOf(highAlch));
    }

    public TextView getTvHighAlch() {
        return tvHighAlch;
    }

    public void setTvHighAlch(TextView tvHighAlch) {
        this.tvHighAlch = tvHighAlch;
        setHighAlch(highAlch);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        tvLimit.setText(String.valueOf(limit == -1 ? "N/A": limit));
    }

    public TextView getTvLimit() {
        return tvLimit;
    }

    public void setTvLimit(TextView tvLimit) {
        this.tvLimit = tvLimit;
        setLimit(limit);
    }

    public boolean isMembers() {
        return isMembers;
    }

    public void setMembers(boolean members) {
        isMembers = members;
    }

    public Integer getProfit(){
        return highAlch - PRICE_NATURE_RUNE + price;
    }

    public OsrsItem(Parcel in){
        id = in.readInt();
        name = in.readString();
        highAlch = in.readInt();
        price = in.readInt();
        limit = in.readInt();
        isMembers = in.readInt() == 1;
        isFavorite = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(highAlch);
        parcel.writeInt(price);
        parcel.writeInt(limit);
        parcel.writeInt(isMembers ? 1 : 0);
        parcel.writeInt(isFavorite ? 1 : 0);

        tvHighAlch = null;
        tvPrice = null;
        tvName = null;
        tvProfit = null;
        tvLimit = null;
        ibFavorite = null;
        row = null;
    }

    public static final Parcelable.Creator<OsrsItem> CREATOR = new Parcelable.Creator<OsrsItem>(){
        @Override
        public OsrsItem createFromParcel(Parcel parcel) {
            return new OsrsItem(parcel);
        }

        @Override
        public OsrsItem[] newArray(int size) {
            return new OsrsItem[size];
        }
    };

}
