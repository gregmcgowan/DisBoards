package com.drownedinsound.data.network.handlers;

import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.parser.streaming.BoardPostParser;
import com.drownedinsound.events.FailedToThisThisEvent;
import com.drownedinsound.events.UserIsNotLoggedInEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

public class ThisACommentHandler extends OkHttpAsyncResponseHandler {

    private String postID;

    private BoardType boardType;

    public ThisACommentHandler(String postID, BoardType boardType) {
        this.postID = postID;
        this.boardType = boardType;
        setUpdateUI(true);
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        BoardPost boardPost;
        if (inputStream != null) {
            BoardPostParser boardPostParser = new BoardPostParser(userSessionManager, inputStream,
                    postID, boardType);
            boardPost = boardPostParser.parse();
            if (boardPost != null) {
                databaseHelper.setBoardPost(boardPost);
                if (isUpdateUI()) {
//                    eventBus.post(
//                            new RetrievedBoardPostEvent(boardPost, false, false));
                }
            }
        } else {
            eventBus.post(new UserIsNotLoggedInEvent());
        }
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        eventBus.post(new FailedToThisThisEvent());
    }
}
