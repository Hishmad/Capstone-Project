package com.stockita.newpointofsales.purchasePack.activity;

import android.content.Intent;
import android.os.Bundle;

import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.activities.BaseActivity;
import com.stockita.newpointofsales.purchasePack.fragment.AddNewPurchaseFragment;
import com.stockita.newpointofsales.salesPack.fragment.AddNewSalesFragment;
import com.stockita.newpointofsales.utilities.Constant;

/**
 * TODO: working on this
 */
public class AddNewPurchaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity_add_purchase);

        /* Check if null */
        if (savedInstanceState == null) {

            /* Get the data from the intent */
            Intent intent = getIntent();
            String usefulEmail = intent.getStringExtra(Constant.KEY_USERFUL_ENCODED_EMAIL);
            boolean booleanStatus = intent.getBooleanExtra(Constant.KEY_BOOLEAN_STATUS, false);

            /* Initialize the fragment and pass the data into it. */
            AddNewPurchaseFragment fragment = AddNewPurchaseFragment.newInstance(mEncodedEmail, usefulEmail, booleanStatus);

            /* Populate the fragment into the container */
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }
}
