package com.example.manishkumargupta.bargraph;

import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * IMMUTABLE ENTRY
 */
public class BarEntry {

    private final float value;
    @NonNull private final String label;

    public BarEntry(@NonNull String label, float value) {
        this.value = value;
        this.label = label;
    }

    public float getValue() {
        return value;
    }

    @NonNull
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "BarEntry{" + "value=" + value + ", label='" + label + '\'' + '}';
    }
}
