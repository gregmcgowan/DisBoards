package com.gregmcgowan.drownedinsound.events;

import java.util.List;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;

public class RetrievedBoardPostSummaryListEvent {

    private List<BoardPost> boardPostSummaryList;

    private String boardId;
    
    public RetrievedBoardPostSummaryListEvent(
	    List<BoardPost> boardPostSummaryList, String boardId) {
	setBoardPostSummaryList(boardPostSummaryList);
	setBoardId(boardId);
    }

    public List<BoardPost> getBoardPostSummaryList() {
	return boardPostSummaryList;
    }

    public void setBoardPostSummaryList(
	    List<BoardPost> boardPostSummaryList) {
	this.boardPostSummaryList = boardPostSummaryList;
    }

    public String getBoardId() {
	return boardId;
    }

    public void setBoardId(String boardId) {
	this.boardId = boardId;
    }

}
