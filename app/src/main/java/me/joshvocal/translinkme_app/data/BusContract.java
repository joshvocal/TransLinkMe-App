package me.joshvocal.translinkme_app.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by josh on 9/4/17.
 */

public class BusContract {

    public static final String CONTENT_AUTHORITY = "me.joshvocal.translinkme_app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BUSES = "buses";

    private BusContract() {
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
    }

    /**
     * Inner class that defines constant values of the database table.
     * Each entry in the table represents a single bus stop number.
     */
    public static final class BusEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BUSES);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUSES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUSES;

        /**
         * Name of database table for buses
         */
        public final static String TABLE_NAME = "buses";

        /**
         * Unique ID number for the bus (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Number of the bus.
         * <p>
         * Type: STRING
         */
        public static final String COLUMN_BUS_STOP_NUMBER = "number";

        /**
         * Name of the bus.
         * <p>
         * Type: STRING
         */
        public static final String COLUMN_BUS_STOP_NAME = "name";

        /**
         * Routes of the bus.
         * <p>
         * Type: STRING
         */
        public static final String COLUMN_BUS_STOP_ROUTES = "routes";

    }
}
