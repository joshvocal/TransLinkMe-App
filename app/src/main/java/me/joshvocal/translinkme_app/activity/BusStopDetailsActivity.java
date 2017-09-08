package me.joshvocal.translinkme_app.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.joshvocal.translinkme_app.R;
import me.joshvocal.translinkme_app.adapter.BusStopDetailsAdapter;
import me.joshvocal.translinkme_app.data.BusContract;
import me.joshvocal.translinkme_app.model.Bus;
import me.joshvocal.translinkme_app.network.BusStopDetailsLoader;
import me.joshvocal.translinkme_app.utils.InternetConnectivity;

import static me.joshvocal.translinkme_app.utils.Constants.BUS_STOP_AT_STREET_KEY;
import static me.joshvocal.translinkme_app.utils.Constants.BUS_STOP_DETAILS_SEARCH_LOADER_ID;
import static me.joshvocal.translinkme_app.utils.Constants.BUS_STOP_NUMBER_KEY;
import static me.joshvocal.translinkme_app.utils.Constants.BUS_STOP_ON_STREET_KEY;
import static me.joshvocal.translinkme_app.utils.Constants.TRANSLINK_OPEN_API_KEY;

public class BusStopDetailsActivity extends AppCompatActivity implements
        View.OnClickListener, LoaderManager.LoaderCallbacks<List<Bus>>,
        SwipeRefreshLayout.OnRefreshListener {

    // Bind FloatingActionButton
    @BindView(R.id.bus_stop_details_favourite_button)
    FloatingActionButton mFavouriteButton;

    // Bind RecyclerView
    @BindView(R.id.bus_stop_details_recycler_view)
    RecyclerView mRecyclerView;

    // Bind TextView
    @BindView(R.id.bus_stop_details_on_street)
    TextView mOnStreetTextView;

    @BindView(R.id.bus_stop_details_at_street)
    TextView mAtStreetTextView;

    // Bind ProgressBar
    @BindView(R.id.bus_stop_details_progress_bar)
    ProgressBar mProgressBar;

    // Bind SwipeRefreshLayout
    @BindView(R.id.bus_stop_details_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private String mBusStopNumber;
    private String mOnStreet;
    private String mAtStreet;
    private InternetConnectivity mInternetConnectivity;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stop_details);

        // Bind ButterKnife
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.bus_stop_details_toolbar);
        setSupportActionBar(toolbar);

        // Set back Action
        ActionBar actionBar = this.getSupportActionBar();
        // Set the action bar back button to look like an up button;
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mBusStopNumber = extras.getString(BUS_STOP_NUMBER_KEY);
            setTitle("Stop: " + mBusStopNumber);
            mAtStreet = extras.getString(BUS_STOP_AT_STREET_KEY);
            mOnStreet = extras.getString(BUS_STOP_ON_STREET_KEY);

        }

        setFavouriteButtonIcon();

        mFavouriteButton.setOnClickListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mOnStreetTextView.setText(mOnStreet);
        mAtStreetTextView.setText(mAtStreet);

        // Use a linear layout manager
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mInternetConnectivity = new InternetConnectivity(this);

        if (mInternetConnectivity.isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(BUS_STOP_DETAILS_SEARCH_LOADER_ID, null, this);
        }
    }

    private void setFavouriteButtonIcon() {
        if (isBusStopFavourite()) {
            mFavouriteButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            mFavouriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }


    private boolean isBusStopFavourite() {

        String selection = BusContract.BusEntry.COLUMN_BUS_NUMBER + " =?";
        String[] selectionArgs = {mBusStopNumber};

        //Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BusContract.BusEntry._ID,
                BusContract.BusEntry.COLUMN_BUS_NUMBER
        };

        Cursor cursor = getContentResolver().query(
                BusContract.BusEntry.CONTENT_URI,   // Provider content URI to query
                projection,                         // Columns to include in the resulting Cursor
                selection,                          // No selection clause
                selectionArgs,                      // No selection arguments
                null);                              // No default sort order


        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }

        cursor.close();

        return false;
    }

    private void addBusToFavourites() {
        ContentValues values = new ContentValues();
        values.put(BusContract.BusEntry.COLUMN_BUS_NUMBER, mBusStopNumber);
        getContentResolver().insert(BusContract.BusEntry.CONTENT_URI, values);
    }

    private void deleteBusFromFavourites() {
        String[] selectionArgs = {mBusStopNumber};
        String selection = BusContract.BusEntry.COLUMN_BUS_NUMBER + "  =?";
        getContentResolver().delete(BusContract.BusEntry.CONTENT_URI, selection, selectionArgs);
    }

    private void addToFavourites() {
        if (isBusStopFavourite()) {
            // Delete
            deleteBusFromFavourites();
            Snackbar.make(findViewById(R.id.bus_stop_details_coordinator_layout),
                    getString(R.string.deleted_from_favourites), Snackbar.LENGTH_SHORT).show();
        } else {
            // Add
            addBusToFavourites();
            Snackbar.make(findViewById(R.id.bus_stop_details_coordinator_layout),
                    getString(R.string.added_to_favourites), Snackbar.LENGTH_SHORT).show();
        }

        setFavouriteButtonIcon();
    }

    @Override
    public Loader<List<Bus>> onCreateLoader(int id, Bundle args) {
        Uri.Builder realTimeTransitInformationBuilder = new Uri.Builder();

        realTimeTransitInformationBuilder.scheme("https")
                .authority("api.translink.ca")
                .appendPath("rttiapi")
                .appendPath("v1")
                .appendPath("stops")
                .appendPath(mBusStopNumber)
                .appendPath("estimates")
                .appendQueryParameter("apikey", TRANSLINK_OPEN_API_KEY);

        return new BusStopDetailsLoader(
                this,
                realTimeTransitInformationBuilder.build().toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Bus>> loader, List<Bus> data) {
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);

        if (data != null && !data.isEmpty()) {
            mAdapter = new BusStopDetailsAdapter(this, data);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Bus>> loader) {
        // Empty generated method.
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bus_stop_details_favourite_button:
                addToFavourites();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        getSupportLoaderManager().restartLoader(BUS_STOP_DETAILS_SEARCH_LOADER_ID, null, this);
    }
}
