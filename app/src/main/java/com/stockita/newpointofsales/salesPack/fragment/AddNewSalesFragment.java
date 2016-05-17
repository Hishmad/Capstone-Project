package com.stockita.newpointofsales.salesPack.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.activities.TransactionActivity;
import com.stockita.newpointofsales.barcodeScanner.BarcodeScanner;
import com.stockita.newpointofsales.data.ModelItemImageMaster;
import com.stockita.newpointofsales.data.ModelItemMaster;
import com.stockita.newpointofsales.data.ModelTransactionPointOfSalesDetail;
import com.stockita.newpointofsales.data.ModelTransactionPointOfSalesHeader;
import com.stockita.newpointofsales.itemMasterPack.newMasterItemPack.ItemMasterDialogFragment;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class about creating sales transaction, the user can sell in one invoice multiple items
 */
public class AddNewSalesFragment extends Fragment {

    // Constant
    private static final String LOG_TAG = AddNewSalesFragment.class.getSimpleName();
    private final static String KEY_DATE = "date";
    private final static String KEY_CUSTOMER_NAME = "customerName";
    private final static String KEY_ITEM_NUMBER = "itemNumber";
    private final static String KEY_ITEM_DESCRIPTION = "itemDescription";
    private final static String KEY_UNIT_OF_MEASURE = "unitOfMeasure";
    private final static String KEY_CATEGORY = "category";
    private final static String KEY_CURRENCY = "currency";
    private final static String KEY_PRICE = "price";
    private final static String KEY_QUANTITY = "quantity";
    private final static String KEY_DISCOUNT = "discount";
    private final static String KEY_SUBTOTAL_PER_ITEM = "subtotalPerItem";
    private final static String KEY_INVOICE_TOTAL_BEFORE_DISC_TAX = "invoiceTotalBeforeDiscAndTax";
    private final static String KEY_DATE_MILLIS = "dateMillis";
    private final static String KEY_ONE = "one";
    private final static String KEY_TWO = "two";
    public static final String KEY_DISCOUNT_INVOICE = "checkoutDiscount";
    public static final String KEY_DISCOUNT_VALUE = "checkoutDiscountValue";
    public static final String KEY_INVOICE_TAX = "checkoutTax";
    public static final String KEY_TAX_VALUE = "checkoutTaxValue";
    public static final String KEY_GRAND_TOTAL = "grandTotal";
    public static final String FRAGMENT_DIALOG_ITEM_MASTER = "fragment_dialog_item_master";
    public static final int GET_FROM_BARCODE_SCANNER = 1;
    public static final int GET_FROM_DIALOG_FRAGMENT = 2;

    // Views
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.article_title)
    TextView articleTitle;

    @Bind(R.id.article_subtitle)
    TextView articleSubtitle;

    @Bind(R.id.transaction_fragment_recycler_view_add_point_of_sales)
    RecyclerView detailSalesTransaction;

    @Bind(R.id.imageButton)
    ImageButton queryImageButton;

    @Bind(R.id.imageButton2)
    ImageButton barcodeScanner;

    @Bind(R.id.textView_transaction_fragment_add_point_of_sales_customer_name)
    EditText customerName;

    @Bind(R.id.textView_transaction_fragment_add_point_of_sales_item_number)
    EditText itemNumber;

    @Bind(R.id.textView_transaction_fragment_add_point_of_sales_item_description)
    TextView itemDescription;

    @Bind(R.id.textView_transaction_fragment_add_point_of_sales_unit_of_measure)
    TextView unitOfMeasure;

    @Bind(R.id.textView_transaction_fragment_add_point_of_sales_currency)
    TextView currency;

    @Bind(R.id.textView_transaction_fragment_add_point_of_sales_price)
    TextView price;

    @Bind(R.id.textView_transaction_fragment_add_point_of_sales_discount)
    EditText discount;

    @Bind(R.id.textView_transaction_fragment_add_point_of_sales_quantity)
    EditText quantity;

    @Bind(R.id.checkout_discount)
    TextView checkoutDiscointRate;

    @Bind(R.id.checkout_tax)
    TextView checkoutTax;

    @Bind(R.id.checkout_tax_value)
    TextView checkoutTaxValue;

    @Bind(R.id.checkout_total_after_tax)
    TextView checkoutGrandTotal;


    /**
     * The adapter to populate the detail sales into the UI
     */
    private AddNewSalesFragment.Adapter mAddPointOfSalesAdapter;

    /**
     * Container for the detail sales so we can pass them around
     */
    private ArrayList<ModelTransactionPointOfSalesDetail> mTransactionPointOfSalesDetailArrayList;

    /**
     * The sum amount that will be display in the UI
     */
    private ArrayList<String> mAccumulateTheTotalAmount;


    // Variables for model
    private String mDate;
    private String mCustomerName;
    private String mInvoiceTotalBeforeDiscountAndTax;
    private String mItemNumber;
    private String mItemDescription;
    private String mUnitOfMeasure;
    private String mCategory;
    private String mCurrency;
    private String mPrice;
    private String mQuantity;
    private String mDiscount;
    private String mSubTotalPerItem;
    private long mDateMillis;
    private String mInvoiceDiscount; // invoiceDiscount (rate)
    private String mDiscountValue; // discountValue
    private String mInvoiceTax;
    private String mTaxValue;
    private String mInvoiceTotalAfterTax;


    /**
     * The push() id of each itemMaster from Firebase has one.
     */
    private String mPushIDofTheItemMaster;

    /**
     * The login encoded email
     */
    private String mEncodedEmail;

    /**
     * This can be a reference to mEncodedEmail or mBossEncodedEmail
     */
    private String mUsefulEncodedEmail;

    /**
     * The authorized status of from the boss email.
     */
    private boolean mBooleanStatus;

    /**
     * The Firebase reference to the item master, using mEncodedEmail node or mBossEncodedEmail node.
     */
    private Firebase mFirebaseItemMasterRef;

    /**
     * Empty constructor
     */
    public AddNewSalesFragment() {

    }


    /**
     * Create a basic bundle to get data from the activity
     */
    public static AddNewSalesFragment newInstance(String encodedEmail, String usefulEmail, boolean booleanStatus) {

        /* Instantiate the fragment */
        AddNewSalesFragment fragment = new AddNewSalesFragment();

        /* Instantiate the Bundle */
        Bundle args = new Bundle();

        /**
         * Pass the data
         */
        args.putString(Constant.KEY_ENCODED_EMAIL, encodedEmail);
        args.putString(Constant.KEY_USERFUL_ENCODED_EMAIL, usefulEmail);
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

        /* Get the current login encoded email from the activity/bundle */
        mEncodedEmail = getArguments().getString(Constant.KEY_ENCODED_EMAIL);

        /* Get the useful encoded email */
        mUsefulEncodedEmail = getArguments().getString(Constant.KEY_USERFUL_ENCODED_EMAIL);

        /* Get the boolean status is he approved as worker or not */
        mBooleanStatus = getArguments().getBoolean(Constant.KEY_BOOLEAN_STATUS);

        /* initialize the Firebase ref to itemMaster */
        mFirebaseItemMasterRef = new Firebase(Constant.FIREBASE_URL).child(mUsefulEncodedEmail).child(Constant.FIREBASE_LOCATION_ITEM_MASTER);

        /* Initialize the adapter */
        mAddPointOfSalesAdapter = new Adapter(getActivity());

        /* Initialize the dataSet */
        mTransactionPointOfSalesDetailArrayList = new ArrayList<>(); // This will store the detail invoice.
        mAccumulateTheTotalAmount = new ArrayList<>(); // This will store the sum of amount per item, for later calculation.

        /* Create timestamp */
        createNewTimestamp();

        /* Restore instance state */
        if (savedInstanceState != null) {
            mDate = savedInstanceState.getString(KEY_DATE);
            mCustomerName = savedInstanceState.getString(KEY_CUSTOMER_NAME);
            mTransactionPointOfSalesDetailArrayList = savedInstanceState.getParcelableArrayList(KEY_ONE);
            mAccumulateTheTotalAmount = savedInstanceState.getStringArrayList(KEY_TWO);
            mItemNumber = savedInstanceState.getString(KEY_ITEM_NUMBER);
            mItemDescription = savedInstanceState.getString(KEY_ITEM_DESCRIPTION);
            mUnitOfMeasure = savedInstanceState.getString(KEY_UNIT_OF_MEASURE);
            mCategory = savedInstanceState.getString(KEY_CATEGORY);
            mCurrency = savedInstanceState.getString(KEY_CURRENCY);
            mPrice = savedInstanceState.getString(KEY_PRICE);
            mQuantity = savedInstanceState.getString(KEY_QUANTITY);
            mDiscount = savedInstanceState.getString(KEY_DISCOUNT);
            mSubTotalPerItem = savedInstanceState.getString(KEY_SUBTOTAL_PER_ITEM);
            mInvoiceTotalBeforeDiscountAndTax = savedInstanceState.getString(KEY_INVOICE_TOTAL_BEFORE_DISC_TAX);
            mDateMillis = savedInstanceState.getLong(KEY_DATE_MILLIS);

            mUsefulEncodedEmail = savedInstanceState.getString(Constant.KEY_USERFUL_ENCODED_EMAIL);
            mPushIDofTheItemMaster = savedInstanceState.getString(Constant.KEY_PUSH_ID_OF_ITEM_MASTER);

            mInvoiceDiscount = savedInstanceState.getString(KEY_DISCOUNT_INVOICE);
            mDiscountValue = savedInstanceState.getString(KEY_DISCOUNT_VALUE);
            mInvoiceTax = savedInstanceState.getString(KEY_INVOICE_TAX);
            mTaxValue = savedInstanceState.getString(KEY_TAX_VALUE);
            mInvoiceTotalAfterTax = savedInstanceState.getString(KEY_GRAND_TOTAL);

             /* Send the data again to the adapter on screen rotation */
            mAddPointOfSalesAdapter.swapData(mTransactionPointOfSalesDetailArrayList, mUsefulEncodedEmail);

        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_DATE, mDate);
        outState.putString(KEY_CUSTOMER_NAME, mCustomerName);
        outState.putParcelableArrayList(KEY_ONE, mTransactionPointOfSalesDetailArrayList);
        outState.putStringArrayList(KEY_TWO, mAccumulateTheTotalAmount);
        outState.putString(KEY_ITEM_NUMBER, mItemNumber);
        outState.putString(KEY_ITEM_DESCRIPTION, mItemDescription);
        outState.putString(KEY_UNIT_OF_MEASURE, mUnitOfMeasure);
        outState.putString(KEY_CATEGORY, mCategory);
        outState.putString(KEY_CURRENCY, mCurrency);
        outState.putString(KEY_PRICE, mPrice);
        outState.putString(KEY_QUANTITY, mQuantity);
        outState.putString(KEY_DISCOUNT, mDiscount);
        outState.putString(KEY_SUBTOTAL_PER_ITEM, mSubTotalPerItem);
        outState.putString(KEY_INVOICE_TOTAL_BEFORE_DISC_TAX, mInvoiceTotalBeforeDiscountAndTax);
        outState.putLong(KEY_DATE_MILLIS, mDateMillis);

        outState.putString(Constant.KEY_USERFUL_ENCODED_EMAIL, mUsefulEncodedEmail);
        outState.putString(Constant.KEY_PUSH_ID_OF_ITEM_MASTER, mPushIDofTheItemMaster);

        outState.putString(KEY_DISCOUNT_INVOICE, mInvoiceDiscount);
        outState.putString(KEY_DISCOUNT_VALUE, mDiscountValue);
        outState.putString(KEY_INVOICE_TAX, mInvoiceTax);
        outState.putString(KEY_TAX_VALUE, mTaxValue);
        outState.putString(KEY_GRAND_TOTAL, mInvoiceTotalAfterTax);
    }


    @Override
    public void onResume() {
        super.onResume();

        /*
        WARNING, this is because the following UI are update via anonymous class,
        so that's why it is necessary to update them here, for configuration changes.
        */
        articleTitle.setText(mInvoiceTotalBeforeDiscountAndTax);
        itemDescription.setText(mItemDescription);
        unitOfMeasure.setText(mUnitOfMeasure);
        price.setText(mPrice);
        currency.setText(mCurrency);
        checkoutDiscointRate.setText(mInvoiceDiscount);
        checkoutTax.setText(mInvoiceTax);
        checkoutTaxValue.setText(mTaxValue);
        checkoutGrandTotal.setText(mInvoiceTotalAfterTax);
        collapsingToolbarLayout.setTitle(mInvoiceTotalAfterTax);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_transaction, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        /* Proceed if there is a transaction */
        if (mTransactionPointOfSalesDetailArrayList.size() > 0 && customerName.length() > 0) {

            /* Get the menu item IDs */
            switch (item.getItemId()) {

                /* checkout page */
                case R.id.checkout_menu:

                    /* Instantiate the model to pack the header. */
                    ModelTransactionPointOfSalesHeader transactionPointOfSalesHeader = new ModelTransactionPointOfSalesHeader();

                    /* Date in Millis new version*/
                    transactionPointOfSalesHeader.setTimeStamps(String.valueOf(mDateMillis));

                    /* Customer name */
                    transactionPointOfSalesHeader.setCustomerName(mCustomerName);

                    /* Invoice amount before discount and tax */
                    transactionPointOfSalesHeader.setSubTotalBeforeTax(mInvoiceTotalBeforeDiscountAndTax);

                    /* Number of items */
                    int numberOfItems = mTransactionPointOfSalesDetailArrayList.size();
                    transactionPointOfSalesHeader.setNumberOfItems(String.valueOf(numberOfItems));

                    /**
                     * Saving
                     */
                    clickPendingPayment(transactionPointOfSalesHeader, mTransactionPointOfSalesDetailArrayList);

                    /* Set to null, this is important, especially when the user hit back button, to add or delete something. */
                    mItemNumber = null;
                    mItemDescription = null;
                    mUnitOfMeasure = null;
                    mCurrency = null;
                    mPrice = null;
                    mInvoiceDiscount = null;
                    mDiscountValue = null;
                    mInvoiceTax = null;
                    mTaxValue = null;
                    mInvoiceTotalAfterTax = null;

                    /* clear up everything */
                    clearUpOnExit();

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
                inflater.inflate(R.layout.fragment_detail_sales_form, container, false);

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

        
        /* When the user type the customer name */
        customerName.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    /**
                     * If there is already transaction made, then the user will not
                     * be able to change the customer name any more.
                     */
                    if (mTransactionPointOfSalesDetailArrayList.size() == 0) {
                        mCustomerName = customerName.getText().toString();
                    } else {
                        customerName.setText(mCustomerName);
                    }

                    /* Hide keyboard */
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });


        /* If the user manually typed the item number, instated of using the lookup/barcode reader. */
        itemNumber.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE && customerName.length() > 0) {

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

                if (customerName.length() > 0) {

                    /* Clear the UI */
                    clearUpTheUI();

                    /* Get the barcode reader */
                    Intent startScanner = new Intent(getActivity(), BarcodeScanner.class);
                    startActivityForResult(startScanner, GET_FROM_BARCODE_SCANNER);
                }

            }
        });


        /**
         * This when the user use the dialog fragment to select an item number
         * from the Recycler view
         */
        final FragmentManager fm = getActivity().getFragmentManager();

        /**
         * Pass the mEncodedEmail, so if the ownership change this will take effect also here.
         */
        final ItemMasterDialogFragment itemMasterDialogFragment = ItemMasterDialogFragment.newInstance(mEncodedEmail);
        itemMasterDialogFragment.setTargetFragment(this, GET_FROM_DIALOG_FRAGMENT);

        /* Query button, so the user can chose an item from the dialog */
        queryImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (customerName.length() > 0) {

                    /* Clear the UI */
                    clearUpTheUI();

                    /* Invoke the show() method to show the dialog fragment */
                    itemMasterDialogFragment.show(fm, FRAGMENT_DIALOG_ITEM_MASTER );
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
        detailSalesTransaction.setLayoutManager(linearLayoutManager);

        /* Set the adapter */
        detailSalesTransaction.setAdapter(mAddPointOfSalesAdapter);

        /* Set the long click listener to delete detail item */
        mAddPointOfSalesAdapter.setOnLongClickListener(onLongClickListener);

        /* Return rootView */
        return rootView;
    }


    /**
     * FAB, will add the item to the sales detail, which is in the UI
     * it is a recycler view, and the adapter is mAddPointOfSalesAdapter
     */
    @OnClick(R.id.fab_transaction_fragment_add_point_of_sales)
    public void fab() {

        if (itemDescription.length() > 0 && customerName.length() > 0) {

            /* Hide the keyboard */
            Utility.hideKeyboard(getActivity());

            /**
             * Get the customer name from the UI and put them into the state,
             * in case the user didn't press DONE on the keyboard.
             */
            if (mTransactionPointOfSalesDetailArrayList.size() == 0) {
                mCustomerName = customerName.getText().toString();
            } else {
                customerName.setText(mCustomerName);
            }

            /* Pack and pass the data to the adapter */
            passDataToTheRecyclerViewInvoiceList(mDateMillis);

            /* Clear the UI */
            clearUpTheUI();
        }
    }


    /**
     * The long click listener on the detail sales to delete an item from that list
     */
    public Adapter.OnLongClickListener onLongClickListener =
            new Adapter.OnLongClickListener() {
                @Override
                public void onLongClickListener(View view, final int position) {

                    /* Alert dialog for the user to click OK or Cancel */
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(getString(R.string.alert_dialog_title_delete))
                            .setMessage(getString(R.string.alert_dialog_message))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.alert_dialog_positive_button_yes), new DialogInterface.OnClickListener() {

                                /* If the user click yes/oke... */
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    /* Delete the detail */
                                    if (mTransactionPointOfSalesDetailArrayList.size() > 0 ) {

                                        /* Delete at this position */
                                        mTransactionPointOfSalesDetailArrayList.remove(position);

                                        /* Pass the data to the adapter */
                                        mAddPointOfSalesAdapter.swapData(mTransactionPointOfSalesDetailArrayList, mUsefulEncodedEmail);

                                        /* Delete the item total amount */
                                        mAccumulateTheTotalAmount.remove(position);

                                        /* Calculate the accumulated invoice amount */
                                        int tempSize = mAccumulateTheTotalAmount.size();
                                        double sum = 0;
                                        for (int i = 0; i < tempSize; i++) {
                                            sum += Double.parseDouble(mAccumulateTheTotalAmount.get(i));
                                        }
                                        mInvoiceTotalBeforeDiscountAndTax = String.valueOf(sum);

                                        /* Display the accumulated invoice amount in to the UI */
                                        articleTitle.setText(mInvoiceTotalBeforeDiscountAndTax);

                                        /* Recalculate the totals */
                                        recalculateTheTotalAmount(mInvoiceTotalBeforeDiscountAndTax);

                                    }

                                }
                            }).setNegativeButton(getString(R.string.alert_dialog_negative_button_cancel), new DialogInterface.OnClickListener() {

                        // If the user click cancel...
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    }).show();
                }
            };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            /* Get the item number from the Barcode scanner */
            case GET_FROM_BARCODE_SCANNER:

                if (resultCode == Activity.RESULT_OK) {

                    /* Get the barcode */
                    mItemNumber = data.getStringExtra(BarcodeScanner.SCAN_CONTENTS);
                    itemNumber.setText(mItemNumber);

                    /* Query Firebase then update the UI */
                    queryTheItemMasterAndUpdateTheUI();


                } else {
                    /* If system error */
                    Toast.makeText(getActivity(), getString(R.string.toast_image_capture_failed), Toast.LENGTH_SHORT).show();

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
     * Helper method to pack and pass data to the adapter,
     * also calculate the discount and the amount
     */
    private void passDataToTheRecyclerViewInvoiceList(long dateMillis) {

        /* Initialize the model for sales detail then pack the data; */
        ModelTransactionPointOfSalesDetail detail =
                new ModelTransactionPointOfSalesDetail();

        /* date */
        detail.setDate(String.valueOf(dateMillis));

        /* date in millis */
        detail.setTimeStamps(String.valueOf(mDateMillis));

        /* item number */
        detail.setItemNumber(mItemNumber);

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
        mDiscount = String.valueOf(calcDiscount);

        /* Calculate the discount value */
        double discountValue = (calcPrice * calcQuantity) * calcDiscount / 100;

        /* Calculate the total amount per item after discount */
        double calcAmount = (calcPrice * calcQuantity) - discountValue;
        mSubTotalPerItem = String.valueOf(calcAmount);

        /**
         * End of calculate the amount and discount
         */

        /* price */
        detail.setPrice(mPrice);

        /* quantity from the UI */
        detail.setQuantity(mQuantity);

        /* discount from the UI */
        detail.setDiscount(mDiscount);

        /* Amount per item after discount */
        detail.setSubTotalPerItem(mSubTotalPerItem);

        /* add data, new data always at index 0. */
        mAccumulateTheTotalAmount.add(0, detail.getSubTotalPerItem());
        mTransactionPointOfSalesDetailArrayList.add(0, detail);

        /* pass data to adapter */
        mAddPointOfSalesAdapter.swapData(mTransactionPointOfSalesDetailArrayList, mUsefulEncodedEmail);

        /* Calculate the accumulated invoice amount */
        int tempSize = mAccumulateTheTotalAmount.size();
        double sum = 0;
        for (int i = 0; i < tempSize; i++) {
            sum += Double.parseDouble(mAccumulateTheTotalAmount.get(i));
        }
        mInvoiceTotalBeforeDiscountAndTax = String.valueOf(sum);

        /* Display the accumulated invoice amount in to the UI */
        articleTitle.setText(mInvoiceTotalBeforeDiscountAndTax);

        /* Recalculate the checkout amount */
        recalculateTheTotalAmount(mInvoiceTotalBeforeDiscountAndTax);

    }


    /**
     * Helper method to query the item master database then update the UI
     */
    private void queryTheItemMasterAndUpdateTheUI() {

        if (itemNumber.length() > 0) {

            /* Get the item number from the UI */
            mItemNumber = itemNumber.getText().toString();

            /* Query for the itemNumber */
            Query queryItemNumber =
                    mFirebaseItemMasterRef.orderByChild(Constant.FIREBASE_PROPERTY_ITEM_NUMBER).equalTo(mItemNumber);
            queryItemNumber.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    /* Get the number of the child */
                    long listSize = dataSnapshot.getChildrenCount();

                    /* Initialize the ArrayList of type ModelItemMaster */
                    ArrayList<ModelItemMaster> modelList = new ArrayList<>((int) listSize);


                    /* Iterate through each child then get the value model */
                    for (DataSnapshot element: dataSnapshot.getChildren()) {

                        /* Get the push() key for this itemMaster, for later use */
                        mPushIDofTheItemMaster = element.getKey();

                        /* Assign the value into the model */
                        ModelItemMaster model = element.getValue(ModelItemMaster.class);

                        /* Pack the model into a list */
                        modelList.add(model);

                    }

                    /* Check if the list is not empty */
                    if (modelList.size() > 0) {

                        /**
                         * Update the UI
                         */
                        itemDescription.setText(modelList.get(0).getItemDescription());
                        unitOfMeasure.setText(modelList.get(0).getUnitOfMeasure());
                        currency.setText(modelList.get(0).getCurrency());
                        price.setText(String.valueOf(modelList.get(0).getPrice()));

                        /**
                         * Update the State
                         */
                        mCategory = modelList.get(0).getCategory();
                        mItemDescription = itemDescription.getText().toString();
                        mUnitOfMeasure = unitOfMeasure.getText().toString();
                        mCurrency = currency.getText().toString();
                        mPrice = price.getText().toString();

                    } else {

                        /* Inform the user */
                        Toast.makeText(getActivity(), getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();

                        /* Cleat the UI if no data */
                        clearUpTheUI();
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
     * Helper method when the user click save
     */
    private void clickPendingPayment(ModelTransactionPointOfSalesHeader header,ArrayList<ModelTransactionPointOfSalesDetail> details) {

        // Mark the row as pending, get the
        String typeOfPayment = "pending";

        /* Save the header and return the push() key */
        String theHeaderPushKey = saveToSalesHeader(typeOfPayment, header);

        /* Save the detail */
        saveToSalesDetail(theHeaderPushKey, header, details);
    }


    /**
     * Helper method to save salesHeader into Firebase database
     */
    private String saveToSalesHeader(String typeOfPayment, ModelTransactionPointOfSalesHeader header) {

        /* Get the totals add them to the model before setValue to Firebase */
        header.setTypeOfPayment(typeOfPayment);
        header.setInvoiceDiscount(mInvoiceDiscount);
        header.setDiscountValue(mDiscountValue);
        header.setInvoiceTax(mInvoiceTax);
        header.setTaxValue(mTaxValue);
        header.setInvoiceTotalAfterTax(mInvoiceTotalAfterTax);

        /* Initialize the Firebase salesHeader node */
        Firebase firebaseSalesHeader = new Firebase(Constant.FIREBASE_URL).child(mUsefulEncodedEmail).child(Constant.FIREBASE_LOCATION_SALES_BILL_HEADER);

        /* Get the header new push() key */
        Firebase thePushKey = firebaseSalesHeader.push();

        /* Get the String of push() key for later use with detailSales */
        String thePushKeyString = thePushKey.getKey();

        /* Save them into Firebase database */
        thePushKey.setValue(header);

        /* the push() key to be use with detailSales */
        return thePushKeyString;

    }


    /**
     * Helper method to save salesDetail into Firebase database
     */
    private void saveToSalesDetail(String headerPushKey, ModelTransactionPointOfSalesHeader header, ArrayList<ModelTransactionPointOfSalesDetail> detailList) {

        /* Initialize the Firebase salesDetail node */
        Firebase firebaseSalesDetail =
                new Firebase(Constant.FIREBASE_URL).child(mUsefulEncodedEmail).child(Constant.FIREBASE_LOCATION_SALES_BILL_DETAIL).child(headerPushKey);

        /* Get the size of the arrayList */
        int sizeOfTheDetail = detailList.size();

        /* Iterate for each element */
        for (int i = 0; i < sizeOfTheDetail; i++) {

            /* Get the model from each element */
            ModelTransactionPointOfSalesDetail model = detailList.get(i);

            /* Add the timestamps into the model */
            model.setTimeStamps(header.getTimeStamps());

            /* Save them into firebase database */
            firebaseSalesDetail.push().setValue(model);

        }

    }


    /**
     * Helper method to recalculate the totals
     */
    private void recalculateTheTotalAmount(String totalBeforeDiscountAndTax) {

        // Display the discount rate
        mInvoiceDiscount = Utility.getAnyString(getActivity(), getString(R.string.key_pref_discount_rate), "0.0");
        checkoutDiscointRate.setText(mInvoiceDiscount);

        // Display the tax
        mInvoiceTax = Utility.getAnyString(getActivity(), getString(R.string.key_pref_tax_rate), "0.0");
        checkoutTax.setText(mInvoiceTax);

        // If length is 0, assign 0 value to discount and discount value
        if (checkoutDiscointRate.length() == 0) {
            checkoutDiscointRate.setText("0");
            mInvoiceDiscount = "0";
            mDiscountValue = "0";
        }


        // if length is 0, assign 0 value to tax and tax value
        if (checkoutTax.length() == 0) {
            checkoutTax.setText("0");
            mInvoiceTax = "0";
            mTaxValue = "0";
        }

        // Calculate the discount
        mInvoiceDiscount = checkoutDiscointRate.getText().toString();
        double discount = Double.parseDouble(mInvoiceDiscount);
        double subTotal = Double.parseDouble(totalBeforeDiscountAndTax);
        double discountValue = (subTotal * discount) / 100;
        mDiscountValue = String.valueOf(discountValue);
        double totalAfterDiscount = subTotal - discountValue;

        // Calculate the tax
        mInvoiceTax = checkoutTax.getText().toString();
        double tax = Double.parseDouble(mInvoiceTax);
        double taxValue = (totalAfterDiscount * tax) / 100;
        mTaxValue = String.valueOf(taxValue);
        double totalAfterTax = totalAfterDiscount + taxValue;
        mInvoiceTotalAfterTax = String.valueOf(totalAfterTax);

        // Update the UI
        checkoutDiscointRate.setText(mInvoiceDiscount);
        checkoutTax.setText(mInvoiceTax);
        checkoutTaxValue.setText(mTaxValue);
        checkoutGrandTotal.setText(mInvoiceTotalAfterTax);
        collapsingToolbarLayout.setTitle(mInvoiceTotalAfterTax);

    }



    /**
     * Create a firebase ref to get the ServerValue.TIMESTAMP
     */
    private void createNewTimestamp() {

        /* Get the firebase refrence to the timestamp node */
        Firebase getMeAtimeStampFromFirebase = new Firebase(Constant.FIREBASE_URL).child(mEncodedEmail).child(Constant.FIREBASE_LOCATION_TIME_STAMP);

        /* This is the Firebase server value time stamp */
        HashMap<String, Object> timestampCreated = new HashMap<>();

        /* Pack the ServerValue.TIMESTAMP into a HashMap */
        timestampCreated.put(Constant.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        /* Add the HashMap as value */
        getMeAtimeStampFromFirebase.setValue(timestampCreated);

        /* Now we can retrieve that time stamp using this listener */
        getMeAtimeStampFromFirebase.child(Constant.FIREBASE_PROPERTY_TIMESTAMP).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /* Get the timestamp in long millis */
                mDateMillis = dataSnapshot.getValue(Long.class);

                /* Update the UI */
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                mDate = simpleDateFormat.format(mDateMillis);
                articleSubtitle.setText(mDate);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

                if (firebaseError != null) {
                    Log.e(LOG_TAG, firebaseError.toString());
                }
            }
        });
    }


    /**
     * Helper method to clear the UI
     */
    private void clearUpTheUI() {
        itemNumber.setText("");
        itemDescription.setText("");
        unitOfMeasure.setText("");
        currency.setText("");
        price.setText("");
        quantity.setText("");
        discount.setText("");

    }


    /**
     * Helper method to clear the header and detail related UI
     */
    private void clearUpOnExit() {

        /* Clear up the UI */
        clearUpTheUI();

        /* Clear up the totals */
        checkoutDiscointRate.setText("");
        checkoutTax.setText("");
        checkoutTaxValue.setText("");
        checkoutGrandTotal.setText("");
        collapsingToolbarLayout.setTitle("");
        articleTitle.setText("");
        customerName.setText("");

        /* Clear up the adapter */
        mAddPointOfSalesAdapter.swapData(null, null);

        /* Clear up the accumulate */
        mAccumulateTheTotalAmount = null;
    }


    /**
     * This is the Adapter class to populate detail sales into recycler view.
     */
    public static class Adapter extends RecyclerView.Adapter<AddNewSalesFragment.Adapter.ViewHolder> {

        /**
         * The activity context
         */
        private Context mContext;

        /**
         * The list of type ModelTransactionPointOfSalesDetail, this is the data set
         * that will populate the recycler view
         */
        private ArrayList<ModelTransactionPointOfSalesDetail> mDataFeedArrayList;

        /**
         * Long click handler, for the call back method/interface
         */
        private OnLongClickListener mOnLongClickListener;

        /**
         * This is the encodedEmail maybe for the current user or for his boss
         */
        private String mUsefulEncodedEmail;


        /**
         * Constructor
         */
        public Adapter(Context context) {
            this.mContext = context;

        }


        /**
         * Get the data source and notify changes
         */
        public void swapData(ArrayList<ModelTransactionPointOfSalesDetail> data, String usefulEncodedEmail) {
            this.mDataFeedArrayList = data;
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
            View mainView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

            /* Return the view holder */
            return new ViewHolder(mainView);
        }


        @Override
        public void onBindViewHolder(final Adapter.ViewHolder holder, int position) {


            /* Get the data */
            if (mDataFeedArrayList != null) {

                /* Get the itemNumber and load to the UI */
                String itemNumber = mDataFeedArrayList.get(position).getItemNumber();
                holder.itemNumber.setText(itemNumber);

                /* Get the itemDescription and load to the UI */
                String itemDescription = mDataFeedArrayList.get(position).getItemDescription();
                holder.itemDescription.setText(itemDescription);

                /* Get the unitOfMeasure and load to the UI */
                String unitOfMeasure = mDataFeedArrayList.get(position).getUnitOfMeasure();
                holder.unitOfMeasure.setText(unitOfMeasure);

                /* Get the price and load to the UI */
                String price = mDataFeedArrayList.get(position).getPrice();
                holder.price.setText(price);

                /* Get the quantity and load to the UI */
                String quantity = mDataFeedArrayList.get(position).getQuantity();
                holder.quantity.setText(quantity);


                /* Get the currency and load to the UI */
                String currency = mDataFeedArrayList.get(position).getCurrency();
                holder.currency.setText(currency);


                /* Get the discount and load to the UI */
                String discount = mDataFeedArrayList.get(position).getDiscount();
                holder.discount.setText(String.format("%s%%", discount));


                /* Get the amount per item after discount and load to the UI */
                String amount = mDataFeedArrayList.get(position).getSubTotalPerItem();
                holder.amount.setText(amount);

                /* Get the itemMaster push() key for this item, so we can use it to get the image */
                String itemMasterPushKey = mDataFeedArrayList.get(position).getItemMasterPushId();


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
                        for (DataSnapshot element: dataSnapshot.getChildren()) {

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

                        if (firebaseError != null) {
                            Log.e(LOG_TAG, firebaseError.toString());
                        }
                    }
                });

            }

        }

        @Override
        public int getItemCount() {
            return mDataFeedArrayList != null ? mDataFeedArrayList.size() : 0;

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
        public interface OnLongClickListener{
            void onLongClickListener(View view, int position);
        }


        /**
         * View holder class to initialize the views
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {


            /**
             * The views
             */
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

            /**
             * Load the image
             */
            public void setItemMasterImage(Context context, String url) {
                if (itemMasterImage != null) {
                    Glide.with(context).load(url).into(itemMasterImage);
                }
            }

            /**
             * The long click
             */
            @Override
            public boolean onLongClick(View v) {
                mOnLongClickListener.onLongClickListener(itemView, getLayoutPosition());
                return true;
            }

        }
    }
}
