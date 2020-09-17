package com.dani.shmiralist;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class menu extends AppCompatActivity {

    TextView tv_names, tv_places, tv_amount_names, tv_amount_places,tvStart,tvEnd;
    Button bPlaces, bNames,bTime;
    ArrayList<String> names, places;

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
        bTime = (Button)findViewById(R.id.bTime);

        setButtons();
        names = getList("names");
        tv_names.setText(listToString(names));
        tv_amount_names.setText("סך הכל: "+ names.size()+" שומרים");
        places = getList("places");
        tv_places.setText(listToString(places));
        tv_amount_places.setText("סך הכל: "+ places.size()+" עמדות");
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
        bTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(menu.this,TimePick.class));
            }
        });
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
    private String listToString(ArrayList<String> list){
        String listedString = "";
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size() - 1; i++)
                listedString += list.get(i) + ", "; //todo: the end shouldnt be with braccats.
            listedString += list.get(list.size()-1);
        }

        return listedString;
    }
}