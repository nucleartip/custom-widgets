package com.example.manishkumargupta.bargraph.core;

import android.support.annotation.NonNull;

import com.example.manishkumargupta.bargraph.view.BarEntry;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class BarDataRepository {

    @NonNull List<EntryWrapper> entries = new ArrayList<>();
    private WeakReference<BarGraphHelper> observableView;
    private BarEntry DEFAULT = new BarEntry("", 0);

    public void updateGraph(@NonNull List<BarEntry> entries) {
        this.entries.clear();
        for(BarEntry entry: entries){
            this.entries.add(new EntryWrapper(entry));
        }
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        if (observableView.get() != null) {
            observableView.get()
                    .invalidate();
        }
    }

    @NonNull
    BarEntry getMax() {
        BarEntry max = DEFAULT;
        for (EntryWrapper entryWrapper : entries) {
            if (entryWrapper.barEntry.getValue() > max.getValue()) {
                max = entryWrapper.barEntry;
            }
        }
        return max;
    }

    int getEntriesCount() {
        return entries.size();
    }

    @NonNull
    List<EntryWrapper> getEntries() {
        return this.entries;
    }

    void registerObservableView(@NonNull BarGraphHelper barGraphHelper) {
        observableView = new WeakReference<>(barGraphHelper);
    }
}
