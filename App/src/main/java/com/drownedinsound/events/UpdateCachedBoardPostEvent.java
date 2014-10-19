package com.drownedinsound.events;

import com.drownedinsound.data.model.BoardPost;

public class UpdateCachedBoardPostEvent {

    private BoardPost boardPost;

    public UpdateCachedBoardPostEvent(BoardPost boardPost) {
        setBoardPost(boardPost);
    }

    public BoardPost getBoardPost() {
        return boardPost;
    }

    public void setBoardPost(BoardPost boardPost) {
        this.boardPost = boardPost;
    }


}
