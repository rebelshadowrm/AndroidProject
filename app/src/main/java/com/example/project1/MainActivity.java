package com.example.project1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private String search, selected;
    private SoundPool soundPool;
    private int sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Project1);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        sound = soundPool.load(this, R.raw.sound, 1);


        Spinner spinner = findViewById(R.id.spinnerCalendar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.calendar, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        EditText searchBox = findViewById(R.id.inputSearchBar);
        Button searchBtn = findViewById(R.id.btnSearchBar);
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        searchBtn.setOnClickListener(v -> {
            search = String.valueOf(searchBox.getText());
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("search", search);
            editor.apply();
            startActivity(new Intent(MainActivity.this, Search.class));
        });
        searchBox.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                search = String.valueOf(searchBox.getText());
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("search", search);
                editor.apply();
                startActivity(new Intent(MainActivity.this, Search.class));
                return true;
            }
            return false;
        });

        Button calendarBtn = findViewById(R.id.btnCalendar);
        calendarBtn.setOnClickListener(v -> {
            soundPool.play(sound, 1, 1, 0, 0, 1);
            selected = spinner.getSelectedItem().toString();
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("selected", selected);
            editor.apply();
            startActivity(new Intent(MainActivity.this, Calendar.class));
        });

    }
}