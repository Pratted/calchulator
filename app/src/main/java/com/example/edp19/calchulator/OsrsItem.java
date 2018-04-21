package com.example.edp19.calchulator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eric on 2/13/18.
 *
 * Basic class to hold attributes related to an OSRS Item.
 *
 */

public class OsrsItem implements Parcelable{
    protected int id;
    protected String name;
    protected int highAlch = 1;
    protected int price = 1;
    protected int limit;
    protected boolean isMembers;
    protected boolean isFavorite;
    protected boolean isBlocked;
    protected boolean isHidden;

    public static Integer PRICE_NATURE_RUNE = 210;
    final public static Integer NATURE_RUNE = 561;

    public OsrsItem(int id, String name, int highAlch, int price, int limit, boolean isMembers, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.highAlch = highAlch;
        this.price = price;
        this.limit = limit;
        this.isMembers = isMembers;
        this.isFavorite = isFavorite;
    }

    public OsrsItem(OsrsItem item){
        this.id = item.id;
        this.name = item.name;
        this.highAlch = item.highAlch;
        this.price = item.price;
        this.limit = item.limit;
        this.isMembers = item.isMembers;
        this.isFavorite = item.isFavorite;
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

    public boolean getBlocked(){
        return isBlocked;
    }

    public boolean getHidden(){
        return isHidden;
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

    public void setPrice(int price){
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }


    public boolean getFavorite(){
        return isFavorite;
    }

    public int getHighAlch() {
        return highAlch;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isMembers() {
        return isMembers;
    }

    public Integer getProfit(){
        return PRICE_NATURE_RUNE + price - highAlch;
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
