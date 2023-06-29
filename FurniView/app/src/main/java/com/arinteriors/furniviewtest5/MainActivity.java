package com.arinteriors.furniviewtest5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    ArrayList<StorageReference> viewsUrls = new ArrayList<>();

    public boolean isListOfImages = false;

    private SharedPreferences sharedPreferences;

    private final String SHARED_DIRECTORY = "MyPref";

    private final String[] SAVED_KEYS = {"saved", "saved1", "saved2", "saved3"};

    @Override
    public void onBackPressed() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        if (getActiveFragment() instanceof HomeFragment || (getActiveFragment() instanceof SearchFragment && !isListOfImages)) {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
        else if (getActiveFragment() instanceof SearchFragment && isListOfImages) {
            SearchFragment searchFragment = (SearchFragment) getActiveFragment();
            searchFragment.linearLayout.removeAllViews();
            searchFragment.ListAll(searchFragment.iconsRef, false);
            isListOfImages = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.search:
                    replaceFragment(new SearchFragment());
                    break;
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.home);

        replaceFragment(new HomeFragment());
        sharedPreferences = getSharedPreferences(SHARED_DIRECTORY, MODE_PRIVATE);
        loadImage();

    }

    @Override
    protected void onDestroy() {
        saveImage();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.
        super.onDestroy();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.replaceConstraintLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setActiveElement(int id) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(id);
    }

    private Fragment getActiveFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragmentList = fragmentManager.getFragments();
        Fragment activeFragment = null;
        for (Fragment fragment : fragmentList) {
            if (fragment != null && fragment.isVisible()) {
                activeFragment = fragment;
                break;
            }
        }
        return activeFragment;
    }

    private void loadImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        for (String key : SAVED_KEYS) {
            String savedText = sharedPreferences.getString(key, "");
            if (!savedText.isEmpty()) {
                try {
                    StorageReference storageRef = storage.getReferenceFromUrl(savedText);
                    viewsUrls.add(storageRef);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void saveImage() {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        for (int i = 0; i < viewsUrls.size(); i++) {
            try {
                ed.putString(SAVED_KEYS[i], viewsUrls.get(i).toString());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        ed.apply();
    }
}

