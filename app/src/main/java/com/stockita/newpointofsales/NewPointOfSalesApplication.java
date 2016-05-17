package com.stockita.newpointofsales;

import com.firebase.client.Firebase;

/**
 * This is the application class, the purpose of this is to initialize the
 * Firebase for online and offline.
 */
public class NewPointOfSalesApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();


        // Initialize Firebase
        Firebase.setAndroidContext(this);

        /* Enable disk persistence, when offline */
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}
