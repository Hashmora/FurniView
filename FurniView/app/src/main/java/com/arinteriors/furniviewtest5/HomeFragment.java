package com.arinteriors.furniviewtest5;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class HomeFragment extends Fragment {
    private LinearLayout linearLayout;

    private ActivityResultLauncher<Intent> fileChooserLauncher;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            Intent intent = new Intent(requireContext(), PlaceActivity.class);
                            assert uri != null;
                            intent.putExtra("modelPath", uri.toString());
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(requireContext(),
                                "File wasn't selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

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
                    intent.putExtra("modelPath", uri.toString());
                    startActivity(intent);
                });
            });

            addView(imageView);
        }

        setUpTouchListener(R.id.constraintLayoutStart, rootView,
                view -> mainActivity.setActiveElement(R.id.search));
        setUpTouchListener(R.id.constraintLayoutDownload, rootView, view -> {
            if (ContextCompat.checkSelfPermission(requireActivity(), READ_EXTERNAL_STORAGE)
                    != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{READ_EXTERNAL_STORAGE}, 1);
            } else {
                showFileChooser();
            }
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
