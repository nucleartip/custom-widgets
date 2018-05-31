package com.example.manishkumargupta.bargraph;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private BarGraph barGraph;
    private BarHandler handler;
    private BarDataRepository barDataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new BarHandler(this);
        barGraph = findViewById(R.id.graph);


    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessageDelayed(1, 500);
    }

    void populateGraph(List<BarEntry> entries, @ColorRes int barColor, @ColorRes int guideLine, @ColorRes int indicator) {
        if (barDataRepository == null) {
            barDataRepository = new BarDataRepository();
            barGraph.setBarDataRepository(barDataRepository);
        }
        barGraph.setBarColor(barColor);
        barGraph.setIndicatorTextColor(indicator);
        barGraph.setGuidelineColor(guideLine);
        barDataRepository.updateGraph(entries);

    }


    List<BarEntry> obtainTestEntry() {

        List<BarEntry> list = new ArrayList<>();
        BarEntry entry = new BarEntry("Mon", 10);
        list.add(entry);
        entry = new BarEntry("Tue", 0);
        list.add(entry);
        entry = new BarEntry("Wed", 20);
        list.add(entry);
        entry = new BarEntry("Thu", 5);
        list.add(entry);
        entry = new BarEntry("Fri", 35);
        list.add(entry);
        entry = new BarEntry("Sat", 50);
        list.add(entry);
        entry = new BarEntry("Sun", 60);
        list.add(entry);
        return list;
    }

    String[] labels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Jan", "Feb", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December", "Mar", "Apr", "Jun", "Aug", "Sep", "Nov", "Oct", "Dec"};
    @ColorRes int[] colors = {android.R.color.holo_green_light, android.R.color.holo_red_light, android.R.color.holo_orange_light,
            android.R.color.holo_red_dark, android.R.color.holo_purple, android.R.color.holo_orange_dark, android.R.color
            .holo_green_dark, android.R.color.holo_blue_bright, android.R.color.white, android.R.color.holo_red_light};

    List<BarEntry> obtainRandomEntry() {

        int entryCount = new Random().nextInt(12);
        List<BarEntry> list = new ArrayList<>(entryCount);
        Random random = new Random();
        for (int i = 0; i < entryCount; i++) {
            int labelIndex = new Random().nextInt(labels.length);
            BarEntry entry = new BarEntry(labels[labelIndex], random.nextInt(1000));
            list.add(entry);
        }

        return list;
    }

    @ColorRes
    int obtainColor() {

        int index = new Random().nextInt(colors.length);
        return colors[index];
    }

    private static class BarHandler extends Handler {

        private WeakReference<MainActivity> activityWeakReference;

        public BarHandler(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activityWeakReference != null && activityWeakReference.get() != null) {
                MainActivity activity = activityWeakReference.get();
                activity.populateGraph(activity.obtainRandomEntry(), activity.obtainColor(), activity.obtainColor(), activity.obtainColor());
                sendEmptyMessageDelayed(1, 3000);
            }
        }
    }
}
