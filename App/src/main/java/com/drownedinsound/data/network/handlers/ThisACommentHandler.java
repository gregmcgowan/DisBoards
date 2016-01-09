package com.drownedinsound.data.network.handlers;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.parser.streaming.BoardPostParser;
import com.drownedinsound.events.FailedToThisThisEvent;
import com.drownedinsound.events.UserIsNotLoggedInEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

public class ThisACommentHandler extends ResponseHandler {

    private String postID;

    private @BoardPostList.BoardPostListType String boardListType;

    public ThisACommentHandler(int calliingUiId, String postID, @BoardPostList.BoardPostListType String boardListType) {
        this.postID = postID;
        this.boardListType = boardListType;
        setUiID(calliingUiId);
        setUpdateUI(true);
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
//        BoardPost boardPost;
//        if (inputStream != null) {
//            BoardPostParser boardPostParser = new BoardPostParser(userSessionManager,
//                    postID, boardListType);
//            boardPost = boardPostParser.parse(inputStream);
//            if (boardPost != null) {
//                //databaseHelper.setBoardPost(boardPost);
//                if (isUpdateUI()) {
//                    //eventBus.post(new RetrievedBoardPostEvent(boardPost, false, true, getUiID()));
//                }
//            }
//        } else {
//            eventBus.post(new UserIsNotLoggedInEvent());
//        }
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        eventBus.post(new FailedToThisThisEvent());
    }
}
