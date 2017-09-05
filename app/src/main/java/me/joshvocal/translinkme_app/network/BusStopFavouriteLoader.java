package me.joshvocal.translinkme_app.network;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.joshvocal.translinkme_app.model.BusStop;
import me.joshvocal.translinkme_app.utils.QueryUtils;

/**
 * Created by josh on 9/4/17.
 */

public class BusStopFavouriteLoader extends AsyncTaskLoader<List<BusStop>> {

    private static final String LOG_TAG = BusStopFavouriteLoader.class.getName();
    private List<BusStop> mBusStopList;
    private List<String> mRealTimeStopsUrls;

    public BusStopFavouriteLoader(Context context, List<String> realTimeStopsUrl) {
        super(context);
        mRealTimeStopsUrls = realTimeStopsUrl;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<BusStop> loadInBackground() {
        if (mRealTimeStopsUrls == null) {
            return null;
        }

        mBusStopList = new ArrayList<>();

        for (String url : mRealTimeStopsUrls) {
            mBusStopList.add(fetchTransitBusStops(url));
        }

        return mBusStopList;
    }

    private BusStop fetchTransitBusStops(String realTimeStopsRequest) {

        // Create URL object
        URL realTimeStopsUrl = QueryUtils.createUrl(realTimeStopsRequest);

        // Perform HTTP request to the URL and receive a JSON response back
        String realTimeStopsResponse = null;

        try {
            realTimeStopsResponse = QueryUtils.makeHttpRequestForJson(realTimeStopsUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        BusStop transitBusStop =
                extractBusStopsFromJson(realTimeStopsResponse);

        return transitBusStop;
    }

    private BusStop extractBusStopsFromJson(String realTimeTransitInformationJson) {
        // If the JSON is empty, return null and exit early.
        if (TextUtils.isEmpty(realTimeTransitInformationJson)) {
            return null;
        }

        try {

            JSONObject currentBusStop = new JSONObject(realTimeTransitInformationJson);

            String name = null;
            if (currentBusStop.has("Name")) {
                name = currentBusStop.getString("Name");
            }

            String routes = null;
            if (currentBusStop.has("Routes")) {
                routes = currentBusStop.getString("Routes");
            }

            String stopNumber = null;
            if (currentBusStop.has("StopNo")) {
                stopNumber = currentBusStop.getString("StopNo");
            }

            String atStreet = null;
            if (currentBusStop.has("AtStreet")) {
                atStreet = currentBusStop.getString("AtStreet");
            }

            String onStreet = null;
            if (currentBusStop.has("OnStreet")) {
                onStreet = currentBusStop.getString("OnStreet");
            }

            return new BusStop(name, routes, stopNumber, atStreet, onStreet);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
