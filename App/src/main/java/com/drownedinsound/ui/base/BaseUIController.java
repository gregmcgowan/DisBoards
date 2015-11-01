package com.drownedinsound.ui.base;

import android.content.Intent;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

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

    protected Set<Ui> getUis() {
        return Collections.unmodifiableSet(mUis);
    }

    protected <T extends Ui> T findUi(Class<T> uiClss) {
        for (Ui ui : mUis) {
            if (uiClss.isInstance(ui)) {
                return (T) ui;
            }
        }
        return null;
    }

    public void init(Intent intent) {

    }

    public void onPause() {

    }

    public void onDestroy() {

    }

    public void onUiAttached(Ui ui) {

    }

    public void onUiDetached(Ui ui) {

    }
}
