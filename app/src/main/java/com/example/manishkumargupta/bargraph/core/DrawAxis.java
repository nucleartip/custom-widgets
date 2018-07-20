package com.example.manishkumargupta.bargraph.core;

import android.support.annotation.IntDef;

import static com.example.manishkumargupta.bargraph.core.DrawAxis.BOTH;
import static com.example.manishkumargupta.bargraph.core.DrawAxis.NONE;
import static com.example.manishkumargupta.bargraph.core.DrawAxis.X_AXIS;
import static com.example.manishkumargupta.bargraph.core.DrawAxis.Y_AXIS;

/**
 * Created by manishkumar.gupta on 18,July,2018
 */
@IntDef({X_AXIS, Y_AXIS, BOTH, NONE})
public @interface DrawAxis {
    int X_AXIS = 1;
    int Y_AXIS = 2;
    int BOTH = 3;
    int NONE = 4;

}
