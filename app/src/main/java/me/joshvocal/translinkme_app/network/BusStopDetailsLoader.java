package me.joshvocal.translinkme_app.network;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.joshvocal.translinkme_app.model.Bus;
import me.joshvocal.translinkme_app.utils.QueryUtils;

/**
 * Created by josh on 9/4/17.
 */

public class BusStopDetailsLoader extends AsyncTaskLoader<List<Bus>> {

    private static final String LOG_TAG = BusStopDetailsLoader.class.getName();
    private List<Bus> mBusList;
    private String mRealTimeStopEstimates;

    public BusStopDetailsLoader(Context context, String realTimeStopEstimates) {
        super(context);
        mRealTimeStopEstimates = realTimeStopEstimates;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Bus> loadInBackground() {
        if (mRealTimeStopEstimates == null) {
            return null;
        }

        mBusList = fetchTransitBusStopEstimates(mRealTimeStopEstimates);

        return mBusList;
    }

    private List<Bus> fetchTransitBusStopEstimates(String realTimeStopEstimatesRequest) {
        // Create URL object
        URL realTimeStopEstimatesUrl = QueryUtils.createUrl(realTimeStopEstimatesRequest);

        // Perform HTTP request to the URL and receive a JSON response back.
        String realTimeStopEstimatesResponse = null;

        try {
            realTimeStopEstimatesResponse =
                    QueryUtils.makeHttpRequestForJson(realTimeStopEstimatesUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Bus> transitBusEstimatesList =
                extractBusStopEstimatesFromJson(realTimeStopEstimatesResponse);

        return transitBusEstimatesList;
    }

    private List<Bus> extractBusStopEstimatesFromJson(String realTimeStopEstimatesJson) {
        // If the JSON is empty, return null and exit early.
        if (TextUtils.isEmpty(realTimeStopEstimatesJson)) {
            return null;
        }

        List<Bus> busEstimatesList = new ArrayList<>();

        try {

            JSONArray busStopEstimatesArray = new JSONArray(realTimeStopEstimatesJson);

            for (int i = 0; i < busStopEstimatesArray.length(); i++) {
                // The current index of the array
                JSONObject currentBusEstimate = busStopEstimatesArray.getJSONObject(i);

                String routeNumber = null;
                if (currentBusEstimate.has("RouteNo")) {
                    routeNumber = currentBusEstimate.getString("RouteNo");
                }

                String routeName = null;
                if (currentBusEstimate.has("RouteName")) {
                    routeName = currentBusEstimate.getString("RouteName");
                }

                List<String> expectedLeaveTimeList = new ArrayList<>();

                if (currentBusEstimate.has("Schedules")) {

                    JSONArray schedulesArray = currentBusEstimate.getJSONArray("Schedules");

                    for (int j = 0; j < schedulesArray.length(); j++) {
                        JSONObject currentSchedule = schedulesArray.getJSONObject(j);

                        if (currentSchedule.has("ExpectedLeaveTime")) {
                            expectedLeaveTimeList.add(currentSchedule.getString("ExpectedLeaveTime"));
                        }
                    }
                }

                busEstimatesList.add(new Bus(routeName, routeNumber, expectedLeaveTimeList));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return busEstimatesList;
    }
}
