package com.dani.shmiralist;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

public class TimePick extends AppCompatActivity {

    CustomTimePicker startTime,endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_pick);
        startTime = (CustomTimePicker)findViewById(R.id.time_start);
        endTime = (CustomTimePicker)findViewById(R.id.time_end);

    }
}