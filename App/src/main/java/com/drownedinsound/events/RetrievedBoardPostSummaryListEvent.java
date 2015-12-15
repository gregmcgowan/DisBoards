package com.drownedinsound.events;

import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardListType;

import java.util.List;

public class RetrievedBoardPostSummaryListEvent {

    private List<BoardPost> boardPostSummaryList;

    private BoardListType boardListType;

    private boolean isCached;

    private boolean append;

    private int uiId;

    public RetrievedBoardPostSummaryListEvent(
            List<BoardPost> boardPostSummaryList, BoardListType boardListType, boolean isCached,
            boolean append, int uiId) {
        setBoardPostSummaryList(boardPostSummaryList);
        setBoardListType(boardListType);
        setCached(isCached);
        setAppend(append);
        setUiId(uiId);
    }

    public List<BoardPost> getBoardPostSummaryList() {
        return boardPostSummaryList;
    }

    public void setBoardPostSummaryList(
            List<BoardPost> boardPostSummaryList) {
        this.boardPostSummaryList = boardPostSummaryList;
    }

    public BoardListType getBoardListType() {
        return boardListType;
    }

    public void setBoardListType(BoardListType boardListType) {
        this.boardListType = boardListType;
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean isCached) {
        this.isCached = isCached;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public int getUiId() {
        return uiId;
    }

    public void setUiId(int uiId) {
        this.uiId = uiId;
    }
}
