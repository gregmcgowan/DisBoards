package com.drownedinsound.ui.base;

import android.os.Bundle;

/**
 * Created by gregmcgowan on 22/03/15.
 */
public abstract class BaseControllerFragment<C extends BaseUIController> extends BaseFragment {

    private AndroidDisplay display;

    protected abstract C getController();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        display = new AndroidDisplay(getActivity());
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
