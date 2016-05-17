package com.stockita.newpointofsales.itemMasterPack.newMasterItemPack;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.stockita.newpointofsales.R;

import com.stockita.newpointofsales.data.ContractData;
import com.stockita.newpointofsales.data.ModelItemImageMaster;
import com.stockita.newpointofsales.data.ModelItemMaster;
import com.stockita.newpointofsales.utilities.Constant;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is a dialog so the user can lookup at itemMaster to choose from.
 */
public class ItemMasterDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * View object using {@link ButterKnife}
     */
    @Bind(R.id.dialog_listView) RecyclerView recyclerView;

    // Constant
    public static final int KEY_LOADER_ONE = 1;
    public static final int GET_FROM_DIALOG_FRAGMENT = 2;


    // Member variable
    private Adapter mItemMasterRecyclerViewAdapter;

    /**
     * Constructor
     */
    public ItemMasterDialogFragment() {}


    /**
     * Static method as constructor to get the data pass into here.
     */
    public static ItemMasterDialogFragment newInstance(String encodedEmail) {

        /* Instantiate this fragment with default constructor */
        ItemMasterDialogFragment fragment = new ItemMasterDialogFragment();

        /* Instantiate the bundle object */
        Bundle args = new Bundle();

        /* Put data into the bundle */
        args.putString(Constant.KEY_ENCODED_EMAIL, encodedEmail);

        /* Pass the bundle to the fragment using setArgument() for later call using getArgument() */
        fragment.setArguments(args);

        /* Return this fragment */
        return fragment;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(KEY_LOADER_ONE, null, this);

    }

    /**
     * Remove all listeners from the adapter and else where if any
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mItemMasterRecyclerViewAdapter != null) {
            mItemMasterRecyclerViewAdapter.removeListeners();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Set the dialog title */
        getDialog().setTitle("Item Master");

        /* Initialize the view */
        View view = inflater.inflate(R.layout.item_master_dialog_fragment, container);

        /* Initialize the ButterKnife */
        ButterKnife.bind(this, view);

        return view;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        // The cursorLoader object
        CursorLoader loader = null;

        switch (id) {
            case KEY_LOADER_ONE:

                /* Query all the data matches the _id 1. */
                loader = new CursorLoader(getActivity(),
                        ContractData.EncodedEmailEntry.CONTENT_URI,
                        null,
                        ContractData.EncodedEmailEntry._ID + "=?",
                        new String[]{"1"},
                        null);
                break;
        }

        /* Return only the loader object that has the _id matches the "1". */
        return loader;


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        /* Get the cursor move to first. */
        data.moveToFirst();

        /* A trigger for the adapter to trigger the notifyDataChange. */
        int trigger = 0;

        switch (loader.getId()) {
            case KEY_LOADER_ONE:

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
                 * The Firebase references to the node firebase/<mEncodedEmail> or <mBossEncodedEmail>/itemMaster
                 * this object will be passed to the adapter, the listener is in the adapter later to populate
                 * the data from. The adapter is {@link ItemMasterRecyclerViewAdapter}
                 */
                Firebase lFirebaseItemMasterRef = null;

                /* check if the mBossEncodedEmail has a valid email address or a word "worker" */
                if (!lBossEncodedEmail.equalsIgnoreCase(Constant.VALUE_JOB_STATUS) && lBooleanStatus) {

                    /**
                     * Initialize the Firebase reference, to the node of itemMaster
                     * under the boss node
                     */
                    lFirebaseItemMasterRef = new Firebase(Constant.FIREBASE_URL)
                            .child(lBossEncodedEmail)
                            .child(Constant.FIREBASE_LOCATION_ITEM_MASTER);

                    /* change the trigger number to one */
                    trigger = 1;

                } else {

                    /**
                     * Initialize the Firebase reference, to the node of ItemMaster
                     * under his own node
                     */
                    if (lEncodedEmail != null) {
                        lFirebaseItemMasterRef = new Firebase(Constant.FIREBASE_URL)
                                .child(lEncodedEmail)
                                .child(Constant.FIREBASE_LOCATION_ITEM_MASTER);
                    }

                    /* change the trigger number to two */
                    trigger = 2;
                }

                /* Pass the Fireabse ref, trigger, booleanStatus, and the encodedEmail to the adapter */
                mItemMasterRecyclerViewAdapter = new Adapter(getActivity(), lFirebaseItemMasterRef, trigger, lBooleanStatus, lEncodedEmail);

                /* For screen size, different number of span */
                int columnCount = getResources().getInteger(R.integer.number_of_span);

                /* Initialize the layout manager */
                GridLayoutManager gridLayoutManager =
                        new GridLayoutManager(getActivity(), columnCount, GridLayoutManager.VERTICAL, false);

                /* Set the layout manager */
                recyclerView.setLayoutManager(gridLayoutManager);

                /* Set the adapter */
                recyclerView.setAdapter(mItemMasterRecyclerViewAdapter);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        loader = null;
    }


    /**
     * Adapter class
     */
    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {



        /**
         * The Firebase reference to the ItemMaster node
         */
        private Firebase mFirebaseRefForItemMaster;

        /**
         * The Firebase reference to the ItemImage node
         */
        private Firebase mFirebaseImageNode;

        /**
         * The listener that listen to list of data for ItemMaster
         */
        private ChildEventListener mChildEventListenerForItemMaster;

        /**
         * Boolean status, if true then he is a worker, but only when he/she set their
         * ownership status to worker
         */
        private boolean isHeWorkerAdapter;

        /**
         * The encoded email, this email or the boss email.
         */
        private String mEncodedEmailAdapter;

        /**
         * The activity context
         */
        private Context mContext;

        /**
         * An array list of type ModelItemMaster this is the data set
         * that been loaded from the Database
         */
        private ArrayList<ModelItemMaster> mListModelItemMaster;

        /**
         * This is the keys that use to store/restore data in the database
         * for ModelItemMaster
         */
        private ArrayList<String> mListModelItemMasterKeys;

        /**
         * Constructor
         */
        public Adapter(Context context, Firebase firebase, int trigger, boolean isHeWorker, String encodedEmail) {

            /* The data set, values */
            this.mListModelItemMaster = new ArrayList<>();

            /* The data set, keys */
            this.mListModelItemMasterKeys = new ArrayList<>();

             /* Activity context */
            this.mContext = context;

            /* The node to the <boss>or<current>/ItemMaster node */
            this.mFirebaseRefForItemMaster = firebase;

            /* True if he is a worker */
            this.isHeWorkerAdapter = isHeWorker;

            /* Encoded email for the current user, which is the log in email  */
            this.mEncodedEmailAdapter = encodedEmail;

            /* Get the data from the Firebase node */
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
            /**
             * Return the layout xml, in this case we only have one layout
             * apply to all position.
             */
            return R.layout.item_recycler_adapter;
        }


        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            /**
             * Initialize the view object the inflate the layout xml
             */
            View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

            /**
             * Return the ViewHolder object with itemView instance as parameter
             */
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final Adapter.ViewHolder holder, final int position) {

            /**
             * Instantiate a local variable type String.
             */
            String lItemNumber;

            // check if not null
            if (mListModelItemMaster != null) {

                // Get the item number
                lItemNumber = mListModelItemMaster.get(position).getItemNumber();
                holder.itemNumber.setText(String.valueOf(lItemNumber));

                // Get the item description
                holder.itemDescription.setText(mListModelItemMaster.get(position).getItemDescription());

                // Get the item category
                holder.itemCategory.setText(mListModelItemMaster.get(position).getCategory());

                // Get the item price
                holder.itemPrice.setText(String.valueOf(mListModelItemMaster.get(position).getPrice()));

                /* Get the key for each of this position */
                String uniqueItemMasterKey = mListModelItemMasterKeys.get(position);

                /* The Firebase reference to the ItemImage node */
                mFirebaseImageNode = new Firebase(Constant.FIREBASE_URL)
                        .child(mEncodedEmailAdapter)
                        .child(Constant.FIREBASE_LOCATION_ITEM_IMAGE)
                        .child(uniqueItemMasterKey);

                /* Retrieve the images for this item then pass to the UI */
                mFirebaseImageNode.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        /* Check if not null */
                        if (dataSnapshot != null) {

                            /* Declare the model */
                            ModelItemImageMaster lModelItemImageMaster = null;

                            /* Get the number of children */
                            long dataSize = dataSnapshot.getChildrenCount();

                            /* Instantiate an array to pack the URL String */
                            ArrayList<ModelItemImageMaster> listModelItemImageMaster = new ArrayList<>((int) dataSize);

                            /* Iterate for each, then pack the URL String into an array */
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                lModelItemImageMaster = postSnapshot.getValue(ModelItemImageMaster.class);

                                listModelItemImageMaster.add(lModelItemImageMaster);

                            }

                            /* Pass the image to the UI, only get the first image which is at index 0 */
                            if (listModelItemImageMaster.size() > 0) {
                                holder.setItemPoster(mContext, Constant.CLOUDINARY_BASE_URL_DOWNLOAD + listModelItemImageMaster.get(0).getImageName());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                /* When the user single click on an item */
                holder.itemMasterPlaceCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /* Get the model at this position */
                        ModelItemMaster modelItemMaster = mListModelItemMaster.get(position);

                        /* Get the item from the POJO for this position  */
                        String itemNumber = modelItemMaster.getItemNumber();

                        /* Use intent to pass the data back, the data will be received in the onActivityResult() */
                        Intent intent = new Intent();
                        intent.putExtra(Constant.KEY_DIALOG_FRAGMENT_ITEM_NUMBER, itemNumber);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), GET_FROM_DIALOG_FRAGMENT, intent);

                        /* Dismiss the dialog */
                        getDialog().dismiss();

                    }
                });
            }
        }

        @Override
        public int getItemCount() {

            /**
             * Return the size of the data, or 0 if null.
             */
            return mListModelItemMaster != null ? mListModelItemMaster.size() : 0;
        }


        /**
         * This helper method will get and listen for the data from Firebase node
         */
        private void getDataFromFirebase() {

        /* Check if it is not null */
            if (mFirebaseRefForItemMaster != null) {

                /**
                 * This listener will get data from Firebase, then pass the data to an ArrayList
                 */
                mChildEventListenerForItemMaster = mFirebaseRefForItemMaster.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        /**
                         * Check if not null
                         */
                        if (dataSnapshot != null) {

                            ModelItemMaster modelItemMaster = dataSnapshot.getValue(ModelItemMaster.class);

                            /*  Pack the POJO into an ArrayList */
                            mListModelItemMaster.add(modelItemMaster);

                            /* Pack the keys into an ArrayList */
                            mListModelItemMasterKeys.add(dataSnapshot.getKey());

                            /* Notify this adapter for any data change */
                            notifyDataSetChanged();

                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    /* Check if not null */
                        if (dataSnapshot != null) {

                            /* Initialize the POJO */
                            ModelItemMaster model = dataSnapshot.getValue(ModelItemMaster.class);

                            /* Get the key for the value that changed */
                            String keyChanged = dataSnapshot.getKey();

                            /* Find the index of that key */
                            int theIndexOfKey = mListModelItemMasterKeys.indexOf(keyChanged);

                            /* Revise/Set the value for that index */
                            mListModelItemMaster.set(theIndexOfKey, model);

                            /* Notify this adapter for any data changes */
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    /* Check if not null */
                        if (dataSnapshot != null) {

                            /* Get the key */
                            String keyRemoved = dataSnapshot.getKey();

                            /* Find the index of that key */
                            int theIndexOfKey = mListModelItemMasterKeys.indexOf(keyRemoved);

                            /* Remove the value from the list */
                            mListModelItemMaster.remove(theIndexOfKey);

                            /* Remove the key from the list */
                            mListModelItemMasterKeys.remove(theIndexOfKey);

                             /* Notify this adapter for any data changes */
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
         * because it will be invoked later by
         */
        public void removeListeners(){
            if (mFirebaseRefForItemMaster != null && mChildEventListenerForItemMaster != null) {
                mFirebaseRefForItemMaster.removeEventListener(mChildEventListenerForItemMaster);
            }

        }


        /**
         * View holder class
         */
        public class ViewHolder extends RecyclerView.ViewHolder {

            // Field
            @Bind(R.id.root)
            CardView itemMasterPlaceCard;
            @Bind(R.id.itemPoster)
            ImageView itemPoster;
            @Bind(R.id.itemName)
            TextView itemDescription;
            @Bind(R.id.itemNumber)
            TextView itemNumber;
            @Bind(R.id.itemPrice)
            TextView itemPrice;
            @Bind(R.id.itemCategory)
            TextView itemCategory;

            /**
             * Constructor
             * @param itemView      View object
             */
            public ViewHolder(View itemView) {
                super(itemView);

                /* Initialize the views */
                ButterKnife.bind(this, itemView);

            }

            /**
             * Load the image to the UI
             */
            public void setItemPoster(Context context, String url) {
                if (itemPoster != null) {
                    Glide.with(context)
                            .load(url)
                            .centerCrop()
                            .into(itemPoster);
                    itemPoster.setBackgroundColor(Color.TRANSPARENT);

                }
            }

        }
    }

}
