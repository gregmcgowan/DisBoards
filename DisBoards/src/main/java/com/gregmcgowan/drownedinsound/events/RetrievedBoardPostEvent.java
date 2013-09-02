package com.gregmcgowan.drownedinsound.events;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;

public class RetrievedBoardPostEvent {

    private BoardPost boardPost;
    private boolean isCached;
    private String commentIDToScrollTo;
    private boolean displayGotToLatestCommentOption;

    public RetrievedBoardPostEvent(BoardPost boardPost, boolean isCached,
                                   boolean showGotToLastCommentOption) {
        setBoardPost(boardPost);
        setCached(isCached);
        setDisplayGotToLatestCommentOption(showGotToLastCommentOption);
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
}
