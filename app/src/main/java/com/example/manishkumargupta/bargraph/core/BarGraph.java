package com.example.manishkumargupta.bargraph.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.example.manishkumargupta.bargraph.view.BarEntry;

import java.util.List;

/**
 * Draws a bar graph from data obtained through class BarDataRepository
 */
public class BarGraph extends View {
    private final BarGraphHelper barGraphHelper;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BarGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.barGraphHelper = new BarGraphHelper(this);
        this.barGraphHelper.init(context, attrs, defStyleAttr, defStyleRes);
        this.setOnTouchListener(new BarGraphTouchListener(barGraphHelper));
    }

    public void setDrawAxis(@DrawAxis int drawAxis) {
        barGraphHelper.setDrawAxis(drawAxis);
    }

    public void setFillType(int fillType) {
        barGraphHelper.setFillType(fillType);
    }

    public void setBarColor(@ColorRes int barColor) {
        barGraphHelper.setBarColor(barColor);
    }

    public void setAxisColor(@ColorRes int guidelineColor) {
        barGraphHelper.setAxisColor(guidelineColor);
    }

    public void setStrokeWidth(@DimenRes int strokeWidth) {
        barGraphHelper.setStrokeWidth(strokeWidth);
    }

    public void setBarDataRepository(@NonNull List<BarEntry> entries) {
        barGraphHelper.setBarEntries(entries);
    }

    public void setIndicatorTextColor(@ColorRes int indicatorTextColor) {
        barGraphHelper.setIndicatorTextColor(indicatorTextColor);
    }

    public void setIndicatorTextSize(@DimenRes int indicatorTextSize) {
        barGraphHelper.setIndicatorTextSize(indicatorTextSize);
    }

    public void setOnEntryClickedListener(OnBarClickedListener onBarClickedListener) {
        barGraphHelper.setOnBarClickedListener(onBarClickedListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // reset canvas if, there is nothing to display, so simply paint, some default image
        if (barGraphHelper.barDataRepository == null || barGraphHelper.barDataRepository.getEntriesCount() <= 0) {
            barGraphHelper.currentMaxDrawingHeight = -1;
            barGraphHelper.heightPerUnit = -1;
            if (barGraphHelper.displayEmptyPlaceholder) {
                drawEmptyPlaceholder(canvas);
            } else {
                clearCanvas(canvas);
            }
            return;
        }

        int width = getWidth();
        int heigth = getHeight();
        int totalEntires = barGraphHelper.barDataRepository.getEntriesCount();
        int eachEntryWidth = (width - (getPaddingLeft() + getPaddingRight())) / totalEntires;
        int barWidth = (eachEntryWidth * 90) / 100;

        if (barGraphHelper.currentMaxDrawingHeight == -1 || barGraphHelper.heightPerUnit == -1) {
            if (barGraphHelper.barDataRepository == null || barGraphHelper.barDataRepository.getEntriesCount() <= 0) {
                barGraphHelper.heightPerUnit = 0;
                return;
            }

            float max = barGraphHelper.barDataRepository.getMax()
                    .getValue();
            barGraphHelper.heightPerUnit = ((getHeight() - (getPaddingTop() + getPaddingBottom() + barGraphHelper.indicatorTextSize +
                    barGraphHelper.labelMargin + barGraphHelper.strokeWidth)) * 1f / max);
            barGraphHelper.startBarDrawingAnimation((int) max);
            return;
        }


        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingRight = getPaddingRight();
        int startY = getHeight() - paddingBottom - barGraphHelper.indicatorTextSize - barGraphHelper.labelMargin;

        switch (barGraphHelper.drawAxis) {
            case DrawAxis.X_AXIS:
                canvas.drawLine(getPaddingLeft(), startY, width - paddingRight, startY, barGraphHelper.axisPaint);
                break;
            case DrawAxis.Y_AXIS:
                canvas.drawLine(paddingLeft, startY, paddingLeft, paddingTop, barGraphHelper.axisPaint);
                break;
            case DrawAxis.BOTH:
                canvas.drawLine(paddingLeft, startY, paddingLeft, paddingTop, barGraphHelper.axisPaint);
                canvas.drawLine(getPaddingLeft(), startY, width - paddingRight, startY, barGraphHelper.axisPaint);
                break;
            case DrawAxis.NONE:
            default:
                //EMPTY
                break;

        }

        // horizontal indicator and bars
        List<EntryWrapper> entires = barGraphHelper.barDataRepository.getEntries();
        // offset from where text has to be drawn
        int offset = getPaddingLeft();

        for (EntryWrapper entryWrapper : entires) {
            BarEntry entry = entryWrapper.barEntry;
            // measure the length text is going to take against available space, otherwise ellipsize it
            CharSequence text = TextUtils.ellipsize(entry.getLabel(), barGraphHelper.indicatorPaint, eachEntryWidth, TextUtils.TruncateAt
                    .END);
            // draw text
            canvas.drawText(text.toString(), offset + eachEntryWidth / 2, heigth - paddingBottom, barGraphHelper.indicatorPaint);
            // calculate bar bounds
            int drawableHeight = (int) (barGraphHelper.currentMaxDrawingHeight < entry.getValue() ? barGraphHelper
                    .currentMaxDrawingHeight : entry.getValue());
            float top = startY - barGraphHelper.heightPerUnit * drawableHeight - barGraphHelper.strokeWidth;
            float left = (offset + eachEntryWidth / 2 - barWidth / 2);
            float right = left + barWidth;
            // draw bar
            entryWrapper.left = left;
            entryWrapper.top = top;
            entryWrapper.right = right;
            entryWrapper.bottom = startY - barGraphHelper.strokeWidth;
            canvas.drawRect(left, top, right, startY - barGraphHelper.strokeWidth, barGraphHelper.barPaint);
            // increase the offset
            offset = offset + eachEntryWidth;
        }
    }

    private void clearCanvas(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    private void drawEmptyPlaceholder(Canvas canvas) {

        int totalEntires = barGraphHelper.emptyLabels.length;
        if (totalEntires <= 0) {
            return;
        }

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingRight = getPaddingRight();
        int width = getWidth();
        int heigth = getHeight();
        int startY = getHeight() - paddingBottom - barGraphHelper.indicatorTextSize - barGraphHelper.labelMargin;
        int eachEntryWidth = (width - (getPaddingLeft() + getPaddingRight())) / totalEntires;
        int barWidth = (eachEntryWidth * 90) / 100;

        switch (barGraphHelper.drawAxis) {
            case DrawAxis.X_AXIS:
                canvas.drawLine(getPaddingLeft(), startY, width - paddingRight, startY, barGraphHelper.axisPaint);
                break;
            case DrawAxis.Y_AXIS:
                canvas.drawLine(paddingLeft, startY, paddingLeft, paddingTop, barGraphHelper.axisPaint);
                break;
            case DrawAxis.BOTH:
                canvas.drawLine(paddingLeft, startY, paddingLeft, paddingTop, barGraphHelper.axisPaint);
                canvas.drawLine(getPaddingLeft(), startY, width - paddingRight, startY, barGraphHelper.axisPaint);
                break;
            case DrawAxis.NONE:
            default:
                //EMPTY
                break;

        }

        // offset from where text has to be drawn
        int offset = getPaddingLeft();
        for (CharSequence entry : barGraphHelper.emptyLabels) {
            // measure the length text is going to take against available space, otherwise ellipsize it
            CharSequence text = TextUtils.ellipsize(entry, barGraphHelper.indicatorPaint, eachEntryWidth, TextUtils.TruncateAt.END);
            // draw text
            canvas.drawText(text.toString(), offset + eachEntryWidth / 2, heigth - paddingBottom, barGraphHelper.indicatorPaint);
            float left = (offset + eachEntryWidth / 2 - barWidth / 2);
            float right = left + barWidth;
            float top = getY() + getPaddingTop();
            // draw bar
            canvas.drawRect(left, top, right, startY - barGraphHelper.strokeWidth, barGraphHelper.barPaint);
            // increase the offset
            offset = offset + eachEntryWidth;
        }

    }
}
