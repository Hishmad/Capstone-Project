package com.stockita.newpointofsales.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by hishmadabubakaralamudi on 11/18/15.
 */
public class ContractData {

    // Constant for the content URI
    public static final String SCHEME = "content://";
    public static final String AUTHORITY =
            "com.stockita.newpointofsales.data.ContentProviderPointOfSale";
    public static final String URI = SCHEME + AUTHORITY;

    // To make the CONTENT_URI we need SCHEME, AUTHORITY
    public static final Uri BASE_CONTENT_URI = Uri.parse(URI);

    // Directories
    public static final String DIR_ITEM_MASTER = "itemdir";
    public static final String DIR_OWNERSHIP = "ownershipdir";
    public static final String DIR_CATEGORY_MASTER = "categorydir";
    public static final String DIR_ITEM_IMAGE_MASTER = "imagedir";
    public static final String DIR_POINT_OF_SALES_HEADER = "salesheaddir";
    public static final String DIR_POINT_OF_SALES_DETAIL = "salesdetaildir";
    public static final String DIR_PURCHASE_RECEIVING_HEADER = "puchaseheaderdir";
    public static final String DIR_PURCHASE_RECEIVING_DETAIL = "purchasedirdetail";
    public static final String DIR_INVENTORY_DETAIL_TRANSACTION = "inventorydetaildir";

    public static final String DIR_FULL_ITEM_MASTER = "fullitemmasterdir";

    /**
     * Item Master Entry Class
     */
    public static final class ItemMasterEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(DIR_ITEM_MASTER).build();

        public static final Uri FULL_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(DIR_FULL_ITEM_MASTER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + DIR_ITEM_MASTER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + AUTHORITY + "/" + DIR_ITEM_MASTER;

        public static final String TABLE_NAME = "itemmastertable";

        // Column
        public static final String COLUMN_ITEM_NUMBER = "itemNumber";
        public static final String COLUMN_ITEM_DESCRIPTION = "itemDescription";
        public static final String COLUMN_ITEM_UNIT_OF_MEASURE = "unitOfMeasure";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_CURRENCY = "currency";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_ITEM_AVAILABLE_STOCK = "availableStock";
        public static final String COLUMN_INVENTORY_IN = "stockIn";
        public static final String COLUMN_INVENTORY_OUT = "stockOut";
        public static final String COLUMN_TRIGGER = "triggger";

        public static Uri buildItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFullItemUri(long id) {
            return ContentUris.withAppendedId(FULL_CONTENT_URI, id);
        }

        // Column Index
        public static final int INDEX_COL_ITEM_ID = 0;
        public static final int INDEX_COL_ITEM_NUMBER = 1;
        public static final int INDEX_COL_ITEM_DESCRIPTION = 2;
        public static final int INDEX_COL_UNIT_OF_MEASURE = 3;
        public static final int INDEX_COL_CATEGORY = 4;
        public static final int INDEX_COL_CURRENCY = 5;
        public static final int INDEX_COL_PRICE = 6;
        public static final int INDEX_COL_AVAILABLE_STOCK = 7;
        public static final int INDEX_COL_INVENTORY_IN = 8;
        public static final int INDEX_COL_INVENTORY_OUT = 9;
        public static final int INDEX_COL_TRIGGER = 10;
    }

    /**
     * Encoded ownership status Entry Class
     */
    public static final class EncodedEmailEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(DIR_OWNERSHIP).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + DIR_OWNERSHIP;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item" + AUTHORITY + "/" + DIR_OWNERSHIP;

        public static final String TABLE_NAME = "encodedEmail";

        // Column
        public static final String COLUMN_ENCODED_EMAIL = "email";
        public static final String COLUMN_BOOLEAN_STATUS = "boolean_status";

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Column Index
        public static final int INDEX_COL_ENCODED_EMAIL_ID = 0;
        public static final int INDEX_COL_ENCODED_EMAIL = 1;
        public static final int INDEX_COL_BOOLEAN_STATUS = 2;

    }

    /**
     * Item Image Master Entry Class
     */
    public static final class CategoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(DIR_CATEGORY_MASTER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + DIR_CATEGORY_MASTER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item" + AUTHORITY + "/" + DIR_CATEGORY_MASTER;

        public static final String TABLE_NAME = "imagemastertable";

        // Column
        public static final String COLUMN_CATEGORY = "categoryKey";
        public static final String COLUMN_IMAGE_URL = "imageUrl";

        public static Uri buildItemImageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Column index
        public static final int INDEX_COL_CATEGORY_ID = 0;
        public static final int INDEX_COL_CATEGORY_KEY = 1;
        public static final int INDEX_COL_ITEM_IMAGE_URL = 2;
    }

    /**
     * Point of sales transaction header entry class
     */
    public static final class PointOfSalesHeaderEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(DIR_POINT_OF_SALES_HEADER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + DIR_POINT_OF_SALES_HEADER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item" + AUTHORITY + "/" + DIR_POINT_OF_SALES_HEADER;

        public static final String TABLE_NAME = "salesheadertable";

        // Column
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_INVOICE_NUMBER = "invoice_number";
        public static final String COLUMN_CUSTOMER_NAME = "customer_name";
        public static final String COLUMN_CUSTOMER_EMAIL = "customer_email";
        public static final String COLUMN_CUSTOMER_ADDRESS = "customer_address";
        public static final String COLUMN_CUSTOMER_PHONE = "customer_phone";
        public static final String COLUMN_SUBTOTAL_BEFORE_DISCOUNT_AND_TAX = "subtotal";
        public static final String COLUMN_INVOICE_DISCOUNT = "invoice_discount";
        public static final String COLUMN_DISCOUNT_VALUE = "discount_value";
        public static final String COLUMN_INVOICE_TAX = "invoice_tax";
        public static final String COLUMN_TAX_VALUE = "tax_value";
        public static final String COLUMN_INVOICE_TOTAL_AFTER_TAX = "grand_total";
        public static final String COLUMN_TYPE_OF_PAYMENT = "type_payment";
        public static final String COLUMN_NUMBER_OF_ITEMS = "number_of_items";
        public static final String COLUMN_TIME_STAMP = "time_stamp";

        public static Uri buildPosHeaderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Column Index
        public static final int INDEX_COL_POS_HEADER_ID = 0;
        public static final int INDEX_COL_DATE = 1;
        public static final int INDEX_COL_INVOICE_NUMBER = 2;
        public static final int INDEX_COL_CUSTOMER_NAME = 3;
        public static final int INDEX_COL_CUSTOMER_EMAIL = 4;
        public static final int INDEX_COL_CUSTOMER_ADDRESS = 5;
        public static final int INDEX_COL_CUSTOMER_PHONE = 6;
        public static final int INDEX_COL_SUBTOTAL_BEFORE_DISCOUNT_AND_TAX = 7;
        public static final int INDEX_COL_INVOICE_DISCOUNT = 8;
        public static final int INDEX_COL_DISCOUNT_VALUE = 9;
        public static final int INDEX_COL_INVOICE_TAX = 10;
        public static final int INDEX_COL_TAX_VALUE = 11;
        public static final int INDEX_COL_INVOICE_TOTAL_AFTER_TAX = 12;
        public static final int INDEX_COL_TYPE_OF_PAYMENT = 13;
        public static final int INDEX_COL_NUMBER_OF_ITEMS = 14;
        public static final int INDEX_COL_TIME_STAMP = 15;

    }

    /**
     * Point of sales transaction detail entry class
     */
    public static final class PointOfSalesDetailEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(DIR_POINT_OF_SALES_DETAIL).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + DIR_POINT_OF_SALES_DETAIL;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item" + AUTHORITY + "/" + DIR_POINT_OF_SALES_DETAIL;

        public static final String TABLE_NAME = "salesdetailtable";

        // Column
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_HEADER_ID = "header_id";
        public static final String COLUMN_INVOICE_NUMBER = "invoice_number";
        public static final String COLUMN_ITEM_NUMBER = "item_number";
        public static final String COLUMN_ITEM_DESCRIPTION = "item_description";
        public static final String COLUMN_UNIT_OF_MEASURE = "unit_of_measure";
        public static final String COLUMN_CURRENCY = "currency";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_DISCOUNT = "discount";
        public static final String COLUMN_DISCOUNT_VALUE = "discount_value";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUB_TOTAL_PER_ITEM = "sub_total";
        public static final String COLUMN_TIME_STAMP = "time_stamp";


        public static Uri buildPosDetailUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Column Index
        public static final int INDEX_COL_POS_DETAIL_ID = 0;
        public static final int INDEX_COL_DATE = 1;
        public static final int INDEX_COL_HEADER_ID = 2;
        public static final int INDEX_COL_INVOICE_NUMBER = 3;
        public static final int INDEX_COL_ITEM_NUMBER = 4;
        public static final int INDEX_COL_DESCRIPTION = 5;
        public static final int INDEX_COL_UNIT_OF_MEASURE = 6;
        public static final int INDEX_COL_CURRENCY = 7;
        public static final int INDEX_COL_CATEGORY = 8;
        public static final int INDEX_COL_PRICE = 9;
        public static final int INDEX_COL_DISCOUNT = 10;
        public static final int INDEX_COL_DISCOUNT_VALUE = 11;
        public static final int INDEX_COL_QUANTITY = 12;
        public static final int INDEX_COL_SUB_TOTAL_PER_ITEM = 13;
        public static final int INDEX_COL_TIME_STAMP = 14;

    }

    /**
     * Purchase receiving header entry class
     */
    public static final class PurchaseReceivingHeaderEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(DIR_PURCHASE_RECEIVING_HEADER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + DIR_PURCHASE_RECEIVING_HEADER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item" + AUTHORITY + "/" + DIR_PURCHASE_RECEIVING_HEADER;

        public static final String TABLE_NAME = "purchaseheadertable";

        // Column
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_INVOICE_NUMBER = "invoice_number";
        public static final String COLUMN_VENDOR_NAME = "vendor_name";
        public static final String COLUMN_VENDOR_EMAIL = "vendor_email";
        public static final String COLUMN_VENDOR_ADDRESS = "vendor_address";
        public static final String COLUMN_VENDOR_PHONE = "vendor_phone";
        public static final String COLUMN_SUBTOTAL_BEFORE_DISCOUNT_AND_TAX = "subtotal";
        public static final String COLUMN_INVOICE_DISCOUNT = "invoice_discount";
        public static final String COLUMN_DISCOUNT_VALUE = "discount_value";
        public static final String COLUMN_INVOICE_TAX = "invoice_tax";
        public static final String COLUMN_TAX_VALUE = "tax_value";
        public static final String COLUMN_INVOICE_TOTAL_AFTER_TAX = "grand_total";
        public static final String COLUMN_TYPE_OF_PAYMENT = "type_payment";
        public static final String COLUMN_NUMBER_OF_ITEMS = "number_of_items";
        public static final String COLUMN_TIME_STAMP = "time_stamp";

        public static Uri buildPurchaseReceivingHeader(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Column Index
        public static final int INDEX_COL_PUR_HEADER_ID = 0;
        public static final int INDEX_COL_DATE = 1;
        public static final int INDEX_COL_INVOICE_NUMBER = 2;
        public static final int INDEX_COL_VENDOR_NAME = 3;
        public static final int INDEX_COL_VENDOR_EMAIL = 4;
        public static final int INDEX_COL_VENDOR_ADDRESS = 5;
        public static final int INDEX_COL_VENDOR_PHONE = 6;
        public static final int INDEX_COL_SUBTOTAL_BEFORE_DISCOUNT_AND_TAX = 7;
        public static final int INDEX_COL_INVOICE_DISCOUNT = 8;
        public static final int INDEX_COL_DISCOUNT_VALUE = 9;
        public static final int INDEX_COL_INVOICE_TAX = 10;
        public static final int INDEX_COL_TAX_VALUE = 11;
        public static final int INDEX_COL_INVOICE_TOTAL_AFTER_TAX = 12;
        public static final int INDEX_COL_TYPE_OF_PAYMENT = 13;
        public static final int INDEX_COL_NUMBER_OF_ITEMS = 14;
        public static final int INDEX_COL_TIME_STAMP = 15;


    }

    /**
     * Purchase receiving detail entry class
     */
    public static final class PurchaseReceivingDetailEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(DIR_PURCHASE_RECEIVING_DETAIL).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + DIR_PURCHASE_RECEIVING_DETAIL;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item" + AUTHORITY + "/" + DIR_PURCHASE_RECEIVING_DETAIL;

        public static final String TABLE_NAME = "purchasedetailtable";

        // Column
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_HEADER_ID = "header_id";
        public static final String COLUMN_INVOICE_NUMBER = "invoice_number";
        public static final String COLUMN_ITEM_NUMBER = "item_number";
        public static final String COLUMN_ITEM_DESCRIPTION = "item_description";
        public static final String COLUMN_UNIT_OF_MEASURE = "unit_of_measure";
        public static final String COLUMN_CURRENCY = "currency";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_DISCOUNT = "discount";
        public static final String COLUMN_DISCOUNT_VALUE = "discount_value";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUB_TOTAL_PER_ITEM = "sub_total";
        public static final String COLUMN_TIME_STAMP = "time_stamp";


        public static Uri buildPurchaseReceivingDetail(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Column Index
        public static final int INDEX_COL_PUR_DETAIL_ID = 0;
        public static final int INDEX_COL_DATE = 1;
        public static final int INDEX_COL_HEADER_ID = 2;
        public static final int INDEX_COL_INVOICE_NUMBER = 3;
        public static final int INDEX_COL_ITEM_NUMBER = 4;
        public static final int INDEX_COL_DESCRIPTION = 5;
        public static final int INDEX_COL_UNIT_OF_MEASURE = 6;
        public static final int INDEX_COL_CURRENCY = 7;
        public static final int INDEX_COL_CATEGORY = 8;
        public static final int INDEX_COL_PRICE = 9;
        public static final int INDEX_COL_DISCOUNT = 10;
        public static final int INDEX_COL_DISCOUNT_VALUE = 11;
        public static final int INDEX_COL_QUANTITY = 12;
        public static final int INDEX_COL_SUB_TOTAL_PER_ITEM = 13;
        public static final int INDEX_COL_TIME_STAMP = 14;

    }

    /**
     * Inventory detail
     */
    public static final class InventoryDetailEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(DIR_INVENTORY_DETAIL_TRANSACTION).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + DIR_INVENTORY_DETAIL_TRANSACTION;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item" + AUTHORITY + "/" + DIR_INVENTORY_DETAIL_TRANSACTION;

        public static final String TABLE_NAME = "inventorydetailtable";

        // Column
        public static final String COL_DATE = "date";
        public static final String COL_HEADER_ID = "header_id";
        public static final String COL_INVOICE_NUMBER = "invoice_number";
        public static final String COL_ITEM_NUMBER = "item_number";
        public static final String COL_ITEM_DESCRIPTION = "item_description";
        public static final String COL_UNIT_OF_MEASURE = "unit_of_measure";
        public static final String COL_CURRENCY = "currency";
        public static final String COL_CATEGORY = "category";
        public static final String COL_PRICE = "price";
        public static final String COL_DISCOUNT = "discount";
        public static final String COL_DISCOUNT_VALUE = "discount_value";
        public static final String COL_QUANTITY = "quantity";
        public static final String COL_SUB_TOTAL_PER_ITEM = "sub_total";
        public static final String COL_TIME_STAMP = "time_stamp";
        public static final String COL_TYPE_TRANSACTION = "type_trans";

        public static Uri buildInventoryDetail(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Column Index
        public static final int INDEX_COL_PUR_DETAIL_ID = 0;
        public static final int INDEX_COL_DATE = 1;
        public static final int INDEX_COL_HEADER_ID = 2;
        public static final int INDEX_COL_INVOICE_NUMBER = 3;
        public static final int INDEX_COL_ITEM_NUMBER = 4;
        public static final int INDEX_COL_DESCRIPTION = 5;
        public static final int INDEX_COL_UNIT_OF_MEASURE = 6;
        public static final int INDEX_COL_CURRENCY = 7;
        public static final int INDEX_COL_CATEGORY = 8;
        public static final int INDEX_COL_PRICE = 9;
        public static final int INDEX_COL_DISCOUNT = 10;
        public static final int INDEX_COL_DISCOUNT_VALUE = 11;
        public static final int INDEX_COL_QUANTITY = 12;
        public static final int INDEX_COL_SUB_TOTAL_PER_ITEM = 13;
        public static final int INDEX_COL_TIME_STAMP = 14;
        public static final int INDEX_COL_TYPE_TRANSACTION = 15;

    }

}
