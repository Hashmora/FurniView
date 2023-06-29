package com.arinteriors.furniviewtest5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    private static final long SPLASH_SCREEN_TIMEOUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();

        try {
            SharedPreferences sharedThemeAndLang =
                    getSharedPreferences("theme_and_lang", Context.MODE_PRIVATE);
            int themeMode = sharedThemeAndLang.getInt("themeMode", -1);

            if (themeMode != -1) {
                AppCompatDelegate.setDefaultNightMode(themeMode);
            }

            String langKey = sharedThemeAndLang.getString("lang", "en");
            Locale locale = new Locale(langKey);
            Resources resources = getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView splashLogo = findViewById(R.id.mainText);
        Animation splashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_text);
        splashLogo.startAnimation(splashAnimation);

        ImageView slash = findViewById(R.id.slash);
        Animation slashAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_slash);
        slashAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                slash.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        slash.startAnimation(slashAnimation);

        ImageView rect = findViewById(R.id.rect);
        Animation rectAnimation = AnimationUtils.loadAnimation(this, R.anim.anim);
        rectAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                rect.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        rect.startAnimation(rectAnimation);


        new Handler().postDelayed(() -> {

            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);

            finish();
        }, SPLASH_SCREEN_TIMEOUT);
    }
}
