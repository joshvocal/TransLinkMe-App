package me.joshvocal.translinkme_app.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by josh on 9/4/17.
 */

public class BusProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BusProvider.class.getSimpleName();

    /**
     * Database helper object
     */
    private BusDbHelper mDbHelper;

    /**
     * URI matcher code for the content URI for the buses table
     */
    private static final int BUSES = 100;

    /**
     * URI matcher code for the content URI for a single bus in the buses table
     */
    private static final int BUS_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passes into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This will run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding  code to return
        // when a match is found

        sUriMatcher.addURI(BusContract.CONTENT_AUTHORITY, BusContract.PATH_BUSES, BUSES);
        sUriMatcher.addURI(BusContract.CONTENT_AUTHORITY, BusContract.PATH_BUSES + "/#", BUSES);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods
        mDbHelper = new BusDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection,
     * selection arguments and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query.
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code.
        switch (sUriMatcher.match(uri)) {
            case BUSES:
                // For the BUSES code, query the buses table directly with the given
                // projection, selection, selection arguments, and sort order. the cursor
                // could contain multiple rows of the buses table.
                cursor = database.query(
                        BusContract.BusEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BUS_ID:
                // For the BUS_ID code, extract out the ID from the URI.
                selection = BusContract.BusEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the buses table where the _id equals 3 to return a
                // Cursor containing the row of the table.
                cursor = database.query(
                        BusContract.BusEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to updated the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor.
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BUSES:
                return BusContract.BusEntry.CONTENT_LIST_TYPE;
            case BUS_ID:
                return BusContract.BusEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (sUriMatcher.match(uri)) {
            case BUSES:
                return insertBus(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBus(Uri uri, ContentValues values) {
        // Check that the name is not null.
        String number = values.getAsString(BusContract.BusEntry.COLUMN_BUS_NUMBER);
        if (number == null) {
            throw new IllegalArgumentException("Bus requires a number");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new bus with the give values.
        long id = database.insert(BusContract.BusEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the bus content URI.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end.
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArguments) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted.
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case BUSES:
                rowsDeleted = database.delete(
                        BusContract.BusEntry.TABLE_NAME,
                        selection,
                        selectionArguments);
                break;
            case BUS_ID:
                selection = BusContract.BusEntry._ID + "=?";
                selectionArguments = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(
                        BusContract.BusEntry.TABLE_NAME,
                        selection,
                        selectionArguments);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Updates the data at the given selection and selections arguments, with the new Content Values.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}
