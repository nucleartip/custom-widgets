package com.example.manishkumargupta.bargraph;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;

import static com.example.manishkumargupta.bargraph.TwoLinerTextView.TextStyle.BOLD;
import static com.example.manishkumargupta.bargraph.TwoLinerTextView.TextStyle.DEFAULT;
import static com.example.manishkumargupta.bargraph.TwoLinerTextView.TextStyle.ITALIC;
import static com.example.manishkumargupta.bargraph.TwoLinerTextView.TextTypeFace.MONOSPACE;
import static com.example.manishkumargupta.bargraph.TwoLinerTextView.TextTypeFace.SANS;
import static com.example.manishkumargupta.bargraph.TwoLinerTextView.TextTypeFace.SERIF;

public class TwoLinerTextView extends View {

    @IntDef({SANS, SERIF, MONOSPACE})
    @interface TextTypeFace {
        int SANS = 0;
        int SERIF = 1;
        int MONOSPACE = 2;
    }

    @IntDef({BOLD, ITALIC, DEFAULT})
    @interface TextStyle {
        int BOLD = 0;
        int ITALIC = 1;
        int DEFAULT = 2;
    }


    private final InvalidateHandler invalidateHandler;
    private CharSequence primaryText;
    private CharSequence secondaryText;
    @ColorInt private int primaryTextColor;
    @ColorInt private int secondaryTextColor;
    private Typeface primaryTypeFace;
    private Typeface secondaryTypeFace;
    private int primaryTextTypeface = 0;
    private int secondaryTextTypeface = 0;
    private int primaryTextSize;
    private int secondaryTextSize;
    private int primaryTextStyle = 2;
    private int secondaryTextStyle = 2;
    private TextPaint primaryTextPaint;
    private TextPaint secondaryTextPaint;
    private boolean isPrimarySingle = false;
    private boolean isSecondarySingle = false;
    // -1 implies no minimum count
    private int primaryMaxLineCount = -1;
    private int secondaryMaxLineCount = -1;
    private int spacing;

    private Layout primary;
    private Layout secondary;


    public TwoLinerTextView(Context context) {
        this(context, null);
    }

    public TwoLinerTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwoLinerTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TwoLinerTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        invalidateHandler = new InvalidateHandler(this);
        primaryTextPaint = new TextPaint();
        secondaryTextPaint = new TextPaint();
        init(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        primaryTextTypeface = 0;
        secondaryTextTypeface = 0;
        primaryTextStyle = 2;
        secondaryTextStyle = 2;
        primaryTextColor = getResources().getColor(R.color.primary_text_color);
        secondaryTextColor = getResources().getColor(R.color.secondary_text_color);
        primaryTextSize = getResources().getDimensionPixelSize(R.dimen.primary_text_size);
        secondaryTextSize = getResources().getDimensionPixelSize(R.dimen.secondary_text_size);
        spacing = getResources().getDimensionPixelSize(R.dimen.spacing);
        isPrimarySingle = false;
        isSecondarySingle = false;
        primaryMaxLineCount = -1;
        secondaryMaxLineCount = -1;

        if (attrs != null) {
            TypedArray array = getContext().getTheme()
                    .obtainStyledAttributes(attrs, R.styleable.BarGraph, defStyleAttr, defStyleRes);
            try {
                primaryTextColor = array.getColor(R.styleable.TwoLinerTextView_primary_text_color, primaryTextColor);
                secondaryTextColor = array.getColor(R.styleable.TwoLinerTextView_secondary_text_color, secondaryTextColor);
                primaryTextSize = array.getDimensionPixelSize(R.styleable.TwoLinerTextView_primary_text_size, primaryTextSize);
                secondaryTextSize = array.getDimensionPixelSize(R.styleable.TwoLinerTextView_secondary_text_size, secondaryTextSize);
                spacing = array.getDimensionPixelSize(R.styleable.TwoLinerTextView_spacing, spacing);
                primaryTextTypeface = array.getInteger(R.styleable.TwoLinerTextView_primary_text_typeface, primaryTextTypeface);
                secondaryTextTypeface = array.getInteger(R.styleable.TwoLinerTextView_secondary_text_typeface, secondaryTextTypeface);
                primaryTextStyle = array.getInteger(R.styleable.TwoLinerTextView_primary_text_style, primaryTextStyle);
                secondaryTextStyle = array.getInteger(R.styleable.TwoLinerTextView_secondary_text_style, secondaryTextStyle);
                isPrimarySingle = array.getBoolean(R.styleable.TwoLinerTextView_primary_text_single_line, isPrimarySingle);
                isSecondarySingle = array.getBoolean(R.styleable.TwoLinerTextView_secondary_text_single_line, isSecondarySingle);
                primaryMaxLineCount = array.getInteger(R.styleable.TwoLinerTextView_primary_text_max_line, primaryMaxLineCount);
                secondaryMaxLineCount = array.getInteger(R.styleable.TwoLinerTextView_secondary_text_max_line, secondaryMaxLineCount);
            } catch (Exception e) {
                Log.d("####", e.getMessage(), e);
            } finally {
                array.recycle();
            }
        }

        loadPrimaryTypeFace();
        loadSecondaryTypeFace();
        loadTextPaints();
    }

    private void loadPrimaryTypeFace() {
        Typeface typeface;
        switch (primaryTextTypeface) {
            case 0:
                typeface = Typeface.SANS_SERIF;
                break;
            case 1:
                typeface = Typeface.SERIF;
                break;
            case 2:
                typeface = Typeface.MONOSPACE;
                break;
            default:
                typeface = Typeface.DEFAULT;

        }

        switch (primaryTextStyle) {
            case BOLD:
                primaryTypeFace = Typeface.create(typeface, Typeface.BOLD);
                break;
            case ITALIC:
                primaryTypeFace = Typeface.create(typeface, Typeface.ITALIC);
                break;
            case DEFAULT:
                primaryTypeFace = Typeface.create(typeface, Typeface.NORMAL);
                break;
        }
    }


    private void loadSecondaryTypeFace() {
        Typeface typeface;
        switch (secondaryTextTypeface) {
            case 0:
                typeface = Typeface.SANS_SERIF;
                break;
            case 1:
                typeface = Typeface.SERIF;
                break;
            case 2:
                typeface = Typeface.MONOSPACE;
                break;
            default:
                typeface = Typeface.DEFAULT;
        }

        switch (secondaryTextStyle) {
            case BOLD:
                secondaryTypeFace = Typeface.create(typeface, Typeface.BOLD);
                break;
            case ITALIC:
                secondaryTypeFace = Typeface.create(typeface, Typeface.ITALIC);
                break;
            case DEFAULT:
                secondaryTypeFace = Typeface.create(typeface, Typeface.NORMAL);
                break;
        }
    }

    private void loadTextPaints() {
        primaryTextPaint.setTypeface(primaryTypeFace);
        primaryTextPaint.setTextSize(primaryTextSize);
        primaryTextPaint.setColor(primaryTextColor);

        secondaryTextPaint.setTypeface(secondaryTypeFace);
        secondaryTextPaint.setTextSize(secondaryTextSize);
        secondaryTextPaint.setColor(secondaryTextColor);
    }


    public void setPrimaryText(@StringRes int text) {
        this.setPrimaryText(getContext().getString(text));
        invalidateView();
    }

    public void setsetSecondaryText(@StringRes int text) {
        this.setSecondaryText(getContext().getString(text));
        invalidateView();
    }

    public void setPrimaryText(@NonNull CharSequence text) {
        if (text.equals(primaryText)) {
            return;
        }
        this.primaryText = text;
        invalidateView();
    }

    public void setSecondaryText(@NonNull String text) {
        if (text.equals(secondaryText)) {
            return;
        }
        this.secondaryText = text;
        invalidateView();
    }

    public void setPrimaryTextColor(@ColorRes int color) {
        this.primaryTextColor = getResources().getColor(color);
        loadTextPaints();
        invalidateView();
    }

    public void setSecondaryTextColor(@ColorRes int color) {
        this.secondaryTextColor = getResources().getColor(color);
        loadTextPaints();
        invalidateView();
    }

    public void setPrimaryTextSize(@DimenRes int size) {
        int temp = getResources().getDimensionPixelSize(size);
        if (temp == primaryTextSize) {
            return;
        }
        this.primaryTextSize = temp;
        loadTextPaints();
        invalidateView();
    }

    public void setSecondaryTextSize(@DimenRes int size) {
        int temp = getResources().getDimensionPixelSize(size);
        if (temp == secondaryTextSize) {
            return;
        }
        this.secondaryTextSize = temp;
        loadTextPaints();
        invalidateView();
    }

    public void setPrimaryTypeFace(@TextTypeFace int typeFace) {

        if (this.primaryTextTypeface == typeFace) {
            return;
        }

        this.primaryTextTypeface = typeFace;
        loadPrimaryTypeFace();
        invalidateView();
    }

    public void setSecondaryTypeFace(@TextTypeFace int typeFace) {
        if (this.secondaryTextTypeface == typeFace) {
            return;
        }
        this.secondaryTextTypeface = typeFace;
        loadSecondaryTypeFace();
        invalidateView();
    }

    public void setPrimaryTextSingleLine(boolean isSingleLine) {
        if (this.isPrimarySingle == isSingleLine) {
            return;
        }
        this.isPrimarySingle = isSingleLine;
        invalidateView();
    }

    public void setSecondarySingleLine(boolean isSingleLine) {
        if (this.isSecondarySingle == isSingleLine) {
            return;
        }
        this.isSecondarySingle = isSingleLine;
        invalidateView();
    }

    public void setPrimaryMaxLines(int max) {
        if (this.primaryMaxLineCount == max) {
            return;
        }
        this.primaryMaxLineCount = max;
        invalidateView();
    }

    public void setSecondaryMaxLines(int max) {
        if (this.secondaryMaxLineCount == max) {
            return;
        }
        this.secondaryMaxLineCount = max;
        invalidateView();
    }

    void invalidateView() {
        invalidateHandler.removeCallbacksAndMessages(null);
        invalidateHandler.sendEmptyMessageDelayed(1, 5);
    }

    private static class InvalidateHandler extends Handler {

        private WeakReference<TwoLinerTextView> weakReference;

        InvalidateHandler(@NonNull TwoLinerTextView twoLinerTextView) {
            weakReference = new WeakReference<>(twoLinerTextView);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference.get() != null) {
                weakReference.get()
                        .requestLayout();
                weakReference.get()
                        .invalidate();
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (TextUtils.isEmpty(primaryText) && TextUtils.isEmpty(secondaryText)) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            return;
        }

        int height = getHeight() - (getPaddingTop() + getPaddingBottom());
        int width = getWidth() - (getPaddingLeft() + getPaddingRight());

        if (isPrimarySingle) {
            CharSequence text = TextUtils.ellipsize(primaryText, primaryTextPaint, width, TextUtils.TruncateAt.END);
            canvas.drawText(text.toString(), getLeft() + getPaddingLeft(), getTop() + getPaddingTop(), primaryTextPaint);
        }


    }
}
