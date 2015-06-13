package com.drownedinsound.events;

import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;

import java.util.List;

public class RetrievedBoardPostSummaryListEvent {

    private List<BoardPost> boardPostSummaryList;

    private BoardType boardType;

    private boolean isCached;

    private boolean append;

    private int uiID;

    public RetrievedBoardPostSummaryListEvent(
            List<BoardPost> boardPostSummaryList, BoardType boardType, boolean isCached,
            boolean append,  int uiID) {
        setBoardPostSummaryList(boardPostSummaryList);
        setBoardType(boardType);
        setCached(isCached);
        setAppend(append);
        setUiID(uiID);
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

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public int getUiID() {
        return uiID;
    }

    public void setUiID(int uiID) {
        this.uiID = uiID;
    }
}
