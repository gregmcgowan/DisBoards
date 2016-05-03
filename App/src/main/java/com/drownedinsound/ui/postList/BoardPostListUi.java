package com.drownedinsound.ui.postList;


import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.ui.base.Ui;

import java.util.List;

/**
 * Created by gregmcgowan on 05/04/15.
 */
public interface BoardPostListUi extends Ui {

    @BoardPostList.BoardPostListType String getBoardListType();

    void setBoardPostSummaries(List<BoardPostSummary> boardPostsSummaries);

    void appendBoardPostSummaries(List<BoardPostSummary> boardPostsSummaries);

    void stopEndlessLoadingUI();

    void showLoadingProgress(boolean show);

    void showErrorView();

    int getPageIndex();

    void scrollToPostAt(int position);

    void onDisplay();

    boolean isDisplayed();
}
