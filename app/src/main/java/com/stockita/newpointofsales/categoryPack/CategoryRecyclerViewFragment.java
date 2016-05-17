package com.stockita.newpointofsales.categoryPack;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.data.ContractData;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.Utility;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is to show a list of category that the user create, it stores the data in the cloud
 * so the user can share these data with others
 */
public class CategoryRecyclerViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    /* Recycler view object */
    @Bind(R.id.tab_one_list) RecyclerView recyclerView;

    /**
     * The adapter object, is the custom adapter to populate data into
     * this Recycler View fragment
     */
    private CategoryRecyclerViewFragment.Adapter mCategoryMasterRecyclerViewAdapter;

    /**
     * Empty constructor
     */
    public CategoryRecyclerViewFragment() {}

    /**
     * Set the data into a bundle, so here we are passing the mEncodedEmail from
     * the activity into here.
     *
     * @param encodedEmail          Email address passed by the activity
     */
    public static CategoryRecyclerViewFragment newInstance(String encodedEmail) {

        /* Instantiate this fragment with default constructor */
        CategoryRecyclerViewFragment fragment = new CategoryRecyclerViewFragment();

        /* Instantiate the bundle instance */
        Bundle args = new Bundle();

        /**
         * Put data into the bundle
         */
        args.putString(Constant.KEY_ENCODED_EMAIL, encodedEmail);

        /* Pass the bundle to the fragment using setArgument() for later call using getArgument() */
        fragment.setArguments(args);

        /* Return this fragment */
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        /* Start the loader<Cursor> */
        getLoaderManager().initLoader(Constant.KEY_LOAD_ONE, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Initialize the view */
        View rootView = inflater.inflate(R.layout.category_recycler_view, container, false);

        /* Initialize the ButterKnife */
        ButterKnife.bind(this, rootView);

        /* For screen size, different number of span */
        int columnCount = getResources().getInteger(R.integer.number_of_span);

        /* Set the fix size */
        recyclerView.setHasFixedSize(true);

        /* Set the layout manager */
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnCount, GridLayoutManager.VERTICAL, false));

        /* Return the view */
        return rootView;
    }

    /**
     * Remove the listener here.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        /**
         * Remove the listeners from within the adapter, the method
         * {@link CategoryRecyclerViewFragment.Adapter#removeListeners()} is set
         * to remove all the listeners there, but invoking this method here inside
         * the onDestroy()
         */
        if (mCategoryMasterRecyclerViewAdapter != null) {
            mCategoryMasterRecyclerViewAdapter.removeListeners();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // The cursorLoader object
        CursorLoader loader = null;

        switch (id) {
            case Constant.KEY_LOAD_ONE:

                // Query all the data matches the _id 1.
                loader = new CursorLoader(getActivity(),
                        ContractData.EncodedEmailEntry.CONTENT_URI,
                        null,
                        ContractData.EncodedEmailEntry._ID + "=?",
                        new String[]{"1"},
                        null);
                break;

        }

        // Return only the loader object that has the _id matches the "1".
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Get the cursor move to first.
        data.moveToFirst();

        // A trigger for the adapter to trigger the notifyDataChange.
        int trigger = 0;

        switch (loader.getId()) {
            case Constant.KEY_LOAD_ONE:

                /**
                 * The encoded email that pass from the activity
                 * this will be used for node in Firebase, this is the current
                 * user encoded email /users/<lEncodedEmail>/ModelUser.
                 * Retrieve the lEncodedEmail from the bundle
                 */
                String lEncodedEmail = getArguments().getString(Constant.KEY_ENCODED_EMAIL);

                /**
                 * Get the boss encoded email & the boolean status both from the local database.
                 * This is the boss encoded email if the user chooses to be a worker.
                 * The boolean status will be changing depend on the listener in
                 * {@link com.stockita.newpointofsales.activities.MainActivity#addTheWorkerToUserWorkersNode(String, String)}
                 */
                String lBossEncodedEmail = data.getString(ContractData.EncodedEmailEntry.INDEX_COL_ENCODED_EMAIL);
                boolean lBooleanStatus = Boolean.parseBoolean(data.getString(ContractData.EncodedEmailEntry.INDEX_COL_BOOLEAN_STATUS));

                /**
                 * The Firebase references to the node firebase/<mEncodedEmail> or <mBossEncodedEmail>/category
                 * this object will be passed to the adapter, the listener is in the adapter later to populate
                 * the data from. The adapter is {@link CategoryRecyclerViewFragment.Adapter}
                 */
                Firebase lFirebaseCategoryRef = null;

                /* check if the mBossEncodedEmail has a valid email address or a word "worker" */
                if (!lBossEncodedEmail.equalsIgnoreCase(Constant.VALUE_JOB_STATUS) && lBooleanStatus) {

                    /**
                     * Initialize the Firebase reference, to the node of category
                     * under the boss node
                     */
                    lFirebaseCategoryRef = new Firebase(Constant.FIREBASE_URL)
                            .child(lBossEncodedEmail)
                            .child(Constant.FIREBASE_LOCATION_CATEGORY);

                    /* change the trigger number to one */
                    trigger = 1;

                } else {

                    /* Just double check, actually this is not necessary */
                    if (lEncodedEmail != null) {

                        /**
                         * Initialize the Firebase reference, to the node of category
                         * under his own node
                         */
                        lFirebaseCategoryRef = new Firebase(Constant.FIREBASE_URL)
                                .child(lEncodedEmail)
                                .child(Constant.FIREBASE_LOCATION_CATEGORY);

                        /* change the trigger number to two */
                        trigger = 2;
                    }
                }

                /* Pass the Fireabse ref, trigger, booleanStatus, and the encodedEmail to the adapter */
                mCategoryMasterRecyclerViewAdapter = new CategoryRecyclerViewFragment.Adapter(getActivity(), lFirebaseCategoryRef, trigger, lBooleanStatus, lEncodedEmail);

                /* Set the adapter */
                recyclerView.setAdapter(mCategoryMasterRecyclerViewAdapter);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = null;
    }


    /**
     * This class is the adapter to populate data into the recycler view
     */
    public class Adapter extends RecyclerView.Adapter<CategoryRecyclerViewFragment.Adapter.ViewHolder> {

        /* Log */
        private final String LOG_TAG = Adapter.class.getSimpleName();

        /**
         * This is the container that hold the keys from Firebase,
         * which is the category.
         */
        private ArrayList<String> mDataKeyAdapter;


        /**
         * The reference to the /<boss>/category or /<encodedEmail>/category node,
         * and the listener to listen as list
         */
        private Firebase mFirebaseRefForCategory;
        private ChildEventListener mChildEventListenerForCategory;


        /**
         * Activity context, passed by the {@link CategoryRecyclerViewFragment}
         */
        private Context mActivityAdapter;


        /**
         * If he is worker then true, if he is not worker then false
         * the false will allow him to delete category item
         * passed from {@link CategoryRecyclerViewFragment#onLoadFinished(Loader, Cursor)}
         */
        private boolean isHeWorkerAdapter;


        /**
         * This is the current user encoded email, passed by {@link CategoryRecyclerViewFragment}
         */
        private String mEncodedEmailAdapter;

        /**
         * Constructor
         */
        public Adapter(Context activity, Firebase firebase, int trigger, boolean isHeWorker, String encodedEmail){

            /**
             * Initialize the ArrayList as container to hold the keys, actually the keys are
             * the category name
             */
            this.mDataKeyAdapter = new ArrayList<>();
            this.mFirebaseRefForCategory = firebase;
            this.mActivityAdapter = activity;
            this.isHeWorkerAdapter = isHeWorker;
            this.mEncodedEmailAdapter = encodedEmail;

            /**
             *  Get the data to populate, first it will listen to Firerbase then
             *  data will populate to mDataKey.
             */
            getDataFromFirebase();

            /**
             * Since the method notifyDataSetChange are unable to detect
             * changes within an object, then we add this trigger to trigger
             * notifyDataSetChange.
             */
            int lTrigger = trigger;

            /* Notify this adapter for any data change */
            notifyDataSetChanged();
        }


        @Override
        public int getItemViewType(int position) {
            return R.layout.category_recycler_adapter;
        }


        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        /* Initialize the view, then inflate that layout xml */
            View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        /* Return a ViewHolder */
            return new ViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(final Adapter.ViewHolder holder, int position) {


            /* Animate the recycler view */
            Utility.animate(mActivityAdapter, holder, R.anim.anticipateovershoot_interpolator);

            /* Check if not null */
            if (mDataKeyAdapter != null) {

                /**
                 * Get the data from the container which is the keys, the key is the
                 * name of the category.
                 */
                final String category = mDataKeyAdapter.get(holder.getAdapterPosition());

                /**
                 * Pass the string category to the UI
                 */
                holder.categoryTitle.setText(category);

                /**
                 * The click listeners
                 */
                holder.categoryMasterPlaceCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mActivityAdapter, "click at " + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    }
                });

                /**
                 * The long click listener
                 */
                holder.categoryMasterPlaceCard.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        /**
                         * Only the owner of this can delete category
                         */
                        if (!isHeWorkerAdapter) {

                            /**
                             * {@link android.app.AlertDialog.Builder}
                             */
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivityAdapter);
                            alertDialog.setTitle("DELETE")
                                    .setMessage("Are you sure you want to delete this?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            /* Get the category references */
                                            Firebase categoryRef = new Firebase(Constant.FIREBASE_URL)
                                                    .child(mEncodedEmailAdapter)
                                                    .child(Constant.FIREBASE_LOCATION_CATEGORY)
                                                    .child(category);

                                             /* Remove the selected category */
                                            categoryRef.setValue(null);

                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing

                                }
                            }).show();
                        }

                        return true;
                    }
                });

            }
        }


        @Override
        public int getItemCount() {
            return mDataKeyAdapter != null ? mDataKeyAdapter.size() : 0;
        }


        /**
         * This helper method to get the data from Firebase's Listeners,
         * this method must be invoke within the constructor of this adapter.
         * {@link Firebase}
         */
        private void getDataFromFirebase() {

            /* Check if it is not null */
            if (mFirebaseRefForCategory != null) {

                // Delete old category local database before insert new data
                try {
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    contentResolver.delete(ContractData.CategoryEntry.CONTENT_URI,
                            null, null);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }


                /**
                 * This listener will get data from Firebase, then pass the data to an ArrayList
                 */
                mChildEventListenerForCategory = mFirebaseRefForCategory.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        /**
                         * Check if not null
                         */
                        if (dataSnapshot != null) {

                            /**
                             * Get the key only for this node, and them into an ArrayList,
                             * this arrayList that will populate
                             * {@link CategoryRecyclerViewFragment.Adapter#onBindViewHolder(ViewHolder, int)}.
                             */
                            String key = dataSnapshot.getKey();
                            mDataKeyAdapter.add(key);

                            /* Containers */
                            ContentValues contentValues = new ContentValues();

                            /* Packing */
                            contentValues.put(ContractData.CategoryEntry.COLUMN_CATEGORY, key);

                            /* Insert to local database for use later with the widget */
                            try {
                                getActivity().getContentResolver().insert(ContractData.CategoryEntry.CONTENT_URI, contentValues);
                            } catch (Exception e) {
                                Log.e(LOG_TAG, e.toString());
                            }

                            /* Notify this adapter for any data change */
                            notifyDataSetChanged();

                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        // Key never change so do nothing here
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                        /**
                         * Check if not null
                         */
                        if (dataSnapshot != null) {

                            /**
                             * Get the key, it is the value that we will remove
                             * it from our ArrayList mDataKey
                             */
                            String keyRemoved = dataSnapshot.getKey();

                            /* Find the index of that key */
                            int theIndexOfKey = mDataKeyAdapter.indexOf(keyRemoved);

                            /* Remove the key from the list, we can remove object
                             * but I prefer this way
                             */
                            mDataKeyAdapter.remove(theIndexOfKey);

                            /* Notify this adapter for any data change */
                            notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        // Nothing
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        // Nothing
                    }
                });
            }
        }


        /**
         * Helper method to remove all listeners, this must be public
         * because it will be invoked later by {@link CategoryRecyclerViewFragment#onDestroy()}
         */
        public void removeListeners(){
            if (mFirebaseRefForCategory != null && mChildEventListenerForCategory != null) {
                mFirebaseRefForCategory.removeEventListener(mChildEventListenerForCategory);
            }
        }


        /**
         * Inner class the ViewHolder
         * {@link android.support.v7.widget.RecyclerView.ViewHolder}
         */
        public class ViewHolder extends RecyclerView.ViewHolder {

            /**
             * Views using {@link ButterKnife}
             */
            @Bind(R.id.categoryMasterPlaceCard)
            CardView categoryMasterPlaceCard;
            @Bind(R.id.categoryPoster)
            ImageView categoryPoster;
            @Bind(R.id.categoryTitle)
            TextView categoryTitle;


            /**
             * Constructor
             * @param itemView      the View object
             */
            public ViewHolder(View itemView) {
                super(itemView);

                /**
                 * Initialize the {@link ButterKnife}
                 */
                ButterKnife.bind(this, itemView);
            }
        }
    }
}


