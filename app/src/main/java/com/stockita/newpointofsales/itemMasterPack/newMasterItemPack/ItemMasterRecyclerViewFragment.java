package com.stockita.newpointofsales.itemMasterPack.newMasterItemPack;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.stockita.newpointofsales.R;

import com.stockita.newpointofsales.activities.MainActivity;
import com.stockita.newpointofsales.data.ContractData;
import com.stockita.newpointofsales.data.ModelItemImageMaster;
import com.stockita.newpointofsales.data.ModelItemMaster;
import com.stockita.newpointofsales.interfaces.ItemSelectedSingleClick;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is to view the list of item master into the user UI, using RecyclerView
 */
public class ItemMasterRecyclerViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ItemMasterRecyclerViewFragment.class.getSimpleName();


    // View
    @Bind(R.id.tab_two_list)
    RecyclerView recyclerView;

    /**
     * Key for the Loader
     */
    private static final int LOADER_ID_ONE = 1;


    /**
     * The adapter instant
     */
    private ItemMasterCustomAdapterUi mFirebaseUiAdapter;


    /**
     * Empty constructor
     */
    public ItemMasterRecyclerViewFragment() {
    }


    /**
     * Returns a new instance of this fragment, to pass data in here
     */
    public static ItemMasterRecyclerViewFragment newInstance(String encodedEmail) {

        /* Instantiate this fragment with default constructor */
        ItemMasterRecyclerViewFragment fragment = new ItemMasterRecyclerViewFragment();

        /* Instantiate the bundle object */
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* Initialize the loader, to get the boss email and the boolean status from local database */
        getLoaderManager().initLoader(LOADER_ID_ONE, null, this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Initialize the view */
        View rootView = inflater.inflate(R.layout.item_recycler_view, container, false);

        /* Initialize the ButterKnife */
        ButterKnife.bind(this, rootView);

        /**
         * Here we want to set the number of span for the recycler view layout,
         */
        int columnCount;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isLand = getResources().getBoolean(R.bool.isLandscape);

        /* if on tablet and landscape then have 2 span */
        if (isTablet && isLand) {
            columnCount = 2;
        } else if (!isTablet && isLand) {
            columnCount = 2;
        } else {
            columnCount = 1;
        }

        /* Fix the size */
        recyclerView.setHasFixedSize(true);

        /* Initialize the layout manager */
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getActivity(), columnCount, GridLayoutManager.VERTICAL, false);

        /* Set the layout manager */
        recyclerView.setLayoutManager(gridLayoutManager);

        /* Return the view */
        return rootView;
    }


    /**
     * Remove all listeners from the adapter and else where if any
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mFirebaseUiAdapter != null) {
            mFirebaseUiAdapter.cleanup();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        /* Constant query record ID 1 */
        final String CONSTANT_QUERY_ID = "1";

        /* The cursorLoader object */
        CursorLoader loader = null;

        switch (id) {
            case LOADER_ID_ONE:

                /* Query all the data matches the _id 1. */
                loader = new CursorLoader(getActivity(),
                        ContractData.EncodedEmailEntry.CONTENT_URI,
                        null,
                        ContractData.EncodedEmailEntry._ID + "=?",
                        new String[]{CONSTANT_QUERY_ID},
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

        /**
         * This is the String node to the itemMaster without the Firebase ref, can be ref to
         * encodedEmail or bossEncodedEmail
         */
        String itemMasterNode = null;

        /* If he is owner then this is the login email, if he is a worker then this is the boss email */
        String usefulEncodedEmail = null;

        switch (loader.getId()) {
            case LOADER_ID_ONE:

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

                    /* the item master node before push() key */
                    itemMasterNode = "/" + lBossEncodedEmail + "/" + Constant.FIREBASE_LOCATION_ITEM_MASTER;

                    /* He is a worker so this is the boss encoded email */
                    usefulEncodedEmail = lBossEncodedEmail;

                } else {

                    /**
                     * Initialize the Firebase reference, to the node of ItemMaster
                     * under his own node
                     */
                    if (lEncodedEmail != null) {
                        lFirebaseItemMasterRef = new Firebase(Constant.FIREBASE_URL)
                                .child(lEncodedEmail)
                                .child(Constant.FIREBASE_LOCATION_ITEM_MASTER);

                        /* the item master node before push() key */
                        itemMasterNode = "/" + lEncodedEmail + "/" + Constant.FIREBASE_LOCATION_ITEM_MASTER;

                        /* He is the owner so this is the login encoded email */
                        usefulEncodedEmail = lEncodedEmail;

                    }
                }


                /**
                 * Initialize the adapter to populate data into the recycler view
                 */
                mFirebaseUiAdapter = new ItemMasterCustomAdapterUi(ModelItemMaster.class, R.layout.item_recycler_adapter, FragmentViewHolderAdapter.class, lFirebaseItemMasterRef, usefulEncodedEmail, lBooleanStatus, itemMasterNode) {

                    @Override
                    protected void populateViewHolder(final FragmentViewHolderAdapter fragmentViewHolderAdapter, ModelItemMaster modelItemMaster, final int position) {

                        /* Get the push() key for item in this position, so we can use it to restore imageItem */
                        Firebase keyRefrence = getRef(fragmentViewHolderAdapter.getLayoutPosition());
                        final String uniqueItemMasterKey = keyRefrence.getKey();

                        /* Get the item number */
                        String lItemNumber = modelItemMaster.getItemNumber();
                        fragmentViewHolderAdapter.itemNumber.setText(String.valueOf(lItemNumber));

                        /* Get the item description */
                        fragmentViewHolderAdapter.itemDescription.setText(modelItemMaster.getItemDescription());

                        /* Get the item category */
                        fragmentViewHolderAdapter.itemCategory.setText(modelItemMaster.getCategory());

                        /* Get the item price */
                        fragmentViewHolderAdapter.itemPrice.setText(String.valueOf(modelItemMaster.getPrice()));

                        /* The Firebase reference to the ItemImage node */
                        Firebase mFirebaseImageNode = new Firebase(Constant.FIREBASE_URL)
                                .child(mUsefulEncodedEmail)
                                .child(Constant.FIREBASE_LOCATION_ITEM_IMAGE);

                        /* Get the node of the imageItem so we can pass them with intent/bundle to detail activity */
                        final String stringImageNode = "/" + mUsefulEncodedEmail + "/" + Constant.FIREBASE_LOCATION_ITEM_IMAGE;

                        /* Retrieve the images for this item then pass to the UI, this we use for the image on the card in recycler view list */
                        mFirebaseImageNode.child(uniqueItemMasterKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                /* Declare the model */
                                ModelItemImageMaster imageModel = null;

                                /* Get the number of children */
                                long dataSize = dataSnapshot.getChildrenCount();

                                /* Instantiate an array to pack the URL String */
                                ArrayList<ModelItemImageMaster> modelList = new ArrayList<>((int) dataSize);

                                /* Iterate for each, then pack the URL String into an array */
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    imageModel = postSnapshot.getValue(ModelItemImageMaster.class);
                                    String keys = postSnapshot.getKey();

                                    /* Add the values here */
                                    modelList.add(imageModel);

                                }

                                /* Only get the first image which is at index 0 to pass to UI */
                                if (modelList.size() > 0) {

                                    /* Get the first image name */
                                    String imageName = modelList.get(0).getImageName();

                                    /* Get the image from the cloud */
                                    fragmentViewHolderAdapter.setItemPoster(getActivity(),
                                             Constant.CLOUDINARY_BASE_URL_DOWNLOAD + imageName);

                                }
                            }


                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                if (firebaseError != null) {
                                    Log.e(LOG_TAG, firebaseError.toString());
                                }
                            }
                        });


                        /**
                         * When the user single click on an item, start detail activity and pass
                         * some data using intent.
                         */
                        fragmentViewHolderAdapter.root.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                /**
                                 * Initialize the intent and pass the data to the destination activity, for phone
                                 * we will go to {@link DetailMasterItemFormActivity.java}
                                 */
                                final Intent intent = new Intent(getActivity(), DetailMasterItemFormActivity.class);

                                /**
                                 *  The list as container for ModelItemMaster, we need this for the sliding/page tab
                                 */
                                intent.putParcelableArrayListExtra(Constant.KEY_ITEM_MASTER_POJO_LIST, mListModelItemMaster);

                                /**
                                 * The list as container for the push() keys for each element in mListModelItemMaster,
                                 * just in case we may need them in the future.
                                 */
                                intent.putStringArrayListExtra(Constant.KEY_ITEM_MASTER_KEY_LIST, mListModelItemMasterKeys);

                                /* Pass the node of this itemMaster including the push() key */
                                String stringItemMasterNode = mItemMasterNode + "/" + uniqueItemMasterKey;
                                intent.putExtra(Constant.KEY_ITEM_MASTER_NODE, stringItemMasterNode);

                                /**
                                 * Pass the node of the itemImage without the push() of this itemMaster,
                                 * so we may use it for other items in the sliding/page tab
                                 */
                                intent.putExtra(Constant.KEY_ITEM_IMAGE_MASTER_NODE, String.valueOf(stringImageNode));

                                /* The boolean status for the ownership status */
                                intent.putExtra(Constant.KEY_BOOLEAN_STATUS, isHeWorkerAdapter);

                                 /* The current position of this item in the list */
                                intent.putExtra(Constant.KEY_CURRENT_POSITION, position);

                                /**
                                 *  Check the version, if he is worker then better not to use transition animation
                                 *  due to some error may occur.
                                 */
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !isHeWorkerAdapter) {

                                    /* Set the scene transition and animation */
                                    ActivityOptionsCompat optionsCompat =
                                            ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), fragmentViewHolderAdapter.root, "root");

                                    /* This will return true if this device is a tablet SW-600 */
                                    boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

                                    /**
                                     * So the workaround here if this device is tablet then use this callbacks method in the {@link MainActivity},
                                     * otherwise start new activity which is {@link DetailMasterItemFormActivity}
                                     * */
                                    if (tabletSize) {

                                        /**
                                         *  Go to MainActivity and implement this method, pas the intent data and the position, this will
                                         *  have a two pane layout
                                         */
                                        ((ItemSelectedSingleClick) getActivity()).setSingleClick(getView(), position, intent);

                                    } else {

                                        /**
                                         * This is a single pane layout and pass the transition also,
                                         * {@link DetailMasterItemFormActivity}
                                         */
                                        startActivity(intent, optionsCompat.toBundle());

                                    }

                                } else {

                                    /**
                                     *  Start a new activity without transition which is {@link DetailMasterItemFormActivity}
                                     */
                                    startActivity(intent);

                                }
                            }
                        });


                        /**
                         *  When the user long click on an item, simple will remove the item from the database
                         *  and from the list UI
                         */
                        fragmentViewHolderAdapter.root.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                /* Instantiate the builder */
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setTitle(getString(R.string.alert_dialog_title_delete))
                                        .setMessage(getString(R.string.alert_dialog_message))
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.alert_dialog_positive_button_yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                /* Get the ItemMaster references */
                                                Firebase itemMaster = new Firebase(Constant.FIREBASE_URL)
                                                        .child(mUsefulEncodedEmail)
                                                        .child(Constant.FIREBASE_LOCATION_ITEM_MASTER)
                                                        .child(uniqueItemMasterKey);

                                                /* Remove the selected itemMaster */
                                                itemMaster.setValue(null);

                                                /* Ge the itemImage reference */
                                                Firebase itemImage = new Firebase(Constant.FIREBASE_URL)
                                                        .child(mUsefulEncodedEmail)
                                                        .child(Constant.FIREBASE_LOCATION_ITEM_IMAGE)
                                                        .child(uniqueItemMasterKey);

                                                /* Get the file name for each image then delete them from the cloud */
                                                itemImage.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        /* Iterate through all the images in this node */
                                                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                                                            /* Initialize the model */
                                                            ModelItemImageMaster model = postSnapshot.getValue(ModelItemImageMaster.class);

                                                            /* Get the image file name */
                                                            String imageName = model.getImageName();

                                                            /* Delete this image from the cloud */
                                                            Utility.deleteImagesFromCloud(getActivity(), imageName);

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(FirebaseError firebaseError) {

                                                        if (firebaseError != null) {
                                                            Log.e(LOG_TAG, firebaseError.toString());
                                                        }
                                                    }
                                                });

                                                /* Remove the selected itemImage from firebase database*/
                                                itemImage.setValue(null);

                                            }
                                        }).setNegativeButton(getString(R.string.alert_dialog_negative_button_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing
                                    }
                                }).show();

                                return true;

                            }
                        });

                    }


                    @Override
                    public void onBindViewHolder(FragmentViewHolderAdapter holder, int position, List<Object> payloads) {
                        super.onBindViewHolder(holder, position, payloads);

                        /* Animate the recycler view */
                        //Utility.animate(getActivity(), holder, R.anim.anticipateovershoot_interpolator);
                    }


                    @Override
                    public FragmentViewHolderAdapter onCreateViewHolder(ViewGroup parent, int viewType) {

                        /**
                         * Initialize the view object the inflate the layout xml
                         */
                        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_adapter, parent, false);

                        /**
                         * Return the ViewHolder object with itemView instance as parameter
                         */
                        return new FragmentViewHolderAdapter(itemView);

                    }

                };

                /* Set the adapter */
                recyclerView.setAdapter(mFirebaseUiAdapter);

                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = null;
    }


    /**
     * This is the view holder to be used with the recycler view adapter
     */
    public class FragmentViewHolderAdapter extends RecyclerView.ViewHolder {

        // Field
        @Bind(R.id.root)
        CardView root;
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
         *
         */
        public FragmentViewHolderAdapter(View itemView) {
            super(itemView);

            /*  Initialize the butter knife */
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

    /**
     * Custom Adapter for the FirebaseRecyclerAdapter UI, this is abstract class
     */
    public abstract class ItemMasterCustomAdapterUi extends FirebaseRecyclerAdapter<ModelItemMaster, ItemMasterRecyclerViewFragment.FragmentViewHolderAdapter> {


        /**
         * The encoded email, if he is the boss then this is the same as login email, but
         * if he is a worker then this is the boss encoded email.
         */
        protected String mUsefulEncodedEmail;

        /**
         * The item master node without push() key
         */
        protected String mItemMasterNode;

        /**
         * Boolean status, if true then he is a worker, but only when he/she set their
         * ownership status to worker
         */
        protected boolean isHeWorkerAdapter;


        /**
         * An array list of type ModelItemMaster this is the data set
         * that been loaded from the Database
         */
        protected ArrayList<ModelItemMaster> mListModelItemMaster;


        /**
         * This is the keys that use to store/restore data in the database
         * for ModelItemMaster
         */
        protected ArrayList<String> mListModelItemMasterKeys;


        /**
         * The Firebase reference to the ItemMaster node
         */
        protected Firebase mFirebaseRefForItemMaster;


        /**
         * The listener that listen to list of data for ItemMaster
         */
        private ChildEventListener mChildEventListenerForItemMaster;


        /**
         * Constructor
         */
        public ItemMasterCustomAdapterUi(Class modelClass, int modelLayout, Class viewHolderClass, Firebase ref, String encodedEmail, boolean worker, String itemMasterNode) {
            super(modelClass, modelLayout, viewHolderClass, ref);

            /* Encoded email for the current user, which is the log in email  */
            this.mUsefulEncodedEmail = encodedEmail;

            /* True if he is a worker */
            this.isHeWorkerAdapter = worker;

            /* The data set, values */
            this.mListModelItemMaster = new ArrayList<>();

            /* The data set, keys */
            this.mListModelItemMasterKeys = new ArrayList<>();

            /* The node to the <boss>or<current>/ItemMaster node */
            this.mFirebaseRefForItemMaster = ref;

            /* The node to the <boss>or<current>/itemMaster without firebase without push() key in String type */
            mItemMasterNode = itemMasterNode;

            /* Get the list of item master from firebase so we can use it for the tab in detail activity */
            getDataFromFirebase();

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

                        /* Instantiate the model */
                        ModelItemMaster modelItemMaster = dataSnapshot.getValue(ModelItemMaster.class);

                        /*  Pack the POJO into an ArrayList */
                        mListModelItemMaster.add(modelItemMaster);

                        /* Pack the keys into an ArrayList */
                        mListModelItemMasterKeys.add(dataSnapshot.getKey());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        /* Instantiate the POJO */
                        ModelItemMaster model = dataSnapshot.getValue(ModelItemMaster.class);

                        /* Get the key for the value that changed */
                        String keyChanged = dataSnapshot.getKey();

                        /* Find the index of that key */
                        int theIndexOfKey = mListModelItemMasterKeys.indexOf(keyChanged);

                        /* Revise/Set the value for that index */
                        mListModelItemMaster.set(theIndexOfKey, model);

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {


                        /* Get the key */
                        String keyRemoved = dataSnapshot.getKey();

                        /* Find the index of that key */
                        int theIndexOfKey = mListModelItemMasterKeys.indexOf(keyRemoved);

                        /* Remove the value from the list */
                        mListModelItemMaster.remove(theIndexOfKey);

                        /* Remove the key from the list */
                        mListModelItemMasterKeys.remove(theIndexOfKey);


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

        @Override
        public void cleanup() {
            super.cleanup();

            if (mFirebaseRefForItemMaster != null && mChildEventListenerForItemMaster != null) {
                mFirebaseRefForItemMaster.removeEventListener(mChildEventListenerForItemMaster);
            }
        }


    } // End abstract adapter
} // Fragment class
