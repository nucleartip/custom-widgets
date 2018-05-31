package com.example.manishkumargupta.bargraph;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.lang.ref.WeakReference;
import java.util.List;


public class BarGraph extends View {


    private final InvalidateHandler invalidateHandler;
    private boolean drawGuidelines = true;
    private int fillType = 2;
    private int strokeWidth;
    private int indicatorTextSize;
    private int labelMargin;
    private float heightPerUnit;
    private int currentMaxDrawingHeight = -1;
    private Paint guidelinePaint;
    private Paint barPaint;
    private TextPaint indicatorPaint;
    @Nullable private BarDataRepository barDataRepository;
    private ValueAnimator barAnimator;


    public BarGraph(Context context) {
        this(context, null);
    }

    public BarGraph(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BarGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        indicatorPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        guidelinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        invalidateHandler = new InvalidateHandler(this);
        barAnimator = ValueAnimator.ofInt(0);
        init(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Resources resources = obtainResources(context);
        int guidelineColor = resources.getColor(R.color.guidelinesColor);
        int barColor = resources.getColor(R.color.barColor);
        strokeWidth = resources.getDimensionPixelSize(R.dimen.strokeWidth);
        int indicatorTextColor = resources.getColor(R.color.indicatorTextColor);
        indicatorTextSize = resources.getDimensionPixelSize(R.dimen.indicatorTextSize);
        labelMargin = resources.getDimensionPixelSize(R.dimen.guideline_label_margin);

        if (attrs != null) {
            TypedArray array = getContext().getTheme()
                    .obtainStyledAttributes(attrs, R.styleable.BarGraph, defStyleAttr, defStyleRes);
            try {
                drawGuidelines = array.getBoolean(R.styleable.BarGraph_drawGuidelines, true);
                fillType = array.getInteger(R.styleable.BarGraph_fillType, 2);
                guidelineColor = array.getColor(R.styleable.BarGraph_guidelinesColor, resources.getColor(R.color.guidelinesColor));
                barColor = array.getColor(R.styleable.BarGraph_barColor, resources.getColor(R.color.barColor));
                strokeWidth = array.getDimensionPixelSize(R.styleable.BarGraph_strokeWidth, resources.getDimensionPixelSize(R.dimen
                        .strokeWidth));
                indicatorTextColor = array.getColor(R.styleable.BarGraph_indicatorTextColor, resources.getColor(R.color
                        .indicatorTextColor));
                indicatorTextSize = array.getDimensionPixelSize(R.styleable.BarGraph_indicatorTextSize, resources.getDimensionPixelSize(R
                        .dimen.indicatorTextSize));
                labelMargin = array.getDimensionPixelSize(R.styleable.BarGraph_label_guideline_spacing, resources.getDimensionPixelSize(R
                        .dimen.guideline_label_margin));
            } catch (Exception e) {
                Log.d("#####", e.getMessage(), e);
            } finally {
                array.recycle();
            }
        }


        guidelinePaint.setDither(true);
        guidelinePaint.setColor(guidelineColor);
        guidelinePaint.setStrokeWidth(strokeWidth);
        guidelinePaint.setStrokeCap(Paint.Cap.SQUARE);
        guidelinePaint.setStyle(Paint.Style.FILL);


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

    private Resources obtainResources(Context context) {
        Resources res;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            res = context.getTheme()
                    .getResources();
        } else {
            res = context.getResources();
        }

        return res;
    }


    public void setDrawGuidelines(boolean drawGuidelines) {
        this.drawGuidelines = drawGuidelines;
        invalidateView();
    }

    public void setFillType(int fillType) {
        this.fillType = fillType;
        invalidateView();
    }

    public void setBarColor(@ColorRes int barColor) {
        barPaint.setColor(getResources().getColor(barColor));
        invalidateView();
    }

    public void setGuidelineColor(@ColorRes int guidelineColor) {
        guidelinePaint.setColor(getResources().getColor(guidelineColor));
        invalidateView();
    }

    public void setStrokeWidth(@DimenRes int strokeWidth) {
        this.strokeWidth = getResources().getDimensionPixelSize(strokeWidth);
        guidelinePaint.setStrokeWidth(this.strokeWidth);
        barPaint.setStrokeWidth(this.strokeWidth);
        invalidateView();
    }

    public void setBarDataRepository(@NonNull BarDataRepository barDataRepository) {
        this.barDataRepository = barDataRepository;
        barDataRepository.registerObservableView(this);
        invalidateView();
    }

    public void setIndicatorTextColor(@ColorRes int indicatorTextColor) {
        indicatorPaint.setColor(getResources().getColor(indicatorTextColor));
        invalidateView();
    }

    public void setIndicatorTextSize(@DimenRes int indicatorTextSize) {
        this.indicatorTextSize = getResources().getDimensionPixelSize(indicatorTextSize);
        indicatorPaint.setTextSize(this.indicatorTextSize);
        invalidateView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // reset canvas if, there is nothing to display, so simply paint, some default image
        if (barDataRepository == null || barDataRepository.getEntriesCount() <= 0) {
            clearCanvas(canvas);
            currentMaxDrawingHeight = -1;
            heightPerUnit = -1;
            return;
        }

        int width = getWidth();
        int heigth = getHeight();
        int totalEntires = barDataRepository.getEntriesCount();
        int eachEntryWidth = (width - (getPaddingLeft() + getPaddingRight())) / totalEntires;
        int barWidth = (eachEntryWidth * 50) / 100;

        if (currentMaxDrawingHeight == -1 || heightPerUnit == -1) {
            if (barDataRepository == null || barDataRepository.getEntriesCount() <= 0) {
                heightPerUnit = 0;
                return;
            }

            float max = barDataRepository.getMax().getValue();
            heightPerUnit =((getHeight() - (getPaddingTop() + getPaddingBottom()
                    + indicatorTextSize + labelMargin + strokeWidth)) * 1f / max);
            startBarDrawingAnimation((int) max);
            return;
        }


        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingRight = getPaddingRight();
        int startY = getHeight() - paddingBottom - indicatorTextSize - labelMargin;

        if (drawGuidelines) {
            // Vertical line
            canvas.drawLine(paddingLeft, startY, paddingLeft, paddingTop, guidelinePaint);
            // horizontal line
            canvas.drawLine(getPaddingLeft(), startY, width - paddingRight, startY, guidelinePaint);
        }

        // horizontal indicator and bars
        List<BarEntry> entires = barDataRepository.getEntries();
        // offset from where text has to be drawn
        int offset = getLeft() + getPaddingLeft();

        for (BarEntry entry : entires) {
            // measure the length text is going to take against available space, otherwise ellipsize it
            CharSequence text = TextUtils.ellipsize(entry.getLabel(), indicatorPaint, eachEntryWidth, TextUtils.TruncateAt.END);
            // draw text
            canvas.drawText(text.toString(), offset + eachEntryWidth / 2, heigth - paddingBottom, indicatorPaint);
            // calculate bar bounds
            int drawableHeight = (int) (currentMaxDrawingHeight < entry.getValue() ? currentMaxDrawingHeight : entry.getValue());
            float top = startY - heightPerUnit * drawableHeight - strokeWidth;
            float left = (offset + eachEntryWidth / 2 - barWidth / 2);
            float right = left + barWidth;
            // draw bar
            canvas.drawRect(left, top, right, startY - strokeWidth, barPaint);
            // increase the offset
            offset = offset + eachEntryWidth;
        }
    }


    private void startBarDrawingAnimation(int max) {
        currentMaxDrawingHeight = 0;
        barAnimator = ValueAnimator.ofInt(0, max);
        barAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        barAnimator.setDuration(500);
        barAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentMaxDrawingHeight = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        barAnimator.start();

    }

    private void clearCanvas(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    void invalidateView() {
        currentMaxDrawingHeight = -1;
        heightPerUnit = -1;
        invalidateHandler.removeCallbacksAndMessages(null);
        invalidateHandler.sendEmptyMessageDelayed(1, 5);
    }

    private static class InvalidateHandler extends Handler {

        private WeakReference<BarGraph> weakReference;

        InvalidateHandler(@NonNull BarGraph barGraph) {
            weakReference = new WeakReference<>(barGraph);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference.get() != null) {
                weakReference.get()
                        .invalidate();
            }
        }
    }
}
