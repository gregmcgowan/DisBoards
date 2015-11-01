package com.drownedinsound.ui.base;

/**
 * Created by gregmcgowan on 14/06/15.
 */
public abstract class BaseControllerActivity<C extends BaseUIController> extends BaseActivity {

    protected abstract C getController();

    @Override
    public void onPause() {
        getController().onPause();
        getController().detachUi(this);

        super.onPause();
    }

    @Override
    public void onResume() {
        getController().attachUi(this);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getController().onDestroy();
    }

}
