package com.drownedinsound.events;

import com.drownedinsound.data.model.BoardPost;

public class RetrievedBoardPostEvent {

    private BoardPost boardPost;

    private boolean isCached;

    private String commentIDToScrollTo;

    private boolean displayGotToLatestCommentOption;

    private int uiId;

    public RetrievedBoardPostEvent(BoardPost boardPost, boolean isCached,
            boolean showGotToLastCommentOption, int uiId) {
        setBoardPost(boardPost);
        setCached(isCached);
        setDisplayGotToLatestCommentOption(showGotToLastCommentOption);
        setUiId(uiId);
    }

    public BoardPost getBoardPost() {
        return boardPost;
    }

    private void setBoardPost(BoardPost boardPost) {
        this.boardPost = boardPost;
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean isCached) {
        this.isCached = isCached;
    }

    public String getCommentIDToScrollTo() {
        return commentIDToScrollTo;
    }

    public void setCommentIDToScrollTo(String commentIDToScrollTo) {
        this.commentIDToScrollTo = commentIDToScrollTo;
    }

    public boolean isDisplayGotToLatestCommentOption() {
        return displayGotToLatestCommentOption;
    }

    public void setDisplayGotToLatestCommentOption(
            boolean displayGotToLatestCommentOption) {
        this.displayGotToLatestCommentOption = displayGotToLatestCommentOption;
    }


    public int getUiId() {
        return uiId;
    }

    public void setUiId(int uiId) {
        this.uiId = uiId;
    }
}
