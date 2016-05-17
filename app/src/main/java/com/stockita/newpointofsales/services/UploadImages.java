package com.stockita.newpointofsales.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.Utility;

import java.io.IOException;
import java.io.InputStream;

/**
 * This intent service will upload an image/images into the cloud in back ground thread
 */
public class UploadImages extends IntentService {

    /* Constants */
    private static String LOG = UploadImages.class.getSimpleName();



    /**
     * Default constructor
     */
    public UploadImages() {
        super("one");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        /* Status */
        boolean status = false;

        /* Get the cloudinary configured object */
        Cloudinary cloudinary = Utility.initCloudinary();

        /* Get the file path of an image from the intent */
        String filePath = intent.getStringExtra(Constant.KEY_IMAGE_FILE_PATH_TO_UPLOAD);

        /* Get the image name intent */
        String imageName = intent.getStringExtra(Constant.KEY_IMAGE_PUSH_KEY_TO_UPLOAD);

        try {

            /* Convert the file path into an InputStream object */
            InputStream inputStream = Utility.convertImageToInputStream(filePath);

            /* Pass the input stream object to the cloud, use the push key as file name */
            cloudinary.uploader().upload(inputStream, ObjectUtils.asMap(Constant.CLOUDINARY_PUBLIC_ID_KEY, imageName));
            cloudinary.url().generate(imageName);
            status = true;
        } catch (IOException e) {
            Log.e(LOG, e.toString());
            status = false;
        }

        /* Send the broadcast */
        Utility.broadcastStatus(getBaseContext(), status, "Somehow the image has failed to upload, try again", Constant.NOTIFY_CODE_NOTIFICATION_MANAGER);


    }




}
