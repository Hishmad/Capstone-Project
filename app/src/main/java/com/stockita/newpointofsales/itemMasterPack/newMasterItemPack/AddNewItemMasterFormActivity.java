package com.stockita.newpointofsales.itemMasterPack.newMasterItemPack;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.activities.BaseActivity;
import com.stockita.newpointofsales.itemMasterPack.newMasterItemPack.AddNewItemMasterFormFragment;

/**
 * Activity thet host the add new item master form fragment
 */
public class AddNewItemMasterFormActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_master_form_activity);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check if this is new activity
        if (savedInstanceState == null) {

            // Initialize the fragment
            AddNewItemMasterFormFragment fragment = AddNewItemMasterFormFragment.newInstance(mEncodedEmail);

            // Get the fragment manager and do transaction
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

        }
    }
}
