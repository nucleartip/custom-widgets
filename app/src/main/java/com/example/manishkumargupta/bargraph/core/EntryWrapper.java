package com.example.manishkumargupta.bargraph.core;

import android.graphics.RectF;

import com.example.manishkumargupta.bargraph.view.BarEntry;

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
