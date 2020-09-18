package com.dani.shmiralist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;

public class menu extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    TextView tv_names, tv_places, tv_amount_names, tv_amount_places, tvStart, tvEnd, tv_amount_hours;
    Button bPlaces, bNames, bGo;
    CheckBox cbCircle;
    NumberPicker npNumber;
    ArrayList<String> names, places;
    String start, end;

    char timePicked; //which timer has been clicked 's'-start, 'e' - end

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //init:
        tv_places = (TextView) findViewById(R.id.tvPlaces);
        tv_names = (TextView) findViewById(R.id.tvNames);
        tv_amount_places = (TextView) findViewById(R.id.tv_amount_places);
        tv_amount_names = (TextView) findViewById(R.id.tv_amount_names);
        tvStart = (TextView) findViewById(R.id.tv_start_time);
        tvEnd = (TextView) findViewById(R.id.tv_end_time);
        bPlaces = (Button) findViewById(R.id.bPlaces);
        bNames = (Button) findViewById(R.id.bNames);
        tv_amount_hours = (TextView) findViewById(R.id.tv_amount_hours);
        bGo = (Button) findViewById(R.id.bGo);


        setButtons();
        names = getList("names");
        tv_names.setText(listToString(names));
        tv_amount_names.setText("סך הכל: " + names.size() + " שומרים");
        places = getList("places");
        tv_places.setText(listToString(places));
        tv_amount_places.setText("סך הכל: " + places.size() + " עמדות");
    }

    private void setButtons() {
        bPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(menu.this, Places.class));
            }
        });
        bNames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(menu.this, MainActivity.class));
            }
        });
        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime('s');

            }
        });
        tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime('e');
            }
        });
        bGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (names.isEmpty() ||places.isEmpty() ||start==null ||end==null) {
                    Toast.makeText(menu.this, "חסרים דברים אחי",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences.Editor editor = getSharedPreferences(MainActivity.PREFS,MODE_PRIVATE).edit();
                editor.putString("start",start);
                editor.putString("end",end);
                editor.apply();
                startActivity(new Intent(menu.this,Result.class));
            }
        });
    }

    private void pickTime(char picked) {
        timePicked = picked;
        TimePick timePick = new TimePick();
        timePick.show(getSupportFragmentManager(), "time picker");
    }

    private ArrayList<String> getList(String key) {
        ArrayList<String> list = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(key, null);
        if (set == null)
            return list;
        for (String att : set)
            list.add(att);
        return list;
    }

    private String listToString(ArrayList<String> list) {
        String listedString = "";
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size() - 1; i++)
                listedString += list.get(i) + ", "; //todo: the end shouldnt be with braccats.
            listedString += list.get(list.size() - 1);
        }

        return listedString;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String min = minute + "";
        if (minute < 10)
            min = "0" + minute;
        String time = (hourOfDay + ":" + min);
        switch (timePicked) {
            case 's':
                start = time;
                time = "שעת התחלה:" + "\n" + time;
                tvStart.setText(time);
                break;
            case 'e':
                end = time;
                time = "שעת סוף:" + "\n" + time;
                tvEnd.setText(time);

            default:
                time = "";//no timer was clicked.
        }
        timePicked = 'n'; //none
        if ((start != null) && (end != null)) {
            TimeDifference td = null;
            try {
                td = new TimeDifference(start, end);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tv_amount_hours.setText("סך הכל: " + td.getStringDifference() + "שמירה");

        }
    }


}

