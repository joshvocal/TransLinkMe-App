package me.joshvocal.translinkme_app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by josh on 9/4/17.
 */

public class BusDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BusDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "transit.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 2;

    /**
     * Constructs a new instance of {@link BusDbHelper}
     *
     * @param context of the app
     */
    public BusDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     *
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String the contains the SQL statement to create the favourite buses table
        String SQL_CREATE_FAVOURITE_BUSES_TABLE = "CREATE TABLE " + BusContract.BusEntry.TABLE_NAME + " ("
                + BusContract.BusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BusContract.BusEntry.COLUMN_BUS_NUMBER + " INTEGER NOT NULL UNIQUE );";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_BUSES_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to be done here.
    }
}
