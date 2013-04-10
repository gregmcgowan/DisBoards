package com.gregmcgowan.drownedinsound.events;

import java.util.List;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;

public class RetrievedBoardPostSummaryListEvent {

    private List<BoardPost> boardPostSummaryList;

    private BoardType boardType;
    
    private boolean isCached;
    
    public RetrievedBoardPostSummaryListEvent(
	    List<BoardPost> boardPostSummaryList, BoardType boardType, boolean isCached) {
	setBoardPostSummaryList(boardPostSummaryList);
	setBoardType(boardType);
	setCached(isCached);
    }

    public List<BoardPost> getBoardPostSummaryList() {
	return boardPostSummaryList;
    }

    public void setBoardPostSummaryList(
	    List<BoardPost> boardPostSummaryList) {
	this.boardPostSummaryList = boardPostSummaryList;
    }

    public BoardType getBoardType() {
	return boardType;
    }

    public void setBoardType(BoardType boardType) {
	this.boardType = boardType;
    }

    public boolean isCached() {
	return isCached;
    }

    public void setCached(boolean isCached) {
	this.isCached = isCached;
    }

}
