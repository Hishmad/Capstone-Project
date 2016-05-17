package com.stockita.newpointofsales.itemMasterPack.newMasterItemPack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.barcodeScanner.BarcodeScanner;
import com.stockita.newpointofsales.data.ModelItemImageMaster;
import com.stockita.newpointofsales.data.ModelItemMaster;
import com.stockita.newpointofsales.fragment.PickerImageDialogFragment;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.Utility;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This class is to add new item into the inventory with images
 */
public class AddNewItemMasterFormFragment extends Fragment {

    // View
    @Bind(R.id.fab_camera)
    FloatingActionButton mFab;

    @Bind(R.id.itemMasterNewList)
    RecyclerView mItemMasterNewList;

    @Bind(R.id.item_number_input)
    EditText itemNumber;

    @Bind(R.id.item_desc_input)
    EditText itemDescription;

    @Bind(R.id.unit_of_measure_edit)
    EditText unitOfMeasure;

    @Bind(R.id.spinner_category)
    Spinner spinnerCategory;

    @Bind(R.id.currency_input)
    EditText itemCurrency;

    @Bind(R.id.item_price_input)
    EditText itemPrice;

    @Bind(R.id.bar_code_scanner_item_master_form)
    ImageButton imageButtonScan;


    /* Constant Keys */
    public static final String KEY_ONE = "one";
    public static final int TAKE_PHOTO = 100;
    public static final int GET_FROM_GALLERY_REQUEST_CODE = 200;
    public static final int GET_FROM_BARCODE_SCANNER = 300;
    public static final int GET_FROM_DIALOG_FRAGMENT = 400;
    public static final String KEY_DIALOG_FRAGMENT_ITEM_NUMBER = "dialogFragmentKey";
    public static final String KEY_DIALOG_FRGMENT_PICKER_IMAGE = "fragment_dialog_picker_image";


    /**
     * This is the adapter instance, to populate the image into recycler view
     */
    private ImageAdapter mItemMasterFormImageAdapter;


    /**
     * This is a temporary container to pack the images that selected by the user
     */
    private ArrayList<ModelItemImageMaster> mModelItemImageMasterArrayList;


    /**
     * The current encoded email, this is the user login email encoded according
     * to {@link Firebase} spec.
     */
    private String mEncodedEmail;


    /**
     * Empty constructor
     */
    public AddNewItemMasterFormFragment() {

    }


    /**
     * Create a basic bundle to get data from the activity
     *
     * @param encodedEmail              The user email encoded
     * @return                          This fragment object
     */
    public static AddNewItemMasterFormFragment newInstance(String encodedEmail) {

        /* Instantiate the fragment */
        AddNewItemMasterFormFragment itemMasterFormFragment = new AddNewItemMasterFormFragment();

        /* Instantiate the Bundle */
        Bundle bundle = new Bundle();

        /* Put the data into the Bundle */
        bundle.putString(Constant.KEY_ENCODED_EMAIL, encodedEmail);

        /* Set the Bundle into the fragment using method setArgument() */
        itemMasterFormFragment.setArguments(bundle);

        /* Return the fragment instance */
        return itemMasterFormFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        /* Get the encoded email from the bundle */
        mEncodedEmail = getArguments().getString(Constant.KEY_ENCODED_EMAIL);
        if (mEncodedEmail == null) getActivity().finish();

        /* Initialize the Adapter for Images recycler view (Nesting) */
        mItemMasterFormImageAdapter = new ImageAdapter(getActivity());

        /* Initialize the ArrayList for the ModelItemImage */
        mModelItemImageMasterArrayList = new ArrayList<>();

        /* Restore variables value after screen rotation */
        if (savedInstanceState != null) {
            mModelItemImageMasterArrayList = savedInstanceState.getParcelableArrayList(KEY_ONE);

            /* Send the image list again to the adapter on screen rotation */
            mItemMasterFormImageAdapter.swapData(mModelItemImageMasterArrayList);

        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_ONE, mModelItemImageMasterArrayList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_category_input_form, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:

                /* Save to Firebase database */
                saveToFirebaseDatabase();

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Initialize the view object */
        View rootView = inflater.inflate(R.layout.item_master_form_fragment, container, false);

        /**
         * {@link ButterKnife}
         */
        ButterKnife.bind(this, rootView);

        /* Get and populate the spinner */
        getCategoryFromFirebaseDatabaseIntoSpinner(mEncodedEmail);

        /* Currency, retrieve the value from the setting preference */
        final String currencyPreference =
                Utility.getAnyString(getActivity(),
                        getResources().getString(R.string.key_pref_currency),
                        getActivity().getString(R.string.default_currency));
        itemCurrency.setText(currencyPreference);

        /**
         *  *** RecyclerView below for images ***
         */

        // Initialize layoutManager
        StaggeredGridLayoutManager lStaggeredGridLayoutManagerCredits =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);

        // Set the layout manager
        mItemMasterNewList.setLayoutManager(lStaggeredGridLayoutManagerCredits);

        // Set the adapter.
        mItemMasterNewList.setAdapter(mItemMasterFormImageAdapter);

        // Set the long click listener
        mItemMasterFormImageAdapter.setOnLongItemClickListener(onLongItemClickListener);

        /**
         * This button will get us to the barcode scanner
         */
        imageButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startScanner = new Intent(getActivity(), BarcodeScanner.class);
                startActivityForResult(startScanner, GET_FROM_BARCODE_SCANNER);
            }
        });

        // Return the view
        return rootView;

    }


    @OnClick(R.id.fab_camera)
    public void setFabCamer(View view) {

        // Dialog so the user can select image from gallery or take a photo shoot
        final FragmentManager fm = getActivity().getFragmentManager();
        final PickerImageDialogFragment pickerImageDialogFragment = new PickerImageDialogFragment();
        pickerImageDialogFragment.setTargetFragment(this, GET_FROM_DIALOG_FRAGMENT);

        // Check if itemNumber is not empty.
        if (itemNumber.length() > 0 && itemDescription.length() > 0) {

            /**
             * Call the {@link PickerImageDialogFragment} to select gallery or photo
             */
            pickerImageDialogFragment.show(fm, KEY_DIALOG_FRGMENT_PICKER_IMAGE);

        } else {
            Toast.makeText(getActivity(), R.string.warning_field_cant_be_empty, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * When the user long click on any image it will alert for delete.
     */
    public AddNewItemMasterFormFragment.ImageAdapter.OnLongItemClickListener onLongItemClickListener =
            new ImageAdapter.OnLongItemClickListener() {
                @Override
                public void onLongItemClick(View view, final int position, int imageId, final String itemNumber) {

                    // Alert dialog for the user to click OK or Cancel
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle(R.string.alert_dialog_title_delete)
                            .setMessage(R.string.alert_dialog_message)
                            .setCancelable(false)
                            .setPositiveButton(R.string.alert_dialog_positive_button_yes, new DialogInterface.OnClickListener() {

                                // If the user click yes/oke...
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    /*
                                    This process will delete an image from the list, because
                                    the images has not yet saved into the database.
                                    In this case we will only remove the element from the list
                                    then update the adapter.
                                    */

                                    // Remove the element from the list
                                    mModelItemImageMasterArrayList.remove(position);

                                    // Update the data in the adapter
                                    mItemMasterFormImageAdapter.swapData(mModelItemImageMasterArrayList);


                                }
                            }).setNegativeButton(R.string.alert_dialog_negative_button_cancel, new DialogInterface.OnClickListener() {

                        // If the user click cancel...
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                        }
                    }).show();

                }
            };


    /**
     * Get the result from intent, Camera, Gallery, Barcode
     *
     * @param requestCode The code for startActivityForResult
     * @param resultCode  The result code return by the resultActivity
     * @param data        The data bundle/extra sent from the result
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            // in case of data from camera
            case TAKE_PHOTO:

                if (resultCode == Activity.RESULT_OK) {

                    // The image Uri
                    Uri selectedImage = data.getData();

                    // Convert the image Uri into real file path
                    String realFilePath = Utility.convertMediaUriToPath(selectedImage, getActivity());

                    // Insert the images into the list
                    // Add the element to the ArrayList
                    mModelItemImageMasterArrayList.add(packTheImageIntoModel(realFilePath, itemNumber.getText().toString()));

                    // Pass the array list of images to the adapter for display.
                    mItemMasterFormImageAdapter.swapData(mModelItemImageMasterArrayList);

                } else {
                    // Image capture failed, advise user
                    Toast.makeText(getActivity(), R.string.toast_image_capture_failed, Toast.LENGTH_SHORT).show();
                }
                break;

            // In case data from Gallery
            case GET_FROM_GALLERY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {

                    // Instantiate the ClipData
                    ClipData clipData = data.getClipData();

                    if (clipData == null) {

                        // The image Uri
                        Uri selectedImage = data.getData();

                        // Convert the image Uri into real file path
                        String realFilePath = Utility.convertMediaUriToPath(selectedImage, getActivity());

                        // Insert the images into the list
                        // Each image model will be pack in to an array list.
                        mModelItemImageMasterArrayList.add(packTheImageIntoModel(realFilePath, itemNumber.getText().toString()));

                    } else {

                        // Get the size of the ClipData, is the total images selected by the user.
                        int totalImagesSelectedByTheUser = clipData.getItemCount();

                        // Iterate through each images selected then insert them into the ArrayList
                        for (int i = 0; i < totalImagesSelectedByTheUser; i++) {

                            // Convert the image URI into real file path
                            String realFilePath = Utility.convertMediaUriToPath(clipData.getItemAt(i).getUri(), getActivity());

                            // Insert the images into the list
                            // Each image model will be pack into an array list.
                            mModelItemImageMasterArrayList.add(packTheImageIntoModel(realFilePath, itemNumber.getText().toString()));
                        }
                    }

                    // Pass the array list of images to the adapter for display.
                    mItemMasterFormImageAdapter.swapData(mModelItemImageMasterArrayList);

                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.toast_image_capture_failed), Toast.LENGTH_SHORT).show();
                }
                break;

            // Barcode scanner
            case GET_FROM_BARCODE_SCANNER:
                if (resultCode == Activity.RESULT_OK) {
                    String lScanContents = data.getStringExtra(BarcodeScanner.SCAN_CONTENTS);
                    itemNumber.setText(lScanContents);

                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.toast_image_capture_failed), Toast.LENGTH_SHORT).show();
                }
                break;

            // Image picker dialog fragment
            case GET_FROM_DIALOG_FRAGMENT:

                // Default choice
                final int DEFAULT_CHOICE = 1;

                // Get the user choice from the dialog fragment
                int choice = data.getIntExtra(KEY_DIALOG_FRAGMENT_ITEM_NUMBER, DEFAULT_CHOICE);

                // Choices
                final int CHOICE_ONE = 1;
                final int CHOICE_TWO = 2;

                switch (choice) {
                    case CHOICE_ONE:
                        // Get data from Gallery of images, use putExtra so the user can select multiple images.
                        getImageFromGallery();
                        break;

                    case CHOICE_TWO:
                        // Get data from the camera capture
                        getImageFromCamera();
                        break;
                }
        }
    }


    /**
     * This helper method will save the newly data into {@link Firebase} database
     */
    private void saveToFirebaseDatabase() {

        /**
         * Check if item number is not empty
         */
        if (itemNumber.length() > 0 && itemDescription.length() > 0) {

            /* Instantiate the Model */
            ModelItemMaster lModelItemMaster = new ModelItemMaster();

            /* This item number will be used for both ItemMaster node and Item Image node */
            String lItemNumber = itemNumber.getText().toString();

            /* Get input from the UI and pack them into the object POJO */
            lModelItemMaster.setItemNumber(lItemNumber);
            lModelItemMaster.setItemDescription(itemDescription.getText().toString());
            lModelItemMaster.setUnitOfMeasure(unitOfMeasure.getText().toString());
            lModelItemMaster.setCategory(spinnerCategory.getSelectedItem().toString());
            lModelItemMaster.setCurrency(itemCurrency.getText().toString());
            lModelItemMaster.setAvailableStock(0);

            /* Price,  this will put 0 if the user left the price empty. */
            if (itemPrice.length() > 0) {
                lModelItemMaster.setPrice(Float.parseFloat(itemPrice.getText().toString()));
            } else {
                lModelItemMaster.setPrice(0);
            }

            /**
             * {@link Firebase} node to /mEncodedEmail/itemMaster
             */
            Firebase itemMasterRef = new Firebase(Constant.FIREBASE_URL)
                    .child(mEncodedEmail)
                    .child(Constant.FIREBASE_LOCATION_ITEM_MASTER);

            /**
             * Use the method {@link Firebase#push()} so a unique number will be generated.
             * Then set pass the model POJO to the setValue() method.
             */
            Firebase itemMasterPush = itemMasterRef.push();
            itemMasterPush.setValue(lModelItemMaster);

            /* Get the push() key of the itemMaster so we can use it to store the images */
            String itemMasterPushKey = itemMasterPush.getKey();

            /**
             *  Get the {@link Firebase} reference for the item image master
             */
            Firebase itemImageMasterRef = new Firebase(Constant.FIREBASE_URL)
                    .child(mEncodedEmail)
                    .child(Constant.FIREBASE_LOCATION_ITEM_IMAGE);

            /* Get the size of the list, which is the number of images */
            int listSize = mModelItemImageMasterArrayList.size();

            /**
             * Iterate through each of the image then store them into Firebase database
             * then after we get the push() key from Firebase for each image, we will use that
             * push() key as the new image file name .jpg, that we will use it to upload the image
             * to cloud using that push() key as name .jpg
             */
            for (int i = 0; i < listSize; i++) {

                /* Get the real file path of each image from the list */
                String filePath = mModelItemImageMasterArrayList.get(i).getImageUrl();

                /**
                 *  This is where the insert to image Firebase is done, we need the item number and the real file path
                 *  then it will return the new image file name so we will use it to upload image to the cloud */
                String imageName = Utility.packAndInsertAnImageIntoDatabase(itemImageMasterRef, itemMasterPushKey, filePath, lItemNumber);

                /**
                 * After saving the data to Firebase database, now upload the image to the cloud.
                 */
                Utility.uploadImagesToCloud(getActivity(), filePath, imageName);



            }

            /**
             * When save has done, then empty all the form, like new form.
             */
            itemNumber.setText("");
            itemDescription.setText("");
            unitOfMeasure.setText("");
            itemCurrency.setText("");
            itemPrice.setText("");

            /* Empty the Item Image adapter */
            mItemMasterFormImageAdapter.swapData(null);

            /* Hide the keyboard */
            Utility.hideKeyboard(getActivity());

        }
    }


    /**
     * This helper method will get the category from the {@link }Firebase} database,  then populate
     * the {@link Spinner} with them.
     * @param encodedEmail          The current user/boss encoded email, this is the node
     */
    private void getCategoryFromFirebaseDatabaseIntoSpinner(String encodedEmail) {

        /* Get the Firebase node, then invoke SingleValueEvent listener */
        Firebase categoryRef = new Firebase(Constant.FIREBASE_URL).child(encodedEmail).child(Constant.FIREBASE_LOCATION_CATEGORY);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /* The number of the children */
                long childrenSize = dataSnapshot.getChildrenCount();

                /* Instantiate an ArrayList */
                ArrayList<String> theCategory = new ArrayList<>((int) childrenSize);

                /* Iterate through DataSnapshot to get the keys, which is the category */
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    /* Add them to a list */
                    theCategory.add(postSnapshot.getKey());
                }

                /**
                 * If the list is empty, which mean no category been added by the user
                 * the return the value "none"
                 */
                if (childrenSize == 0) {
                    theCategory.add("none");
                }

                /* Initialize the ArrayAdapter */
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_category, theCategory);

                /* Invoke the resource/layout xml */
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                /* Set the adapter into the spinner view object */
                spinnerCategory.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // Later
            }
        });

    }


    /**
     * This method will pack the image into a POJO
     * with out the file name, because file name will be generated
     * later once we got the push() key from firebase database
     * @return      {@link ModelItemImageMaster}
     */
    private ModelItemImageMaster packTheImageIntoModel(String realFilePath, String itemNumber) {

        /* Instantiate a model to hold the data/image one by one */
        ModelItemImageMaster model = new ModelItemImageMaster();

        /* Set the data into the object. */
        model.setItemNumber(itemNumber);
        model.setImageUrl(realFilePath);

        return model;
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
     * Adapter to populate the images into the {@link AddNewItemMasterFormFragment}
     */
    public static class ImageAdapter extends RecyclerView.Adapter<AddNewItemMasterFormFragment.ImageAdapter.ViewHolder> {

        /**
         * Context activity
         */
        private Context mContextAdapter;

        /**
         * The data set that will populate the list
         */
        private ArrayList<ModelItemImageMaster> mModelItemImageArrayListAdapter;

        /**
         * The long lick object
         */
        private OnLongItemClickListener mOnLongItemClickListenerAdapter;


        /**
         * Constructor
         * @param context       Activity context
         */
        public ImageAdapter(Context context) {
            this.mContextAdapter = context;
        }

        /**
         * Get the data from the fragment
         * @param data          an Arraylist of type {@link ModelItemImageMaster}
         */
        public void swapData(ArrayList<ModelItemImageMaster> data) {
            this.mModelItemImageArrayListAdapter = data;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            /* The XML layout to populate the recycler view */
            return R.layout.item_master_form_image_adapter;

        }

        @Override
        public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            /* Initialize the view */
            View mainView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

            /* Return the ViewHolder */
            return new ViewHolder(mainView);
        }

        @Override
        public void onBindViewHolder(final ImageAdapter.ViewHolder holder, int position) {

            /* Get the data from an ArrayList and populate the RecyclerView */
            if (mModelItemImageArrayListAdapter != null) {

                /* Pass the itemNumber String into the viewHolder */
                holder.h_ItemNumber = mModelItemImageArrayListAdapter.get(position).getItemNumber();

                /* The image URL (file path) */
                String imageUrl = mModelItemImageArrayListAdapter.get(position).getImageUrl();

                /* Pass the imageUrl String into the viewHolder */
                holder.setImageView(mContextAdapter, imageUrl);


                /**
                 * Single click listener, but now it does nothing, just a toast.
                 */
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(mContextAdapter,  holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return mModelItemImageArrayListAdapter != null ? mModelItemImageArrayListAdapter.size() : 0;
        }


        /**
         * The long click to delete
         * @param itemClickListener         object
         */
        public void setOnLongItemClickListener(final OnLongItemClickListener itemClickListener) {
            mOnLongItemClickListenerAdapter = itemClickListener;
        }

        /**
         * The long click interface
         */
        public interface OnLongItemClickListener {

            /**
             *
             * @param view      the {@link View} object
             * @param position  the current position of the list
             * @param imageId   the _ID database
             */
            void onLongItemClick(View view, int position, int imageId, String itemNumber);
        }


        /**
         * The view holder class
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {


            /* Views */
            @Bind(R.id.viewCard)
            CardView cardView;
            @Bind(R.id.itemMasterImage)
            ImageView imageView;

            private int h_id;
            private String h_ItemNumber;



            public ViewHolder(View itemView) {
                super(itemView);

                /* Initialize the views */
                ButterKnife.bind(this, itemView);

                /* Set the click */
                if (cardView != null) {
                    cardView.setOnLongClickListener(this);
                }
            }


            /**
             * Set the Item Image url
             * @param context       Activity context
             * @param url           The file URL
             */
            public void setImageView(Context context, String url) {
                if (imageView != null) {
                    Glide.with(context)
                            .load(url)
                            .centerCrop()
                            .into(imageView);
                    imageView.setBackgroundColor(Color.TRANSPARENT);
                }
            }

            /**
             * Long click
             * @param v     {@link View} object
             * @return      must return true
             */
            @Override
            public boolean onLongClick(View v) {
                mOnLongItemClickListenerAdapter.onLongItemClick(v, getLayoutPosition(), h_id, h_ItemNumber);
                return true;
            }
        }
    }

}
