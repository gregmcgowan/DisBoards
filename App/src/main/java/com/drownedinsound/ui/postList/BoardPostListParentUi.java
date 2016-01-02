package com.drownedinsound.ui.postList;

import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.Ui;

import java.util.List;

/**
 * Created by gregmcgowan on 21/07/15.
 */
public interface BoardPostListParentUi extends Ui {

    boolean boardPostListShown(BoardPostListUi boardPostListUi);

    void setBoardPostLists(List<BoardPostList> boardPostListInfos);

    int getNoOfBoardListShown();

}
