package com.dani.shmiralist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Places extends AppCompatActivity {

    ArrayList<String> places;
    ListView listView;
    Button bSave, bAdd;
    EditText etAdd;
    CustomAdapter adapter;
    String[] etHint = {"הוסף שמות של עמדות +", "הוסף +"};
    boolean isKeyboardOpen;

    public static final String PREFS = "shared_prefrences";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        listView = (ListView) findViewById(R.id.list_view_places);
        bSave = (Button) findViewById(R.id.bSave_places);
        etAdd = (EditText) findViewById(R.id.etAdd_places);
        bAdd = (Button) findViewById(R.id.bAdd_place);


        places = getPlaces();
        if (places.isEmpty())
            etAdd.setHint(etHint[0]);
        else
            etAdd.setHint(etHint[1]);

        adapter = new CustomAdapter(Places.this, R.layout.list_item, places);
        listView.setAdapter(adapter);
        setButtons();

    }

    private void setButtons() {
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaces(places);
                startActivity(new Intent(Places.this, MainActivity.class));
            }
        });

        etAdd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    places.add(etAdd.getText().toString());
                    adapter.notifyDataSetChanged();
                    etAdd.setText("");
                    etAdd.setHint(etHint[1]);
                }
                return false;
            }
        });
        etAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setRightButton('a');
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etAdd.getText().toString().matches(""))
                    setRightButton('s');
            }
        });
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                places.add(etAdd.getText().toString());
                adapter.notifyDataSetChanged();
                etAdd.setText("");
                etAdd.setHint(etHint[1]);
                setRightButton('s');
            }
        });


    }

    private void setRightButton(char button) {
        switch (button){
            case 's':
                bAdd.setVisibility(View.GONE);
                bSave.setVisibility(View.VISIBLE);
                break;
            case 'a':
                bSave.setVisibility(View.GONE);
                bAdd.setVisibility(View.VISIBLE);
                break;
        }
    }

    private ArrayList<String> getPlaces() {
        ArrayList<String> places = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        Set<String> places_set = prefs.getStringSet("places", null);
        if (places_set == null)
            return places;
        for (String place : places_set)
            places.add(place);
        return places;
    }

    private void setPlaces(ArrayList<String> places) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        Set<String> places_set = new HashSet<String>();
        places_set.addAll(places);
        editor.putStringSet("places", places_set);
        editor.apply();
    }

}