package com.example.sixtyplus.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

public class IntervalTimePicker extends TimePicker {

    private int minuteInterval = 15;

    public IntervalTimePicker(Context context) {
        super(context);
        init();
    }

    public IntervalTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IntervalTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMinuteInterval(minuteInterval);

        setOnTimeChangedListener(new OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                int roundedMinute = Math.round(minute / (float) minuteInterval) * minuteInterval;
                if (roundedMinute == 60) {
                    roundedMinute = 0;
                }

                if (minute != roundedMinute) {
                    view.setMinute(roundedMinute);
                }
            }
        });
    }

    public void setMinuteInterval(int interval) {
        this.minuteInterval = interval;

        try {
            LinearLayout timePickerLayout = (LinearLayout) getChildAt(0);

            if (timePickerLayout != null) {
                NumberPicker minutePicker = (NumberPicker) timePickerLayout.getChildAt(1);

                if (minutePicker != null) {
                    int numIntervals = 60 / interval;
                    String[] displayedValues = new String[numIntervals];

                    for (int i = 0; i < numIntervals; i++) {
                        displayedValues[i] = String.format("%02d", i * interval);
                    }

                    minutePicker.setMinValue(0);
                    minutePicker.setMaxValue(numIntervals - 1);
                    minutePicker.setDisplayedValues(displayedValues);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}