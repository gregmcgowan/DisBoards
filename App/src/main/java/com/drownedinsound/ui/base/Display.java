package com.drownedinsound.ui.base;

import com.drownedinsound.data.generatered.BoardPostList;

/**
 * Created by gregmcgowan on 17/01/16.
 */
public interface Display {

    void showMainScreen();

    void showBoardPost(@BoardPostList.BoardPostListType String boardListType , String boardPostId);

    void showNotLoggedInUI();

    void hideCurrentScreen();

    void showErrorMessageDialog(int stringID);

    void showFeatureExpiredUI();
}
