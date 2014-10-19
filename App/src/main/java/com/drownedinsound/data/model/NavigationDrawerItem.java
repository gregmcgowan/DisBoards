package com.drownedinsound.data.model;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Created by gregmcgowan on 08/09/2013.
 */
public abstract class NavigationDrawerItem {

    private WeakReference<Context> contextWeakReference;

    private String displayText;

    public NavigationDrawerItem(String displayText, WeakReference<Context> contextWeakReference) {
        this.displayText = displayText;
        this.contextWeakReference = contextWeakReference;
    }

    public String getDisplayText() {
        return displayText;
    }

    protected Context getContext() {
        Context context = null;
        if (contextWeakReference != null) {
            context = contextWeakReference.get();
        }
        return context;
    }


    public abstract void doNavigationDrawerItemSelectedAction();
}
