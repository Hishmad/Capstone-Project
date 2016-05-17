package com.stockita.newpointofsales.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This call has the content provider
 */
public class ContentProviderPOS extends ContentProvider {

    // Constant
    private static final String ERROR_UNKNOWN_URI = "Unknown URI: ";
    private static final String ERROR_FILED_TO_INSERT = "Failed to insert row into URI: ";

    private static final int ITEM_ID = 100;
    private static final int ITEM_ENTRY = 101;

    private static final int OWNERSHIP_ID = 200;
    private static final int OWNGERSHIP_ENTRY = 201;

    private static final int ITEM_CATEGORYS_ID = 300;
    private static final int ITEM_CATEGORYES_ENTRY = 301;

    private static final int POINT_OF_SALES_HEADER_ID = 400;
    private static final int POINT_OF_SALES_HEADER_ENTRY = 401;

    private static final int POINT_OF_SALES_DETAIL_ID = 500;
    private static final int POINT_OF_SALES_DETAIL_ENTRY = 501;

    private static final int PURCHASE_RECEIVING_HEADER_ID = 600;
    private static final int PURCHASE_RECEIVING_HEADER_ENTRY = 601;

    private static final int PURCHASE_RECEIVING_DETAIL_ID = 700;
    private static final int PURCHASE_RECEIVING_DETAIL_ENTRY = 701;

    private static final int INVENTORY_DETAIL_ID = 800;
    private static final int INVENTORY_DETAIL_ENTRY = 801;

    private static final int ITEM_FULL = 1000;
    private static final int ITEM_FULL_DETAIL = 1001;

    private static final SQLiteQueryBuilder sQueryBuilder;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DatabaseHelper dbHelper;

    static {

        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setTables(
                ContractData.ItemMasterEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        ContractData.EncodedEmailEntry.TABLE_NAME + " USING ("
                        + ContractData.ItemMasterEntry._ID + ")");
    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ContractData.AUTHORITY;

        matcher.addURI(authority, ContractData.DIR_ITEM_MASTER + "/#", ITEM_ID);
        matcher.addURI(authority, ContractData.DIR_OWNERSHIP + "/#", OWNERSHIP_ID);
        matcher.addURI(authority, ContractData.DIR_CATEGORY_MASTER + "/#", ITEM_CATEGORYS_ID);
        matcher.addURI(authority, ContractData.DIR_POINT_OF_SALES_HEADER + "/#", POINT_OF_SALES_HEADER_ID);
        matcher.addURI(authority, ContractData.DIR_POINT_OF_SALES_DETAIL + "/#", POINT_OF_SALES_DETAIL_ID);
        matcher.addURI(authority, ContractData.DIR_PURCHASE_RECEIVING_HEADER + "/#", PURCHASE_RECEIVING_HEADER_ID);
        matcher.addURI(authority, ContractData.DIR_PURCHASE_RECEIVING_DETAIL + "/#", PURCHASE_RECEIVING_DETAIL_ID);
        matcher.addURI(authority, ContractData.DIR_INVENTORY_DETAIL_TRANSACTION + "/#", INVENTORY_DETAIL_ID);

        matcher.addURI(authority, ContractData.DIR_ITEM_MASTER, ITEM_ENTRY);
        matcher.addURI(authority, ContractData.DIR_OWNERSHIP, OWNGERSHIP_ENTRY);
        matcher.addURI(authority, ContractData.DIR_CATEGORY_MASTER, ITEM_CATEGORYES_ENTRY);
        matcher.addURI(authority, ContractData.DIR_POINT_OF_SALES_HEADER, POINT_OF_SALES_HEADER_ENTRY);
        matcher.addURI(authority, ContractData.DIR_POINT_OF_SALES_DETAIL, POINT_OF_SALES_DETAIL_ENTRY);
        matcher.addURI(authority, ContractData.DIR_PURCHASE_RECEIVING_HEADER, PURCHASE_RECEIVING_HEADER_ENTRY);
        matcher.addURI(authority, ContractData.DIR_PURCHASE_RECEIVING_DETAIL, PURCHASE_RECEIVING_DETAIL_ENTRY);
        matcher.addURI(authority, ContractData.DIR_INVENTORY_DETAIL_TRANSACTION, INVENTORY_DETAIL_ENTRY);

        matcher.addURI(authority, ContractData.DIR_FULL_ITEM_MASTER + "/#", ITEM_FULL_DETAIL);
        matcher.addURI(authority, ContractData.DIR_FULL_ITEM_MASTER, ITEM_FULL);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor;
        switch (sUriMatcher.match(uri)) {

            case ITEM_ENTRY:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.ItemMasterEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection == null ? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case OWNGERSHIP_ENTRY:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.EncodedEmailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case ITEM_CATEGORYES_ENTRY:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case POINT_OF_SALES_HEADER_ENTRY:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.PointOfSalesHeaderEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case POINT_OF_SALES_DETAIL_ENTRY:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.PointOfSalesDetailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case PURCHASE_RECEIVING_HEADER_ENTRY:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.PurchaseReceivingHeaderEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case PURCHASE_RECEIVING_DETAIL_ENTRY:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.PurchaseReceivingDetailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case INVENTORY_DETAIL_ENTRY:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.InventoryDetailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case ITEM_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.ItemMasterEntry.TABLE_NAME,
                        projection,
                        ContractData.ItemMasterEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case OWNERSHIP_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.EncodedEmailEntry.TABLE_NAME,
                        projection,
                        ContractData.EncodedEmailEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case ITEM_CATEGORYS_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.CategoryEntry.TABLE_NAME,
                        projection,
                        ContractData.CategoryEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case POINT_OF_SALES_HEADER_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.PointOfSalesHeaderEntry.TABLE_NAME,
                        projection,
                        ContractData.PointOfSalesHeaderEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case POINT_OF_SALES_DETAIL_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.PointOfSalesDetailEntry.TABLE_NAME,
                        projection,
                        ContractData.PointOfSalesDetailEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case PURCHASE_RECEIVING_HEADER_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.PurchaseReceivingHeaderEntry.TABLE_NAME,
                        projection,
                        ContractData.PurchaseReceivingHeaderEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case PURCHASE_RECEIVING_DETAIL_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.PurchaseReceivingDetailEntry.TABLE_NAME,
                        projection,
                        ContractData.PurchaseReceivingDetailEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case INVENTORY_DETAIL_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ContractData.InventoryDetailEntry.TABLE_NAME,
                        projection,
                        ContractData.InventoryDetailEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException(ERROR_UNKNOWN_URI + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM_ID:
                return ContractData.ItemMasterEntry.CONTENT_ITEM_TYPE;
            case OWNERSHIP_ID:
                return ContractData.EncodedEmailEntry.CONTENT_ITEM_TYPE;
            case ITEM_CATEGORYS_ID:
                return ContractData.CategoryEntry.CONTENT_ITEM_TYPE;
            case POINT_OF_SALES_HEADER_ID:
                return ContractData.PointOfSalesHeaderEntry.CONTENT_ITEM_TYPE;
            case POINT_OF_SALES_DETAIL_ID:
                return ContractData.PointOfSalesDetailEntry.CONTENT_ITEM_TYPE;
            case PURCHASE_RECEIVING_HEADER_ID:
                return ContractData.PurchaseReceivingHeaderEntry.CONTENT_ITEM_TYPE;
            case PURCHASE_RECEIVING_DETAIL_ID:
                return ContractData.PurchaseReceivingDetailEntry.CONTENT_ITEM_TYPE;
            case INVENTORY_DETAIL_ID:
                return ContractData.InventoryDetailEntry.CONTENT_ITEM_TYPE;
            case ITEM_ENTRY:
                return ContractData.ItemMasterEntry.CONTENT_TYPE;
            case OWNGERSHIP_ENTRY:
                return ContractData.EncodedEmailEntry.CONTENT_TYPE;
            case ITEM_CATEGORYES_ENTRY:
                return ContractData.CategoryEntry.CONTENT_TYPE;
            case POINT_OF_SALES_HEADER_ENTRY:
                return ContractData.PointOfSalesHeaderEntry.CONTENT_TYPE;
            case POINT_OF_SALES_DETAIL_ENTRY:
                return ContractData.PointOfSalesDetailEntry.CONTENT_TYPE;
            case PURCHASE_RECEIVING_HEADER_ENTRY:
                return ContractData.PurchaseReceivingHeaderEntry.CONTENT_TYPE;
            case PURCHASE_RECEIVING_DETAIL_ENTRY:
                return ContractData.PurchaseReceivingDetailEntry.CONTENT_TYPE;
            case INVENTORY_DETAIL_ENTRY:
                return ContractData.InventoryDetailEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException(ERROR_UNKNOWN_URI + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case ITEM_ENTRY: {
                long _id = db.insert(ContractData.ItemMasterEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ContractData.ItemMasterEntry.buildItemUri(_id);
                } else {
                    throw new android.database.SQLException(ERROR_FILED_TO_INSERT + uri);
                }
                break;
            }

            case OWNGERSHIP_ENTRY: {
                long _id = db.insert(ContractData.EncodedEmailEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ContractData.EncodedEmailEntry.buildCategoryUri(_id);
                } else {
                    throw new android.database.SQLException(ERROR_FILED_TO_INSERT + uri);
                }
                break;
            }

            case ITEM_CATEGORYES_ENTRY: {
                long _id = db.insert(ContractData.CategoryEntry.TABLE_NAME, null, values);
                if ( _id > 0) {
                    returnUri = ContractData.CategoryEntry.buildItemImageUri(_id);
                } else {
                    throw new android.database.SQLException(ERROR_FILED_TO_INSERT + uri);
                }
                break;
            }

            case POINT_OF_SALES_HEADER_ENTRY: {
                long _id = db.insert(ContractData.PointOfSalesHeaderEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                    returnUri = ContractData.PointOfSalesHeaderEntry.buildPosHeaderUri(_id);
                } else {
                    throw new android.database.SQLException(ERROR_FILED_TO_INSERT + uri);
                }
                break;
            }

            case POINT_OF_SALES_DETAIL_ENTRY: {
                long _id = db.insert(ContractData.PointOfSalesDetailEntry.TABLE_NAME, null, values);
                if ( _id > 0) {
                    returnUri = ContractData.PointOfSalesDetailEntry.buildPosDetailUri(_id);
                } else {
                    throw  new android.database.SQLException(ERROR_FILED_TO_INSERT + uri);
                }
                break;
            }

            case PURCHASE_RECEIVING_HEADER_ENTRY: {
                long _id = db.insert(ContractData.PurchaseReceivingHeaderEntry.TABLE_NAME, null, values);
                if ( _id > 0) {
                    returnUri = ContractData.PurchaseReceivingHeaderEntry.buildPurchaseReceivingHeader(_id);
                } else {
                    throw  new android.database.SQLException(ERROR_FILED_TO_INSERT + uri);
                }
                break;
            }

            case PURCHASE_RECEIVING_DETAIL_ENTRY: {
                long _id = db.insert(ContractData.PurchaseReceivingDetailEntry.TABLE_NAME, null, values);
                if ( _id > 0) {
                    returnUri = ContractData.PurchaseReceivingDetailEntry.buildPurchaseReceivingDetail(_id);
                } else {
                    throw  new android.database.SQLException(ERROR_FILED_TO_INSERT + uri);
                }
                break;
            }

            case INVENTORY_DETAIL_ENTRY: {
                long _id = db.insert(ContractData.InventoryDetailEntry.TABLE_NAME, null, values);
                if ( _id > 0) {
                    returnUri = ContractData.InventoryDetailEntry.buildInventoryDetail(_id);
                } else {
                    throw  new android.database.SQLException(ERROR_FILED_TO_INSERT + uri);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException(ERROR_UNKNOWN_URI + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowDeleted;

        switch (match) {

            case ITEM_ENTRY:
                rowDeleted = db.delete(
                        ContractData.ItemMasterEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case OWNGERSHIP_ENTRY:
                rowDeleted = db.delete(
                        ContractData.EncodedEmailEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case ITEM_CATEGORYES_ENTRY:
                rowDeleted = db.delete(
                        ContractData.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case POINT_OF_SALES_HEADER_ENTRY:
                rowDeleted = db.delete(
                        ContractData.PointOfSalesHeaderEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case POINT_OF_SALES_DETAIL_ENTRY:
                rowDeleted = db.delete(
                        ContractData.PointOfSalesDetailEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PURCHASE_RECEIVING_HEADER_ENTRY:
                rowDeleted = db.delete(
                        ContractData.PurchaseReceivingHeaderEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PURCHASE_RECEIVING_DETAIL_ENTRY:
                rowDeleted = db.delete(
                        ContractData.PurchaseReceivingDetailEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case INVENTORY_DETAIL_ENTRY:
                rowDeleted = db.delete(
                        ContractData.InventoryDetailEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException(ERROR_UNKNOWN_URI + uri);
        } // end switch

        // null deletes all rows
        if (selection == null || rowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ITEM_ENTRY:
                rowsUpdated = db.update(ContractData.ItemMasterEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case OWNGERSHIP_ENTRY:
                rowsUpdated = db.update(ContractData.EncodedEmailEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ITEM_CATEGORYES_ENTRY:
                rowsUpdated = db.update(ContractData.CategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case POINT_OF_SALES_HEADER_ENTRY:
                rowsUpdated = db.update(ContractData.PointOfSalesHeaderEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case POINT_OF_SALES_DETAIL_ENTRY:
                rowsUpdated = db.update(ContractData.PointOfSalesDetailEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PURCHASE_RECEIVING_HEADER_ENTRY:
                rowsUpdated = db.update(ContractData.PurchaseReceivingHeaderEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PURCHASE_RECEIVING_DETAIL_ENTRY:
                rowsUpdated = db.update(ContractData.PurchaseReceivingDetailEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INVENTORY_DETAIL_ENTRY:
                rowsUpdated = db.update(ContractData.InventoryDetailEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(ERROR_UNKNOWN_URI + uri);
        }

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Database Helper class
     */
    private class DatabaseHelper extends SQLiteOpenHelper {

        // Constant
        private static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "pointofsales.db";

        // Constructor
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Create new table
         */
        @Override
        public void onCreate(SQLiteDatabase db) {

            final String CREATE_ITEM_MASTER_TABLE = "CREATE TABLE " +
                    ContractData.ItemMasterEntry.TABLE_NAME + " (" +
                    ContractData.ItemMasterEntry._ID + " INTEGER PRIMARY KEY, " +
                    ContractData.ItemMasterEntry.COLUMN_ITEM_NUMBER + " TEXT UNIQUE NOT NULL, " +
                    ContractData.ItemMasterEntry.COLUMN_ITEM_DESCRIPTION + " TEXT, " +
                    ContractData.ItemMasterEntry.COLUMN_ITEM_UNIT_OF_MEASURE + " TEXT, " +
                    ContractData.ItemMasterEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                    ContractData.ItemMasterEntry.COLUMN_CURRENCY + " TEXT, " +
                    ContractData.ItemMasterEntry.COLUMN_PRICE + " DOUBLE, " +
                    ContractData.ItemMasterEntry.COLUMN_ITEM_AVAILABLE_STOCK + " DOUBLE, " +
                    ContractData.ItemMasterEntry.COLUMN_INVENTORY_IN + " DOUBLE, " +
                    ContractData.ItemMasterEntry.COLUMN_INVENTORY_OUT + " DOUBLE, " +
                    ContractData.ItemMasterEntry.COLUMN_TRIGGER + " TEXT)";

            final String CREATE_OWNERSHIP_TABLE = "CREATE TABLE " +
                    ContractData.EncodedEmailEntry.TABLE_NAME + " (" +
                    ContractData.EncodedEmailEntry._ID + " INTEGER PRIMARY KEY, " +
                    ContractData.EncodedEmailEntry.COLUMN_ENCODED_EMAIL + " TEXT, " +
                    ContractData.EncodedEmailEntry.COLUMN_BOOLEAN_STATUS + " TEXT)";

            final String CREATE_CATEGORY_MASTER_TABLE = "CREATE TABLE " +
                    ContractData.CategoryEntry.TABLE_NAME + " (" +
                    ContractData.CategoryEntry._ID + " INTEGER PRIMARY KEY, " +
                    ContractData.CategoryEntry.COLUMN_CATEGORY + " TEXT, " +
                    ContractData.CategoryEntry.COLUMN_IMAGE_URL + " TEXT)";

            final String CREATE_POINT_OF_SALES_HEADER_TABLE = "CREATE TABLE " +
                    ContractData.PointOfSalesHeaderEntry.TABLE_NAME + " (" +
                    ContractData.PointOfSalesHeaderEntry._ID + " INTEGER PRIMARY KEY, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_DATE + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_INVOICE_NUMBER + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_CUSTOMER_NAME + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_CUSTOMER_EMAIL + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_CUSTOMER_ADDRESS + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_CUSTOMER_PHONE + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_SUBTOTAL_BEFORE_DISCOUNT_AND_TAX + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_INVOICE_DISCOUNT + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_DISCOUNT_VALUE + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_INVOICE_TAX + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_TAX_VALUE + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_INVOICE_TOTAL_AFTER_TAX + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_TYPE_OF_PAYMENT + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_NUMBER_OF_ITEMS + " TEXT, " +
                    ContractData.PointOfSalesHeaderEntry.COLUMN_TIME_STAMP + " TEXT)";

            final String CREATE_POINT_OF_SALES_DETAIL_TABLE = "CREATE TABLE " +
                    ContractData.PointOfSalesDetailEntry.TABLE_NAME + " (" +
                    ContractData.PointOfSalesDetailEntry._ID + " INTEGER PRIMARY KEY, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_DATE + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_HEADER_ID + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_INVOICE_NUMBER + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_ITEM_NUMBER + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_ITEM_DESCRIPTION + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_UNIT_OF_MEASURE + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_CURRENCY + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_CATEGORY + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_PRICE + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_DISCOUNT + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_DISCOUNT_VALUE + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_QUANTITY + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_SUB_TOTAL_PER_ITEM + " TEXT, " +
                    ContractData.PointOfSalesDetailEntry.COLUMN_TIME_STAMP + " TEXT)";

            final String CREATE_PURCHASE_RECEIVING_HEADER_TABLE = "CREATE TABLE " +
                    ContractData.PurchaseReceivingHeaderEntry.TABLE_NAME + " (" +
                    ContractData.PurchaseReceivingHeaderEntry._ID + " INTEGER PRIMARY KEY, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_DATE + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_INVOICE_NUMBER + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_VENDOR_NAME + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_VENDOR_EMAIL + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_VENDOR_ADDRESS + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_VENDOR_PHONE + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_SUBTOTAL_BEFORE_DISCOUNT_AND_TAX + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_INVOICE_DISCOUNT + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_DISCOUNT_VALUE + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_INVOICE_TAX + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_TAX_VALUE + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_INVOICE_TOTAL_AFTER_TAX + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_TYPE_OF_PAYMENT + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_NUMBER_OF_ITEMS + " TEXT, " +
                    ContractData.PurchaseReceivingHeaderEntry.COLUMN_TIME_STAMP + " TEXT)";

            final String CREATE_PURCHASE_RECEIVING_DETAIL_TABLE = "CREATE TABLE " +
                    ContractData.PurchaseReceivingDetailEntry.TABLE_NAME + " (" +
                    ContractData.PurchaseReceivingDetailEntry._ID + " INTEGER PRIMARY KEY, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_DATE + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_HEADER_ID + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_INVOICE_NUMBER + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_ITEM_NUMBER + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_ITEM_DESCRIPTION + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_UNIT_OF_MEASURE + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_CURRENCY + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_CATEGORY + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_PRICE + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_DISCOUNT + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_DISCOUNT_VALUE + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_QUANTITY + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_SUB_TOTAL_PER_ITEM + " TEXT, " +
                    ContractData.PurchaseReceivingDetailEntry.COLUMN_TIME_STAMP + " TEXT)";

            final String CREATE_INVENTORY_DETAIL_TABLE = "CREATE TABLE " +
                    ContractData.InventoryDetailEntry.TABLE_NAME + " (" +
                    ContractData.InventoryDetailEntry._ID + " INTEGER PRIMARY KEY, " +
                    ContractData.InventoryDetailEntry.COL_DATE + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_HEADER_ID + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_INVOICE_NUMBER + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_ITEM_NUMBER + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_ITEM_DESCRIPTION + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_UNIT_OF_MEASURE + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_CURRENCY + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_CATEGORY + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_PRICE + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_DISCOUNT + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_DISCOUNT_VALUE + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_QUANTITY + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_SUB_TOTAL_PER_ITEM + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_TIME_STAMP + " TEXT, " +
                    ContractData.InventoryDetailEntry.COL_TYPE_TRANSACTION + " TEXT)";

            // Create
            db.execSQL(CREATE_ITEM_MASTER_TABLE);
            db.execSQL(CREATE_CATEGORY_MASTER_TABLE);
            db.execSQL(CREATE_OWNERSHIP_TABLE);
            db.execSQL(CREATE_POINT_OF_SALES_HEADER_TABLE);
            db.execSQL(CREATE_POINT_OF_SALES_DETAIL_TABLE);
            db.execSQL(CREATE_PURCHASE_RECEIVING_HEADER_TABLE);
            db.execSQL(CREATE_PURCHASE_RECEIVING_DETAIL_TABLE);
            db.execSQL(CREATE_INVENTORY_DETAIL_TABLE);

        }

        /**
         * Upgrade table
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + ContractData.ItemMasterEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ContractData.EncodedEmailEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ContractData.CategoryEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ContractData.PointOfSalesHeaderEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ContractData.PointOfSalesDetailEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ContractData.PurchaseReceivingHeaderEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ContractData.PurchaseReceivingDetailEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ContractData.InventoryDetailEntry.TABLE_NAME);

            onCreate(db);

        }
    }
}
