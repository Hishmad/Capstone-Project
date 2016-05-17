package com.stockita.newpointofsales.purchasePack.activity;

import android.content.Intent;
import android.os.Bundle;

import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.activities.BaseActivity;
import com.stockita.newpointofsales.purchasePack.fragment.DetailPurchaseFragment;
import com.stockita.newpointofsales.salesPack.fragment.DetailSalesFragment;
import com.stockita.newpointofsales.utilities.Constant;

/**
 * This class is the detail activity for each purchase transactions
 */
public class DetailPurchaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity_add_purchase);


        if (savedInstanceState == null) {

            /* Get data from the intent */
            Intent intent = getIntent();
            String usefulEmail = intent.getStringExtra(Constant.KEY_USERFUL_ENCODED_EMAIL);
            String purchaseHeader = intent.getStringExtra(Constant.KEY_PURCHASE_HEADER);
            String purchaseDetail = intent.getStringExtra(Constant.KEY_PURCHASE_DETAIL);
            boolean booleanStatus = intent.getBooleanExtra(Constant.KEY_BOOLEAN_STATUS, false);

            /* Initialize the fragment and pass the data into it. */
            DetailPurchaseFragment fragment = DetailPurchaseFragment.newInstance(mEncodedEmail, usefulEmail, purchaseHeader, purchaseDetail, booleanStatus);

            /* Populate the fragment into the container */
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

        }



    }
}
