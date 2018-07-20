package com.example.manishkumargupta.bargraph.core;

import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.example.manishkumargupta.bargraph.view.BarEntry;

/**
 * Created by manishkumar.gupta on 17,July,2018
 */
public interface OnBarClickedListener {
    void onEntryClicked(@NonNull BarEntry barEntry, RectF rect);
}
