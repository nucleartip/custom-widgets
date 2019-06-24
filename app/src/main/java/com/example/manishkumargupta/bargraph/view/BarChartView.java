package com.example.manishkumargupta.bargraph.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.example.manishkumargupta.bargraph.core.BarGraph;
import com.example.manishkumargupta.bargraph.core.DrawAxis;
import com.example.manishkumargupta.bargraph.core.OnBarClickedListener;
import com.example.manishkumargupta.bargraph.R;

import java.util.List;

/**
 * Created by manishkumar.gupta on 20,July,2018
 */
public class BarChartView extends FrameLayout implements OnBarClickedListener {


    private final BarGraph barGraph;
    private View markerView;
    private final FrameLayout.LayoutParams markerParams;
    @LayoutRes private int markerLayout = -1;

    public BarChartView(@NonNull Context context) {
        this(context, null);
    }

    public BarChartView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarChartView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BarChartView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        obtainMarkerLayoutIfAny(attrs, defStyleAttr, defStyleRes);
        barGraph = new BarGraph(context, attrs, defStyleAttr, defStyleRes);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        if (markerLayout != -1) {
            layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.marker_display_area_size);
        }
        markerParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(barGraph, layoutParams);
        barGraph.setOnEntryClickedListener(this);
    }


    public void setDrawAxis(@DrawAxis int drawAxis) {
        barGraph.setDrawAxis(drawAxis);
    }

    public void setFillType(int fillType) {
        barGraph.setFillType(fillType);
    }

    public void setBarColor(@ColorRes int barColor) {
        barGraph.setBarColor(barColor);
    }

    public void setAxisColor(@ColorRes int guidelineColor) {
        barGraph.setAxisColor(guidelineColor);
    }

    public void setStrokeWidth(@DimenRes int strokeWidth) {
        barGraph.setStrokeWidth(strokeWidth);
    }

    public void setBarEntries(@NonNull List<BarEntry> entries) {
        barGraph.setBarDataRepository(entries);
    }

    public void setIndicatorTextColor(@ColorRes int indicatorTextColor) {
        barGraph.setIndicatorTextColor(indicatorTextColor);
    }

    public void setIndicatorTextSize(@DimenRes int indicatorTextSize) {
        barGraph.setIndicatorTextSize(indicatorTextSize);
    }

    private void obtainMarkerLayoutIfAny(@Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null) {
            return;
        }

        try {
            TypedArray array = getContext().getTheme()
                    .obtainStyledAttributes(attrs, R.styleable.BarChartView, defStyleAttr, defStyleRes);
            markerLayout = array.getResourceId(R.styleable.BarChartView_markerLayout, -1);
        } catch (Exception e) {
            Log.d("####", e.getMessage(), e);
        }
    }

    @Override
    public void onEntryClicked(@NonNull BarEntry barEntry, RectF rect) {

        if (markerLayout == -1) {
            return;
        }
        if (markerView == null) {
            markerView = inflate(getContext(), markerLayout, null);
        }

        removeViewInLayout(markerView);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP;
        addView(markerView, markerParams);
        markerView.animate()
                .x(rect.left)
                .y(0)
                .setDuration(0)
                .start();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }
}
