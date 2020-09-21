package com.dani.shmiralist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telecom.GatewayInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    TextView tv_names, tv_places, tv_amount_names, tv_amount_places, tvStart, tvEnd, tv_amount_hours;
    Button bGo;//fix this!! bPlaces, bNames;
    CheckBox cbCircle, cbMax;
    ArrayList<String> names, places;
    String start, end;
    Spinner spinner_max, spinner_circle;

    LinearLayout ll_names, ll_places;

    private InterstitalAd
    char timePicked; //which timer has been clicked 's'-start, 'e' - end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init:
        tv_places = (TextView) findViewById(R.id.tvPlaces);
        tv_names = (TextView) findViewById(R.id.tvNames);
        tv_amount_places = (TextView) findViewById(R.id.tv_amount_places);
        tv_amount_names = (TextView) findViewById(R.id.tv_amount_names);
        tvStart = (TextView) findViewById(R.id.tv_start_time);
        tvEnd = (TextView) findViewById(R.id.tv_end_time);
        ll_places = (LinearLayout) findViewById(R.id.ll_places);
        ll_names = (LinearLayout) findViewById(R.id.ll_names);
        spinner_max = (Spinner) findViewById(R.id.spinner_max);
        spinner_circle = (Spinner) findViewById(R.id.spinner_circle);
        cbMax = (CheckBox) findViewById(R.id.cb_max);
        tv_amount_hours = (TextView) findViewById(R.id.tv_amount_hours);
        bGo = (Button) findViewById(R.id.bGo);
        cbCircle = (CheckBox) findViewById(R.id.cb_circle);

        setButtons();
        getPrefs();
        names = getList("names");
        tv_names.setText(listToString(names));
        tv_amount_names.setText("סך הכל: " + names.size() + " שומרים");
        places = getList("places");
        tv_places.setText(listToString(places));
        tv_amount_places.setText("סך הכל: " + places.size() + " עמדות");
    }

    private void getPrefs() {
        SharedPreferences prefs = getSharedPreferences(Names.PREFS, MODE_PRIVATE);
        if (prefs.getString("result",null)!=null)
            startActivity(new Intent(MainActivity.this,Result.class));
        boolean maxChecked = prefs.getBoolean("cb_max", false);
        boolean circleChecked = prefs.getBoolean("cb_circle", false);
        if (maxChecked)
            spinner_max.setVisibility(View.VISIBLE);
        else
            spinner_max.setVisibility(View.GONE);
        if(circleChecked)
            spinner_circle.setVisibility(View.VISIBLE);
        else
            spinner_circle.setVisibility(View.GONE);
        cbMax.setChecked(maxChecked);
        cbCircle.setChecked(circleChecked);
        start = prefs.getString("start", null);
        if (start != null)
            tvStart.setText("שעת התחלה:" + "\n" + start);
        end = prefs.getString("end", null);
        if (end != null)
            tvEnd.setText("שעת סוף:" + "\n" + end);
    }

    private void setButtons() {
        ll_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Places.class));
            }
        });
        ll_names.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Names.class));
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

                if (names.isEmpty() || places.isEmpty() || start == null || end == null) {
                    Toast.makeText(MainActivity.this, "חסרים דברים אחי",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(new Intent(MainActivity.this, Result.class));
            }
        });
        //spinner:
        final String[] options_max = {"0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5", "5.5", "6", "6.5", "7", "7.5", "8", "8.5", "9"};
        final String[] options_circle = {"5", "10", "15", "20", "25", "30", "45"};
        ArrayAdapter<String> sMaxArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, options_max);
        ArrayAdapter<String> sCircleArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, options_circle);
        sMaxArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCircleArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_max.setAdapter(sMaxArrayAdapter);
        spinner_circle.setAdapter(sCircleArrayAdapter);
        spinner_max.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences(Names.PREFS, MODE_PRIVATE).edit();
                editor.putFloat("max", Float.parseFloat(options_max[position]));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SharedPreferences.Editor editor = getSharedPreferences(Names.PREFS, MODE_PRIVATE).edit();
                editor.putFloat("max", 0);
                editor.apply();
            }
        });
        spinner_circle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences(Names.PREFS, MODE_PRIVATE).edit();
                editor.putInt("interval", Integer.parseInt(options_circle[position]));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cbMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    spinner_max.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor editor = getSharedPreferences(Names.PREFS, MODE_PRIVATE).edit();
                    editor.putBoolean("cb_max",true);
                    editor.apply();
                }
                else{
                    spinner_max.setVisibility(View.GONE);
                    SharedPreferences.Editor editor = getSharedPreferences(Names.PREFS, MODE_PRIVATE).edit();
                    editor.putBoolean("cb_circle",false);
                    editor.apply();
                }
            }
        });
        cbCircle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spinner_circle.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor editor = getSharedPreferences(Names.PREFS, MODE_PRIVATE).edit();
                    editor.putBoolean("cb_circle",true);
                    editor.apply();
                } else {
                    spinner_circle.setVisibility(View.GONE);
                    SharedPreferences.Editor editor = getSharedPreferences(Names.PREFS, MODE_PRIVATE).edit();
                    editor.putBoolean("cb_circle",false);
                    editor.apply();
                }
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
        SharedPreferences prefs = getSharedPreferences(Names.PREFS, MODE_PRIVATE);
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
        SharedPreferences.Editor editor = getSharedPreferences(Names.PREFS, MODE_PRIVATE).edit();
        String min = minute + "";
        if (minute < 10)
            min = "0" + minute;
        String time = (hourOfDay + ":" + min);
        switch (timePicked) {
            case 's':
                start = time;
                time = "שעת התחלה:" + "\n" + time;
                tvStart.setText(time);
                editor.putString("start", start);
                editor.apply();
                break;
            case 'e':
                end = time;
                time = "שעת סוף:" + "\n" + time;
                tvEnd.setText(time);
                editor.putString("end", end);
                editor.apply();
                break;

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

