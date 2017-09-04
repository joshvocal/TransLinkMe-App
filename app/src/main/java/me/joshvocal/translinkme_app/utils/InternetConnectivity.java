package me.joshvocal.translinkme_app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by josh on 9/4/17.
 */

public class InternetConnectivity {

    private final Context mContext;

    public InternetConnectivity(Context context) {
        this.mContext = context;
    }

    public boolean isConnected() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
}
