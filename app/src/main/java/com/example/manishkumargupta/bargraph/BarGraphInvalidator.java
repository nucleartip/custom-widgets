package com.example.manishkumargupta.bargraph;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by manishkumar.gupta on 17,July,2018
 */
public class BarGraphInvalidator extends Handler {

    private final WeakReference<BarGraphHelper> barGraphHelperWeakReference;

    public BarGraphInvalidator(BarGraphHelper barGraphHelper) {
        this.barGraphHelperWeakReference = new WeakReference<>(barGraphHelper);
    }

    void invalidate() {
        removeCallbacksAndMessages(null);
        sendEmptyMessageDelayed(1, 5);
    }

    @Override
    public void handleMessage(Message msg) {
        if (barGraphHelperWeakReference.get() != null) {
            barGraphHelperWeakReference.get()
                    .invalidate();
        }
    }

}
