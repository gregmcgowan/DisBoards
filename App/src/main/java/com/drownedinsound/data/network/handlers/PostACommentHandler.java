package com.drownedinsound.data.network.handlers;

import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.parser.streaming.BoardPostParser;
import com.drownedinsound.events.FailedToPostCommentEvent;
import com.drownedinsound.events.RetrievedBoardPostEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

public class PostACommentHandler extends OkHttpAsyncResponseHandler {

    private String postID;

    private BoardType boardType;


    public PostACommentHandler(String boardPostId, BoardType boardType) {
        this.postID = boardPostId;
        this.boardType = boardType;
        setUpdateUI(true);
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        BoardPost boardPost = null;
        if (inputStream != null) {
            BoardPostParser boardPostParser = new BoardPostParser(userSessionManager, inputStream,
                    postID, boardType);
            boardPost = boardPostParser.parse();
            if (boardPost != null) {
                databaseHelper.setBoardPost(boardPost);
            }
        }
        if (isUpdateUI()) {
//            eventBus.post(
//                    new RetrievedBoardPostEvent(boardPost, false, false));
        }
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        eventBus.post(new FailedToPostCommentEvent());
    }


}
