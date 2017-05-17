package com.divitngoc.android.udacityprojectinventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    //private InventoryContract to prevent an instance of this class
    private InventoryContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider
     */
    public static final String CONTENT_AUTHORITY = "com.divitngoc.android.udacityprojectinventoryapp";

    //Adding a scheme to CONTENT_AUTHORITY for base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Appended to base content URI for possible URI's
     * For instance, content://com.divitngoc.android.udacityprojectinventoryapp/inventory is a valid path for
     */
    public static final String PATH_INVENTORY = "inventory";

    /**
     * Inner class that defines the table contents of the inventory table.
     * Each entry in the table represents a single item in the inventory.
     */
    public static class InventoryEntry implements BaseColumns {

        /**
         * The content URI to access the inventory data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single inventory item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * Name of database table for inventory
         */
        public static final String TABLE_NAME = "inventory";

        /**
         * Unique ID number for the inventory.
         * <p>
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * Picture of the inventory.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_INVENTORY_PICTURE = "picture";

        /**
         * Name of the pet.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_INVENTORY_NAME = "name";

        /**
         * price of the inventory.
         * <p>
         * Type: DOUBLE
         */
        public static final String COLUMN_INVENTORY_PRICE = "price";

        /**
         * Supplier email of the inventory.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_INVENTORY_SUPPLIER_EMAIL = "supplier_email";

        /**
         * stock of inventory item.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_INVENTORY_STOCK = "stock";
    }

}
