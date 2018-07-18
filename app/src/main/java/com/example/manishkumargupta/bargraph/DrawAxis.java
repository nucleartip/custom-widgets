package com.example.manishkumargupta.bargraph;

import android.support.annotation.IntDef;

import static com.example.manishkumargupta.bargraph.DrawAxis.BOTH;
import static com.example.manishkumargupta.bargraph.DrawAxis.NONE;
import static com.example.manishkumargupta.bargraph.DrawAxis.X_AXIS;
import static com.example.manishkumargupta.bargraph.DrawAxis.Y_AXIS;

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
