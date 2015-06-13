package com.drownedinsound.ui.post;

import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.ui.base.Ui;

import java.util.List;

/**
 * Created by gregmcgowan on 05/04/15.
 */
public interface BoardPostListUi extends Ui {

    public Board getBoardList();
    public void setBoardPosts(List<BoardPost> boardPosts);
    public void appendBoardPosts(List<BoardPost> boardPosts);
    public void showLoadingProgress(boolean show);
    public void showErrorView();

}
