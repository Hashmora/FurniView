package com.arinteriors.furniviewtest5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    private static final long SPLASH_SCREEN_TIMEOUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();

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
