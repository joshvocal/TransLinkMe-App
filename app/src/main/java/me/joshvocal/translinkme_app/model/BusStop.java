package me.joshvocal.translinkme_app.model;

/**
 * Created by josh on 9/4/17.
 */

public class BusStop {

    private String mName;
    private String mRoutes;
    private String mStopNumber;
    private String mAtStreet;
    private String mOnStreet;


    public BusStop(String name, String routes, String stopNumber, String atStreet, String onStreet) {
        mName = name;
        mRoutes = routes;
        mStopNumber = stopNumber;
        mAtStreet = atStreet;
        mOnStreet = onStreet;
    }

    public String getName() {
        return mName;
    }

    public String getRoutes() {
        return mRoutes;
    }

    public String getStopNumber() {
        return mStopNumber;
    }

    public String getAtStreet() {
        return mAtStreet;
    }

    public String getOnStreet() {
        return mOnStreet;
    }
}
