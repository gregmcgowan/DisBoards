package com.gregmcgowan.drownedinsound.utils;

import com.crashlytics.android.Crashlytics;


import timber.log.Timber;

/**
 * Created by gregmcgowan on 11/10/2014.
 */
public class CrashlyticsTree extends Timber.HollowTree {

    @Override
    public void i(String message, Object... args) {
        Crashlytics.log(String.format(message,args));
    }

    @Override
    public void e(String message, Object... args) {
        super.e(message, args);
        Crashlytics.log(String.format(message,args));
    }

    @Override
    public void e(Throwable t, String message, Object... args) {
        super.e(t, message, args);
        Crashlytics.logException(t);
    }
}
