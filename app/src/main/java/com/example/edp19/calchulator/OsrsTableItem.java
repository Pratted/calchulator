package com.example.edp19.calchulator;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by eric on 4/21/18.
 */

public class OsrsTableItem extends OsrsItem {

    private TextView tvLimit;
    private TextView tvName;
    private ImageButton ibFavorite;
    private TextView tvPrice;
    private TextView tvHighAlch;
    private TextView tvProfit;
    private TableRow row;

    private OsrsItemTimer timer;

    public OsrsTableItem(Context context, OsrsItem parent){
        super(parent);

        row = new TableRow(context);
        row.setMinimumHeight(100);

        //tvName = new TextView(context);
        tvPrice = new TextView(context);
        tvHighAlch = new TextView(context);
        tvLimit = new TextView(context);
        tvProfit = new TextView(context);
        ibFavorite = new ImageButton(context);
        tvProfit = new TextView(context);

        timer = new OsrsItemTimer(context, this);
        tvName = timer.getTextView();

        tvName.setMaxLines(2);

        tvPrice.setGravity(Gravity.RIGHT);
        tvHighAlch.setGravity(Gravity.RIGHT);
        tvProfit.setGravity(Gravity.RIGHT);
        tvLimit.setGravity(Gravity.RIGHT);

        ibFavorite.setBackgroundDrawable(null);
        ibFavorite.setPadding(0,-8,0,0);
        tvName.setTextSize(18);
        tvName.setPadding(4,4,2,2);

        setName(name);
        setHighAlch(highAlch);
        setPrice(price);
        setLimit(limit);
        setMembers(isMembers);
        setFavorite(isFavorite);
        refreshProfit();

        formatTextViews(tvName, tvPrice, tvHighAlch, tvLimit, tvProfit);

        row.addView(ibFavorite);
        row.addView(timer.getLayout());
        row.addView(tvHighAlch);
        row.addView(tvPrice);
        row.addView(tvProfit);
        row.addView(tvLimit);
        row.setId(id);
    }

    //restores an existing timer
    public void restoreTimer(long timerStartTime){
        timer.startTimer(timerStartTime);
    }

    public void startTimer(){
        timer.startTimer();
    }

    public OsrsItemTimer getTimer() {
        return timer;
    }



    public TableRow getTableRow() {
        return row;
    }

    private void formatTextViews(TextView... views){
        for(TextView view: views){
            view.setTypeface(Osrs.typefaces.FONT_REGULAR);
            view.setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);
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


    public TextView getTvName(){
        return tvName;
    }

    public void setPrice(int price){
        this.price = price;
        tvPrice.setText(String.valueOf(price));
    }

    public void refreshProfit() {
        tvProfit.setText(String.valueOf(getProfit()));
    }

    public void setFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
        ibFavorite.setImageResource(isFavorite ?
                android.R.drawable.star_on :
                android.R.drawable.star_off);
    }

    public void setHidden(boolean hidden){
        this.isHidden = hidden;
        row.setVisibility(hidden ? View.GONE : View.VISIBLE);
    }

    public void setBlocked(boolean blocked){
        this.isBlocked = blocked;
        row.setVisibility(blocked ? View.GONE : View.VISIBLE);
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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        tvLimit.setText(String.valueOf(limit == -1 ? Osrs.strings.NA: limit));
    }

    public void hide(){
        row.setVisibility(View.GONE);
    }

    public void show(){
        row.setVisibility(View.VISIBLE);
    }

    public boolean isMembers() {
        return isMembers;
    }

    public void setMembers(boolean members) {
        isMembers = members;
    }

    public Integer getProfit(){
        return highAlch - Osrs.PRICE_NATURE_RUNE - price;
    }
}
