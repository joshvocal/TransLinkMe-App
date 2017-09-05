package me.joshvocal.translinkme_app.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

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

    private RecyclerView.Adapter mAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    private InternetConnectivity mInternetConnectivity;

    private String mCurrentLatitude;
    private String mCurrentLongitude;

    private FusedLocationProviderClient mFusedLocationClient;

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
                .appendQueryParameter("lat", mCurrentLatitude)
                .appendQueryParameter("long", mCurrentLongitude)
                .appendQueryParameter("radius", "25");

        Log.d("DDDDDDDDDDDDDDD", realTimeTransitInformationBuilder.build().toString());


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

        }
    }

    @Override
    public void onLoaderReset(Loader<List<BusStop>> loader) {
        // Empty
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch directions


                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(getContext(), "Enable permissions for directions", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate: {

                if (ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

                    // If you do not have permission, request it
                    ActivityCompat.requestPermissions((Activity) getContext(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_FINE_LOCATION_PERMISSION);

                } else {

                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        mCurrentLatitude = Double.toString(location.getLatitude());
                                        mCurrentLongitude = Double.toString(location.getLongitude());
                                        Toast.makeText(getContext(), mCurrentLatitude + mCurrentLongitude, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.restartLoader(BUS_STOP_LOCATION_LOADER_ID, null, this);
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
