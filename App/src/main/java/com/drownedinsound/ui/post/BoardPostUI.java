package com.drownedinsound.ui.post;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.ui.base.Ui;

/**
 * Created by gregmcgowan on 02/10/15.
 */
public interface BoardPostUI extends Ui {

    void showLoadingProgress(boolean show);

    void showBoardPost(BoardPost boardPost, int commentIDToShow);

    void showCachedPopup();

    void showErrorView();
}
