package com.example.manishkumargupta.bargraph;

import android.graphics.RectF;

/**
 * Created by manishkumar.gupta on 17,July,2018
 */
class EntryWrapper {
    final BarEntry barEntry;
    float left, right, top, bottom;
    EntryWrapper(BarEntry entry){
        this.barEntry = entry;
    }

    boolean contains(float x, float y){
        RectF rectF = new RectF(left, top, right, bottom);
        return rectF.contains(x, y);
    }
}
