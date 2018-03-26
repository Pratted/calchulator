package com.example.edp19.calchulator;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
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

    public static Integer PRICE_NAT;
    public static Context CONTEXT;
    public static TableLayout TABLE;

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

        row.addView(ibFavorite);//, 0, TableRow.LayoutParams.WRAP_CONTENT);
        row.addView(tvName);//, 200, TableRow.LayoutParams.WRAP_CONTENT);
        row.addView(tvPrice);//, 50, TableRow.LayoutParams.WRAP_CONTENT);
        row.addView(tvHighAlch);//, 50, TableRow.LayoutParams.WRAP_CONTENT);
        row.addView(tvProfit);
        row.addView(tvLimit);//, 50, TableRow.LayoutParams.WRAP_CONTENT);
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

        ibFavorite = (ImageButton) row.getChildAt(0);
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
    public void setColumnWeights(float wIsFavorite, float wName, float wPrice, float wHighAlch, float wProfit, float wLimit){
        float maxWidth = ((TableLayout) row.getParent()).getWidth();
        float weight = wName + wHighAlch + wProfit+ wPrice + wLimit + wIsFavorite + 0.0000001f;

        wIsFavorite = wIsFavorite / weight * maxWidth;
        wName = wName / weight * maxWidth;
        wPrice = wPrice / weight * maxWidth;
        wHighAlch = wHighAlch / weight * maxWidth;
        wProfit = wProfit / weight * maxWidth;
        wLimit = wLimit / weight * maxWidth;

        System.out.println(wIsFavorite);
        System.out.println(wName);
        System.out.println(wPrice);
        System.out.println(wHighAlch);
        System.out.println(wProfit);
        System.out.println(wLimit);

        row.getChildAt(0).setLayoutParams(new TableRow.LayoutParams((int) wIsFavorite, TableRow.LayoutParams.WRAP_CONTENT));
        row.getChildAt(1).setLayoutParams(new TableRow.LayoutParams((int) wName, TableRow.LayoutParams.WRAP_CONTENT));
        row.getChildAt(2).setLayoutParams(new TableRow.LayoutParams((int) wPrice, TableRow.LayoutParams.WRAP_CONTENT));
        row.getChildAt(3).setLayoutParams(new TableRow.LayoutParams((int) wHighAlch, TableRow.LayoutParams.WRAP_CONTENT));
        row.getChildAt(4).setLayoutParams(new TableRow.LayoutParams((int) wProfit, TableRow.LayoutParams.WRAP_CONTENT));
        row.getChildAt(5).setLayoutParams(new TableRow.LayoutParams((int) wLimit, TableRow.LayoutParams.WRAP_CONTENT));

        System.out.println("Parent " + row.getParent());
        System.out.println("Width: " + maxWidth);
    }

    private void formatTextViews(TextView... views){
        for(TextView view: views){
            //view.setTypeface(typeface);
            view.setTextColor(Color.WHITE);
        }
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
        return highAlch - PRICE_NAT + price;
    }


    public OsrsItem(Parcel in){
        id = in.readInt();
        highAlch = in.readInt();
        price = in.readInt();
        limit = in.readInt();
        name = in.readString();
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
