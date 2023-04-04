package com.example.fibath.classes.networking.verification;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Looser on 6/7/2018.
 */

public class DeviceNetworkingVerifier {
    private static String networkProvider;

    public DeviceNetworkingVerifier() {
        this.networkProvider = "NO_NETWORK";
    }

    public static String checkNetworkStatus(final Context context) {

        // GET CONNECTIVITY MANAGER
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // CHECK FOR WIFI
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // CHECK FOR MOBILE DATA
        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable()) {
            networkProvider = "WIFI";
        } else if (mobile.isAvailable()) {
            networkProvider = "MOBILE_DATA";
        } else {
            networkProvider = "NO_NETWORK";
        }
        return networkProvider;
    }

    // CHECK FOR ALL POSSIBLE INTERNET PROVIDERS
    public boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }
}
