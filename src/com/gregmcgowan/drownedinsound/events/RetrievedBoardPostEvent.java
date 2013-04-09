package com.gregmcgowan.drownedinsound.events;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;

public class RetrievedBoardPostEvent {

    private BoardPost boardPost;
    private boolean isCached;
    
    public RetrievedBoardPostEvent(BoardPost boardPost, boolean isCached){
	setBoardPost(boardPost);
	setCached(isCached);
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
}
