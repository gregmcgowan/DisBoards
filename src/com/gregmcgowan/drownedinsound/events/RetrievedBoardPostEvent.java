package com.gregmcgowan.drownedinsound.events;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;

public class RetrievedBoardPostEvent {

    private BoardPost boardPost;
    
    public RetrievedBoardPostEvent(BoardPost boardPost){
	this.setBoardPost(boardPost);
    }

    public BoardPost getBoardPost() {
	return boardPost;
    }

    private void setBoardPost(BoardPost boardPost) {
	this.boardPost = boardPost;
    }
}
