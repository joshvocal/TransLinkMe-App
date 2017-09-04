package me.joshvocal.translinkme_app.model;

import java.util.List;

/**
 * Created by josh on 9/4/17.
 */

public class Bus {

    private String mRouteName;
    private String mRouteNumber;
    private List<String> mArrivalEstimates;

    public Bus(String routeName, String routeNumber, List<String> arrivalEstimates) {
        mRouteName = routeName;
        mRouteNumber = routeNumber;
        mArrivalEstimates = arrivalEstimates;
    }

    public String getRouteName() {
        return mRouteName;
    }

    public String getRouteNumber() {
        return mRouteNumber;
    }

    public List<String> getArrivalTimeEstimates() {
        return mArrivalEstimates;
    }
}
