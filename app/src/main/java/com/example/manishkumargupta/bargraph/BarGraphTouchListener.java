package com.example.manishkumargupta.bargraph;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by manishkumar.gupta on 17,July,2018
 */
class BarGraphTouchListener implements View.OnTouchListener {

    private final static int TOUCH_TIME_THRESHOLD = 200;
    private final static int MOVEMENT_THRESHOLD = 100;
    private final BarGraphHelper barGraphHelper;

    private float startX;
    private float startY;
    private long touchTime;

    BarGraphTouchListener(BarGraphHelper barGraphHelper) {
        this.barGraphHelper = barGraphHelper;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                touchTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                float diffX = Math.abs(startX - event.getX());
                float diffY = Math.abs(startY - event.getY());
                long diffTime = System.currentTimeMillis() - touchTime;
                if (diffX <= MOVEMENT_THRESHOLD && diffY <= MOVEMENT_THRESHOLD && diffTime <= TOUCH_TIME_THRESHOLD) {
                    // its a click
                    barGraphHelper.processOnClick(startX, startY);
                }
                break;

        }

        return true;
    }
}
