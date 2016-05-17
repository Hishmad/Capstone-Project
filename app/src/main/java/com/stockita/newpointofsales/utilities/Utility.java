package com.stockita.newpointofsales.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;


import com.cloudinary.Cloudinary;
import com.firebase.client.Firebase;
import com.stockita.newpointofsales.data.ModelItemImageMaster;
import com.stockita.newpointofsales.services.DeleteImages;
import com.stockita.newpointofsales.services.UploadImages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Helper call to store all the formula and algorithms
 */
public class Utility {


    /**
     * Helper method to hide the soft keyboard
     *
     * @param activity The activity context
     */
    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



    /**
     * Check if the network is available
     * @return          true if available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    /**
     * This method is special for BroadcastReceiver onReceive()
     * @param context       The activity context
     * @param intent        The data packed in the intent object
     */
    public static void reconnectInternet(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE"))
        {
            if (isNetworkAvailable(context))
            {
                // TODO: reconnect code
            }
        }

    }



    /**
     * Helper method to convert from URI to Real file path, as String.
     *
     * @param uri      File URI
     * @param activity Activity context
     */
    public static String convertMediaUriToPath(Uri uri, Context activity) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }



    /**
     * We can use this method to store any String into SharedPreferences.
     *
     * @param context Activity context
     * @param key     {@link SharedPreferences} Key
     * @param value   The value of String type that will be sore
     */
    public static void setAnyString(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(key, value).apply();
    }



    /**
     * We can use this method to store any boolean into SharePreferences.
     * @param context       Activity
     * @param key           {@link SharedPreferences} key
     * @param value         The value which is true/false
     */
    public static void setAnyBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(key, value).apply();
    }


    /**
     * We can use this method to store any int into SharePreferences.
     * @param context
     * @param key
     * @param value
     */
    public static void setAnyInt(Context context, String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putInt(key, value).apply();
    }



    /**
     * We can use this method to restore any String from SharedPReferences.
     *
     * @param context       Activity context
     * @param key           {@link SharedPreferences} Key
     * @param defaultString If no value then use this default value
     */
    public static String getAnyString(Context context, String key, String defaultString) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultString);
    }



    /**
     * We can use this method to restore any Boolean from SharedPreferences.
     *
     * @param context               Activity context
     * @param key                   {@link SharedPreferences} key
     * @param defaultBoolean        False or True
     */
    public static boolean getAnyBoolean(Context context, String key, boolean defaultBoolean) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, defaultBoolean);
    }



    /**
     * We can use this method to restore any int from SharedPreferences.
     * @param context
     * @param key
     * @param defaultInt
     * @return
     */
    public static int getAnyInt(Context context, String key, int defaultInt) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, defaultInt);
    }



    /**
     * Add the quantity to the current stock
     * @param quantity          The detail quantity
     * @param currentStock      The current stock from the item master entry
     * @return                  new available stock
     */
    public static double calculateAddToStock(double quantity, double currentStock) {

        return currentStock + quantity;
    }


    /**
     * Subtract the quantity from the current stock
     * @param quantity          The detail quantity
     * @param currentStock      The current stock from the item master entry
     * @return                  new available stock
     */
    public static double calculateSubtractFromStock(double quantity,  double currentStock) {

        return currentStock - quantity;
    }



    /**
     * Encode user email to use it as a Firebase key (Firebase dows not allow) "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".",",");
    }


    /**
     * Decode user email from firebase format to normal format.
     */
    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",",".");
    }



    /**
     * This method is used for checking valid email id format.
     *
     * @param email         The user input
     * @return boolean      True for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression =  "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    /**
     * This method is to give an interpolator anim to the recycler view
     */
    public static void animate(Context context, RecyclerView.ViewHolder viewHolder, int animResource) {
        final Animation animation = AnimationUtils.loadAnimation(context, animResource);
        viewHolder.itemView.setAnimation(animation);
    }


    /**
     * Pack then insert an image into Firebase
     */
    public static String packAndInsertAnImageIntoDatabase(Firebase imageNode, String itemMasterPushKey ,String realFilePath, String itemNumber) {

        /* Instantiate a POJO */
        ModelItemImageMaster model = new ModelItemImageMaster();

        /**
         * Pack the data into a POJO then pass them to {@link Firebase} node
         * we need to pass two thing the item number and the image url
         */
        model.setItemNumber(itemNumber);
        model.setImageUrl(realFilePath);

        /**
         * Create a reference for the new push() id for this node
         */
        Firebase imagePush = imageNode.child(itemMasterPushKey).push();

        /**
         * This is the new file name for this image
         * Once we already have the push() id we will use it as the new file name so
         * later we can use it to upload images to the cloud
         */
        model.setImageName(imagePush.getKey());

        /**
         * Set the value
         */
        imagePush.setValue(model);

        /**
         * Return the new file name for this image
         */
        return model.getImageName();

    }


    /**
     * This helper method is to convert a string file path to an InputStream object
     */
    public static InputStream convertImageToInputStream(String fileUrl) throws IOException {

        File file = new File(fileUrl);

        return new FileInputStream(file);

    }


    /**
     * Initialize the Cloudinary object with the standard configurations
     */
    public static Cloudinary initCloudinary() {

        /* Initialize the map */
        Map<String, String> config = new HashMap<>();

        /* put the configurations  */
        config.put(Constant.CLOUDINARY_COULD_NAME_KEY, Constant.CLOUDINARY_CLOUD_NAME_VALUE);
        config.put(Constant.CLOUDINARY_API_KEY_KEY, Constant.CLOUDINARY_API_KEY_VALUE);
        config.put(Constant.CLOUDINARY_API_SECRET_KEY, Constant.CLOUDINARY_API_SECRET_VALUE);

        /* return cloudinary object, assign the config as argument */
        return new Cloudinary(config);

    }


    /**
     * Method to upload images to the cloud
     * using intent service
     */
    public static void uploadImagesToCloud(Context context, String realFilePath, String imageName) {
        Intent imageIntent = new Intent(context, UploadImages.class);
        imageIntent.putExtra(Constant.KEY_IMAGE_FILE_PATH_TO_UPLOAD, realFilePath);
        imageIntent.putExtra(Constant.KEY_IMAGE_PUSH_KEY_TO_UPLOAD, imageName);
        context.startService(imageIntent);

    }

    /**
     * Method to remove images from the cloud
     * using intent service
     */
    public static void deleteImagesFromCloud(Context context, String imageName) {
        Intent intent = new Intent(context, DeleteImages.class);
        intent.putExtra(Constant.KEY_IMAGE_PUSH_KEY_TO_UPLOAD, imageName);
        context.startService(intent);

    }


    /**
     * Check if this String is numeric
     *
     * @param input         The string to check if it is numeric
     * @return              true if numeric
     */
    public static boolean isThisStringNumeric(String input) {

        /* Remove the white spaces */
        String removeAllWhiteSpaces = input.replaceAll("\\s", "");

        try {
            /* Parse to double if no error then it is true */
            double parse = Double.parseDouble(removeAllWhiteSpaces);
        } catch (Exception e) {

            /* If error return false */
            return false;
        }

        /* If no error return true */
        return true;

    }


    /**
     * This method is to broadcast a local message
     */
    public static void broadcastStatus(Context context, boolean status, String message, int notifyCode) {
        Intent intent =
                new Intent()
                        .setAction(Constant.BROADCAST_ACTION_SEND_NOTIFICATION)
                        .putExtra(Constant.KEY_NOTIFY_MESSAGE, message)
                        .putExtra(Constant.KEY_NOTIFY_BOOLEAN_STATUS, status)
                        .putExtra(Constant.KEY_NOTIFY_CODE, notifyCode);
        /* Broadcasts the Intent to receivers in this app. */
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


    }


    /**
     * This method will return the key notify code for the broadcast receiver
     */
    public static Intent notifyCode(Intent intent, int code) {

        return intent.putExtra(Constant.KEY_NOTIFY_CODE, code);
    }


    /**
     * Share with other, implicit intent
     * @param context
     * @param data
     */
    public static void shareData(Context context, String[] data) {

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        String title = data[0];
        String message = data[1];
        String tag = data[2];

        share.putExtra(Intent.EXTRA_SUBJECT, title);
        share.putExtra(Intent.EXTRA_TEXT, message);

        context.startActivity(Intent.createChooser(share, tag));
    }


}
