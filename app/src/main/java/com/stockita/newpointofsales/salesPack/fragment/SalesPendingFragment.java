package com.stockita.newpointofsales.salesPack.fragment;

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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.activities.MainActivity;
import com.stockita.newpointofsales.data.ModelItemImageMaster;
import com.stockita.newpointofsales.data.ModelTransactionPointOfSalesDetail;
import com.stockita.newpointofsales.data.ModelTransactionPointOfSalesHeader;

import com.stockita.newpointofsales.data.ContractData;
import com.stockita.newpointofsales.salesPack.activity.DetailSalesActivity;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.Utility;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is a fragment to show the Sales on the recycler view
 */
public class SalesPendingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /* Constant */
    private static final String LOG_TAG = SalesPendingFragment.class.getSimpleName();
    private static final int LOADER_ID_ONE = 1;
    private static final String ARG_SECTION_NUMBER = "selection_number";


    /**
     * The recycler view instance variable
     */
    @Bind(R.id.pending_payment)
    RecyclerView recyclerView;


    /**
     * The tab section number
     */
    private static int sSelection;


    /**
     * Variable to hold the payment type
     */
    private boolean booleanStatus;
    private Firebase mSalesHeaderRef;
    private Firebase mSalesDetailRef;
    private String mSalesHeaderNode;
    private String mSalesDetailNode;
    private String mUsefulEncodedEmail;
    private String mEncodedEmail;


    /**
     * The adapter to populate salesHeader into recyclerView
     */
    private Adapter mAdapter;


    /**
     * Empty Constructor
     */
    public SalesPendingFragment() {
    }


    /**
     * This method is to pass data into this fragment
     */
    public static SalesPendingFragment newInstance(int sectionNumber, String encodedEmail) {
        SalesPendingFragment fragment = new SalesPendingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(Constant.KEY_ENCODED_EMAIL, encodedEmail);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /* Static field */
        sSelection = getArguments().getInt(ARG_SECTION_NUMBER);

        /* Get the login encoded email from the activity */
        mEncodedEmail = getArguments().getString(Constant.KEY_ENCODED_EMAIL);
        if (mEncodedEmail == null) getActivity().finish();

        /* Initialize the loader, to get the boss email and the boolean status from local database */
        getLoaderManager().initLoader(LOADER_ID_ONE, null, this);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Initialize the view */
        View rootView = inflater.inflate(R.layout.sales_pending_fragment, container, false);

        /* Initialize the View Holder */
        ButterKnife.bind(this, rootView);

        /* Return the view */
        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        /* Remove all listeners here */
        mAdapter.removeListeners();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /* The cursorLoader object */
        CursorLoader loader = null;

        switch (id) {
            case LOADER_ID_ONE:

                // Query all the data matches the _id 1.
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

        switch (loader.getId()) {
            case LOADER_ID_ONE:

                /**
                 * Get the boss encoded email & the boolean status both from the local database.
                 * This is the boss encoded email if the user chooses to be a worker.
                 */
                String bossEncodedEmail = data.getString(ContractData.EncodedEmailEntry.INDEX_COL_ENCODED_EMAIL);
                booleanStatus = Boolean.parseBoolean(data.getString(ContractData.EncodedEmailEntry.INDEX_COL_BOOLEAN_STATUS));

                /* check if the mBossEncodedEmail has a valid email address or a word "worker" */
                if (!bossEncodedEmail.equalsIgnoreCase(Constant.VALUE_JOB_STATUS) && booleanStatus) {

                    /**
                     * Initialize the Firebase reference, to the node of SalesHeader & SalesDetail
                     * under the boss node
                     */
                    mSalesHeaderRef = new Firebase(Constant.FIREBASE_URL)
                            .child(bossEncodedEmail)
                            .child(Constant.FIREBASE_LOCATION_SALES_BILL_HEADER);
                    mSalesDetailRef = new Firebase(Constant.FIREBASE_URL)
                            .child(bossEncodedEmail)
                            .child(Constant.FIREBASE_LOCATION_SALES_BILL_DETAIL);

                    /* the sales header * sales detail node before push() key */
                    mSalesHeaderNode = "/" + bossEncodedEmail + "/" + Constant.FIREBASE_LOCATION_SALES_BILL_HEADER;
                    mSalesDetailNode = "/" + bossEncodedEmail + "/" + Constant.FIREBASE_LOCATION_SALES_BILL_DETAIL;

                    /* He is a worker so this is the boss encoded email */
                    mUsefulEncodedEmail = bossEncodedEmail;

                } else {

                    /**
                     * Initialize the Firebase reference, to the node of SalesHeader & SalesDetail
                     * under his own node
                     */
                    if (mEncodedEmail != null) {
                        mSalesHeaderRef = new Firebase(Constant.FIREBASE_URL)
                                .child(mEncodedEmail)
                                .child(Constant.FIREBASE_LOCATION_SALES_BILL_HEADER);
                        mSalesDetailRef = new Firebase(Constant.FIREBASE_URL)
                                .child(mEncodedEmail)
                                .child(Constant.FIREBASE_LOCATION_SALES_BILL_DETAIL);

                        /* the sales header * sales detail node before push() key */
                        mSalesHeaderNode = "/" + mEncodedEmail + "/" + Constant.FIREBASE_LOCATION_SALES_BILL_HEADER;
                        mSalesDetailNode = "/" + mEncodedEmail + "/" + Constant.FIREBASE_LOCATION_SALES_BILL_DETAIL;

                        /* He is the owner so this is the login encoded email */
                        mUsefulEncodedEmail = mEncodedEmail;

                    }
                }
        }

        /* Initialize the adapter */
        mAdapter = new Adapter(getActivity(), mSalesHeaderRef, mSalesDetailRef, mUsefulEncodedEmail, mSalesHeaderNode, mSalesDetailNode, booleanStatus);

        /**
         * Here we want to set the number of span for the recycler view layout,
         */
        int columnCount;
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        boolean isLand = getResources().getBoolean(R.bool.isLandscape);

        /* if on tablet and landscape */
        if (isTablet && isLand) {
            columnCount = 3;
        } else if (isTablet) {
            columnCount = 2;
        } else if (isLand){
            columnCount = 2;
        } else {
            columnCount = 1;
        }

        /* Fix the size */
        recyclerView.setHasFixedSize(true);

        /* Initialize the layout manager */
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getActivity(), columnCount, GridLayoutManager.VERTICAL, false);

        /* Set and initialize the layout manager */
        recyclerView.setLayoutManager(gridLayoutManager);

        /* Set the adapter */
        recyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = null;
    }


    /**
     * This class is the adapter to populate header into the recycler view
     */
    public class Adapter extends RecyclerView.Adapter<SalesPendingFragment.Adapter.ViewHolder> {

        /**
         *  The activity context
         */
        private Context mContext;

        /**
         * if approved as worker
         */
        private boolean aBooleanStatus;

        /**
         * A container for the salesHeader values
         */
        private ArrayList<ModelTransactionPointOfSalesHeader> mHeaderValue;

        /**
         * A string continer for the salesHeader push() keys
         */
        private ArrayList<String> mHeaderKeys;

        /**
         * Firebase reference to the salesHeader node
         */
        private Firebase mSalesHeaderRef;
        private String mSalesHeaderNode;

        /**
         * Firebase listener object
         */
        private ChildEventListener mSalesHeaderRefListener;

        /**
         * Firebase reference to the salesDetail node
         */
        private Firebase mSalesDetailRef;
        private String mSalesDetailNode;

        /**
         * This can be the boss encoded email or the login encoded email
         */
        private String mUsefulEncodedEmail;


        /**
         * Constructor
         */
        public Adapter(Context context, Firebase salesHeader, Firebase salesDetail, String usefulEncodedEmail, String headerNode, String detailNode, boolean booleanStatus) {

            /* Initialize the containers */
            mHeaderKeys = new ArrayList<>();
            mHeaderValue = new ArrayList<>();

            // The activity
            this.mContext = context;
            this.mSalesHeaderRef = salesHeader;
            this.mSalesHeaderNode = headerNode;
            this.mSalesDetailRef = salesDetail;
            this.mSalesDetailNode = detailNode;
            this.mUsefulEncodedEmail = usefulEncodedEmail;
            this.aBooleanStatus = booleanStatus;

            /* Get the data feed from Firebase database */
            getDataFromFirebase();

            /* Notify change */
            notifyDataSetChanged();
        }


        @Override
        public int getItemViewType(int position) {
            // Only one layout for all position
            return R.layout.sales_pending_adapter;
        }


        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            /* Initialize the View */
            View rootView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

            /* Return the view holder pass the View object into it */
            return new ViewHolder(rootView);
        }


        @Override
        public void onBindViewHolder(final Adapter.ViewHolder holder, int p) {

            /* Get the element position from the holder */
            int holderPosition = holder.getAdapterPosition();

            /* Time stamp */
            final String timeAndDateStamp = mHeaderValue.get(holderPosition).getTimeStamps();
            holder.timeAndDateStampSales.setText(timeAndDateStamp);

            /* Customer name */
            final String customerName = mHeaderValue.get(holderPosition).getCustomerName();
            holder.customerNameSales.setText(customerName);

            /* Invoice amount */
            final String totalInvoice = mHeaderValue.get(holderPosition).getInvoiceTotalAfterTax();
            holder.invoiceAmountSales.setText(totalInvoice);

            /* Get the push() key for this item in this position */
            String headerPushKey = mHeaderKeys.get(holderPosition);

            /* Add the header push() key to child node for the salesDetail */
            Firebase childNode = mSalesDetailRef.child(headerPushKey);


            /**
             *  Recycler view, pass the salesDetail including the childNode
             */
            NestedImageAdapter nestedAdapter = new NestedImageAdapter(mContext, childNode, mUsefulEncodedEmail);

            /* Initialize the layout manager */
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

            /* Set the size */
            holder.recyclerViewSales.setHasFixedSize(true);

            /* Set the layout manager */
            holder.recyclerViewSales.setLayoutManager(linearLayoutManager);

            /* Set the adapter */
            holder.recyclerViewSales.setAdapter(nestedAdapter);


            /* Add the header push() keys to the node, and pass them to the intent below */
            final String headerWithPushKey = mSalesHeaderNode + "/" + headerPushKey;
            final String detailWithHeaderPushKey = mSalesDetailNode + "/"  + headerPushKey;

            /**
             * The click to pass data to the detail activity
             */
            holder.cardViewSales.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), DetailSalesActivity.class);
                    intent.putExtra(Constant.KEY_USERFUL_ENCODED_EMAIL, mUsefulEncodedEmail);
                    intent.putExtra(Constant.KEY_SALES_HEADER, headerWithPushKey);
                    intent.putExtra(Constant.KEY_SALES_DETAIL, detailWithHeaderPushKey);
                    intent.putExtra(Constant.KEY_BOOLEAN_STATUS, aBooleanStatus);

                    startActivity(intent);

                }
            });


            /**
             * The long click event is to delete salesHeader, salesDetail from the database
             */
            holder.cardViewSales.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    /* Alert dialog for the user to click OK or Cancel */
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(getString(R.string.alert_dialog_title_delete))
                            .setMessage(getString(R.string.alert_dialog_message))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.alert_dialog_positive_button_yes), new DialogInterface.OnClickListener() {

                                /* If the user click yes/oke... */
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    /* Initialize the header push key as string */
                                    String headerPushKey = null;

                                    /* Get the push key for this position from the list */
                                    if (mHeaderKeys.size() > 0) {
                                        headerPushKey = mHeaderKeys.get(holder.getAdapterPosition());
                                    }

                                    /* Delete from salesHeader node */
                                    if (mSalesHeaderRef != null && headerPushKey != null) {
                                        mSalesHeaderRef.child(headerPushKey).setValue(null);
                                    }

                                    /* Delete from salesDetail node */
                                    if (mSalesDetailRef != null && headerPushKey != null) {
                                        mSalesDetailRef.child(headerPushKey).setValue(null);
                                    }

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


            /**
             * If the user click the share button
             */
            holder.imageButtonShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Utility.shareData(getActivity(),
                            new String[]{getString(R.string.share_tile) + timeAndDateStamp, customerName + " " + totalInvoice, "flag"});

                }
            });

        }


        @Override
        public int getItemCount() {

            /* Number of element */
            return mHeaderValue != null ? mHeaderValue.size() : 0;
        }


        /**
         * This helper method will get and listen for the data from Firebase node
         */
        private void getDataFromFirebase() {

            /* Check if it is not null */
            if (mHeaderValue != null) {

                /**
                 * This listener will get data from Firebase, then pass the data to an ArrayList
                 */
                mSalesHeaderRefListener = mSalesHeaderRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        /* Get the model from the dataSnapshot */
                        ModelTransactionPointOfSalesHeader model = dataSnapshot.getValue(ModelTransactionPointOfSalesHeader.class);

                        if (model != null) {

                            /*  Pack the POJO into an ArrayList */
                            mHeaderValue.add(model);

                            /* Pack the keys into an ArrayList */
                            mHeaderKeys.add(dataSnapshot.getKey());

                            /* Notify this adapter for any data change */
                            notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                        /* Initialize the POJO */
                        ModelTransactionPointOfSalesHeader model = dataSnapshot.getValue(ModelTransactionPointOfSalesHeader.class);

                        if (model != null) {

                            /* Get the key for the value that changed */
                            String keyChanged = dataSnapshot.getKey();

                            /* Find the index of that key */
                            int theIndexOfKey = mHeaderKeys.indexOf(keyChanged);

                            /* Revise/Set the value for that index */
                            mHeaderValue.set(theIndexOfKey, model);

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
                            int theIndexOfKey = mHeaderKeys.indexOf(keyRemoved);

                            /* Remove the value from the list */
                            mHeaderValue.remove(theIndexOfKey);

                            /* Remove the key from the list */
                            mHeaderKeys.remove(theIndexOfKey);

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
        public void removeListeners() {
            if (mSalesHeaderRef != null && mSalesHeaderRefListener != null) {
                mSalesHeaderRef.removeEventListener(mSalesHeaderRefListener);
            }
        }


        /**
         * View holder class to support
         * {@link com.stockita.newpointofsales.salesPack.fragment.SalesPendingFragment.Adapter}
         */
        public class ViewHolder extends RecyclerView.ViewHolder {


            /* Views */
            @Bind(R.id.transaction_sales_pending_payment_card_view)
            CardView cardViewSales;

            @Bind(R.id.transaction_sales_pending_payment_time_stamp)
            TextView timeAndDateStampSales;

            @Bind(R.id.transaction_sales_pending_payment_customer)
            TextView customerNameSales;

            @Bind(R.id.transaction_sales_pending_payment_amount)
            TextView invoiceAmountSales;

            @Bind(R.id.transaction_sales_pending_payment_adapter_image)
            RecyclerView recyclerViewSales;

            @Bind(R.id.image_button_share)
            ImageButton imageButtonShare;


            /**
             * Constructor
             */
            public ViewHolder(View itemView) {
                super(itemView);

                /* Initialize the butterKnife */
                ButterKnife.bind(this, itemView);
            }
        }
    }


    /**
     * This class is a nest adapter, so it populate the detail sales into the header
     * sales recycler view, including the images of each item
     */
    public class NestedImageAdapter extends RecyclerView.Adapter<SalesPendingFragment.NestedImageAdapter.ViewHolder> {

        /**
         * The activity context
         */
        private Context mContext;

        /**
         * The array list of type SalesDetail, this is the value of data set
         */
        private ArrayList<ModelTransactionPointOfSalesDetail> mDetailValues;

        /**
         * This is the push() key of each element in the mDetailValues
         */
        private ArrayList<String> mDetailKeys;

        /**
         * This can be a boss email or login email depan on the ownership setting
         */
        private String mUsefulEncodedEmail;

        /**
         * The Firebase reference to the salesDetail node
         */
        private Firebase mSalesDetailRef;


        /**
         * Constructor
         */
        public NestedImageAdapter(Context context, Firebase salesDetailRef, String usefulEncodedEmail) {

            this.mContext = context;
            this.mSalesDetailRef = salesDetailRef;
            this.mUsefulEncodedEmail = usefulEncodedEmail;

            /**
             * Initialize the array here, for each element of the sales header must pack the sales detail
             * in new arrayList
             */
            this.mDetailValues = new ArrayList<>();
            this.mDetailKeys = new ArrayList<>();

            /**
             * Here is where we get data from Firebase for this sales detail
             */
            getDataFromFirebase();

        }

        @Override
        public int getItemViewType(int position) {

            /* The layout xml */
            return R.layout.sales_image_nested_adapter;
        }

        @Override
        public NestedImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            /* Initialize the view */
            ViewGroup mainView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

            /* Return the ViewHolder pass the view group object as argument */
            return new NestedImageAdapter.ViewHolder(mainView);
        }

        @Override
        public void onBindViewHolder(final NestedImageAdapter.ViewHolder holder, int position) {

            /* Get the item master push key from the data set */
            String itemMasterPushKey = mDetailValues.get(position).getItemMasterPushId();

            /* Firebase ref to the itemImage Node for this item */
            Firebase itemImage = new Firebase(Constant.FIREBASE_URL)
                    .child(mUsefulEncodedEmail)
                    .child(Constant.FIREBASE_LOCATION_ITEM_IMAGE)
                    .child(itemMasterPushKey);


            /* Now we can get the first image in itemImage then update the UI */
            itemImage.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    /* The container to pack the images */
                    ArrayList<ModelItemImageMaster> images = new ArrayList<>();

                    /* Iterate  */
                    for (DataSnapshot snap: dataSnapshot.getChildren()) {

                        /* Initialize the image reference to snapshot */
                        ModelItemImageMaster model = snap.getValue(ModelItemImageMaster.class);

                        /* Add into a container */
                        images.add(model);
                    }

                    /* Get the image URL then update the UI */
                    if (images.size() > 0) {
                        holder.setImageView(mContext, Constant.CLOUDINARY_BASE_URL_DOWNLOAD + images.get(0).getImageName());
                    }

                    /* Get the quantity then update the UI */
                    String quantity = mDetailValues.get(holder.getAdapterPosition()).getQuantity();
                    holder.quantity.setText(quantity);


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                    if (firebaseError != null) {
                        Log.e(LOG_TAG, firebaseError.toString());
                    }
                }
            });

        }


        @Override
        public int getItemCount() {
            return mDetailValues != null ? mDetailValues.size() : 0;
        }


        /**
         * This helper method will get and listen for the data from Firebase node
         */
        private void getDataFromFirebase() {

        /* Check if it is not null */
            if (mDetailValues != null) {

                mSalesDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        /* Iterate  */
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {

                            /* Pass the snapShop into a model */
                            ModelTransactionPointOfSalesDetail model = snap.getValue(ModelTransactionPointOfSalesDetail.class);

                            if (model != null) {
                                /* Pack the model into a list */
                                mDetailValues.add(model);

                                /* Get the push key then pack them into a list */
                                String pushKey = snap.getKey();
                                mDetailKeys.add(pushKey);

                                /* Since this is only a single event, must invoke this method down here */
                                notifyDataSetChanged();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                        if (firebaseError != null) {
                            Log.e(LOG_TAG, firebaseError.toString());
                        }
                    }
                });


            }
        }


        /**
         * This view holder class is to support the {@link NestedImageAdapter}
         */
        public class ViewHolder extends RecyclerView.ViewHolder {

            /* Views */
            @Bind(R.id.transaction_adapter_display_item_image_viewCard)
            CardView viewCard;

            @Bind(R.id.transaction_adapter_display_item_image)
            ImageView imageView;

            @Bind(R.id.transaction_adapter_display_item_image_quantity)
            TextView quantity;

            /**
             * Constructor
             */
            public ViewHolder(View itemView) {
                super(itemView);

                /* Initialize the butterKnife */
                ButterKnife.bind(this, itemView);

            }


            /**
             * Set the Item Image url
             */
            public void setImageView(Context context, String url) {
                if (imageView != null) {
                    Glide.with(context).load(url).into(imageView);
                    imageView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }
    }


}
