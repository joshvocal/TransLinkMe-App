package me.joshvocal.translinkme_app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.joshvocal.translinkme_app.R;
import me.joshvocal.translinkme_app.adapter.BusStopItemAdapter;
import me.joshvocal.translinkme_app.model.BusStop;
import me.joshvocal.translinkme_app.network.BusStopRadiusLoader;
import me.joshvocal.translinkme_app.utils.InternetConnectivity;

import static me.joshvocal.translinkme_app.utils.Constants.BUS_STOP_LOCATION_LOADER_ID;
import static me.joshvocal.translinkme_app.utils.Constants.REQUEST_FINE_LOCATION_PERMISSION;
import static me.joshvocal.translinkme_app.utils.Constants.TRANSLINK_OPEN_API_KEY;


public class LocationFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<BusStop>> {

    // Bind RecyclerView
    @BindView(R.id.fragment_location_recycler_view)
    RecyclerView mRecyclerView;

    // Bind ProgressBar
    @BindView(R.id.fragment_location_progress_bar)
    ProgressBar mProgressBar;

    // Bind LinearLayout
    @BindView(R.id.fragment_location_welcome_layout)
    LinearLayout mLocationWelcomeLinearLayout;

    @BindView(R.id.fragment_location_no_bus_stops_near_layout)
    LinearLayout mLocationNoBusStopsNearLinearLayout;

    private String LOG_TAG = LocationFragment.class.getSimpleName();

    private RecyclerView.Adapter mAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    private InternetConnectivity mInternetConnectivity;

    /**
     * Provides the entry point to the Fused Locaiton Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    private Location mLastLocation;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        // ButterKnife Bind
        ButterKnife.bind(this, rootView);

        // Use a linear layout manager
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Get location services
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        // Decorate the recycler view
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Check for internet connectivity
        mInternetConnectivity = new InternetConnectivity(getContext());

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                        }
                    }
                });
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions((Activity) getContext(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION_PERMISSION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            // Request Permission

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_location_fragment, menu);
    }

    @Override
    public Loader<List<BusStop>> onCreateLoader(int id, Bundle args) {
        Uri.Builder realTimeTransitInformationBuilder = new Uri.Builder();

        realTimeTransitInformationBuilder.scheme("https")
                .authority("api.translink.ca")
                .appendPath("rttiapi")
                .appendPath("v1")
                .appendPath("stops")
                .appendQueryParameter("apikey", TRANSLINK_OPEN_API_KEY)
                .appendQueryParameter("lat", String.format(Locale.CANADA, "%.4f", mLastLocation.getLatitude()))
                .appendQueryParameter("long", String.format(Locale.CANADA, "%.4f", mLastLocation.getLongitude()))
                .appendQueryParameter("radius", "200");

        Log.d(LOG_TAG, realTimeTransitInformationBuilder.build().toString());

        return new BusStopRadiusLoader(
                getActivity(),
                realTimeTransitInformationBuilder.build().toString());
    }

    @Override
    public void onLoadFinished(Loader<List<BusStop>> loader, List<BusStop> data) {
        mProgressBar.setVisibility(View.GONE);

        if (data != null && !data.isEmpty()) {
            mLocationWelcomeLinearLayout.setVisibility(View.GONE);
            mAdapter = new BusStopItemAdapter(getContext(), data);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mLocationWelcomeLinearLayout.setVisibility(View.GONE);
            mLocationNoBusStopsNearLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BusStop>> loader) {
        // Empty
    }

    /**
     * Callback received when a permission request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case REQUEST_FINE_LOCATION_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                } else {
                    // Permission denied.
                    Toast.makeText(getContext(), "Permission Denied.", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_locate: {

                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    getLastLocation();

                    if (mLastLocation != null) {
                        Toast.makeText(getContext(), Double.toString(mLastLocation.getLongitude()), Toast.LENGTH_SHORT).show();
                        LoaderManager loaderManager = getLoaderManager();
                        loaderManager.restartLoader(BUS_STOP_LOCATION_LOADER_ID, null, this);
                    } else {
                        Toast.makeText(getContext(), "Unable to determine location.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }

        return super.onOptionsItemSelected(item);
    }
}
