package com.stockita.newpointofsales.itemMasterPack.newMasterItemPack;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.activities.BaseActivity;
import com.stockita.newpointofsales.data.ModelItemMaster;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.InOutTransformation;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This class is the activity to show the detail fragment of each item master
 * it is also has a view pager so the the user can swipe between detail screen
 */
public class DetailMasterItemFormActivity extends BaseActivity {

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
    @Bind(R.id.root)
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_detail_item_master);

        // ButterKnife
        ButterKnife.bind(this);

        // Get the data model from the intent
        Intent intent = getIntent();

        /* This is the list of all model of type ModelItemMaster */
        ArrayList<ModelItemMaster> lListModelItemMaster = intent.getParcelableArrayListExtra(Constant.KEY_ITEM_MASTER_POJO_LIST);

        /* The list as container for the push() keys for each element in mListModelItemMaster */
        ArrayList<String> lListModelItemMasterKeys = intent.getStringArrayListExtra(Constant.KEY_ITEM_MASTER_KEY_LIST);

        /* The Firebase reference to the itemMaster node including the push() key */
        String firebaseItemMasterNode = intent.getStringExtra(Constant.KEY_ITEM_MASTER_NODE);

        /* The Firebase reference to the ItemImage node */
        String firebaseItemImageNode =  intent.getStringExtra(Constant.KEY_ITEM_IMAGE_MASTER_NODE);

        /* The boolean status for the ownership status */
        boolean isHeWorker = intent.getBooleanExtra(Constant.KEY_BOOLEAN_STATUS, false);

        /* The current position of this item in the list */
        int lCurrentItemPosition = intent.getIntExtra(Constant.KEY_CURRENT_POSITION, 0);

        // Pass the list to the adapter
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), lListModelItemMaster, firebaseItemImageNode, lListModelItemMasterKeys, firebaseItemMasterNode);

        // Set up the view pager adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set the current page position if any.
        mViewPager.setCurrentItem(lCurrentItemPosition);

        // Set transformer when the user swipe between pagers
        mViewPager.setPageTransformer(true, new InOutTransformation());

        // This is important to avoid crash
        mSectionsPagerAdapter.notifyDataSetChanged();

    }



    /**
     * Adapter class for the Pager
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        /* Field */
        private ArrayList<ModelItemMaster> mModel;
        private ArrayList<String> mModelKeys;
        private String mItemImageNode;
        private String mItemMasterNode;


        /**
         * Constructor
         */
        public SectionsPagerAdapter(FragmentManager fm, ArrayList<ModelItemMaster> modelList, String itemImageNode, ArrayList<String> modelListKeys, String itemMasterNode) {
            super(fm);

            this.mModel = modelList;
            this.mItemImageNode = itemImageNode;
            this.mModelKeys = modelListKeys;
            this.mItemMasterNode = itemMasterNode;

        }

        @Override
        public Fragment getItem(int position) {


            return DetailMasterItemFormFragment.newInstance(position, mModel.get(position), mItemImageNode, mModelKeys.get(position), mItemMasterNode);

        }

        @Override
        public int getCount() {
            return mModel != null ? mModel.size() : 0;

        }
    }

}
