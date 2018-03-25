package com.example.edp19.calchulator;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageButton;

/**
 * Created by eric on 2/13/18.
 *
 * Basic class to hold attributes related to an OSRS Item.
 *
 */

public class OsrsItem implements Parcelable{
    public int id;
    public String name;
    public int highAlch = 1;
    public int buy = 1;
    public int limit;
    boolean isMembers;
    public boolean isFavorite = false;
    public ImageButton buttonFavorite;

    public OsrsItem(int id, String name, boolean isMembers){
        this.id = id;
        this.name = name;
        this.isMembers = isMembers;
    }

    public OsrsItem(int id, String name, boolean isMembers, int alch, int qty){
        this.id = id;
        this.name = name;
        this.isMembers = isMembers;
        this.highAlch = alch;
        this.limit = qty;
    }

    public OsrsItem(Parcel in){
        id = in.readInt();
        highAlch = in.readInt();
        buy = in.readInt();
        limit = in.readInt();
        name = in.readString();
    }

    public OsrsItem(Cursor c){
        this.id = c.getInt(0);
        this.name = c.getString(1);
        this.highAlch = c.getInt(2);
        this.isMembers = c.getInt(5) == 1;
        this.isFavorite = c.getInt(6) == 1;
        this.limit = c.getInt(4);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(highAlch);
        parcel.writeInt(buy);
        parcel.writeInt(limit);
        parcel.writeString(name);
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
