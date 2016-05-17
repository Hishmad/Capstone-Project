package com.stockita.newpointofsales.utilities;

import com.stockita.newpointofsales.BuildConfig;

/**
 * Put all the constant in this place
 */
public class Constant {


    /**
     * Constants related to locations in Firebase
     */
    public static final String FIREBASE_LOCATION_CATEGORY = "category";
    public static final String FIREBASE_LOCATION_ITEM_MASTER = "itemMaster";
    public static final String FIREBASE_LOCATION_ITEM_IMAGE = "itemImage";
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_UID_MAPPINGS = "uidMappings";
    public static final String FIREBASE_LOCATION_USER_WORKERS = "userWorkers";
    public static final String FIREBASE_LOCATION_BOSS_EMAIL = "bossEmail";
    public static final String FIREBASE_LOCATION_SALES_BILL_HEADER = "salesBillHeader";
    public static final String FIREBASE_LOCATION_SALES_BILL_DETAIL = "salesBillDetail";
    public static final String FIREBASE_LOCATION_PURCHASE_BILL_HEADER = "purchaseBillHeader";
    public static final String FIREBASE_LOCATION_PURCHASE_BILL_DETAIL = "purchaseBillDetail";
    public static final String FIREBASE_LOCATION_TIME_STAMP = "timeStamp";


    /**
     * Constants for Firebase URL
     */
    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_UID_MAPPINGS = FIREBASE_URL + "/" + FIREBASE_LOCATION_UID_MAPPINGS;
    public static final String FIREBASE_URL_USER_WORKERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USER_WORKERS;
    public static final String FIREBASE_URL_BOSS_EMAIL = FIREBASE_URL + "/" + FIREBASE_LOCATION_BOSS_EMAIL;


    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_PROPERTY_JOB_STATUS = "jobStatus";
    public static final String FIREBASE_PROPERTY_BOOLEAN_STATUS = "booleanStatus";
    public static final String FIREBASE_PROPERTY_ITEM_MASTER_PRICE = "price";
    public static final String FIREBASE_PROPERTY_ITEM_MASTER_UOM = "unitOfMeasure";
    public static final String FIREBASE_PROPERTY_ITEM_NUMBER = "itemNumber";


    /**
     * Constants for Firebase login
     */
    public static final String GOOGLE_PROVIDER = "google";
    public static final String PROVIDED_DATA_DISPLAY_NAME = "displayName";


    /**
     * Constants for bundles, extras and shared preferences keys
     */
    public static final String KEY_ENCODED_EMAIL = "KEY_ENCODED_EMAIL";
    public static final String KEY_GOOGLE_EMAIL = "GOOGLE_EMAIL";
    public static final String KEY_THIS_USER_NAME = "KEY_THE_USER_OF_THIS_ACCOUNT";
    public static final String KEY_STARTUP_FIRSTTIME = "KEY_START_UP_FIRST_TIME";
    public static final int KEY_LOAD_ONE = 1;
    public static final int KEY_LOAD_TWO = 2;
    public static final String KEY_CATEGORY_MODEL = "KEY_CATEGORY_MODEL";

    public static final String KEY_ITEM_MASTER_POJO_LIST = "KEY_ITEM_MASTER_POJO_LIST";
    public static final String KEY_ITEM_MASTER_KEY_LIST = "KEY_ITEM_MASTER_KEY_LIST";
    public static final String KEY_SALES_HEADER = "headerSales";
    public static final String KEY_SALES_DETAIL = "detailSales";
    public static final String KEY_SALES_PASS_HEADER = "headerSalesModelPass";
    public static final String KEY_SALES_PASS_DETAIL = "arrayDetailSalesPass";
    public static final String KEY_PURCHASE_HEADER = "headerPurchase";
    public static final String KEY_PURCHASE_DETAIL = "detailPurchase";
    public static final String KEY_PURCHASE_PASS_HEADER = "headerPurchaseModelPass";
    public static final String KEY_PURCHASE_PASS_DETAIL = "detailPurchaseModelPass";
    public static final String KEY_DIALOG_FRAGMENT_ITEM_NUMBER = "dialogFragmentKeyItemNumber";
    public static final String KEY_USERFUL_ENCODED_EMAIL = "KEY_USEFUL_ENCODED_EMAIL";
    public static final String KEY_PUSH_ID_OF_ITEM_MASTER = "KEY_PUSH_ID_OF_ITEM_MASTER";
    public static final String KEY_BOOLEAN_STATUS = "KEY_BOOLEAN_STATUS";

    public static final String KEY_IMAGE_FILE_PATH_TO_UPLOAD = "KEY_IMAGE_FILE_PATH_TO_UPLOAD";
    public static final String KEY_IMAGE_PUSH_KEY_TO_UPLOAD = "KEY_IMAGE_PUSH_KEY_TO_UPLOAD";

    public static final String BROADCAST_ACTION_SEND_NOTIFICATION = "com.stockita.newpointofsales.SEND_NOTIFICATIONS";
    public static final String KEY_NOTIFY_BOOLEAN_STATUS = "KEY_NOTIFY_BOOLEAN_STATUS";
    public static final String KEY_NOTIFY_MESSAGE = "KEY_NOTIFY_MESSAGE";
    public static final String KEY_NOTIFY_CODE = "KEY_NOTIFY_CODE";
    public static final String KEY_ITEM_MASTER_NODE = "KEY_ITEM_MASTER_NODE";
    public static final String KEY_ITEM_IMAGE_MASTER_NODE = "KEY_ITEM_IMAGE_MASTER_NODE";
    public static final String KEY_CURRENT_POSITION = "KEY_CURRENT_POSITION";


    /**
     * Notify codes
     */
    public static final int NOTIFY_CODE_NOTIFICATION_MANAGER = 1;
    public static final int NOTIFY_CODE_TOAST = 2;


    /**
     * Constants default values
     */
    public static final String VALUE_JOB_STATUS = "worker";
    public static final String VALUE_DEFAULT_BOSS_EMAIL = "none";
    public static final String VALUE_BOOLEAN_STATUS = "false";
    public static final String VALUE_OWNER = "owner";
    public static final String VALUE_NULL_STRING = "null";


    /**
     * Constants cloudinary
     */
    public static final String CLOUDINARY_BASE_URL_DOWNLOAD = "https://res-1.cloudinary.com/stockita/image/upload/";
    public static final String CLOUDINARY_COULD_NAME_KEY = "cloud_name";
    public static final String CLOUDINARY_CLOUD_NAME_VALUE = "[YOURS]";
    public static final String CLOUDINARY_API_KEY_KEY = "api_key";
    public static final String CLOUDINARY_API_KEY_VALUE = "[YOURS]";
    public static final String CLOUDINARY_API_SECRET_KEY = "api_secret";
    public static final String CLOUDINARY_API_SECRET_VALUE = "[YOURS]";
    public static final String CLOUDINARY_PUBLIC_ID_KEY = "public_id";

}
