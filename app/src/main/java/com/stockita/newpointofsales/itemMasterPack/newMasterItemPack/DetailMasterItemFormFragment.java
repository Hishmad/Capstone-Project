package com.stockita.newpointofsales.itemMasterPack.newMasterItemPack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.iid.InstanceID;
import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.data.ModelItemImageMaster;
import com.stockita.newpointofsales.data.ModelItemMaster;
import com.stockita.newpointofsales.fragment.PickerImageDialogFragment;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.Utility;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This is a fragment class that shows the detail of each item master
 */
public class DetailMasterItemFormFragment extends Fragment {


    // Constant
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_MODEL_LIST = "model_list";
    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_MODEL_KEY = "model_keys";
    private static final String ARG_ITEM_URL = "model_url";
    public static final int TAKE_PHOTO = 100;
    public static final int GET_FROM_GALLERY_REQUEST_CODE = 200;
    public static final int GET_FROM_DIALOG_FRAGMENT = 400;
    public final static String KEY_DIALOG_FRAGMENT_ITEM_NUMBER = "dialogFragmentKey";
    public final static String FRAGMENT_DIALOG_ITEM_MASTER = "fragment_dialog_item_master";

    // The views
    @Bind(R.id.coordinator)
    CoordinatorLayout mCoordinator;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.article_title)
    TextView mTitle;
    @Bind(R.id.article_subtitle)
    TextView mSubtitle;
    @Bind(R.id.currencyTextEdit)
    TextView mCurrencyTextEdit;
    @Bind(R.id.priceTextEdit)
    TextView mPriceTextEdit;
    @Bind(R.id.unitOfMeasureTextEdit)
    TextView mUnitOfMeasureTextEdit;
    @Bind(R.id.stockTextEdit)
    TextView mAvailableStock;
    @Bind(R.id.fragment_article_detail_recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.adView)
    AdView adView;

    /**
     * Tab's section id
     */
    private int mSelectionId;

    /**
     * Model for ItemMaster
     */
    private ModelItemMaster mModelItemMaster;

    /**
     * The ItemMaster model Firebase push() key
     */
    private String mModelItemMasterKey;

    /**
     * Reference to Firebase + ItemImage node
     */
    private Firebase mItemImageRef;

    /**
     * Reference to Firebase + itemMaster + push() key node.
     */
    private Firebase mItemMasterRef;

    /**
     * FirebaseUI RecyclerView adapter
     */
    private FirebaseRecyclerAdapter<ModelItemImageMaster, DetailMasterItemFormFragment.ViewHolders> mAdapter;

    /**
     * The device ID generated by the InstanceID API
     */
    protected String mIid;


    /**
     * Empty constructor
     */
    public DetailMasterItemFormFragment() {
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DetailMasterItemFormFragment newInstance(int sectionNumber, ModelItemMaster modelItemMaster, String itemImageRef, String modelKey, String itemMasterRef) {
        DetailMasterItemFormFragment fragment = new DetailMasterItemFormFragment();
        Bundle args = new Bundle();

        /**
         * The tab position that passed from {@link SectionsPageAdapter#getItem}
         */
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);

        /**
         * Model of type {@link ModelItemMaster}
         */
        args.putParcelable(ARG_MODEL_LIST, modelItemMaster);

        /**
         * A String to the node for itemImage
         */
        args.putString(ARG_IMAGE_URL, itemImageRef);

        /**
         * A String of Firebase push() key for each {@link ModelItemMaster}
         */
        args.putString(ARG_MODEL_KEY, modelKey);

        /**
         * A string to the node for itemMaster including push() key
         */
        args.putString(ARG_ITEM_URL, itemMasterRef);

        /* Set the argument */
        fragment.setArguments(args);

        /* Return the fragment */
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Set the menu to true */
        setHasOptionsMenu(true);

        /* Get data from the argument/bundle */
        mSelectionId = getArguments().getInt(ARG_SECTION_NUMBER);
        mModelItemMaster = getArguments().getParcelable(ARG_MODEL_LIST);
        String imageNode = getArguments().getString(ARG_IMAGE_URL);
        if (imageNode != null) {
            mItemImageRef = new Firebase(Constant.FIREBASE_URL + imageNode);
        }
        String itemNode = getArguments().getString(ARG_ITEM_URL);
        if (itemNode != null) {
            mItemMasterRef = new Firebase(Constant.FIREBASE_URL + itemNode);
        }
        mModelItemMasterKey = getArguments().getString(ARG_MODEL_KEY);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Initialize the view and inflate the layout xml */
        View rootView = inflater.inflate(R.layout.fragment_detail_master_item, container, false);

        /* Initialize the ButterKnife */
        ButterKnife.bind(this, rootView);

        /**
         *  This API {@link InstanceId} is available in {@link com.google.android.gms:play-services-gcm:8.4.0}
         *  We need this API to get the iid then pass it to the argument addTestDevice() method
         */
        mIid = InstanceID.getInstance(getActivity()).getId();

        /**
         * Display a banner, pass the mIid as argument,
         * it has to be outside the if statement below in order to
         * survive screen rotations
         */
        requestNewAdMob(mIid);

        /* Toolbar, if tablet then don't show toolbar */
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!tabletSize) {

            /* Get the support for toolbar */
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

            /* Toolbar navigation area back button arrow */
            mToolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }


        /* Item Number on images to UI */
        String lItemNumber = mModelItemMaster.getItemNumber();
        mCollapsingToolbarLayout.setTitle(lItemNumber);

        /* Item Description to UI */
        String itemDescription = mModelItemMaster.getItemDescription();
        mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ActionBarText);

        /* Title of the article */
        mTitle.setText(itemDescription);

        /* Subtitle which is the Category */
        String category = mModelItemMaster.getCategory();
        mSubtitle.setText(category);

        /* Currency */
        final String currency = mModelItemMaster.getCurrency();
        mCurrencyTextEdit.setText(currency);

        /* Price */
        final String price = String.valueOf(mModelItemMaster.getPrice());
        mPriceTextEdit.setText(price);

        /**
         * The user can edit the text here, then update the Firebase database
         */
        mPriceTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Instantiate the builder, set title and message */
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.alert_dialog_title_update_price);
                builder.setMessage(R.string.alert_dialog_message_update_price);

                /* Use an EditText view to get user input. */
                final EditText input = new EditText(getActivity());

                /* Set the keyboard specific for numeric */
                input.setInputType(InputType.TYPE_CLASS_NUMBER);

                /* Set the view for the builder */
                builder.setView(input);

                /**
                 * The positive and negative button
                 */
                builder.setPositiveButton(getString(R.string.alert_dialog_positive_button_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /* Get the string value from the edit text */
                        String value = input.getText().toString();

                        try {

                            /* convert to float */
                            float valueDouble = Float.parseFloat(value);

                            /* update the firebase database */
                            mItemMasterRef.child(Constant.FIREBASE_PROPERTY_ITEM_MASTER_PRICE).setValue(valueDouble);

                            /* update the model */
                            mModelItemMaster.setPrice(valueDouble);

                            /* update the UI */
                            mPriceTextEdit.setText(value);

                        } catch (Exception ignore) {

                        }

                    }
                }).setNegativeButton(getString(R.string.alert_dialog_negative_button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing
                    }
                });

                builder.show();

            }
        });

        /* Unit of measure */
        final String unitOfMeasure = mModelItemMaster.getUnitOfMeasure();
        mUnitOfMeasureTextEdit.setText(unitOfMeasure);

        /**
         * The user can edit the text here, then update the Firebase database
         */
        mUnitOfMeasureTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Instantiate the builder, set title and message */
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.alert_dialog_title_update_unit_of_measure);
                builder.setMessage(R.string.alert_dialog_message_unit_of_measure);

                /* Use an EditText view to get user input. */
                final EditText input = new EditText(getActivity());

                /* Set the view for the builder */
                builder.setView(input);

                /**
                 * The positive and negative button
                 */
                builder.setPositiveButton(getString(R.string.alert_dialog_positive_button_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /* Get the string value from the edit text */
                        String value = input.getText().toString();

                        /* update the firebase database */
                        mItemMasterRef.child(Constant.FIREBASE_PROPERTY_ITEM_MASTER_UOM).setValue(value);

                        /* update the model */
                        mModelItemMaster.setUnitOfMeasure(value);

                        /* update the UI */
                        mUnitOfMeasureTextEdit.setText(value);

                    }
                }).setNegativeButton(getString(R.string.alert_dialog_negative_button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing
                    }
                });

                builder.show();

            }
        });

        /* Available stock */
        String availableStock = String.valueOf(mModelItemMaster.getAvailableStock());
        mAvailableStock.setText(availableStock);

        /* these are the recycler view to display the images in horizontal orientation */
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        mAdapter =
                new FirebaseRecyclerAdapter<ModelItemImageMaster, ViewHolders>(ModelItemImageMaster.class, R.layout.item_master_form_image_adapter, DetailMasterItemFormFragment.ViewHolders.class, mItemImageRef.child(mModelItemMasterKey)) {

                    @Override
                    protected void populateViewHolder(final ViewHolders viewHolder, final ModelItemImageMaster model, int i) {

                        /* Download the image then pass it to the UI */
                        Glide.with(getActivity())
                                .load(Constant.CLOUDINARY_BASE_URL_DOWNLOAD + model.getImageName())
                                .into(viewHolder.itemMasterImage);
                        viewHolder.itemMasterImage.setBackgroundColor(Color.TRANSPARENT);


                        /**
                         * Long click to delete an image from Firebase as well
                         */
                        viewHolder.viewCard.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                /* Alert dialog for the user to click OK or Cancel */
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                alertDialog.setTitle(getString(R.string.alert_dialog_title_delete))
                                        .setMessage(getString(R.string.alert_dialog_message))
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.alert_dialog_positive_button_yes), new DialogInterface.OnClickListener() {

                                            // If the user click yes/oke...
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                /* Get the reference for this current position of the image */
                                                Firebase currentKey = getRef(viewHolder.getLayoutPosition());

                                                /* Remove this image from the cloud */
                                                Utility.deleteImagesFromCloud(getActivity(), model.getImageName());

                                                /* Remove this image from Firebase database*/
                                                currentKey.setValue(null);

                                            }
                                        }).setNegativeButton(getString(R.string.alert_dialog_negative_button_cancel), new DialogInterface.OnClickListener() {

                                    // If the user click cancel...
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing
                                    }
                                }).show();

                                return true;
                            }
                        });
                    }

                    @Override
                    public void onBindViewHolder(ViewHolders holder, int position, List<Object> payloads) {
                        super.onBindViewHolder(holder, position, payloads);

                        /* Animate the recycler view */
                        Utility.animate(getActivity(), holder, R.anim.bounce_interpolator);

                    }

                    @Override
                    public ViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

                        /* Initialize the view  */
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_master_form_image_adapter, parent, false);

                        /* Return the view holder object and pass the view as argument */
                        return new ViewHolders(view);


                    }
                };

        /* Set the adapter */
        recyclerView.setAdapter(mAdapter);

        // Return the view
        return rootView;

    }

    /**
     * Request the AdMob, and display the banner
     */
    private void requestNewAdMob(String iid) {

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(iid)
                .build();
        adView.loadAd(adRequest);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();

    }


    /**
     * FAB for the camera
     *
     * @param view The View object
     */
    @OnClick(R.id.fab)
    public void onClickFab(View view) {

        /* Dialog so the user can select image from gallery or take a photo shoot */
        FragmentManager fm = getActivity().getFragmentManager();
        PickerImageDialogFragment pickerImageDialogFragment = new PickerImageDialogFragment();
        pickerImageDialogFragment.setTargetFragment(this, GET_FROM_DIALOG_FRAGMENT);

        /* Call the Fragment Dialog to select gallery or photo */
        pickerImageDialogFragment.show(fm, FRAGMENT_DIALOG_ITEM_MASTER);

    }


    /**
     * Get result from camera/gallery and
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            /* In case data from photo capture */
            case TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {

                    /* The image Uri */
                    Uri selectedImage = data.getData();

                    /* Convert the image Uri into real file path */
                    String realFilePath = Utility.convertMediaUriToPath(selectedImage, getActivity());

                    /* Get the item number */
                    String itemNumber = mModelItemMaster.getItemNumber();

                    /* Make sure the image is available */
                    if (realFilePath != null) {

                        /* This is where the insert to image Firebase is done, we need the item number and the real file path */
                        String imageFileName = Utility.packAndInsertAnImageIntoDatabase(mItemImageRef, mModelItemMasterKey, realFilePath, itemNumber);

                        /* Upload the image to the cloud */
                        Utility.uploadImagesToCloud(getActivity(), realFilePath, imageFileName);
                    } else {
                        /* Notify user */
                        Utility.broadcastStatus(getActivity(), false, getActivity().getString(R.string.couldnt_find_the_image_file), Constant.NOTIFY_CODE_TOAST);
                    }

                } else {
                    /* Image capture failed, advise user */
                    Utility.broadcastStatus(getActivity(), false, getString(R.string.toast_image_capture_failed), Constant.NOTIFY_CODE_TOAST);
                }
                break;

            /* In case data from Gallery */
            case GET_FROM_GALLERY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {

                    /* Instantiate the ClipData */
                    ClipData clipData = data.getClipData();

                    if (clipData == null) {

                        /* The image Uri */
                        Uri selectedImage = data.getData();

                        /* Convert the image Uri into real file path */
                        String realFilePath = Utility.convertMediaUriToPath(selectedImage, getActivity());

                        /* Get the item number */
                        String itemNumber = mModelItemMaster.getItemNumber();

                        if (realFilePath != null) {

                            /* This is where the insert to image Firebase is done, we need the item number and the real file path */
                            String imageFileName = Utility.packAndInsertAnImageIntoDatabase(mItemImageRef, mModelItemMasterKey, realFilePath, itemNumber);

                            /* Upload the image to the cloud */
                            Utility.uploadImagesToCloud(getActivity(), realFilePath, imageFileName);
                        } else {
                            /* Notify user */
                            Utility.broadcastStatus(getActivity(), false, getString(R.string.couldnt_find_the_image_file), Constant.NOTIFY_CODE_TOAST);
                        }

                    } else {

                        /* Get the size of the ClipData, is the total images selected by the user. */
                        int dataSize = clipData.getItemCount();

                        /* Iterate through each image selected then insert them into the database */
                        for (int i = 0; i < dataSize; i++) {

                            /* Convert the image URI into real file path */
                            String realFilePath = Utility.convertMediaUriToPath(clipData.getItemAt(i).getUri(), getActivity());

                            /* Get the item number */
                            String itemNumber = mModelItemMaster.getItemNumber();

                            if (realFilePath != null) {

                                /* This is where the insert to image Firebase is done, we need the item number and the real file path */
                                String imageFileName = Utility.packAndInsertAnImageIntoDatabase(mItemImageRef, mModelItemMasterKey, realFilePath, itemNumber);

                                /* Upload the image to the cloud */
                                Utility.uploadImagesToCloud(getActivity(), realFilePath, imageFileName);
                            } else {
                                /* Notify user */
                                Utility.broadcastStatus(getActivity(), false, getString(R.string.couldnt_find_the_image_file), Constant.NOTIFY_CODE_TOAST);
                            }
                        }
                    }

                } else {
                    Utility.broadcastStatus(getActivity(), false, getString(R.string.toast_image_capture_failed), Constant.NOTIFY_CODE_TOAST);
                }
                break;

            /* Image picker */
            case GET_FROM_DIALOG_FRAGMENT:

                /* Default choice */
                final int DEFAULT_CHOICE = 1;

                /* Get the user choice from the dialog fragment */
                int choice = data.getIntExtra(KEY_DIALOG_FRAGMENT_ITEM_NUMBER, DEFAULT_CHOICE);

                /* choices */
                final int CHOICE_ONE = 1;
                final int CHOICE_TWO = 2;

                switch (choice) {
                    case CHOICE_ONE:
                        /* Get data from Gallery of images, use putExtra so the user can select multiple images. */
                        getImageFromGallery();
                        break;

                    case CHOICE_TWO:
                        /* Get data from the camera capture */
                        getImageFromCamera();
                        break;
                }
                break;
        }

    }


    /**
     * An intent to let the user get image or images from the gallery
     */
    private void getImageFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(i, GET_FROM_GALLERY_REQUEST_CODE);
    }


    /**
     * An intent to let the user take a picture from the camera
     */
    private void getImageFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, TAKE_PHOTO);
        }
    }


    /**
     * The view holder class for the images
     */
    public class ViewHolders extends RecyclerView.ViewHolder {


        /* Views */
        @Bind(R.id.viewCard)
        CardView viewCard;
        @Bind(R.id.itemMasterImage)
        ImageView itemMasterImage;


        /**
         * Constructor
         * @param itemView      View object
         */
        public ViewHolders(View itemView) {
            super(itemView);

            /* The ButterKnife */
            ButterKnife.bind(this, itemView);

        }
    }


}
