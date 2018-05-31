package com.example.manishkumargupta.bargraph;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class BarDataRepository {

    @NonNull List<BarEntry> entries = new ArrayList<>();
    private WeakReference<BarGraph> observableView;
    private BarEntry DEFAULT = new BarEntry("", 0);


    void registerObservableView(@NonNull BarGraph view) {
        observableView = new WeakReference<>(view);
    }

    public void updateGraph(@NonNull List<BarEntry> entries) {

        this.entries.clear();
        this.entries.addAll(entries);
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        if (observableView.get() != null) {
            observableView.get()
                    .invalidateView();
        }
    }

    @NonNull
    BarEntry getMax() {
        BarEntry max = DEFAULT;
        for (BarEntry entry1 : entries) {
            if (entry1.getValue() > max.getValue()) {
                max = entry1;
            }
        }
        return max;
    }

    int getEntriesCount() {
        return entries.size();
    }

    List<BarEntry> getEntries() {
        return this.entries;
    }

}
