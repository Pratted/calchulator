package com.example.edp19.calchulator;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by eric on 3/28/18.
 */

public class OsrsPopupColumnSelector {
    private PopupWindow window;
    private LinearLayout layout;
    private Switch swHiddenItems;
    private boolean isSwitchChecked;

    private int OFFSET_X = -20;
    private int OFFSET_Y = 50;

    CheckBox checkboxes[];

    public static Point getPoint(View v){
        int[] location = new int[2];

        v.getLocationOnScreen(location);

        Point point = new Point();
        point.x = location[0];
        point.y = location[1];

        return point;
    }

    public OsrsPopupColumnSelector(Context context){
        checkboxes = new CheckBox[6];

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) layoutInflater.inflate(R.layout.popup_header, null);

        swHiddenItems = layout.findViewById(R.id.swHiddenItems);
        isSwitchChecked = swHiddenItems.isChecked();

        window = new PopupWindow(context);
        window.setContentView(layout);
        window.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);


        ((TextView) layout.getChildAt(0)).setTypeface(Osrs.typefaces.FONT_REGULAR_BOLD);
        ((TextView) layout.getChildAt(0)).setTextSize(Osrs.fonts.FONT_SIZE_LARGE);

        for(int i = 1, j = 0; i < layout.getChildCount()-1; i++, j++){
            checkboxes[j] = (CheckBox) layout.getChildAt(i);
            checkboxes[j].setTextColor(Osrs.colors.ORANGE);
            checkboxes[j].setTextSize(Osrs.fonts.FONT_SIZE_MEDIUM);
            checkboxes[j].setTypeface(Osrs.typefaces.FONT_REGULAR_BOLD);

            final CheckBox c = checkboxes[j];
            checkboxes[j].setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int selected = totalChecked();
                    System.out.println(c.getText() + " checked (" + selected + " total)");

                    if(totalChecked() == 4){
                        disableAllCheckboxes();
                    }
                    else{
                        enableAllCheckboxes();
                    }
                }
            });
        }

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                System.out.println("Calling dismiss()");
                dismiss();
            }
        });
    }

    public interface OnDismissListener {
        void onDismiss(boolean switchChecked);
    }

    private OnDismissListener onDismissListener;

    public void setOnDismissListener(OnDismissListener listener){
        onDismissListener = listener;
    }

    public void dismiss(){
        if(onDismissListener != null){
            onDismissListener.onDismiss(isSwitchChecked);
        }
    }

    public boolean getIsSwitchChecked() {
        return isSwitchChecked;
    }

    public boolean[] getSelectedColumns(){
        boolean cols[] = new boolean[6];

        for(int i = 0; i < checkboxes.length; i++){
            cols[i] = checkboxes[i].isChecked();
        }

        return cols;
    }

    private int totalChecked(){
        int n = 0;

        for(CheckBox cb: checkboxes){
            if(cb.isChecked())
                n++;
        }

        return n;
    }

    public void selectColumns(boolean[] cols){
        for(int i = 0; i < checkboxes.length; i++){
            checkboxes[i].setChecked(cols[i]);
        }
    }

    private void enableAllCheckboxes(){
        for(CheckBox cb: checkboxes){
            if(!cb.isChecked())
                cb.setEnabled(true);
        }
    }

    private void disableAllCheckboxes(){
        for(CheckBox cb: checkboxes){
            if(!cb.isChecked())
                cb.setEnabled(false);
        }
    }

    public void show(View view){
        Point p = getPoint(view);

        window.setBackgroundDrawable(new BitmapDrawable());
        window.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
    }
}
