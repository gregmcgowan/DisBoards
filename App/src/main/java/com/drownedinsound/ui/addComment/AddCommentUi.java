package com.drownedinsound.ui.addComment;

import com.drownedinsound.ui.base.Ui;

/**
 * Created by gregmcgowan on 14/11/15.
 */
public interface AddCommentUi extends Ui {

    void showLoadingProgress(boolean show);
    void handlePostCommentFailure();
}
