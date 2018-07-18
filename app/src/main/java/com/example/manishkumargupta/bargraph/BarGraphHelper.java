package com.example.manishkumargupta.bargraph;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by manishkumar.gupta on 17,July,2018
 */
public class BarGraphHelper {

    private final BarGraphInvalidator invalidateHandler;
    @DrawAxis int drawAxis = DrawAxis.BOTH;
    int fillType = 2;
    int strokeWidth;
    int indicatorTextSize;
    int labelMargin;
    float heightPerUnit;
    int currentMaxDrawingHeight = -1;
    boolean displayEmptyPlaceholder = true;
    Paint axisPaint;
    Paint barPaint;
    TextPaint indicatorPaint;
    CharSequence[] emptyLabels = {};
    @Nullable BarDataRepository barDataRepository;
    private ValueAnimator barAnimator;
    private final BarGraph barGraph;
    private final Resources resources;
    private OnEntryClickedListener onEntryClickedListener;

    BarGraphHelper(BarGraph barGraph) {
        resources = barGraph.getResources();
        this.barGraph = barGraph;
        this.invalidateHandler = new BarGraphInvalidator(this);
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barAnimator = ValueAnimator.ofInt(0);
        indicatorTextSize = resources.getDimensionPixelSize(R.dimen.indicatorTextSize);
        labelMargin = resources.getDimensionPixelSize(R.dimen.label_axis_margin);
        strokeWidth = resources.getDimensionPixelSize(R.dimen.strokeWidth);
        int guidelineColor = resources.getColor(R.color.axisColor);
        int barColor = resources.getColor(R.color.barColor);
        int indicatorTextColor = resources.getColor(R.color.indicatorTextColor);
        emptyLabels = resources.getTextArray(R.array.empty_labels);
        initPaint(guidelineColor, barColor, indicatorTextColor);
    }


    public void setDrawAxis(@DrawAxis int drawAxis) {
        this.drawAxis = drawAxis;
        invalidateHandler.invalidate();
    }

    public void setFillType(int fillType) {
        this.fillType = fillType;
        invalidateHandler.invalidate();
    }

    public void setBarColor(@ColorRes int barColor) {
        barPaint.setColor(resources.getColor(barColor));
        invalidateHandler.invalidate();
    }

    public void setAxisColor(@ColorRes int axisColor) {
        axisPaint.setColor(resources.getColor(axisColor));
        invalidateHandler.invalidate();
    }

    public void setStrokeWidth(@DimenRes int strokeWidth) {
        this.strokeWidth = resources.getDimensionPixelSize(strokeWidth);
        axisPaint.setStrokeWidth(this.strokeWidth);
        barPaint.setStrokeWidth(this.strokeWidth);
        invalidateHandler.invalidate();
    }

    public void setBarDataRepository(@NonNull BarDataRepository barDataRepository) {
        this.barDataRepository = barDataRepository;
        barDataRepository.registerObservableView(this);
        invalidateHandler.invalidate();
    }

    public void setIndicatorTextColor(@ColorRes int indicatorTextColor) {
        indicatorPaint.setColor(resources.getColor(indicatorTextColor));
        invalidateHandler.invalidate();
    }

    public void setIndicatorTextSize(@DimenRes int indicatorTextSize) {
        this.indicatorTextSize = resources.getDimensionPixelSize(indicatorTextSize);
        indicatorPaint.setTextSize(this.indicatorTextSize);
        invalidateHandler.invalidate();
    }

    public void setOnEntryClickedListener(OnEntryClickedListener onEntryClickedListener) {
        this.onEntryClickedListener = onEntryClickedListener;
    }

    void invalidate() {
        currentMaxDrawingHeight = -1;
        heightPerUnit = -1;
        barGraph.invalidate();
    }

    void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        int guidelineColor = resources.getColor(R.color.axisColor);
        int barColor = resources.getColor(R.color.barColor);
        int indicatorTextColor = resources.getColor(R.color.indicatorTextColor);
        strokeWidth = resources.getDimensionPixelSize(R.dimen.strokeWidth);
        indicatorTextSize = resources.getDimensionPixelSize(R.dimen.indicatorTextSize);
        labelMargin = resources.getDimensionPixelSize(R.dimen.label_axis_margin);

        if (attrs != null) {
            TypedArray array = context.getTheme()
                    .obtainStyledAttributes(attrs, R.styleable.BarGraph, defStyleAttr, defStyleRes);
            try {
                drawAxis = array.getInt(R.styleable.BarGraph_drawAxis, DrawAxis.BOTH);
                fillType = array.getInteger(R.styleable.BarGraph_fillType, 2);
                guidelineColor = array.getColor(R.styleable.BarGraph_axisColor, resources.getColor(R.color.axisColor));
                barColor = array.getColor(R.styleable.BarGraph_barColor, resources.getColor(R.color.barColor));
                strokeWidth = array.getDimensionPixelSize(R.styleable.BarGraph_strokeWidth, resources.getDimensionPixelSize(R.dimen
                        .strokeWidth));
                indicatorTextColor = array.getColor(R.styleable.BarGraph_indicatorTextColor, resources.getColor(R.color
                        .indicatorTextColor));
                indicatorTextSize = array.getDimensionPixelSize(R.styleable.BarGraph_indicatorTextSize, resources.getDimensionPixelSize(R
                        .dimen.indicatorTextSize));
                labelMargin = array.getDimensionPixelSize(R.styleable.BarGraph_labelAxisSpacing, resources.getDimensionPixelSize(R.dimen
                        .label_axis_margin));
                displayEmptyPlaceholder = array.getBoolean(R.styleable.BarGraph_displayEmptyPlaceholder, true);
                CharSequence[] temp = array.getTextArray(R.styleable.BarGraph_emptyLabels);
                if (temp != null && temp.length > 0) {
                    emptyLabels = temp;
                }
            } catch (Exception e) {
                Log.d("#####", e.getMessage(), e);
            } finally {
                array.recycle();
            }
        }
        initPaint(guidelineColor, barColor, indicatorTextColor);
    }

    private void initPaint(int guidelineColor, int barColor, int indicatorTextColor) {
        axisPaint.setDither(true);
        axisPaint.setColor(guidelineColor);
        axisPaint.setStrokeWidth(strokeWidth);
        axisPaint.setStrokeCap(Paint.Cap.SQUARE);
        axisPaint.setStyle(Paint.Style.FILL);


        barPaint.setDither(true);
        barPaint.setColor(barColor);
        barPaint.setStrokeWidth(strokeWidth);
        barPaint.setStrokeCap(Paint.Cap.SQUARE);
        if (fillType == 1) {
            barPaint.setStyle(Paint.Style.STROKE);
        } else {
            barPaint.setStyle(Paint.Style.FILL);
        }

        indicatorPaint.setDither(true);
        indicatorPaint.setColor(indicatorTextColor);
        indicatorPaint.setTextSize(indicatorTextSize);
        indicatorPaint.setTypeface(Typeface.SANS_SERIF);
        indicatorPaint.setTextAlign(Paint.Align.CENTER);
    }

    void startBarDrawingAnimation(int max) {
        currentMaxDrawingHeight = 0;
        barAnimator = ValueAnimator.ofInt(0, max);
        barAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        barAnimator.setDuration(500);
        barAnimator.addUpdateListener(animation -> {
            currentMaxDrawingHeight = (int) animation.getAnimatedValue();
            barGraph.invalidate();
        });
        barAnimator.start();

    }

    void processOnClick(float x, float y) {
        if (barDataRepository != null && barDataRepository.entries.size() > 0) {
            for (EntryWrapper entryWrapper : barDataRepository.entries) {
                if (entryWrapper.contains(x, y)) {
                    //onEntryClickedListener.onEntryClicked(entryWrapper.barEntry, new RectF(entryWrapper.left, entryWrapper.top,
                    //entryWrapper.right, entryWrapper.bottom));
                    Log.d("####", "entry clicked = " + entryWrapper.barEntry.toString());
                }
            }
        }
    }
}