package com.example.manishkumargupta.bargraph.core;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by manishkumar.gupta on 17,July,2018
 */
class BarGraphInvalidator extends Handler {

    private final WeakReference<BarGraphHelper> barGraphHelperWeakReference;

    BarGraphInvalidator(BarGraphHelper barGraphHelper) {
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
