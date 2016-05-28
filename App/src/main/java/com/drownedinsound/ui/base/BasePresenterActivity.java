package com.drownedinsound.ui.base;

import android.os.Bundle;

/**
 * Created by gregmcgowan on 19/05/2016.
 */
public abstract class BasePresenterActivity <P extends BasePresenter> extends BaseActivity {

    protected abstract P getPresenter();

    private AndroidDisplay display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        display = new AndroidDisplay(this);
        getPresenter().onUiCreated(this);
    }

    @Override
    public void onPause() {
        getPresenter().onPause();
        getPresenter().detachUi(this);
        getPresenter().detachDisplay(display);
        super.onPause();
    }

    @Override
    public void onResume() {
        getPresenter().attachUi(this);
        getPresenter().attachDisplay(display);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        display = null;
        getPresenter().onDestroy();
    }
}
