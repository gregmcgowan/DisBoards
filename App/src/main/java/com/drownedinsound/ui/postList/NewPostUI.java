package com.drownedinsound.ui.postList;

import com.drownedinsound.ui.base.Ui;

/**
 * Created by gregmcgowan on 28/01/16.
 */
public interface NewPostUI extends Ui {

    void showLoadingProgress(boolean show);
    void handleNewPostFailure();
}
