package com.stockita.newpointofsales.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.stockita.newpointofsales.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This is to show a small dialog when the user click on the camera FAB
 * so he can select to use a camera or to get images from the gallery
 */
public class PickerImageDialogFragment extends DialogFragment {

    // Constant
    public final static String KEY_DIALOG_FRAGMENT_ITEM_NUMBER = "dialogFragmentKey";
    public static final int GET_FROM_DIALOG_FRAGMENT = 400;

    // The view
    @Bind(R.id.pickImage)Button pickImage;
    @Bind(R.id.takeImage)Button takeImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize the view
        View view = inflater.inflate(R.layout.fragment_picker_image_dialog, container, false);

        // ButterKnife
        ButterKnife.bind(this, view);

        // The dialog window
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        // Return the view
        return view;

    }

    @OnClick(R.id.pickImage)
    void pickImage(View view) {
        // Use intent to pass the data back, the data will be received in the onActivityResult()
        Intent intent = new Intent();
        final int PICK_IMAGE = 1;
        intent.putExtra(KEY_DIALOG_FRAGMENT_ITEM_NUMBER, PICK_IMAGE);
        getTargetFragment().onActivityResult(getTargetRequestCode(), GET_FROM_DIALOG_FRAGMENT, intent);
        getDialog().dismiss();
    }

    @OnClick(R.id.takeImage)
    void takeImage(View view) {
        // Use intent to pass the data back, the data will be received in the onActivityResult()
        Intent intent = new Intent();
        final int TAKE_IMAGE = 2;
        intent.putExtra(KEY_DIALOG_FRAGMENT_ITEM_NUMBER, TAKE_IMAGE);
        getTargetFragment().onActivityResult(getTargetRequestCode(), GET_FROM_DIALOG_FRAGMENT, intent);
        getDialog().dismiss();
    }

}
