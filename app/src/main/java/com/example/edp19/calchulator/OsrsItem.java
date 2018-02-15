package com.example.edp19.calchulator;

import android.os.Parcel;
import android.os.Parcelable;
import android.system.Os;

/**
 * Created by eric on 2/13/18.
 *
 * Basic class to hold attributes related to an OSRS Item.
 *
 */

public class OsrsItem implements Parcelable{
    public int id;
    public String name;
    public int highAlch;
    public int buy;
    public int limit;
    boolean isMembers;

    public OsrsItem(int id, String name, boolean isMembers){
        this.id = id;
        this.name = name;
        this.isMembers = isMembers;
    }

    public OsrsItem(Parcel in){
        id = in.readInt();
        highAlch = in.readInt();
        buy = in.readInt();
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
