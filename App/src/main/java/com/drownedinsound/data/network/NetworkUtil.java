package com.drownedinsound.data.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by gregmcgowan on 12/03/2016.
 */
@Singleton
public class NetworkUtil {

    private ConnectivityManager connectivityManager;

    @Inject
    public NetworkUtil(ConnectivityManager connectivityManagerCompat) {
        this.connectivityManager = connectivityManagerCompat;
    }


    public boolean isConnected(){
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

}
