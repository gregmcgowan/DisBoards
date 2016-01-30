package com.drownedinsound.ui.post;

import com.drownedinsound.ui.base.Ui;

/**
 * Created by gregmcgowan on 14/11/15.
 */
public interface ReplyToCommentUi extends Ui {

    void showLoadingProgress(boolean show);
    void handlePostCommentFailure();
}
