package com.example.edp19.calchulator;

import android.system.Os;

/**
 * Created by eric on 2/13/18.
 *
 * Basic class to hold attributes related to an OSRS Item.
 *
 */

public class OsrsItem {
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
}
