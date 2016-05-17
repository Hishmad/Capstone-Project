package com.stockita.newpointofsales.services;

import android.app.IntentService;
import android.content.Intent;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.Utility;

import java.io.IOException;

/**
 * Created by hishmadabubakaralamudi on 5/2/16.
 */
public class DeleteImages extends IntentService {

    /**
     * Constructor
     */
    public DeleteImages() {
        super("two");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        /* Get the image url from the intent */
        String fileName = intent.getStringExtra(Constant.KEY_IMAGE_PUSH_KEY_TO_UPLOAD);

        /* Initialize the cloudinary object */
        Cloudinary cloudinary = Utility.initCloudinary();

        /* Delete this image from cloudinary database */
        try {
            cloudinary.uploader().destroy(fileName, ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
