/**
 * Copyright 2016
 */

package com.stockita.newpointofsales.categoryPack;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.activities.MainActivity;
import com.stockita.newpointofsales.data.ModelUser;
import com.stockita.newpointofsales.utilities.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * This class is to add new category
 */
public class AddNewCategoryFormFragment extends DialogFragment {

    /* Constant */
    private static final String LOG_TAG = AddNewCategoryFormFragment.class.getSimpleName();

    // View
    @Bind(R.id.categoryInput) EditText mCategoryInput;
    @Bind(R.id.button_save)
    Button buttonSave;

    // Member variables
    private String mEncodedEmail;

    // Firebase listener
    private ValueEventListener mValueEventListener;
    private Firebase mUserRef;

    /**
     * Empty constructor
     */
    public AddNewCategoryFormFragment() {

    }

    /**
     * Create a basic bundle to get data from the activity
     * @param encodedEmail          User email encoded
     * @return                      This fragment object
     */
    public static AddNewCategoryFormFragment newInstance(String encodedEmail) {

        /* Instantiate the fragment */
        AddNewCategoryFormFragment categoryMasterFormFragment = new AddNewCategoryFormFragment();

        /* Instantiate the Bundle */
        Bundle bundle = new Bundle();

        /* Put the data into the Bundle */
        bundle.putString(Constant.KEY_ENCODED_EMAIL, encodedEmail);

        /* Set the Bundle into the fragment using method setArgument() */
        categoryMasterFormFragment.setArguments(bundle);

        /* Return the fragment instance */
        return categoryMasterFormFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        /**
         * Get the encoded email from the bundle
         */
        mEncodedEmail = getArguments().getString(Constant.KEY_ENCODED_EMAIL);
        if (mEncodedEmail == null) dismiss();

        /**
         *  This is the URL for ModelUser in Firebase of the owner the this device
         */
        mUserRef = new Firebase(Constant.FIREBASE_URL_USERS).child(mEncodedEmail);

    }


    /**
     * Helper method, if the user click save MenuItem, then this will
     * save to Firebase
     */
    private void saveToFirebaseDatabase() {

        /**
         * Get the text from the UI
         */
        final String category = mCategoryInput.getText().toString();

        /**
         * Check if it is not empty.
         */
        if (!category.matches("")) {

            /**
             * Create Firebase references for Category
             */
            final Firebase categoryRef = new Firebase(Constant.FIREBASE_URL)
                    .child(mEncodedEmail)
                    .child(Constant.FIREBASE_LOCATION_CATEGORY);


            /**
             * Get the user data then add them in to the category for future use.
             */
            mValueEventListener = mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    /**
                     * The the user model
                     */
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);

                    /**
                     * The key is the category that is from the UI, and the value
                     * is the ModelUser.class
                     */
                    categoryRef.child(category).setValue(user);


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    if (firebaseError != null) {
                        Log.e(LOG_TAG, firebaseError.toString());
                    }
                }
            });

        } else {
            mCategoryInput.setError(getActivity().getString(R.string.error_field_cant_be_empty));
        }

        /* Clear the UI after saved */
        mCategoryInput.setText("");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Initialize the view */
        View rootView = inflater.inflate(R.layout.category_master_form_fragment, container, false);

        /* Initialize the butter knife */
        ButterKnife.bind(this, rootView);

        /* Set the dialog title */
        getDialog().setTitle(R.string.category_dialog_title);

        /* Return the view */
        return rootView;

    }

    /**
     * The button to save
     */
    @OnClick(R.id.button_save)
    void setButtonSave() {

        /* Save */
        saveToFirebaseDatabase();

        /* Close the dialog */
        dismiss();
    }


    /**
     * Remove all listeners
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            mUserRef.removeEventListener(mValueEventListener);
        }
    }
}
