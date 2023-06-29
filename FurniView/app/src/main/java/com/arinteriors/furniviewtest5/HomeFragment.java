package com.arinteriors.furniviewtest5;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import java.util.Locale;

public class HomeFragment extends Fragment {
    private LinearLayout linearLayout;

    private ActivityResultLauncher<Intent> fileChooserLauncher;

    private boolean isPolicyAgreed = false;

    private final String INTENT_KEY = "modelPath";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            String langKey = requireActivity()
                    .getSharedPreferences("theme_and_lang", Context.MODE_PRIVATE)
                    .getString("lang", "en");
            Locale locale = new Locale(langKey);
            Resources resources = getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        } catch (Exception e) {
            e.printStackTrace();
        }

        fileChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            Intent intent = new Intent(requireContext(), PlaceActivity.class);
                            assert uri != null;
                            intent.putExtra(INTENT_KEY, uri.toString());
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(requireContext(),
                                "File wasn't selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if (result) {
                    showFileChooser();
                    // PERMISSION GRANTED
                } else {
                    // PERMISSION NOT GRANTED
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        linearLayout = rootView.findViewById(R.id.recentlyUsedLayout);

        MainActivity mainActivity = (MainActivity) requireActivity();

        for (StorageReference item : mainActivity.viewsUrls) {
            ImageView imageView = new ImageView(mainActivity);
            item.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).transform(
                    new RoundedCornersTransformation(
                            (int) (10 * getResources().getDisplayMetrics().density),
                            0, // Margin from edges
                            RoundedCornersTransformation.CornerType.ALL // Type of corner rounding
                    )
            ).into(imageView));

            imageView.setOnClickListener(view -> {
                String path = "/Models/" + removeExtension(item.getName()) + ".glb";
                StorageReference modelRef = FirebaseStorage.getInstance().getReference().child(path);
                modelRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Intent intent = new Intent(requireContext(), PlaceActivity.class);
                    intent.putExtra(INTENT_KEY, uri.toString());
                    startActivity(intent);
                });
            });

            addView(imageView);
        }

        setUpTouchListener(R.id.constraintLayoutStart, rootView,
                view -> mainActivity.setActiveElement(R.id.search));

        setUpTouchListener(R.id.constraintLayoutDownload, rootView,
                view -> {
            try {
                SharedPreferences sharedPreferences = requireActivity()
                        .getSharedPreferences("policy", Context.MODE_PRIVATE);
                isPolicyAgreed = sharedPreferences.getBoolean("policy", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isPolicyAgreed) {requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE);}
            else showConfirmationDialog();
        });



        setUpTouchListener(R.id.infoLayout, rootView, v -> startActivity(
                new Intent(requireContext(), InformationActivity.class)));
        setUpTouchListener(R.id.settingsLayout, rootView, v -> startActivity(
            new Intent(requireContext(), SettingsActivity.class)));

        return rootView;
    }

    private String removeExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        } else {
            return fileName;
        }
    }

    private void showConfirmationDialog() {
        try {
            SharedPreferences sharedPreferences = requireActivity()
                    .getSharedPreferences("policy", Context.MODE_PRIVATE);
            isPolicyAgreed = sharedPreferences.getBoolean("policy", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isPolicyAgreed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(getResources().getString(R.string.confirmation));
            builder.setMessage(getResources().getString(R.string.conf_des));

            builder.setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                try {
                    SharedPreferences sharedPreferences = requireActivity()
                            .getSharedPreferences("policy", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("policy", true);
                    editor.apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE);
            });

            // Обработчик кнопки Cancel
            builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
                dialog.dismiss();
            });

            builder.setNeutralButton(getResources().getString(R.string.usage_policy), (dialog, which) -> {
                startActivity(new Intent(requireActivity(), AppPolicy.class));
            });


            // Создаем и отображаем всплывающее окно
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void setUpTouchListener(int viewId, View rootView, View.OnClickListener action) {
        rootView.findViewById(viewId).setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    animateLayout(view, 0.92f);
                    view.performClick();
                    break;
                case MotionEvent.ACTION_UP:
                    animateLayout(view, 1.0f);
                    action.onClick(view);
                    break;
            }
            return true;
        });
    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/octet-stream");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        fileChooserLauncher.launch(intent);
    }


    private void addView(ImageView imageView) {
        float scale = getResources().getDisplayMetrics().density;
        int imageViewMargin = (int) (16 * scale + 0.5f); // Margin between buttons
        int imageViewSize = (int) (100 * scale + 0.5f); // Button size

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageViewSize, imageViewSize);
        params.setMargins(imageViewMargin, imageViewMargin, imageViewMargin, imageViewMargin);
        linearLayout.addView(imageView, params);
    }

    private void animateLayout(View view, float scale) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", scale);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(300);
        animatorSet.start();
    }
}
