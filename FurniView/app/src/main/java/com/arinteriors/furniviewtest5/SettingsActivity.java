package com.arinteriors.furniviewtest5;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Spinner languageSpinner;
    private Spinner themeSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        languageSpinner = findViewById(R.id.languageSpinner);
        themeSpinner = findViewById(R.id.themeSpinner);


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

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем, если ничего не выбрано
            }
        });
    }

    private void setAppLanguage(String language) {
        // Получаем текущий язык приложения
        String currentLanguage = Locale.getDefault().getLanguage();

        // Проверяем, если выбранный язык уже установлен, не перезапускаем активность
        if (language.equals(currentLanguage)) {

        }
        else {
            Resources resources = getResources();
            Configuration configuration = resources.getConfiguration();
            Locale newLocale = new Locale(language);

            // Устанавливаем выбранный язык в конфигурацию приложения
            configuration.setLocale(newLocale);

            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

//            recreate();
        }
    }

}
