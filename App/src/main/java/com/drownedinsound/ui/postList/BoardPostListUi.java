package com.drownedinsound.ui.postList;

import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.ui.base.Ui;

import java.util.List;

/**
 * Created by gregmcgowan on 05/04/15.
 */
public interface BoardPostListUi extends Ui {

     Board getBoardList();
     void setBoardPosts(List<BoardPost> boardPosts);
     void appendBoardPosts(List<BoardPost> boardPosts);
     void showLoadingProgress(boolean show);
     void showErrorView();
     int  getPageIndex();

}
