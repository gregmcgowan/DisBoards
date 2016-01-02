package com.drownedinsound.utils;

import android.os.Looper;


/**
 * Created by gregmcgowan on 01/01/16.
 */
public class AssertUtils {


    public static void checkMainThread() {
        if(Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("Is on Main Thread !!!");
        }
    }
}
