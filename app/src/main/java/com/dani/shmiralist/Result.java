package com.dani.shmiralist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Set;

public class Result extends AppCompatActivity {

    TextView tvResult;
    ImageView bShare;
    String start,end;
    ArrayList<String> names,places;
    TimeDifference td;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tvResult = (TextView) findViewById(R.id.tvResult);
        bShare = (ImageView) findViewById(R.id.bShare);

        //get stuff:
        names = getList("names");
        places = getList("places");
        start = getTime("start");
        end = getTime("end");
        try {
            td = new TimeDifference(start,end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (td==null)
            startActivity(new Intent(Result.this,menu.class));
        int timeToWorkInMin = td.getTotalInMin()*places.size();
        tvResult.setText("total work in min: "+ timeToWorkInMin);



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
    private String getTime(String key){
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS,MODE_PRIVATE);
        String time = prefs.getString(key,null);
        return time;
    }
}