package com.drownedinsound.ui.base;

import android.app.Activity;
import android.content.Intent;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import timber.log.Timber;

/**
 * Created by gregmcgowan on 22/03/15.
 */
public abstract class BaseUIController {

    private final Set<Ui> mUis;

    public BaseUIController() {
        mUis = new CopyOnWriteArraySet<>();
    }

    public synchronized final void attachUi(Ui ui) {
        mUis.add(ui);
        onUiAttached(ui);
    }

    public synchronized final void detachUi(Ui ui) {
        onUiDetached(ui);

        mUis.remove(ui);

        if (ui instanceof Activity) {
            Activity activity = (Activity) ui;
            if (activity.isFinishing()) {
                Timber.d("Activity is finishing");
            } else {
                Timber.d("Activity is not finishing");
            }
        }
    }

    protected int getId(Ui ui) {
        return ui.hashCode();
    }

    protected synchronized Ui findUi(final int id) {
        for (Ui ui : mUis) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

    protected Activity getActivityUI() {
        for (Ui ui : mUis) {
            if (ui instanceof Activity) {
                return (Activity) ui;
            }
        }
        return null;
    }


    public abstract void init(Intent intent);

    public abstract void onPause();

    public abstract void onDestroy();

    public abstract void onUiAttached(Ui ui);

    public abstract void onUiDetached(Ui ui);
}
