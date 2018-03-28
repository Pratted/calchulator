package com.example.edp19.calchulator;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
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
    public static Context CONTEXT;

    public static Typeface typeface;

    public OsrsItem(int id, String name, int highAlch, int price, int limit, boolean isMembers, boolean isFavorite) {
        row = new TableRow(CONTEXT);

        tvName = new TextView(CONTEXT);
        tvPrice = new TextView(CONTEXT);
        tvHighAlch = new TextView(CONTEXT);
        tvLimit = new TextView(CONTEXT);
        tvProfit = new TextView(CONTEXT);
        ibFavorite = new ImageButton(CONTEXT);
        tvProfit = new TextView(CONTEXT);

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

    /*
    Favorite
    Name
    Buy
    High Alch
    Profit
    Buy Limit
     */
    public void setColumnWeights(float wIsFavorite, float wName, float wHighAlch, float wPrice, float wProfit, float wLimit){
        ibFavorite.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, wIsFavorite));
        tvName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, wName));
        tvHighAlch.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, wHighAlch));
        tvPrice.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, wPrice));
        tvProfit.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, wProfit));
        tvLimit.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, wLimit));

        for(int i = 0; i < row.getChildCount(); i++){
            row.getChildAt(i).setVisibility(View.VISIBLE);
        }

        if(wIsFavorite == 0) row.getChildAt(0).setVisibility(View.GONE);
        if(wName == 0) row.getChildAt(1).setVisibility(View.GONE);
        if(wHighAlch == 0) row.getChildAt(2).setVisibility(View.GONE);
        if(wPrice == 0) row.getChildAt(3).setVisibility(View.GONE);
        if(wProfit == 0) row.getChildAt(4).setVisibility(View.GONE);
        if(wLimit == 0) row.getChildAt(5).setVisibility(View.GONE);
    }

    private void formatTextViews(TextView... views){
        for(TextView view: views){
            view.setTypeface(typeface);
            view.setTextColor(Color.WHITE);
        }
    }

    public int getInt(String name){
        if(name.compareTo("Alch") == 0) return highAlch;
        if(name.compareTo("Price") == 0) return price;
        if(name.compareTo("Profit") == 0) return getProfit();
        if(name.compareTo("Limit") == 0) return limit;
        if(name.compareTo("Favorite") == 0) return isFavorite ? 1 : 0;

        return 0;
    }

    public String getString(String name){
        if(name.compareTo("Item") == 0) return this.name;

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
        tvLimit.setText(String.valueOf(limit == 0 ? "NA": limit));
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

    public void hide(){
        row.setVisibility(View.GONE);
    }

    public void show(){
        row.setVisibility(View.VISIBLE);
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
