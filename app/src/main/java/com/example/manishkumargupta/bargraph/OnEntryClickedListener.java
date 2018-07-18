package com.example.manishkumargupta.bargraph;

import android.graphics.RectF;
import android.support.annotation.NonNull;

/**
 * Created by manishkumar.gupta on 17,July,2018
 */
public interface OnEntryClickedListener {
    void onEntryClicked(@NonNull BarEntry barEntry, RectF rect);
}
