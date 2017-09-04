package me.joshvocal.translinkme_app.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.joshvocal.translinkme_app.R;
import me.joshvocal.translinkme_app.adapter.BusStopItemAdapter;
import me.joshvocal.translinkme_app.model.BusStop;
import me.joshvocal.translinkme_app.network.BusStopLoader;
import me.joshvocal.translinkme_app.utils.InternetConnectivity;

import static me.joshvocal.translinkme_app.utils.Constants.BUS_STOP_SEARCH_LOADER_ID;
import static me.joshvocal.translinkme_app.utils.Constants.TRANSLINK_OPEN_API_KEY;

public class SearchFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<BusStop>>, SearchView.OnQueryTextListener {

    // Bind RecyclerView
    @BindView(R.id.fragment_search_recycler_view)
    RecyclerView mRecyclerView;

    // Bind ProgressBar
    @BindView(R.id.fragment_search_progress_bar)
    ProgressBar mProgressBar;

    // Bind LinearLayout
    @BindView(R.id.fragment_search_welcome_layout)
    LinearLayout mWelcomeLayout;

    private RecyclerView.Adapter mAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    private InternetConnectivity mInternetConnectivity;

    private SearchView mSearchView;

    private String mBusStopNumber;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // ButterKnife bind
        ButterKnife.bind(this, rootView);

        // Use a linear layout manager
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

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

        inflater.inflate(R.menu.menu_search_view, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        mSearchView = (SearchView) menuItem.getActionView();
        mSearchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSearchView.setQueryHint(getString(R.string.fragment_search_search_view_hint));
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public Loader<List<BusStop>> onCreateLoader(int id, Bundle args) {
        Uri.Builder realTimeTransitInformationBuilder = new Uri.Builder();

        realTimeTransitInformationBuilder.scheme("https")
                .authority("api.translink.ca")
                .appendPath("rttiapi")
                .appendPath("v1")
                .appendPath("stops")
                .appendPath(mBusStopNumber)
                .appendQueryParameter("apikey", TRANSLINK_OPEN_API_KEY);

        return new BusStopLoader(
                getActivity(),
                realTimeTransitInformationBuilder.build().toString());
    }

    @Override
    public void onLoadFinished(Loader<List<BusStop>> loader, List<BusStop> data) {
        mProgressBar.setVisibility(View.GONE);

        if (data != null && !data.isEmpty()) {
            mAdapter = new BusStopItemAdapter(getContext(), data);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BusStop>> loader) {
        // Empty
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        mBusStopNumber = query;

        if (mInternetConnectivity.isConnected()) {
            mWelcomeLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);

            getLoaderManager().restartLoader(BUS_STOP_SEARCH_LOADER_ID, null, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);

        }

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
