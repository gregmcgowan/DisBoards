package com.drownedinsound.ui.start;

import com.drownedinsound.ui.base.Ui;

/**
 * Created by gregmcgowan on 01/11/15.
 */
public interface LoginUi extends Ui {

    void showLoadingProgress(boolean visible);

    void handleLoginSuccess();

    void handleLoginFailure();

}
