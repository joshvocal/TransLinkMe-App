package me.joshvocal.translinkme_app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.joshvocal.translinkme_app.R;
import me.joshvocal.translinkme_app.model.BusStop;

public class LocationFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<BusStop>> {

    // Bind RecyclerView

    private RecyclerView.Adapter mAdapter;


    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        return rootView;
    }


    @Override
    public Loader<List<BusStop>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<BusStop>> loader, List<BusStop> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<BusStop>> loader) {

    }
}
