package com.gregmcgowan.drownedinsound.events;

import java.util.List;

import com.gregmcgowan.drownedinsound.data.model.BoardPostSummary;

public class RetrievedBoardPostSummaryListEvent {

    private List<BoardPostSummary> boardPostSummaryList;

    private String boardId;
    
    public RetrievedBoardPostSummaryListEvent(
	    List<BoardPostSummary> boardPostSummaryList, String boardId) {
	setBoardPostSummaryList(boardPostSummaryList);
	setBoardId(boardId);
    }

    public List<BoardPostSummary> getBoardPostSummaryList() {
	return boardPostSummaryList;
    }

    public void setBoardPostSummaryList(
	    List<BoardPostSummary> boardPostSummaryList) {
	this.boardPostSummaryList = boardPostSummaryList;
    }

    public String getBoardId() {
	return boardId;
    }

    public void setBoardId(String boardId) {
	this.boardId = boardId;
    }

}
