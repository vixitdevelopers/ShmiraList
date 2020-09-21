package com.dani.shmiralist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Set;

public class Result extends AppCompatActivity {

    TextView tvResult;
    ImageView bShare,bCopy;
    String start, end;
    ArrayList<String> names, places;
    TimeDifference td;
    float max;
    int original;//to keep track of original amount of people before multiply for place.
    int INTERVAL = 5; //what to round up to.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tvResult = (TextView) findViewById(R.id.tvResult);
        bShare = (ImageView) findViewById(R.id.bShare);
        bCopy = (ImageView) findViewById(R.id.bCopy);

        bShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String share = "רשימת שמירה" + "\n\n"+ tvResult.getText().toString();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,share);
                sharingIntent.setPackage("com.whatsapp");
                startActivity(sharingIntent);
            }
        });
        bCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String share = "רשימת שמירה" + "\n\n"+ tvResult.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("שמירה", share);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Result.this, "הרשימה הועתקה",
                        Toast.LENGTH_SHORT).show();
            }
        });
        String resultFromActivity = getResult("result");
        if (resultFromActivity!=null){
            tvResult.setText(resultFromActivity);
            return;
        }
        //get stuff:
        names = getList("names");
        places = getList("places");
        start = getTime("start");
        end = getTime("end");
        INTERVAL = getPrefsInt("interval");
        max = getPrefsFloat("max")*60;//to minutes.
        int totalInMin = td.getTotalInMin();
        if (totalInMin==0)
            restart();
        if (max==0)
            max = 10_000_000;
        try {
            td = new TimeDifference(start, end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (td == null)
            startActivity(new Intent(Result.this, MainActivity.class));
        int[] arrayOfAllPlaces = getArrayOfPlaces(places.size(), names.size());//[3,3,3,4] - pres. names


        try {
            String result = "";

            for (int i = 0; i < arrayOfAllPlaces.length; i++) {
                int[] arrayForPlace = getArrayForPlace(arrayOfAllPlaces[i], td.getTotalInMin());
                result += "\n\n"+putInPlaceArray(arrayForPlace,td.getTotalInMin(),places.get(i));
            }
            tvResult.setText(result);
            SharedPreferences.Editor editor = getSharedPreferences(Names.PREFS, MODE_PRIVATE).edit();
            editor.putString("result",result);
            editor.apply();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


    private void restart() {
        SharedPreferences.Editor editor = getSharedPreferences(Names.PREFS, MODE_PRIVATE).edit();
        editor.putString("result",null);
        editor.apply();
        startActivity(new Intent(Result.this,MainActivity.class));
    }

    private int getPrefsInt(String key) {
        SharedPreferences prefs = getSharedPreferences(Names.PREFS, MODE_PRIVATE);
        int interval = prefs.getInt(key, 1);
        return interval;
    }

    private String getResult(String key) {
        SharedPreferences prefs = getSharedPreferences(Names.PREFS, MODE_PRIVATE);
        String result = prefs.getString(key, null);
        return result;
    }

    private float getPrefsFloat(String key) {
        SharedPreferences prefs = getSharedPreferences(Names.PREFS, MODE_PRIVATE);
        float interval = prefs.getFloat(key, 0);
        return interval;
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

    private String getTime(String key) {
        SharedPreferences prefs = getSharedPreferences(Names.PREFS, MODE_PRIVATE);
        String time = prefs.getString(key, null);
        return time;
    }

    private int getRoundUp(int min, int interval) {
        int modus = min % interval;
        if (modus != 0)
            min += interval - modus;
        return min;
    }


    private int[] getArrayOfPlaces(int numOfPlaces, int numOfNames) {
        //Returns an array of each place and amount of names in them.
        // e.g: [3][3][3][2] - means first 3 have 3 names, last one has 2 names.

        int[] namesForPlaces = new int[numOfPlaces];
        for (int i = 0; i < numOfPlaces; i++)
            namesForPlaces[i] = numOfNames / numOfPlaces;//fill in all the names.
        int modus = numOfNames % numOfPlaces;
        for (int i = 0; i < modus; i++)
            namesForPlaces[i]++;//add the remainders(modus amount) to the first few.
        return namesForPlaces;
    }

    private int[] getArrayForPlace(int numOfNamesInPlace, int workMin) {
        //returns an array of each guy in place how many minutes.
        // e.g: [35][35][35][30]- last guy is 5 min less.
        int minForEachName = getRoundUp((workMin / numOfNamesInPlace), INTERVAL);// 135/5=35
        int[]minForPlace;
        if (minForEachName>max) {
            minForPlace = new int[getSizeMultArray(minForEachName,max,numOfNamesInPlace,workMin)];
            minForEachName = getMinForNameMult(minForPlace.length,workMin);
        }
        else
            minForPlace = new int[numOfNamesInPlace];
        original = numOfNamesInPlace;
        int allocated = 0;

        for (int i = 0; i < minForPlace.length; i++) {
            if (i == minForPlace.length - 1) {//last guy
                int lastGuy = workMin - allocated;//25
                if (lastGuy < minForEachName)
                    while (minForEachName - lastGuy > INTERVAL) { // 40-25
                        minForPlace[getSmallestIndex(minForPlace)] -= INTERVAL;
                        lastGuy += INTERVAL;
                    }
                minForPlace[i] = lastGuy;
            } else { //regular dudes.
                minForPlace[i] = minForEachName;
                allocated += minForEachName;
            }
        }
        return minForPlace;
    }

    private int getMinForNameMult(int size, int workTime) {
        int minForName = getRoundUp((workTime / size), INTERVAL);// 135/5=35
        return minForName;
    }

    private int getSizeMultArray(int eachTime,float max,int originalSize,int workForPlace) {
        int size = originalSize;
        while (eachTime>max){
            size = size*2;
            eachTime = getRoundUp((workForPlace / size), INTERVAL);// 135/5=35
        }
        return size;
    }

    private String putInPlaceArray(int[] arrayForplace, int timeEachPlaceMin, String place) throws ParseException {
        int addedTime = 0;
        String startTime = start;
        String endTime = "";
        String resultForPlace =place + "\n";
        ArrayList<String> namesForPlace = getNamesForPlace(arrayForplace,original);

        for (int i = 0; i < arrayForplace.length; i++) {
            addedTime += arrayForplace[i];
            endTime = TimeDifference.addTime(startTime, arrayForplace[i]);
            if (addedTime > timeEachPlaceMin)
                endTime = end;//שלמה עד הסוף
            //add name to shmira interval.
            resultForPlace += "\n" + startTime + " - " + endTime + "  " + namesForPlace.get(0);
            //delete name from list:
            namesForPlace.remove(0);
            //change time
            startTime = endTime;
        }

        return resultForPlace;
    }

    private ArrayList<String> getNamesForPlace(int[] arrayForplace,int originalAmount) {

        ArrayList<String> namesForPlace = new ArrayList<String>();
        for (int i = 0 ; i<originalAmount; i++){
            namesForPlace.add(names.get(0));
            names.remove(0); //names for place is now base 4 first. יוסף,דני,שלמה ,יעקב
        }
        int index =0;
        for (int i = 0; i<arrayForplace.length; i++){
            if (index>=originalAmount)
                index = 0;
            namesForPlace.add(namesForPlace.get(index));
            index++;
        }
        return namesForPlace;
    }

    private int getSmallestIndex(int[] minForPlace) {
        int[] withoutLast = new int[minForPlace.length - 1];
        // add this
        if (withoutLast.length == 0)
            return -1;

        int index = 0;
        int min = withoutLast[index];

        for (int i = 1; i < withoutLast.length; i++) {
            if (withoutLast[i] < min) {
                min = withoutLast[i];
                index = i;
            }
        }
        return index;
    }
}