package com.stockita.newpointofsales.salesPack.activity;

import android.content.Intent;
import android.os.Bundle;

import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.activities.BaseActivity;

import com.stockita.newpointofsales.salesPack.fragment.DetailSalesFragment;
import com.stockita.newpointofsales.utilities.Constant;

/**
 * This class is an activity for detail sales
 */
public class DetailSalesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity_add_point_of_sales);

        if (savedInstanceState == null) {

            /* Get data from the intent */
            Intent intent = getIntent();
            String usefulEmail = intent.getStringExtra(Constant.KEY_USERFUL_ENCODED_EMAIL);
            String salesHeader = intent.getStringExtra(Constant.KEY_SALES_HEADER);
            String salesDetail = intent.getStringExtra(Constant.KEY_SALES_DETAIL);
            boolean booleanStatus = intent.getBooleanExtra(Constant.KEY_BOOLEAN_STATUS, false);

            /* Initialize the fragment and pass the data into it. */
            DetailSalesFragment fragment = DetailSalesFragment.newInstance(mEncodedEmail, usefulEmail, salesHeader, salesDetail, booleanStatus);

            /* Populate the fragment into the container */
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

        }

    }
}
