package com.arinteriors.furniviewtest5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Spinner languageSpinner;
    private Spinner themeSpinner;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        languageSpinner = findViewById(R.id.languageSpinner);
        themeSpinner = findViewById(R.id.themeSpinner);

        try {
            sharedPreferences =
                    getSharedPreferences("theme_and_lang", Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages,
                android.R.layout.simple_spinner_item
        );
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.themes,
                android.R.layout.simple_spinner_item
        );
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        languageSpinner.setAdapter(languageAdapter);
        themeSpinner.setAdapter(themeAdapter);

        int theme = -1;
        try {
            theme = sharedPreferences.getInt("theme", -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (theme != -1) {
            themeSpinner.setSelection(theme);
        } else themeSpinner.setSelection(2);

        String langKey = "en";

        try {
            langKey = sharedPreferences.getString("lang", "en");
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (langKey) {
            case "ru":
                languageSpinner.setSelection(0);
                break;
            default:
                languageSpinner.setSelection(1);
        }

        // Устанавливаем слушатель выбора элемента в спиннере
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = parent.getItemAtPosition(position).toString();
                setAppLanguage(selectedLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем, если ничего не выбрано
            }
        });

        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTheme = parent.getItemAtPosition(position).toString();
                setAppTheme(selectedTheme);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем, если ничего не выбрано
            }
        });
    }

    private void setAppTheme(String selectedTheme) {
        int themeMode;

        if (selectedTheme.equals(getResources().getString(R.string.light))) {
            themeMode = AppCompatDelegate.MODE_NIGHT_NO;
        } else if (selectedTheme.equals(getResources().getString(R.string.system))) {
            themeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        } else themeMode = AppCompatDelegate.MODE_NIGHT_YES;

        AppCompatDelegate.setDefaultNightMode(themeMode);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("theme", themeSpinner.getSelectedItemPosition());
        editor.putInt("themeMode", themeMode);

        editor.apply();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

    public void setAppLanguage(String selectedLanguage) {

        String localeKey;

        String currentLanguage = getResources().getConfiguration().locale.getLanguage();


        switch (selectedLanguage) {
            case "Русский":
                localeKey = "ru";
                break;
            default:
                localeKey = "en";
                break;
        }
        Locale locale = new Locale(localeKey);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lang", localeKey);
        editor.apply();

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        if (!currentLanguage.equals(localeKey)) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

}
