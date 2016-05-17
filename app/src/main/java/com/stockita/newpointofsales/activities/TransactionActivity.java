package com.stockita.newpointofsales.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.FragmentStatePagerAdapter;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.data.ContractData;
import com.stockita.newpointofsales.purchasePack.activity.AddNewPurchaseActivity;
import com.stockita.newpointofsales.purchasePack.fragment.PurchasePendingFragment;
import com.stockita.newpointofsales.salesPack.activity.AddNewSalesActivity;
import com.stockita.newpointofsales.salesPack.fragment.SalesPendingFragment;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.NewSettingActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This is the activity that host the transaction, sales and purchase, using sliding tabs
 */
public class TransactionActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    /* Constant */
    private static final int LOADER_ID_ONE = 1;
    private static final String LOG_TAG = TransactionActivity.class.getSimpleName();

    /* Static field */
    private static int selection;

    /* Get the Useful email for sales and purchase from the SQLite */
    private String mUsefulEncodedEmail;

    /* Get the booleanStatus from the SQLite */
    private boolean booleanStatus;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v13.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @Bind(R.id.container)
    ViewPager mViewPager;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* The layout xml */
        setContentView(R.layout.activity_transaction);

        /* The butter knife for better life */
        ButterKnife.bind(this);

        /* The toolBar */
        setSupportActionBar(toolbar);

        /*
        Create the adapter that will return a fragment for each of the three
        primary sections of the activity.
        */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        /* Set up the ViewPager with the sections adapter. */
        mViewPager.setAdapter(mSectionsPagerAdapter);

        /* The tab layout */
        tabLayout.setupWithViewPager(mViewPager);

        /* Check in case that the mEncodedEmail is null, then logout to prevent crash */
        if (mGoogleApiClient != null && mEncodedEmail == null) {
            logout();
        }

        /* Initialize the loader, to get the boss email and the boolean status from local database */
        getSupportLoaderManager().initLoader(LOADER_ID_ONE, null, this);

        /* Get the position of the fragment in the ViewPager */
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                selection = tab.getPosition();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    /**
     * Floating Action Button
     */
    @OnClick(R.id.fab)
    void theFab() {

        switch (selection) {

            /* The sales pack */
            case 0:

                /* Initialize intent then pass these data to the destination activity */
                Intent intentOne = new Intent(this, AddNewSalesActivity.class);
                intentOne.putExtra(Constant.KEY_USERFUL_ENCODED_EMAIL, mUsefulEncodedEmail);
                intentOne.putExtra(Constant.KEY_BOOLEAN_STATUS, booleanStatus);
                startActivity(intentOne);

                break;

            /* The purchase pack */
            case 1:

                /* Initialize intent then pass these data to the destination activity */
                Intent intentTwo = new Intent(this, AddNewPurchaseActivity.class);
                intentTwo.putExtra(Constant.KEY_USERFUL_ENCODED_EMAIL, mUsefulEncodedEmail);
                intentTwo.putExtra(Constant.KEY_BOOLEAN_STATUS, booleanStatus);
                startActivity(intentTwo);

                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sales, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.about:
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                break;
            case R.id.master:
                finish();
                break;
            case R.id.action_settings:
                goesToTheSetting = true;
                startActivity(new Intent(this, NewSettingActivity.class));
                break;
        }

        return true;

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /* The cursorLoader object */
        android.support.v4.content.CursorLoader loader = null;

        switch (id) {
            case LOADER_ID_ONE:

                /* Query all the data matches the _id 1. */
                loader = new android.support.v4.content.CursorLoader(getBaseContext(),
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

        /* Get the cursor move to first */
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

                    /* He is a worker so this is the boss encoded email */
                    mUsefulEncodedEmail = bossEncodedEmail;

                } else {

                    /* He is the owner so this is the login encoded email */
                    mUsefulEncodedEmail = mEncodedEmail;

                }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = null;
    }


    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            // Final variables
            final int ZERO = 0;
            final int ONE = 1;

            switch (position) {
                case ZERO:
                    return SalesPendingFragment.newInstance(selection, mEncodedEmail);
                case ONE:
                    return PurchasePendingFragment.newInstance(selection, mEncodedEmail);
            }

            return null;
        }

        @Override
        public int getCount() {
            // Number of tabs
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // Final variables
            final int ZERO = 0;
            final int ONE = 1;

            switch (position) {
                case ZERO:
                    return "Sales";
                case ONE:
                    return "Purchase";
            }
            return null;
        }
    }
}
