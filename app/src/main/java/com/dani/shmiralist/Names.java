package com.dani.shmiralist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Names extends AppCompatActivity {

    ArrayList<String> names;
    ListView listView;
    Button bSave,bAdd;
    EditText etAdd;
    CustomAdapter adapter;
    String[] etHint = {"הוסף שמות של שומרים +", "הוסף +"};
    boolean isKeyboardOpen;


    public static final String PREFS = "shared_prefrences";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names);
        listView = (ListView) findViewById(R.id.list_view);
        bSave = (Button) findViewById(R.id.bSave);
        etAdd = (EditText) findViewById(R.id.etAdd);
        bAdd = (Button) findViewById(R.id.bAdd_name);
        names = getNames();
        if (names.isEmpty())
            etAdd.setHint(etHint[0]);
        else
            etAdd.setHint(etHint[1]);

        adapter = new CustomAdapter(Names.this, R.layout.list_item, names);
        listView.setAdapter(adapter);
        setButtons();

    }

    private void setButtons() {
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNames(names);
                startActivity(new Intent(Names.this, MainActivity.class));

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
                names.add(etAdd.getText().toString());
                adapter.notifyDataSetChanged();
                etAdd.setText("");
                etAdd.setHint(etHint[1]);
                setRightButton('s');
            }
        });



        etAdd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    names.add(etAdd.getText().toString());
                    adapter.notifyDataSetChanged();
                    etAdd.setText("");
                    etAdd.setHint(etHint[1]);
                    if (names.size() < 9){
                        handled = true;
                        setRightButton('s');
                    }
                }
                return handled;
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

    private ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        Set<String> names_set = prefs.getStringSet("names", null);
        if (names_set == null)
            return names;
        for (String name : names_set)
            names.add(name);
        return names;
    }

    private void setNames(ArrayList<String> names) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        Set<String> names_set = new HashSet<String>();
        names_set.addAll(names);
        editor.putStringSet("names", names_set);
        editor.apply();
    }

}