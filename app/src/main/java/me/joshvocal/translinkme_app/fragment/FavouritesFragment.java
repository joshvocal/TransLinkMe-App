package me.joshvocal.translinkme_app.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.joshvocal.translinkme_app.R;
import me.joshvocal.translinkme_app.adapter.BusStopItemAdapter;
import me.joshvocal.translinkme_app.data.BusContract;
import me.joshvocal.translinkme_app.model.BusStop;
import me.joshvocal.translinkme_app.network.BusStopFavouriteLoader;
import me.joshvocal.translinkme_app.utils.InternetConnectivity;

import static me.joshvocal.translinkme_app.utils.Constants.BUS_STOP_FAVOURITE_LOADER_ID;
import static me.joshvocal.translinkme_app.utils.Constants.TRANSLINK_OPEN_API_KEY;

public class FavouritesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<BusStop>> {

    // Bind LinearLayout
    @BindView(R.id.fragment_favourites_empty_layout)
    LinearLayout mEmptyFavouriteLinearLayout;

    @BindView(R.id.fragment_favourites_no_internet_layout)
    LinearLayout mNoInternetLinearLayout;

    // Bind RecyclerView
    @BindView(R.id.fragment_favourites_recycler_view)
    RecyclerView mRecyclerView;

    // Bind ProgressBar
    @BindView(R.id.fragment_favourite_progress_bar)
    ProgressBar mProgressBar;

    private RecyclerView.Adapter mAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    private InternetConnectivity mInternetConnectivity;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);

        // Bind ButterKnife
        ButterKnife.bind(this, rootView);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Decorate the recycler view
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mInternetConnectivity = new InternetConnectivity(getContext());

        if (mInternetConnectivity.isConnected()) {
            getLoaderManager().restartLoader(BUS_STOP_FAVOURITE_LOADER_ID, null, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mNoInternetLinearLayout.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public Loader<List<BusStop>> onCreateLoader(int id, Bundle args) {
        List<String> busStopNumbers = new ArrayList<>();

        //Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BusContract.BusEntry._ID,
                BusContract.BusEntry.COLUMN_BUS_NUMBER
        };

        Cursor cursor = getContext().getContentResolver().query(
                BusContract.BusEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        if (null != cursor && cursor.getCount() >= 1) {

            int index = cursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_NUMBER);

            while (cursor.moveToNext()) {

                Uri.Builder realTimeTransitInformationBuilder = new Uri.Builder();

                realTimeTransitInformationBuilder.scheme("https")
                        .authority("api.translink.ca")
                        .appendPath("rttiapi")
                        .appendPath("v1")
                        .appendPath("stops")
                        .appendPath(cursor.getString(index))
                        .appendQueryParameter("apikey", TRANSLINK_OPEN_API_KEY);

                busStopNumbers.add(realTimeTransitInformationBuilder.build().toString());
            }

            cursor.close();
        }

        return new BusStopFavouriteLoader(getActivity(), busStopNumbers);
    }

    @Override
    public void onLoadFinished(Loader<List<BusStop>> loader, List<BusStop> data) {
        mProgressBar.setVisibility(View.GONE);

        if (data != null && !data.isEmpty()) {
            mEmptyFavouriteLinearLayout.setVisibility(View.GONE);
            mNoInternetLinearLayout.setVisibility(View.GONE);

            mAdapter = new BusStopItemAdapter(getContext(), data);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mEmptyFavouriteLinearLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BusStop>> loader) {
        // Empty
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null) {
            getLoaderManager().restartLoader(BUS_STOP_FAVOURITE_LOADER_ID, null, this);
        }
    }
}
