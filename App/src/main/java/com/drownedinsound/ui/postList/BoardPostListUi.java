package com.drownedinsound.ui.postList;

import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardListType;
import com.drownedinsound.ui.base.Ui;

import java.util.List;

/**
 * Created by gregmcgowan on 05/04/15.
 */
public interface BoardPostListUi extends Ui {

    BoardListType getBoardListType();

    void setBoardPosts(List<BoardPost> boardPosts);

    void appendBoardPosts(List<BoardPost> boardPosts);

    void stopEndlessLoadingUI();

    void showLoadingProgress(boolean show);

    void showErrorView();

    int getPageIndex();

}
