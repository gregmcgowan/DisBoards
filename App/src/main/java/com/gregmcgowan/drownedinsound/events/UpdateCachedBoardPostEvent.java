package com.gregmcgowan.drownedinsound.events;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;

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
