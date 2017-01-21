package com.drownedinsound.ui.base;

import com.drownedinsound.data.generatered.BoardPostList;


public interface Navigator {

    void showMainScreen();

    void showBoardPostScreen(@BoardPostList.BoardPostListType String boardListType , String boardPostId);

    void showNotLoggedInUI();

    void hideCurrentScreen();

    void showErrorMessageDialog(int stringID);

    void showFeatureExpiredUI();
}
