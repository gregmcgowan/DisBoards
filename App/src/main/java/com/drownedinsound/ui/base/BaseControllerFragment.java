package com.drownedinsound.ui.base;
/**
 * Created by gregmcgowan on 22/03/15.
 */
public abstract class BaseControllerFragment<C extends BaseUIController> extends BaseFragment {

    protected abstract C getController();

    @Override
    public void onPause() {
        getController().onPause();
        getController().detachUi(this);

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().attachUi(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getController().onDestroy();
    }




}
