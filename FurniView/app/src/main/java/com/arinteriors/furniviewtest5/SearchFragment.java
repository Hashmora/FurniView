package com.arinteriors.furniviewtest5;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class SearchFragment extends Fragment {

    LinearLayout linearLayout;

    private View rootView;

    private ProgressBar progressBar;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    private LinearLayout lastRow;

    StorageReference iconsRef;

    private final String INTENT_KEY = "modelPath";

    public SearchFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        linearLayout = rootView.findViewById(R.id.linearLayout);

        progressBar = rootView.findViewById(R.id.progressBar);

        FirebaseApp.initializeApp(requireContext());

        iconsRef = FirebaseStorage.getInstance().getReference().child("/Pictures/Icons");

        ListAll(iconsRef, false);

        return rootView;
    }

    private void addView(ImageView imageView) {
        int rotation = requireActivity().getWindowManager().getDefaultDisplay().getRotation();

        float scale = getResources().getDisplayMetrics().density;

        int imageViewMargin = (int) (16 * scale + 0.5f);

        int imageViewSize = (int) (140 * scale + 0.5f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageViewSize, imageViewSize);
        params.setMargins(imageViewMargin, imageViewMargin, imageViewMargin, imageViewMargin);
        int imageViewsPerRow = 2;

        if ( rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            imageViewsPerRow = 4;
        }

        if (lastRow == null || lastRow.getChildCount() >= imageViewsPerRow) {
            lastRow = new LinearLayout(rootView.getContext());
            lastRow.setGravity(Gravity.CENTER);
            linearLayout.addView(lastRow);
        }

        lastRow.addView(imageView, params);
    }




    private String removeExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        } else {
            return fileName;
        }
    }

    void ListAll(StorageReference storageReference, boolean isModel) {
        executorService.execute(() -> storageReference.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        ImageView imageView = new ImageView(rootView.getContext());
                        loadImageFromUrl(item, imageView);
                        setupClickListener(item, imageView, isModel);
                        addView(imageView);
                    }
                    requireActivity().runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                })
                .addOnFailureListener(exception -> {
                    // Log and handle the exception.
                }));
    }

    private void loadImageFromUrl(StorageReference item, ImageView imageView) {
        executorService.execute(() -> item.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).transform(
                    new RoundedCornersTransformation(
                            (int) (40 * getResources().getDisplayMetrics().density),
                            0, // Margin
                            RoundedCornersTransformation.CornerType.ALL // Corner type
                    )
            ).into(imageView);
        }));
    }

    private void setupClickListener(StorageReference item, ImageView imageView, boolean isModel) {
        imageView.setOnClickListener(view -> {
            if (isModel) {
                handleModelClick(item);
            } else {
                handlePictureClick(item);
            }
        });
    }

    private void handleModelClick(StorageReference item) {
        String path = "/Models/" + removeExtension(item.getName()) + ".glb";
        StorageReference modelRef = FirebaseStorage.getInstance().getReference().child(path);
        executorService.execute(() -> modelRef.getDownloadUrl().addOnSuccessListener(uri ->
                launchModelActivity(uri, item))
                .addOnFailureListener(exception -> {
            // Handle error
        }));
    }

    private void handlePictureClick(StorageReference item) {
        String path = "/Pictures/" + removeExtension(item.getName());
        StorageReference picturesRef = FirebaseStorage.getInstance().getReference().child(path);
        linearLayout.removeAllViews();

        Activity parentActivity = getActivity();
        if (parentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) parentActivity;
            mainActivity.isListOfImages = true;
        }
        requireActivity().runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        ListAll(picturesRef, true);
    }

    private void launchModelActivity(Uri uri, StorageReference item) {
        Intent intent = new Intent(rootView.getContext(), PlaceActivity.class);
        intent.putExtra(INTENT_KEY, uri.toString());
        Activity parentActivity = getActivity();
        if (parentActivity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) parentActivity;
            mainActivity.viewsUrls.add(0, item);
            mainActivity.saveImage();
        }
        startActivity(intent);
    }
    private void animateConstraintLayout(View view, float scale) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", scale);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(300);
        animatorSet.start();
    }
}