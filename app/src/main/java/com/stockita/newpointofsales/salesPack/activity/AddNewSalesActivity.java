package com.stockita.newpointofsales.salesPack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.activities.BaseActivity;
import com.stockita.newpointofsales.data.ModelTransactionPointOfSalesDetail;
import com.stockita.newpointofsales.data.ModelTransactionPointOfSalesHeader;
import com.stockita.newpointofsales.salesPack.fragment.AddNewSalesFragment;
import com.stockita.newpointofsales.utilities.Constant;

import java.util.ArrayList;

/**
 * This is the activity class for adding a new sales transaction
 */
public class AddNewSalesActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity_add_point_of_sales);

        /* Check if null */
        if (savedInstanceState == null) {

            /* Get the data from the intent */
            Intent intent = getIntent();
            String usefulEmail = intent.getStringExtra(Constant.KEY_USERFUL_ENCODED_EMAIL);
            boolean booleanStatus = intent.getBooleanExtra(Constant.KEY_BOOLEAN_STATUS, false);

            /* Initialize the fragment and pass the data into it. */
            AddNewSalesFragment fragment = AddNewSalesFragment.newInstance(mEncodedEmail, usefulEmail, booleanStatus);

            /* Populate the fragment into the container */
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

        }

    }

}
