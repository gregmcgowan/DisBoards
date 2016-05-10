package com.drownedinsound.ui.addPost;

import com.drownedinsound.ui.base.Ui;

/**
 * Created by gregmcgowan on 28/01/16.
 */
public interface AddPostUI extends Ui {

    void showLoadingProgress(boolean show);
    void handleNewPostFailure();
}
