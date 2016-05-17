package com.stockita.newpointofsales.purchasePack.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.barcodeScanner.BarcodeScanner;
import com.stockita.newpointofsales.data.ModelItemImageMaster;
import com.stockita.newpointofsales.data.ModelItemMaster;
import com.stockita.newpointofsales.data.ModelTransactionPointOfSalesDetail;
import com.stockita.newpointofsales.data.ModelTransactionPurchaseDetail;
import com.stockita.newpointofsales.data.ModelTransactionPurchaseHeader;
import com.stockita.newpointofsales.itemMasterPack.newMasterItemPack.ItemMasterDialogFragment;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This is the detail purchase fragment, the user can delete or add items from the existing
 * purchase transaction in the list.
 */
public class DetailPurchaseFragment extends Fragment {

    // Constant
    private static final String KEY_ONE = "KEYONE";
    private final static String KEY_INVOICE_TOTAL_BEFORE_DISC_TAX = "invoiceTotalBeforeDiscAndTax";
    public static final int GET_FROM_BARCODE_SCANNER = 1;
    public static final int GET_FROM_DIALOG_FRAGMENT = 2;
    private static final String KEY_ARTICLE_SUBTITLE = "KEY_ARTICLE_SUBTITLE";
    private static final String KEY_VENDOR_NAME = "KEY_VENDOR_NAME";
    private static final String KEY_CHECKOUT_GRAND_TOTAL = "KEY_CHECKOUT_GRAND_TOTAL";
    private static final String KEY_CHECKOUT_DISCOUNT_RATE = "KEY_CHECKOUT_DISCOUNT";
    private static final String KEY_CHECKOUT_DISCOUNT_VALUE = "KEY_CHECKOUT_DISCOUNT_VALUE";
    private static final String KEY_CHECKOUT_TAX = "KEY_CHECKOUT_TAX";
    private static final String KEY_CHECKOUT_TAX_VALUE = "KEY_CHECKOUT_TAX_VALUE";
    private static final String KEY_ITEM_DESCRIPTION = "KEY_ITEM_DESCRIPTION";
    private static final String KEY_UNIT_OF_MEASURE = "KEY_UNIT_OF_MEASURE";
    private static final String KEY_CURRENCY = "KEY_CURRENCY";
    private static final String KEY_PRICE = "KEY_PRICE";
    private static final String KEY_QUANTITY = "KEY_QUANTITY";
    private static final String KEY_DISCOUNT_ITEM = "KEY_DISCOUNT_ITEM";
    private static final String KEY_CATEGORY = "KEY_CATEGORY";


    // Views
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.article_title)
    TextView articleTitle;

    @Bind(R.id.article_subtitle)
    TextView articleSubtitle;

    @Bind(R.id.transaction_fragment_recycler_view_add_purchase)
    RecyclerView detailPurchaseTransaction;

    @Bind(R.id.imageButton)
    ImageButton queryImageButton;

    @Bind(R.id.imageButton2)
    ImageButton barcodeScanner;

    @Bind(R.id.textView_transaction_fragment_add_purchase_vendor_name)
    EditText vendorName;

    @Bind(R.id.textView_transaction_fragment_add_purchase_item_number)
    EditText itemNumber;

    @Bind(R.id.textView_transaction_fragment_add_purchase_item_description)
    TextView itemDescription;

    @Bind(R.id.textView_transaction_fragment_add_purchase_unit_of_measure)
    TextView unitOfMeasure;

    @Bind(R.id.textView_transaction_fragment_add_purchase_currency)
    TextView currency;

    @Bind(R.id.textView_transaction_fragment_add_purchase_price)
    TextView price;

    @Bind(R.id.textView_transaction_fragment_add_purchase_discount)
    EditText discount;

    @Bind(R.id.textView_transaction_fragment_add_purchase_quantity)
    EditText quantity;

    @Bind(R.id.checkout_discount)
    TextView checkoutDiscount;

    @Bind(R.id.checkout_tax)
    TextView checkoutTax;

    @Bind(R.id.checkout_tax_value)
    TextView checkoutTaxValue;

    @Bind(R.id.checkout_total_after_tax)
    TextView checkoutGrandTotal;


    // Variables for model
    private String mArticleTitle;
    private String mArticleSubTitle;
    private String mVendorName;
    private String mItemDescription;
    private String mUnitOfMeasure;
    private String mCategory;
    private String mCurrency;
    private String mPrice;
    private String mQuantity;
    private String mCheckoutGrandTotal;
    private String mCheckoutDiscountRate;
    private String mCheckoutDiscountValue;
    private String mCheckoutTax;
    private String mCheckoutTaxValue;
    private String mDiscountRate;



    /**
     * This is the login email, encoded.
     */
    private String mEncodedEmail;

    /**
     * This could be the boss email or the login email, encoded
     */
    private String mUsefulEncodedEmail;

    /**
     * This is related to the authorized if he is a worker or boss
     */
    private boolean mBooleanStatus;

    /**
    * This is the string node to the specific purchase header
    * selected by the user from the list
    */
    private String mPurchaseHeaderNodeWithPushKey;

    /**
     * This is the string node to the specific purchase detail
     * that is attached to the sales header
     */
    private String mPurchaseDetailNodeWithHeaderPushKey;

    /**
     * Model to restore data from Firebase database
     */
    private ModelTransactionPurchaseHeader mModelPurchaseHeader;

    /**
     * Container to pack detail data from Fireabse database
     */
    private ArrayList<ModelTransactionPurchaseDetail> mListPurchaseDetail;

    /**
     * Firebase reference to the purchaseHeader + pushKey()
     */
    private Firebase mPurchaseHeaderWithPushKeyRef;

    /**
     * Firebase reference to the purchaseDetail + header pushKey()
     */
    private Firebase mPurchaseDetailWithHeaderPushKeyRef;

    /**
     * The Firebase reference to the item master, using mEncodedEmail node or mBossEncodedEmail node.
     */
    private Firebase mFirebaseItemMasterRef;

    /**
     * The adapter to populate data into list
     */
    private DetailPurchaseFragment.Adapter mAdapter;

    /**
     * The sum amount that will be display in the UI
     */
    private ArrayList<String> mAccumulateTheTotalAmount;

    /**
     * ItemMaster push() key
     */
    private String mPushIDofTheItemMaster;


    /**
     * Empty constructor
     */
    public DetailPurchaseFragment() {}


    /**
     * Basic bundle to get data from the activity
     */
    public static DetailPurchaseFragment newInstance(String encodedEmail, String usefulEmail, String salesHeaderNode, String salesDetailNode, boolean booleanStatus) {

        /* Instantiate the fragment */
        DetailPurchaseFragment fragment = new DetailPurchaseFragment();

        /* Instantiate the Bundle */
        Bundle args = new Bundle();

        /**
         * Pass the data
         */
        args.putString(Constant.KEY_ENCODED_EMAIL, encodedEmail);
        args.putString(Constant.KEY_USERFUL_ENCODED_EMAIL, usefulEmail);
        args.putString(Constant.KEY_PURCHASE_HEADER, salesHeaderNode);
        args.putString(Constant.KEY_PURCHASE_DETAIL, salesDetailNode);
        args.putBoolean(Constant.KEY_BOOLEAN_STATUS, booleanStatus);

        /* Set the Bundle into the fragment using method setArgument() */
        fragment.setArguments(args);

        /* Return the fragment instance */
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Set the tool bar menu to true. */
        setHasOptionsMenu(true);

        /* Initialize the adapter */
        mAdapter = new Adapter(getActivity());

        /* Restore the data from the bundle */
        mEncodedEmail = getArguments().getString(Constant.KEY_ENCODED_EMAIL);
        mUsefulEncodedEmail = getArguments().getString(Constant.KEY_USERFUL_ENCODED_EMAIL);
        mPurchaseHeaderNodeWithPushKey = getArguments().getString(Constant.KEY_PURCHASE_HEADER);
        mPurchaseDetailNodeWithHeaderPushKey = getArguments().getString(Constant.KEY_PURCHASE_DETAIL);



        /* Restore state on screen rotation */
        if (savedInstanceState != null) {
            mModelPurchaseHeader = savedInstanceState.getParcelable(Constant.KEY_PURCHASE_PASS_HEADER);
            mListPurchaseDetail = savedInstanceState.getParcelableArrayList(Constant.KEY_PURCHASE_PASS_DETAIL);
            mAccumulateTheTotalAmount = savedInstanceState.getStringArrayList(KEY_ONE);
            mArticleTitle = savedInstanceState.getString(KEY_INVOICE_TOTAL_BEFORE_DISC_TAX);
            mArticleSubTitle = savedInstanceState.getString(KEY_ARTICLE_SUBTITLE);
            mVendorName = savedInstanceState.getString(KEY_VENDOR_NAME);
            mCheckoutGrandTotal = savedInstanceState.getString(KEY_CHECKOUT_GRAND_TOTAL);
            mCheckoutDiscountRate = savedInstanceState.getString(KEY_CHECKOUT_DISCOUNT_RATE);
            mCheckoutDiscountValue = savedInstanceState.getString(KEY_CHECKOUT_DISCOUNT_VALUE);
            mCheckoutTax = savedInstanceState.getString(KEY_CHECKOUT_TAX);
            mCheckoutTaxValue = savedInstanceState.getString(KEY_CHECKOUT_TAX_VALUE);
            mItemDescription = savedInstanceState.getString(KEY_ITEM_DESCRIPTION);
            mUnitOfMeasure = savedInstanceState.getString(KEY_UNIT_OF_MEASURE);
            mCurrency = savedInstanceState.getString(KEY_CURRENCY);
            mPrice = savedInstanceState.getString(KEY_PRICE);
            mQuantity = savedInstanceState.getString(KEY_QUANTITY);
            mDiscountRate = savedInstanceState.getString(KEY_DISCOUNT_ITEM);
            mCategory = savedInstanceState.getString(KEY_CATEGORY);

            /* Update the adapter */
            mAdapter.swapData(mListPurchaseDetail, mUsefulEncodedEmail);

        }

        /* Check if null */
        if (savedInstanceState == null) {

            /**
             * We want to get these data from the Firebase database
             * We are interested in ModelPurchaseHeader, and in a list of ModelSalesDetail
             */
            mPurchaseHeaderWithPushKeyRef = new Firebase(Constant.FIREBASE_URL).child(mPurchaseHeaderNodeWithPushKey);
            mPurchaseDetailWithHeaderPushKeyRef = new Firebase(Constant.FIREBASE_URL).child(mPurchaseDetailNodeWithHeaderPushKey);


            /**
             * Now lets capture header from firebase
             */
            mPurchaseHeaderWithPushKeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mModelPurchaseHeader = dataSnapshot.getValue(ModelTransactionPurchaseHeader.class);

                    if (mModelPurchaseHeader != null) {

                        /* Update the UI & state, convert from millis to date format */
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                        long longTimeStamp = Long.parseLong(mModelPurchaseHeader.getTimeStamps());
                        String date = simpleDateFormat.format(longTimeStamp);
                        mArticleSubTitle = date;
                        articleSubtitle.setText(mArticleSubTitle);


                        /* update UI, customer name & the state */
                        mVendorName = mModelPurchaseHeader.getVendorName();
                        vendorName.setText(mVendorName);

                        /* update UI, total invoice after tax and the state */
                        mCheckoutGrandTotal = mModelPurchaseHeader.getInvoiceTotalAfterTax();
                        collapsingToolbarLayout.setTitle(mCheckoutGrandTotal);
                        checkoutGrandTotal.setText(mCheckoutGrandTotal);


                        /* Update UI & state, invoice discount */
                        mCheckoutDiscountRate = mModelPurchaseHeader.getInvoiceDiscount();
                        mCheckoutDiscountValue = mModelPurchaseHeader.getDiscountValue();
                        checkoutDiscount.setText(mCheckoutDiscountRate);

                        /* Update UI & state, tax rate */
                        mCheckoutTax = mModelPurchaseHeader.getInvoiceTax();
                        checkoutTax.setText(mCheckoutTax);

                        /* Update UI & state, tax value */
                        mCheckoutTaxValue = mModelPurchaseHeader.getTaxValue();
                        checkoutTaxValue.setText(mCheckoutTaxValue);
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            /**
             * Now lets capture detail from firebase
             */
            mPurchaseDetailWithHeaderPushKeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    /* A container to pack the model  */
                    mListPurchaseDetail = new ArrayList<>();

                    /* A container to pack the model.subTotalPerItem */
                    mAccumulateTheTotalAmount = new ArrayList<>();

                    /* Iterate */
                    for (DataSnapshot detail : dataSnapshot.getChildren()) {

                        /* Initialize the model */
                        ModelTransactionPurchaseDetail model = detail.getValue(ModelTransactionPurchaseDetail.class);

                        /* Pack the data into the container */
                        mListPurchaseDetail.add(model);
                        mAccumulateTheTotalAmount.add(model.getSubTotalPerItem());

                    }

                    if (mListPurchaseDetail.size() > 0) {

                        /**
                         * Pass the data to the adapter
                         */
                        mAdapter.swapData(mListPurchaseDetail, mUsefulEncodedEmail);

                        /* Calculate the sub total */
                        double calculate = recalculateTheItemTotal(mAccumulateTheTotalAmount);

                        /* Update temporary state */
                        mArticleTitle = String.valueOf(calculate);

                        /* update the UI */
                        articleTitle.setText(mArticleTitle);

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }

        /* initialize the Firebase ref to itemMaster */
        mFirebaseItemMasterRef = new Firebase(Constant.FIREBASE_URL).child(mUsefulEncodedEmail).child(Constant.FIREBASE_LOCATION_ITEM_MASTER);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(Constant.KEY_PURCHASE_PASS_HEADER, mModelPurchaseHeader);
        outState.putParcelableArrayList(Constant.KEY_PURCHASE_PASS_DETAIL, mListPurchaseDetail);
        outState.putStringArrayList(KEY_ONE, mAccumulateTheTotalAmount);
        outState.putString(KEY_INVOICE_TOTAL_BEFORE_DISC_TAX, mArticleTitle);
        outState.putString(KEY_ARTICLE_SUBTITLE, mArticleSubTitle);
        outState.putString(KEY_VENDOR_NAME, mVendorName);
        outState.putString(KEY_CHECKOUT_GRAND_TOTAL, mCheckoutGrandTotal);
        outState.putString(KEY_CHECKOUT_DISCOUNT_RATE, mCheckoutDiscountRate);
        outState.putString(KEY_CHECKOUT_DISCOUNT_VALUE, mCheckoutDiscountValue);
        outState.putString(KEY_CHECKOUT_TAX, mCheckoutTax);
        outState.putString(KEY_CHECKOUT_TAX_VALUE, mCheckoutTaxValue);
        outState.putString(KEY_ITEM_DESCRIPTION, mItemDescription);
        outState.putString(KEY_UNIT_OF_MEASURE, mUnitOfMeasure);
        outState.putString(KEY_CURRENCY, mCurrency);
        outState.putString(KEY_PRICE, mPrice);
        outState.putString(KEY_QUANTITY, mQuantity);
        outState.putString(KEY_DISCOUNT_ITEM, mDiscountRate);
        outState.putString(KEY_CATEGORY, mCategory);
    }


    @Override
    public void onResume() {
        super.onResume();

        /* check if not null then update the UI */
        if (mModelPurchaseHeader != null) {

            /* update UI*/
            articleTitle.setText(mArticleTitle);
            articleSubtitle.setText(mArticleSubTitle);
            vendorName.setText(mVendorName);
            collapsingToolbarLayout.setTitle(mCheckoutGrandTotal);
            checkoutGrandTotal.setText(mCheckoutGrandTotal);
            checkoutDiscount.setText(mCheckoutDiscountRate);
            checkoutTax.setText(mCheckoutTax);
            checkoutTaxValue.setText(mCheckoutTaxValue);

        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_transaction, menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /* Proceed if there is a transaction */
        if (mListPurchaseDetail.size() > 0 && vendorName.length() > 0) {

            /* Get the menu item IDs */
            switch (item.getItemId()) {

                /* checkout page */
                case R.id.checkout_menu:

                    /* Instantiate the model to pack the header. */
                    ModelTransactionPurchaseHeader header = new ModelTransactionPurchaseHeader();

                    /* Date in Millis new version*/
                    header.setTimeStamps(mModelPurchaseHeader.getTimeStamps());

                    /* Vendor name */
                    header.setVendorName(mVendorName);

                    /* Invoice amount before discount and tax */
                    header.setSubTotalBeforeTax(mArticleTitle);

                    /* Number of items */
                    int numberOfItems = mListPurchaseDetail.size();
                    header.setNumberOfItems(String.valueOf(numberOfItems));

                    /**
                     * Saving
                     */
                    clickPendingPayment(header, mListPurchaseDetail);

                    /* Set to null, this is important, especially when the user hit back button, to add or delete something. */
                    clearUpTheUI();

                    /* clear up everything */
                    clearUpOnExit();

                    /* Exit this */
                    getActivity().finish();

                    break;
            }
        }


        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Initialize the view */
        View rootView =
                inflater.inflate(R.layout.fragment_detail_purchase_form, container, false);

        /* Initialize the ButterKnife */
        ButterKnife.bind(this, rootView);

                /* Toolbar */
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        /* Toolbar navigation area back button arrow */
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        /**
         * Dialog fragment to select itemMaster from
         */
        final FragmentManager fm = getActivity().getFragmentManager();
        final ItemMasterDialogFragment itemMasterDialogFragment = ItemMasterDialogFragment.newInstance(mEncodedEmail);
        itemMasterDialogFragment.setTargetFragment(this, GET_FROM_DIALOG_FRAGMENT);

        /* Query button, so the user can chose an item from the dialog */
        queryImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Clear the UI */
                clearUpTheUI();

                /* Invoke the show() method to show the dialog fragment */
                itemMasterDialogFragment.show(fm, "fragment_dialog_item_master");


            }
        });

        /* If the user type manually the item number, instated of using the lookup/barcode reader. */
        itemNumber.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE && vendorName.length() > 0) {

                    /* Hide keyboard */
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    /* Query the item Master database then update the UI */
                    queryTheItemMasterAndUpdateTheUI();
                    return true;
                }
                return false;
            }
        });


        /* If the user want to use the Barcode scanner */
        barcodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vendorName.length() > 0) {

                    /* Clear the UI */
                    clearUpTheUI();

                    /* Get the barcode reader */
                    Intent startScanner = new Intent(getActivity(), BarcodeScanner.class);
                    startActivityForResult(startScanner, GET_FROM_BARCODE_SCANNER);
                }

            }
        });

        /**
         *  Recycler view for sales detail item, below here...
         */

        /* Initialize the layout manager */
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        /* Set the layout manager */
        detailPurchaseTransaction.setLayoutManager(linearLayoutManager);

        /* Set the adapter */
        detailPurchaseTransaction.setAdapter(mAdapter);

        /* Set the long click listener to delete detail item */
        mAdapter.setOnLongClickListener(onLongClickListener);

        return rootView;

    }


    /**
     * FAB, will add the item to the sales detail, which is in the UI
     * it is a recycler view, and the adapter is mAddPointOfSalesAdapter
     */
    @OnClick(R.id.fab_transaction_fragment_add_purchase)
    void fab() {

        if (mModelPurchaseHeader != null) {

            if (itemDescription.length() > 0 && vendorName.length() > 0) {

                /* Hide the keyboard */
                Utility.hideKeyboard(getActivity());

                long timestamp = Long.parseLong(mModelPurchaseHeader.getTimeStamps());

                /* Pack and pass the data to the adapter */
                passDataToTheRecyclerViewInvoiceList(timestamp);

                /* Clear the UI */
                clearUpTheUI();

            }
        }

    }


    /**
     * The long click call back listener to remove item from the list
     */
    public Adapter.OnLongClickListener onLongClickListener = new Adapter.OnLongClickListener() {
        @Override
        public void onLongClickListener(View view, int position) {

            /* Remove data set at this position */
            mAccumulateTheTotalAmount.remove(position);
            mListPurchaseDetail.remove(position);

            /* Send the data to the adapter */
            mAdapter.swapData(mListPurchaseDetail, mUsefulEncodedEmail);

            /* Recalculate the subTotal */
            double subTotal = recalculateTheItemTotal(mAccumulateTheTotalAmount);

            /* Calculate the invoice amount and update the UI inside this method */
            recalculateTheTotalAmount(subTotal);

            /* Update the UI & the state*/
            mArticleTitle = String.valueOf(subTotal);
            articleTitle.setText(mArticleTitle);

        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            /* Get the item number from the Barcode scanner */
            case GET_FROM_BARCODE_SCANNER:

                if (resultCode == Activity.RESULT_OK) {

                    /* Get the barcode */
                    String lItemNumber = data.getStringExtra(BarcodeScanner.SCAN_CONTENTS);
                    itemNumber.setText(lItemNumber);

                    /* Query Firebase then update the UI */
                    queryTheItemMasterAndUpdateTheUI();


                } else {
                    /* If system error */
                    Toast.makeText(getActivity(), "User hit back button", Toast.LENGTH_SHORT).show();

                }
                break;


            /* Get item master number back from the dialog fragment */
            case GET_FROM_DIALOG_FRAGMENT:

                /* Get the item number from the intent */
                String lDataFromDialog = data.getStringExtra(Constant.KEY_DIALOG_FRAGMENT_ITEM_NUMBER);

                /* Set the item number to the UI */
                itemNumber.setText(lDataFromDialog);

                /* Query the item using the item number then update the UI */
                queryTheItemMasterAndUpdateTheUI();
                break;
        }
    }


    /**
     * Helper method to query the item master database then update the UI
     */
    private void queryTheItemMasterAndUpdateTheUI() {

        if (itemNumber.length() > 0) {

            /* Get the item number from the UI */
            String lItemNumber = itemNumber.getText().toString();

            /* Query for the itemNumber */
            Query queryItemNumber =
                    mFirebaseItemMasterRef.orderByChild(Constant.FIREBASE_PROPERTY_ITEM_NUMBER).equalTo(lItemNumber);
            queryItemNumber.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    /* Get the number of the child */
                    long listSize = dataSnapshot.getChildrenCount();

                    /* Initialize the ArrayList of type ModelItemMaster */
                    ArrayList<ModelItemMaster> modelList = new ArrayList<>((int) listSize);

                    /* Iterate through each child then get the value model */
                    for (DataSnapshot element : dataSnapshot.getChildren()) {

                        /* Get the push() key for this itemMaster, for later use */
                        mPushIDofTheItemMaster = element.getKey();

                        /* Assign the value into the model */
                        ModelItemMaster model = element.getValue(ModelItemMaster.class);

                        if (model != null) {
                            /* Pack the model into a list */
                            modelList.add(model);
                        }

                    }

                    /* Check if the list is not empty */
                    if (modelList.size() > 0) {


                        /* Update the UI and the state */
                        mItemDescription = modelList.get(0).getItemDescription();
                        itemDescription.setText(mItemDescription);

                        mUnitOfMeasure = modelList.get(0).getUnitOfMeasure();
                        unitOfMeasure.setText(mUnitOfMeasure);

                        mCurrency = modelList.get(0).getCurrency();
                        currency.setText(mCurrency);

                        mPrice = String.valueOf(modelList.get(0).getPrice());
                        price.setText(mPrice);

                        mCategory = String.valueOf(modelList.get(0).getCategory());

                    } else {

                        /* Inform the user */
                        Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();

                        /* Cleat the UI if no data */
                        clearUpTheUI();
                    }


                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }
    }


    /**
     * Calculate the subTotalPerItem
     */
    private double recalculateTheItemTotal(ArrayList<String> data) {

        int sizeOfTheData = data.size();
        double calc = 0;
        for (int i = 0; i < sizeOfTheData; i++) {
            double dataInDouble = Double.parseDouble(data.get(i));
            calc += dataInDouble;
        }
        return calc;
    }


    /**
     * Helper method to recalculate the totals
     */
    private void recalculateTheTotalAmount(double totalBeforeDiscountAndTax) {

        if (mModelPurchaseHeader != null) {

            // Display the discount
            mCheckoutDiscountRate = Utility.getAnyString(getActivity(), "discount_rate", "0.0");
            checkoutDiscount.setText(mCheckoutDiscountRate);


            // Calculate the discount
            double discount;
            double discountValue = 0;
            double totalAfterDiscount;
            try {
                discount = Double.parseDouble(mCheckoutDiscountRate);
                discountValue = (totalBeforeDiscountAndTax * discount) / 100;
                totalAfterDiscount = totalBeforeDiscountAndTax - discountValue;
            } catch (Exception e) {
                totalAfterDiscount = 0;
            }

            // Calculate the tax
            double tax;
            double taxValue = 0;
            double totalAfterTax = 0;

            try {
                tax = Double.parseDouble(mCheckoutTax);
                taxValue = (totalAfterDiscount * tax) / 100;
                totalAfterTax = totalAfterDiscount + taxValue;

            } catch (Exception ee) {
                taxValue = 0;
                totalAfterTax = 0;
            }

            // Update the UI and state
            mCheckoutDiscountValue = String.valueOf(discountValue);
            mCheckoutTaxValue = String.valueOf(taxValue);
            checkoutTaxValue.setText(mCheckoutTaxValue);

            mCheckoutGrandTotal = String.valueOf(totalAfterTax);
            checkoutGrandTotal.setText(mCheckoutGrandTotal);
            collapsingToolbarLayout.setTitle(mCheckoutGrandTotal);

        }
    }


    /**
     * Helper method to clear the UI
     */
    private void clearUpTheUI() {

        itemNumber.setText("");

        mItemDescription = null;
        itemDescription.setText("");

        mUnitOfMeasure = null;
        unitOfMeasure.setText("");

        mCurrency = null;
        currency.setText("");

        mPrice = null;
        price.setText("");

        mQuantity = null;
        quantity.setText("");

        mDiscountRate = null;
        discount.setText("");

    }


    /**
     * Helper method to clear the header and detail related UI
     */
    private void clearUpOnExit() {

        /* Clear up the UI */
        clearUpTheUI();

        /* Clear up the totals */
        mCheckoutDiscountRate = null;
        mCheckoutDiscountValue = null;
        checkoutDiscount.setText("");

        mCheckoutTax = null;
        checkoutTax.setText("");

        mCheckoutTaxValue = null;
        checkoutTaxValue.setText("");

        mCheckoutGrandTotal = null;
        checkoutGrandTotal.setText("");
        collapsingToolbarLayout.setTitle("");

        mArticleTitle = null;
        mArticleSubTitle = null;

        articleSubtitle.setText("");
        articleTitle.setText("");

        mVendorName = null;
        vendorName.setText("");

        /* Clear up the adapter */
        mAdapter.swapData(null, null);

        /* Clear up the accumulate */
        mAccumulateTheTotalAmount = null;

    }


    /**
     * Helper method to pack and pass data to the adapter,
     * also calculate the discount and the amount
     */
    private void passDataToTheRecyclerViewInvoiceList(long dateMillis) {

        /* Initialize the model for sales detail then pack the data; */
        ModelTransactionPurchaseDetail detail =
                new ModelTransactionPurchaseDetail();

        /* date */
        detail.setDate(String.valueOf(dateMillis));

        /* date in millis */
        detail.setTimeStamps(String.valueOf(dateMillis));

        /* item number,  we can get them directly from the UI, no worry */
        detail.setItemNumber(itemNumber.getText().toString());

        /* item description */
        detail.setItemDescription(mItemDescription);

        /* unit of measure */
        detail.setUnitOfMeasure(mUnitOfMeasure);

        /* category */
        detail.setCategory(mCategory);

        /* currency */
        detail.setCurrency(mCurrency);

        /* item Master push id */
        detail.setItemMasterPushId(mPushIDofTheItemMaster);

        /**
         * Calculate the amount and discount...
         */

        /* If there is a price else put 0 */
        double calcPrice;
        if (price.length() > 0) {
            calcPrice = Double.parseDouble(mPrice);
        } else {
            calcPrice = 0;
        }

        /* If there is a quantity else put 1 */
        double calcQuantity;
        if (quantity.length() > 0) {
            calcQuantity = Double.parseDouble(quantity.getText().toString());
        } else {
            calcQuantity = 1;
        }
        mQuantity = String.valueOf(calcQuantity);


        /* If there is a discount else put 0 */
        double calcDiscount;
        if (discount.length() > 0) {
            calcDiscount = Double.parseDouble(discount.getText().toString());
        } else {
            calcDiscount = 0;
        }
        mDiscountRate = String.valueOf(calcDiscount);


        /* Calculate the discount value */
        double discountValue = (calcPrice * calcQuantity) * calcDiscount / 100;

        /* Calculate the total amount per item after discount */
        double calcAmount = (calcPrice * calcQuantity) - discountValue;
        String subTotalPerItem = String.valueOf(calcAmount);

        /**
         * End of calculate the amount and discount
         */

        /* price */
        detail.setPrice(mPrice);

        /* quantity from the UI */
        detail.setQuantity(mQuantity);

        /* discount from the UI */
        detail.setDiscount(mDiscountRate);

        /* Amount per item after discount */
        detail.setSubTotalPerItem(subTotalPerItem);

        /* add data, new data always at index 0. */
        mAccumulateTheTotalAmount.add(0, detail.getSubTotalPerItem());
        mListPurchaseDetail.add(0, detail);

        /* pass data to adapter */
        mAdapter.swapData(mListPurchaseDetail, mUsefulEncodedEmail);

        /* Calculate the accumulated invoice amount */
        int tempSize = mAccumulateTheTotalAmount.size();
        double sum = 0;
        for (int i = 0; i < tempSize; i++) {
            sum += Double.parseDouble(mAccumulateTheTotalAmount.get(i));
        }
        mArticleTitle = String.valueOf(sum);

        /* Display the accumulated invoice amount in to the UI */
        articleTitle.setText(mArticleTitle);

        /* Recalculate the checkout amount */
        recalculateTheTotalAmount(sum);


    }


    /**
     * Helper method when the user click pending payment from the menu
     */
    private void clickPendingPayment(ModelTransactionPurchaseHeader header,ArrayList<ModelTransactionPurchaseDetail> details) {

        // Mark the row as pending, get the
        String typeOfPayment = "pending";

        /* Save the header and return the push() key */
        saveToPurchaseHeader(typeOfPayment, header);

        /* Save the detail */
        saveToPurchaseDetail(header, details);
    }


    /**
     * Helper method to save purchaseHeader into Firebase database
     */
    private void saveToPurchaseHeader(String typeOfPayment, ModelTransactionPurchaseHeader header) {

        /* Get the totals add them to the model before setValue to Firebase */
        header.setTypeOfPayment(typeOfPayment);
        header.setInvoiceDiscount(mCheckoutDiscountRate);
        header.setDiscountValue(mCheckoutDiscountValue);
        header.setInvoiceTax(mCheckoutTax);
        header.setTaxValue(mCheckoutTaxValue);
        header.setInvoiceTotalAfterTax(mCheckoutGrandTotal);

        /* Initialize the Firebase salesHeader node */
        Firebase firebasePurchaseHeader = new Firebase(Constant.FIREBASE_URL).child(mPurchaseHeaderNodeWithPushKey);

        /* Save them into Firebase database */
        firebasePurchaseHeader.setValue(header);
    }

    /**
     * Helper method to save purchaseDetail into Firebase database
     */
    private void saveToPurchaseDetail(ModelTransactionPurchaseHeader header, ArrayList<ModelTransactionPurchaseDetail> detailList) {

        /* Initialize the Firebase purchaseDetail node */
        Firebase firebasePurchaseDetail =
                new Firebase(Constant.FIREBASE_URL).child(mPurchaseDetailNodeWithHeaderPushKey);

        /* Remove all value */
        firebasePurchaseDetail.setValue(null);

        /* Get the size of the arrayList */
        int sizeOfTheDetail = detailList.size();

        /* Iterate for each element */
        for (int i = 0; i < sizeOfTheDetail; i++) {

            /* Get the model from each element */
            ModelTransactionPurchaseDetail model = detailList.get(i);

            /* Add the timestamps into the model */
            model.setTimeStamps(header.getTimeStamps());

            /* Save them into firebase database */
            firebasePurchaseDetail.push().setValue(model);

        }
    }


    /**
     * Adapter to populate detail data and their images
     */
    public static class Adapter extends RecyclerView.Adapter<DetailPurchaseFragment.Adapter.ViewHolder> {

        /**
         * The activity context
         */
        private Context mContext;

        /**
         * The container of salesDetail
         */
        private ArrayList<ModelTransactionPurchaseDetail> mListDetail;

        /**
         * This could be boss email or login email
         */
        private String mUsefulEncodedEmail;

        /**
         * Long click handler, for the call back method/interface
         */
        private OnLongClickListener mOnLongClickListener;


        /**
         * Constructor
         */
        public Adapter(Context context) {
            this.mContext = context;

        }


        /**
         * Feed the data into this adapter (like a setter)
         */
        public void swapData(ArrayList<ModelTransactionPurchaseDetail> purchaseDetail, String usefulEncodedEmail) {
            this.mListDetail = purchaseDetail;
            this.mUsefulEncodedEmail = usefulEncodedEmail;
            notifyDataSetChanged();
        }


        @Override
        public int getItemViewType(int position) {
            /* The layout xml */
            return R.layout.transaction_adapter_detail_item;
        }

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            /* Initialize the view */
            ViewGroup mainView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

            /* Initialize the view holder and pass the mainView as argument */
            return new ViewHolder(mainView);

        }


        @Override
        public void onBindViewHolder(final Adapter.ViewHolder holder, int position) {

            if (mListDetail != null) {

                /* Get the itemNumber and load to the UI */
                String itemNumber = mListDetail.get(position).getItemNumber();
                holder.itemNumber.setText(itemNumber);

                /* Get the itemDescription and load to the UI */
                String itemDescription = mListDetail.get(position).getItemDescription();
                holder.itemDescription.setText(itemDescription);

                /* Get the unitOfMeasure and load to the UI */
                String unitOfMeasure = mListDetail.get(position).getUnitOfMeasure();
                holder.unitOfMeasure.setText(unitOfMeasure);

                /* Get the price and load to the UI */
                String price = mListDetail.get(position).getPrice();
                holder.price.setText(price);

                /* Get the quantity and load to the UI */
                String quantity = mListDetail.get(position).getQuantity();
                holder.quantity.setText(quantity);

                /* Get the currency and load to the UI */
                String currency = mListDetail.get(position).getCurrency();
                holder.currency.setText(currency);

                /* Get the discount and load to the UI */
                String discount = mListDetail.get(position).getDiscount();
                holder.discount.setText(discount + "%");


                /* Get the amount per item after discount and load to the UI */
                String amount = mListDetail.get(position).getSubTotalPerItem();
                holder.amount.setText(amount);

                /* Get the itemMaster push() key for this item, so we can use it to get the image */
                String itemMasterPushKey = mListDetail.get(position).getItemMasterPushId();


                /**
                 * We want to get the images for this master item, but we only interested to the get one image.
                 */
                Firebase getItemImageForThisItemMaster =
                        new Firebase(Constant.FIREBASE_URL).child(mUsefulEncodedEmail).child(Constant.FIREBASE_LOCATION_ITEM_IMAGE).child(itemMasterPushKey);

                getItemImageForThisItemMaster.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        /* Get the number of the child */
                        long listSize = dataSnapshot.getChildrenCount();

                        /* Initialize the ArrayList of type ModelItemMaster */
                        ArrayList<ModelItemImageMaster> modelList = new ArrayList<>((int) listSize);

                        /* Iterate through each child then get the value model */
                        for (DataSnapshot element : dataSnapshot.getChildren()) {


                            /* Assign the value into the model */
                            ModelItemImageMaster model = element.getValue(ModelItemImageMaster.class);

                            /* Pack the model into a list */
                            modelList.add(model);

                        }

                        if (modelList.size() > 0) {

                            /* update the UI */
                            String imageUrl = modelList.get(0).getImageName();
                            holder.setItemMasterImage(mContext, Constant.CLOUDINARY_BASE_URL_DOWNLOAD + imageUrl);
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

        }


        @Override
        public int getItemCount() {
            return mListDetail != null ? mListDetail.size() : 0;
        }


        /**
         * The long click to delete
         */
        public void setOnLongClickListener(final OnLongClickListener onLongClickListener) {
            mOnLongClickListener = onLongClickListener;
        }


        /**
         * Interface for long click listener
         */
        public interface OnLongClickListener {
            void onLongClickListener(View view, int position);
        }


        /**
         * ViewHolder class
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

            @Bind(R.id.viewCard_transaction_add_point_of_sales_adapter)
            CardView cardView;

            @Bind(R.id.itemMasterImage_transaction_fragment_adapter)
            ImageView itemMasterImage;

            @Bind(R.id.textView_transaction_fragment_add_point_of_sales_adapter_item_number)
            TextView itemNumber;

            @Bind(R.id.textView_transaction_fragment_add_point_of_sales_adapter_item_description)
            TextView itemDescription;

            @Bind(R.id.textView_transaction_fragment_add_point_of_sales_adapter_unit_of_measure)
            TextView unitOfMeasure;

            @Bind(R.id.textView_transaction_fragment_add_point_of_sales_adapter_price)
            TextView price;

            @Bind(R.id.textView_transaction_fragment_add_point_of_sales_adapter_currency)
            TextView currency;

            @Bind(R.id.textView_transaction_fragment_add_point_of_sales_adapter_qty)
            TextView quantity;

            @Bind(R.id.textView_transaction_fragment_add_point_of_sales_adapter_discount)
            TextView discount;

            @Bind(R.id.textView_transaction_fragment_add_point_of_sales_adapter_amount)
            TextView amount;

            /**
             * Constructor
             */
            public ViewHolder(View itemView) {
                super(itemView);

                /**
                 * Initialize the ButterKnife
                 */
                ButterKnife.bind(this, itemView);

                // Long click
                if (cardView != null) {
                    cardView.setOnLongClickListener(this);
                }
            }


            @Override
            public boolean onLongClick(View v) {
                mOnLongClickListener.onLongClickListener(itemView, getLayoutPosition());
                return true;
            }


            /**
             * Load the image to the UI
             */
            public void setItemMasterImage(Context context, String url) {
                if (itemMasterImage != null) {
                    Glide.with(context).load(url).into(itemMasterImage);
                }
            }
        }
    }
}
