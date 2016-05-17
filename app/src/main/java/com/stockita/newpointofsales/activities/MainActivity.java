package com.stockita.newpointofsales.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.categoryPack.AddNewCategoryFormFragment;
import com.stockita.newpointofsales.data.ModelItemMaster;
import com.stockita.newpointofsales.data.ModelUser;
import com.stockita.newpointofsales.categoryPack.CategoryRecyclerViewFragment;
import com.stockita.newpointofsales.fragment.CoworkerRecyclerViewFragment;
import com.stockita.newpointofsales.interfaces.ItemSelectedSingleClick;
import com.stockita.newpointofsales.itemMasterPack.newMasterItemPack.DetailMasterItemFormFragment;
import com.stockita.newpointofsales.itemMasterPack.newMasterItemPack.AddNewItemMasterFormActivity;
import com.stockita.newpointofsales.itemMasterPack.newMasterItemPack.ItemMasterRecyclerViewFragment;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.NewSettingActivity;

import java.util.ArrayList;

/**
 * Here is the MainActivity, that host the three fragment using a sliding tab.
 */
public class MainActivity extends BaseActivity implements ItemSelectedSingleClick {

    /* Constant */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String FRAGMENT_DIALOG_TAG = "add_new_category_form_fragment";


    /* For /users/mEncodedEmail */
    private Firebase mUserRef;
    private ValueEventListener mUserRefListener;


    /**
     * This will get the position of the fragment and pass them to the FAB
     */
    private static int sSelection;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Create the adapter that will return a fragment for each of the three
         * primary sections of the activity.
         */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        /* Set up the ViewPager with the sections adapter. */
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        /* Tab layout */
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }

        /* Check in case that the mEncodedEmail is null, then logout to prevent crash */
        if (mGoogleApiClient != null && mEncodedEmail == null) {
            logout();
        }


        /**
         * Create {@link Firebase} references, then get the mEncodedEmail from parent which
         * is {@link BaseActivity#mEncodedEmail}
         */
        mUserRef = new Firebase(Constant.FIREBASE_URL_USERS).child(mEncodedEmail);

        /**
         * Get the user information from the Firebase/users using the following listener,
         * the information for the user name
         */
        mUserRefListener = mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /* dataSnapshot reference for ModelUser.class */
                ModelUser user = dataSnapshot.getValue(ModelUser.class);

                /**
                 * Set the activity title to current user name if user is not null
                 */
                if (user != null) {
                    /* Assumes that the first word in the user's name is the user's first name. */
                    String firstName = user.getName().split("\\s+")[0];
                    String title = firstName + "'s " + getString(R.string.merchant);
                    setTitle(title);
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
         * This is listener is to get the tab position, however it does a other think
         * but we are only interested for the current tab position
         */
        if (tabLayout != null) {
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    /* First we invoke this method for mViewPager */
                    mViewPager.setCurrentItem(tab.getPosition());

                    /**
                     *  Then we can assign the value of the current position to
                     *  a static field, there are some other way to do this.
                     */
                    sSelection = tab.getPosition();

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
         * Floating Action Button, only one FAB but it has different use,
         * we get the sliding tab position from {@link sSelection}
         */
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /* Local final constants */
                    final int SELECTION_ZERO = 0; // For category adding a new form
                    final int SELECTION_ONE = 1; // For Item adding a new form
                    final int SELECTION_TWO = 2; // For co-workers a

                    switch (sSelection) {
                        case SELECTION_ZERO:

                            /* Dialog to add new category */
                            FragmentManager fm = getFragmentManager();
                            AddNewCategoryFormFragment fragment = AddNewCategoryFormFragment.newInstance(mEncodedEmail);
                            fragment.show(fm, FRAGMENT_DIALOG_TAG);

                            break;
                        case SELECTION_ONE:

                            /* Activity to add new item master and images */
                            startActivity(new Intent(getBaseContext(), AddNewItemMasterFormActivity.class));
                            break;
                        case SELECTION_TWO:

                            /* Future development */
                            Toast.makeText(getBaseContext(), R.string.message_future_development, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }

    }


    /**
     * Remove any Firebase listeners
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* For /users/mEncodedEmail */
        if (mUserRef != null && mUserRefListener != null) {
            mUserRef.removeEventListener(mUserRefListener);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            case R.id.about:
                // TODO: create about activity
                return true;
            case R.id.transaction:
                startActivity(new Intent(this, TransactionActivity.class));
                return true;
            case R.id.action_settings:
                goesToTheSetting = true;
                startActivity(new Intent(this, NewSettingActivity.class));
                return true;

        }
        return true;
    }

    /**
     * This is a callbacks method, specially for two pane fragment,
     * if the user click on an item master, which is ItemMasterRecyclerFragment
     * adapter clicked an item.
     * This is only for sw600 and sw600-land, for activity with two
     * span fragment.
     */
    @Override
    public void setSingleClick(View view, int position, Intent intent) {


        /* This is the list of all model of type ModelItemMaster */
        ArrayList<ModelItemMaster> lListModelItemMaster = intent.getParcelableArrayListExtra(Constant.KEY_ITEM_MASTER_POJO_LIST);

        /* The list as container for the push() keys for each element in mListModelItemMaster */
        ArrayList<String> lListModelItemMasterKeys = intent.getStringArrayListExtra(Constant.KEY_ITEM_MASTER_KEY_LIST);

        /* The Firebase reference to the itemMaster node including the push() key */
        String itemMasterNode = intent.getStringExtra(Constant.KEY_ITEM_MASTER_NODE);

        /* The Firebase reference to the ItemImage node */
        String itemImageNode =  intent.getStringExtra(Constant.KEY_ITEM_IMAGE_MASTER_NODE);

        /* The boolean status for the ownership status */
        boolean isHeWorker = intent.getBooleanExtra(Constant.KEY_BOOLEAN_STATUS, false);

        /* Instantiate the fragment */
        DetailMasterItemFormFragment fragment =
                DetailMasterItemFormFragment.newInstance(position, lListModelItemMaster.get(position),
                        itemImageNode, lListModelItemMasterKeys.get(position), itemMasterNode);

        /* Manage the fragment */
        getFragmentManager().beginTransaction()
                .replace(R.id.container2, fragment)
                .commit();

    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {


        /**
         * Constructor calling super
         *
         * @param fm FragmentManager object
         */
        public SectionsPagerAdapter(android.app.FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.app.Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            // Local final variables
            final int ZERO = 0;
            final int ONE = 1;
            final int TWO = 2;

            switch (position) {

                case ZERO:
                    /**
                     * {@link CategoryRecyclerViewFragment#newInstance(String)}
                     */
                    return CategoryRecyclerViewFragment.newInstance(mEncodedEmail);
                case ONE:
                    /**
                     * {@link ItemMasterRecyclerViewFragment#newInstance(String)}
                     */
                    return ItemMasterRecyclerViewFragment.newInstance(mEncodedEmail);
                case TWO:
                    /**
                     * {@link CoworkerRecyclerViewFragment#newInstance(String)}
                     */
                    return CoworkerRecyclerViewFragment.newInstance(mEncodedEmail);

            }

            return null;
        }


        @Override
        public int getCount() {
            // Return the total number of tab(s)
            return 3;
        }


        /**
         * This will return the header label for each sliding tab
         *
         * @param position The position of the tab
         */
        @Override
        public CharSequence getPageTitle(int position) {

            // local final variables
            final int ZERO = 0;
            final int ONE = 1;
            final int TWO = 2;

            switch (position) {

                // Return a String for header label of of each sliding tab
                case ZERO:
                    return getString(R.string.title_sliding_tab_category);
                case ONE:
                    return getString(R.string.title_sliding_tab_item);
                case TWO:
                    return getString(R.string.title_sliding_tab_worker);

            }
            return null;
        }
    }
}
