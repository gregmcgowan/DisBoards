package com.drownedinsound.ui.base;

import android.os.Bundle;

/**
 * Created by gregmcgowan on 14/06/15.
 */
public abstract class BaseControllerActivity<C extends BaseUIController> extends BaseActivity {

    protected abstract C getController();

    private AndroidDisplay display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        display = new AndroidDisplay(this);
        getController().onUiCreated(this);
    }

    @Override
    public void onPause() {
        getController().onPause();
        getController().detachUi(this);
        getController().detachDisplay(display);
        super.onPause();
    }

    @Override
    public void onResume() {
        getController().attachUi(this);
        getController().attachDisplay(display);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        display = null;
        getController().onDestroy();
    }

}
