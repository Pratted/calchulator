package com.example.edp19.calchulator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * Created by eric on 4/22/18.
 */

public class OsrsItemTimer extends OsrsItem{
    private TextView tvName;

    private CountDownTimer timer;
    private ProgressBar progressBar;
    private ConstraintLayout layout;
    private Drawable background;
    private long ticks = 0;
    private long totalTicks;
    private long totalTime = Osrs.DEFAULT_TIMER;

    private long timeRemaining = Osrs.DEFAULT_TIMER;
    private boolean isRunning = false;

    private WeakReference<OsrsTableItem> item;

    public OsrsItemTimer(Context context, OsrsTableItem parent){
        super(parent);
        item = new WeakReference<>(parent);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (ConstraintLayout) layoutInflater.inflate(R.layout.special_attack, null);

        progressBar = layout.findViewById(R.id.pbSpecialAttack);
        progressBar.setVisibility(View.INVISIBLE);
        background = layout.getBackground();
        layout.setBackgroundDrawable(null);

        tvName = layout.findViewById(R.id.tvSpecialAttack);
        tvName.setVisibility(View.VISIBLE);

        totalTicks = totalTime / 100;
    }

    interface OnItemTimerFinishedListener {
         void onItemTimerFinished(OsrsItem item);
    }

    OnItemTimerFinishedListener listener;

    public void setOnItemTimerFinishedListener(OnItemTimerFinishedListener listener){
        this.listener = listener;
    }

    public void startTimer(long timerStartTime){
        System.out.println("Timer start time: " + timerStartTime);
        long timeElapsed = new Date().getTime() - timerStartTime;
        timeRemaining = totalTime - timeElapsed;

        System.out.println(timeElapsed + " millseconds have elapsed");
        double percentageTimeElapsed = (timeElapsed * 1.0 / totalTime);

        System.out.println("Percent: " + percentageTimeElapsed);
        ticks = (int) (percentageTimeElapsed * totalTicks);

        System.out.println("THE STARTING TICKS IS: " + ticks);

        startTimer();
    }

    public void startTimer(){
        tvName.setGravity(Gravity.CENTER);
        isRunning = true;
        this.show();

        System.out.println("Starting the timer for " + timeRemaining + " milliseconds");

        timer = new CountDownTimer(timeRemaining, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) (((float) ++ticks / (float) totalTicks) * 100);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                ticks = 0;
                isRunning = false;
                timeRemaining = totalTime;

                OsrsItemTimer.this.hide();
                progressBar.setProgress(0);

                tvName.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

                listener.onItemTimerFinished(OsrsItemTimer.this);
                System.out.println("Finished");
            }
        }.start();
    }

    //hides the progress bar and background
    public void hide(){
        tvName.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        progressBar.setVisibility(View.INVISIBLE);
        layout.setBackgroundDrawable(null);
    }

    //shows the progress bar and background
    public void show(){
        layout.setBackgroundDrawable(background);
        progressBar.setVisibility(View.VISIBLE);
    }

    public boolean isRunning() {
        return isRunning;
    }


    public TextView getTextView(){
        return tvName;
    }

    public ConstraintLayout getLayout(){
        return layout;
    }
}
