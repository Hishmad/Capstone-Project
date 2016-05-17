package com.stockita.newpointofsales.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.iid.InstanceID;
import com.stockita.newpointofsales.R;
import com.stockita.newpointofsales.data.ContractData;
import com.stockita.newpointofsales.data.ModelCoworkers;
import com.stockita.newpointofsales.login.LoginActivity;
import com.stockita.newpointofsales.utilities.Constant;
import com.stockita.newpointofsales.utilities.Utility;

import java.util.Objects;

/**
 * Base Activity the most of the Activities will inherit from.
 * It implements GoogleApiClient callbacks to enable "Logout" in all activities
 * and defines variables that are being shared across all activities.
 */
public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    /* Constant */
    private static final int PLAY_SERVICES_REQUEST = 9999;
    private static final String LOG_TAG = BaseActivity.class.getSimpleName();

    /* Encoded email, is the the login email but encoded into Firebase spec  */
    protected String mEncodedEmail;

    /* User name */
    protected String mTheUserNameOfThisGadget;

    /* Client used to interact with Google APIs */
    protected GoogleApiClient mGoogleApiClient;
    protected Firebase.AuthStateListener mAuthListener;

    /* Firebase references to root node */
    protected Firebase mFirebaseRef;

    /* For /users/ */
    protected Firebase mUsersRef;
    protected ValueEventListener mQueryIfThisBossIsUserListener;

    /* For /userWorkers/bossEmail/workerEmail */
    protected Firebase mUserWorkersRef;
    protected ValueEventListener mUserWorkersRefListener;

    /* Check if GooglePlayServices API is up to date */
    protected boolean isGooglePlayServices;

    /**
     * This boolean is just a trigger that if the user had been to the setting
     * it will return true.
     */
    protected boolean goesToTheSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Setup the Google API object to allow Google login */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        /**
         * Build a GoogleApiClient with access to the Google Sign-In API and the
         * options specified by gso.
         */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /**
         * Get mEncodedEmail from SharedPreferences, use null as default value.
         */
        mEncodedEmail = Utility.getAnyString(this, Constant.KEY_ENCODED_EMAIL, null);
        mTheUserNameOfThisGadget = Utility.getAnyString(this, Constant.KEY_THIS_USER_NAME, null);

        if (!(this instanceof LoginActivity)) {
            mFirebaseRef = new Firebase(Constant.FIREBASE_URL);
            mAuthListener = mFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {

                    /* The user has been logged out */
                    if (authData == null) {

                        /* Clear out sharedPreferences */
                        Utility.setAnyString(getBaseContext(), Constant.KEY_ENCODED_EMAIL, null);

                        /* Go to login page */
                        takeUserLoginScreenOnUnAuth();
                    }
                }
            });
        }

        /* Check if this application is start for a very first time */
        boolean startupTheFirstTime = Utility.getAnyBoolean(this, Constant.KEY_STARTUP_FIRSTTIME, false);

        /* If this app is start at a very fist time, we need to make database record _id 1 */
        if (!startupTheFirstTime) {

            /** Set the boolean to true if this is was the first time using this app,
             * so later no need to run this code again
             */
            Utility.setAnyBoolean(this, Constant.KEY_STARTUP_FIRSTTIME, true);

            /**
             * Below code is to create a local database record...
             */

            /* Initialize the ContentResolver */
            ContentResolver contentResolver = getContentResolver();

            /* Initialize the ContentValues */
            ContentValues contentValues = new ContentValues();

            /* Pack the data into contentValues object */
            contentValues.put(ContractData.EncodedEmailEntry.COLUMN_ENCODED_EMAIL, Constant.VALUE_JOB_STATUS);
            contentValues.put(ContractData.EncodedEmailEntry.COLUMN_BOOLEAN_STATUS, Constant.VALUE_BOOLEAN_STATUS);

            /* Insert to the database table */
            try {
                contentResolver.insert(ContractData.EncodedEmailEntry.CONTENT_URI, contentValues);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
            }

        }

    }


    /**
     * Remove Firebase listeners if any.
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();
         /* Cleanup the AuthStateListener */
        if (!(this instanceof LoginActivity)) {
            mFirebaseRef.removeAuthStateListener(mAuthListener);
        }

        /* For /users/ */
        if (mUsersRef != null && mQueryIfThisBossIsUserListener != null) {
            mUsersRef.removeEventListener(mQueryIfThisBossIsUserListener);
        }

        /* For /userWorkers/... */
        if (mUserWorkersRef != null && mUserWorkersRefListener != null) {
            mUserWorkersRef.removeEventListener(mUserWorkersRefListener);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        /* Unregister the Broadcast Receiver */
        LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(mReceiver);

    }


    @Override
    protected void onResume() {
        super.onResume();

        /* Register the Broadcast Receiver */
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(mReceiver, new IntentFilter(Constant.BROADCAST_ACTION_SEND_NOTIFICATION));


        /**
         * Check if up to date Google Play Services API
         */
        isGooglePlayServices = checkPlayServices();

        /**
         * Run this code if the user had went to setting
         */
        if (goesToTheSetting) {

            /**
             * Get the ownership status that selected by the user in the settings, whether owner or worker,
             * the default value is owner.
             */
            String ownerShip = Utility.getAnyString(this, this.getResources().getString(R.string.key_pref_ownership_status), Constant.VALUE_OWNER);


            /* Check if ownership as worker and not owner */
            if (Objects.equals(ownerShip, Constant.VALUE_JOB_STATUS)) {

                /* Get the owner email from shared preference if any */
                String savedPreferenceBossEmail = Utility.getAnyString(this, this.getResources().getString(R.string.key_pref_owner_email_address), Constant.VALUE_DEFAULT_BOSS_EMAIL);
                addTheWorkerToUserWorkersNode(savedPreferenceBossEmail, mEncodedEmail);

            } else {

                /* If the user change ownership to owner */
                Utility.setAnyString(this, this.getResources().getString(R.string.key_pref_owner_email_address), null);
                removeTheWorkerToUserFriendsNode(Constant.VALUE_NULL_STRING, mEncodedEmail);
            }
        }

        /* Set to false */
        goesToTheSetting = false;
    }


    /**
     * Logs out the user from their current session and starts LoginActivity.
     * Also disconnects the mGoogleApiClient.
     */
    protected void logout() {

        /* Logout is not null */
        mFirebaseRef.unauth();

        /* Logout from Google+ */
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status != null) {
                    Log.v(LOG_TAG, status.toString());
                }
            }
        });

    }


    /**
     * Goto login screen
     */
    private void takeUserLoginScreenOnUnAuth() {
        /* Move user to loginActivity, and remove the back stack */
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult != null) {
            Log.e(LOG_TAG, connectionResult.toString());
        }
    }


    /**
     * This helper method is to check if the user types boss
     * encoded email address is already exist in local database
     *
     * @param newBossEncodedEmail From SharedPreference Settings
     * @return True if already exist in SQLite
     */
    protected boolean isThisBossInOurDataBase(String newBossEncodedEmail) {

        /* Get the content resolver */
        ContentResolver contentResolver = getContentResolver();

        /* Query the database */
        Cursor cursor = contentResolver.query(
                ContractData.EncodedEmailEntry.CONTENT_URI,
                null,
                ContractData.EncodedEmailEntry.COLUMN_ENCODED_EMAIL + "=?",
                new String[]{newBossEncodedEmail},
                null);

        /* Check if not null */
        if (cursor != null) {

            /* Make the cursor move to first */
            cursor.moveToFirst();

            /* If the data is found, return true */
            if (cursor.isFirst()) {
                return true;
            }
        }

        /* If data not found, return false */
        return false;
    }


    /**
     * This is a helper method to add the worker into the userWorkers Node.
     *
     * @param bossEmail          The user typed boss email in the setting
     * @param workerEncodedEmail The current email used by the user when login
     */
    protected void addTheWorkerToUserWorkersNode(final String bossEmail,
                                                 final String workerEncodedEmail) {

        /* Check if the user has typed a valid email format */
        if (Utility.isEmailValid(bossEmail)) {

            /* Encode the owner email to match Firebase spec, replace "." with "," */
            final String encodedBossEmail = Utility.encodeEmail(bossEmail);

            /* Check if the user not typed his/her own email */
            if (!Objects.equals(encodedBossEmail, workerEncodedEmail)) {

                /**
                 * Now, what we are doing here is we want to know if the boss/owner has approved
                 * the sharing with this worker, if the owner/boss return true then sharing is
                 * approved by default is false, store the result in a local database, so we can
                 * get them later when invoking {@link android.app.LoaderManager.LoaderCallbacks}
                 */
                getTheBossApprovalFromFirebaseNode(encodedBossEmail, workerEncodedEmail);

                /**
                 * First, we need to check if this encodedBossEmail is already exist
                 * in local database if true then no need to repeat same procedure every {@link onResume()}
                 * Second, let that the user change the boss email address in the setting, so we need to
                 * reset everything again as if the user typed null, then recreate again the node to the
                 * newly typed boss. The code below does exactly that.
                 *
                 */
                if (!isThisBossInOurDataBase(encodedBossEmail)) {

                    /* First!!! remove all the data from every where */
                    removeTheWorkerToUserFriendsNode(Constant.VALUE_NULL_STRING, workerEncodedEmail);

                    /**
                     * Firebase ref to /users/encodedBossEmail, which we will check if this user
                     * is already exist.
                     */
                    mUsersRef = new Firebase(Constant.FIREBASE_URL_USERS).child(encodedBossEmail);

                    /* Start the listener */
                    mQueryIfThisBossIsUserListener = mUsersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            /* Check if not null */
                            if (dataSnapshot != null) {

                                /** Check if this email address is available, simple if it does not have
                                 * children then this email is not available yet
                                 */
                                boolean isAvailable = dataSnapshot.hasChildren();

                                /* If true */
                                if (isAvailable) {

                                    /**
                                     * Firebase ref to /userFriends/encodedBossEmail.
                                     * This listener is to know whether if the worker is already exist or new one, so if
                                     * dataSnapshot.getValue == null then this is a new one, so we can create a new node
                                     * and a new database record for this one.
                                     */
                                    final Firebase newWorkers = new Firebase(Constant.FIREBASE_URL_USER_WORKERS).child(encodedBossEmail);
                                    newWorkers.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            /**
                                             * If not is true, then new one. Here we must be explicit,
                                             * so later the boss/owner can have many workers
                                             */
                                            if (!dataSnapshot.hasChild(workerEncodedEmail)) {

                                                /**
                                                 * Instantiate ModelCoWorkers and pass the worker logged in email address, user name, and status false.
                                                 */
                                                ModelCoworkers coworkers = new ModelCoworkers(workerEncodedEmail, mTheUserNameOfThisGadget, Constant.VALUE_JOB_STATUS, false);

                                                /**
                                                 * Get to the node /thisEmail which is the worker logged in email
                                                 * then pass the coworkers object into that node, we are creating a new node
                                                 * with key (mEncodedEmail) and value (coworkers)
                                                 */
                                                newWorkers.child(workerEncodedEmail).setValue(coworkers);

                                                /**
                                                 * Add the boss/owner encoded email address into the /bossEmail node
                                                 */
                                                Firebase flagTheBossField = new Firebase(Constant.FIREBASE_URL_BOSS_EMAIL).child(workerEncodedEmail);
                                                flagTheBossField.setValue(encodedBossEmail);

                                                /* Initialize the ContentResolver */
                                                ContentResolver contentResolver = getContentResolver();

                                                /* Initialize the ContentValues */
                                                ContentValues contentValues = new ContentValues();

                                                /* Put the new value */
                                                contentValues.put(ContractData.EncodedEmailEntry.COLUMN_ENCODED_EMAIL, encodedBossEmail);

                                                /* Update the database table */
                                                try {
                                                    contentResolver.update(ContractData.EncodedEmailEntry.CONTENT_URI,
                                                            contentValues,
                                                            ContractData.ItemMasterEntry._ID + "=?",
                                                            new String[]{"1"});
                                                } catch (Exception e) {
                                                    Log.e(LOG_TAG, e.toString());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                            if (firebaseError != null) {
                                                Log.e(LOG_TAG, firebaseError.toString());
                                            }
                                        }
                                    });

                                } else {

                                    /**
                                     * If the user change the boss email address, and it is
                                     * not available in the /users node then remove all the node,
                                     * as if the user typed null.
                                     */
                                    removeTheWorkerToUserFriendsNode(Constant.VALUE_NULL_STRING, workerEncodedEmail);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                            if (firebaseError != null) {
                                Log.e(LOG_TAG, firebaseError.toString());
                            }
                        }
                    });
                }
            }

        }
    }


    /**
     * This helper method, will return value true or false and then store them in local database
     * for later use in {@link android.app.LoaderManager.LoaderCallbacks<Cursor>}
     */
    protected void getTheBossApprovalFromFirebaseNode(final String encodedBossEmail, final String workerEncodedEmail) {

        /**
         * Now, what we are doing here is we want to know if the boss/owner has approved
         * the sharing with this worker, if the owner/boss return true then sharing is
         * approved by default is false, store the result in a local database, so we can
         * get them later when invoking {@link android.app.LoaderManager.LoaderCallbacks}
         */
        mUserWorkersRef = new Firebase(Constant.FIREBASE_URL_USER_WORKERS).child(encodedBossEmail).child(workerEncodedEmail);
        mUserWorkersRefListener = mUserWorkersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /* Check if this node is exist */
                if (dataSnapshot.hasChild(Constant.FIREBASE_PROPERTY_BOOLEAN_STATUS)) {

                    /* Get the value true for share, false for no share */
                    boolean lBooleanStatus = dataSnapshot.child(Constant.FIREBASE_PROPERTY_BOOLEAN_STATUS).getValue(Boolean.class);

                    /* Initialize the ContentResolver */
                    ContentResolver contentResolver = getContentResolver();

                    /* Initialize the ContentValues */
                    ContentValues contentValues = new ContentValues();

                    /* Put the new value */
                    contentValues.put(ContractData.EncodedEmailEntry.COLUMN_BOOLEAN_STATUS, String.valueOf(lBooleanStatus));

                    try {
                        /* Update the database table */
                        contentResolver.update(ContractData.EncodedEmailEntry.CONTENT_URI,
                                contentValues,
                                ContractData.ItemMasterEntry._ID + "=?",
                                new String[]{"1"});
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.toString());
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

                if (firebaseError != null) {
                    Log.e(LOG_TAG, firebaseError.toString());
                }

            }
        });

    }


    /**
     * Helper method to remove the worker email, from all node in {@link Firebase} and in local database
     * as well.
     *
     * @param bossEmail          The boss email
     * @param workerEncodedEmail The current user email
     */
    private void removeTheWorkerToUserFriendsNode(final String bossEmail,
                                                  final String workerEncodedEmail) {

        /**
         * If the user set the boss email in SharePreference to null, then remove him as a worker
         */
        if (bossEmail.equalsIgnoreCase(Constant.VALUE_NULL_STRING)) {

            /* Get the boss encoded email from /bossEmail/mEncodedEmail */
            Firebase getTheBossEmail = new Firebase(Constant.FIREBASE_URL_BOSS_EMAIL).child(workerEncodedEmail);
            getTheBossEmail.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        /* Once we have the email we can perform the delete */
                        String lBossEmailFromFirebase = dataSnapshot.getValue(String.class);

                        /**
                         *  Check if not null or use try/catch otherwise it will crash for passing null
                         */
                        if (lBossEmailFromFirebase != null) {

                            /**
                             * Since we already have the boss encoded email then we can delete them,
                             * we can add listeners here, but for simplicity and performance,
                             * better use if != null or try/catch to handle the error for not be able to pass null
                             * if this node is not exis.
                             */
                            Firebase newWorkers = new Firebase(Constant.FIREBASE_URL_USER_WORKERS);
                            newWorkers.child(lBossEmailFromFirebase).child(workerEncodedEmail).setValue(null);

                        }

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
             * Now below code, is to remove the /bossEmail/mEncodedEmail
             */

            /* Get to the node */
            Firebase flagTheBossField =
                    new Firebase(Constant.FIREBASE_URL_BOSS_EMAIL).child(workerEncodedEmail);

            /* Set the value to null */
            flagTheBossField.setValue(null);

            /**
             * Finally we must also update the local database, replace the boss encoded email
             * with string "worker", and replace the boolean status with "false".
             */

            /* Initialize the ContentResolver */
            ContentResolver contentResolver = getContentResolver();

            /* Initialize the ContentValues */
            ContentValues contentValues = new ContentValues();

            /* Put the new value */
            contentValues.put(ContractData.EncodedEmailEntry.COLUMN_ENCODED_EMAIL, Constant.VALUE_JOB_STATUS);
            contentValues.put(ContractData.EncodedEmailEntry.COLUMN_BOOLEAN_STATUS, Constant.VALUE_BOOLEAN_STATUS);

            /**
             * Update the database table
             */
            try {
                contentResolver.update(ContractData.EncodedEmailEntry.CONTENT_URI,
                        contentValues,
                        ContractData.ItemMasterEntry._ID + "=?",
                        new String[]{"1"});
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_REQUEST)
                        .show();
            } else {
                Log.i(LOG_TAG, getString(R.string.log_play_service));
                Toast.makeText(this, R.string.toast_no_play_service, Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }


    /**
     * Broadcast receiver, to receive the image upload status from
     * {@link com.stockita.newpointofsales.services.UploadImages},
     * then send notification to the user if the upload has failed.
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {


            /* Get the data from the intent */
            boolean status = intent.getBooleanExtra(Constant.KEY_NOTIFY_BOOLEAN_STATUS, false);
            String message = intent.getStringExtra(Constant.KEY_NOTIFY_MESSAGE);
            int notifyCode = intent.getIntExtra(Constant.KEY_NOTIFY_CODE, 0);


            switch (notifyCode) {

                /* Notify using notification manager */
                case Constant.NOTIFY_CODE_NOTIFICATION_MANAGER:

                    /* If status return false then send notification so the user can try again */
                    if (!status) {

                        /* Use the Notification API */
                        Notification builder = new Notification.Builder(context)
                                .setContentTitle(getString(R.string.notify_title_image_upload))
                                .setContentText(message)
                                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                                .build();

                        /* Dismiss the notification after click */
                        builder.flags |= Notification.FLAG_AUTO_CANCEL;

                        /* Initialize the manager */
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        /* notify */
                        notificationManager.notify(0, builder);
                    }
                    break;

                /* Notify using toast */
                case Constant.NOTIFY_CODE_TOAST:
                    if (!status) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                    }
                    break;

            }
        }
    };

}
