package com.drownedinsound.data.network.handlers;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.parser.streaming.BoardPostParser;
import com.drownedinsound.events.PostCommentEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

public class PostACommentHandler extends ResponseHandler {

    private String postID;

    private @BoardPostList.BoardPostListType String boardListType;


    public PostACommentHandler(String boardPostId,@BoardPostList.BoardPostListType String boardListType, int uiID) {
        this.postID = boardPostId;
        this.boardListType = boardListType;
        setUpdateUI(true);
        setUiID(uiID);
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        if (inputStream != null) {
            BoardPostParser boardPostParser = new BoardPostParser(userSessionManager);
            //BoardPost boardPost = boardPostParser.parse(inputStream);
            //if (boardPost != null) {
                //databaseHelper.setBoardPost(boardPost);
            //}
        }
        if (isUpdateUI()) {
            eventBus.postSticky(new PostCommentEvent(getUiID(), true));
        }
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        eventBus.postSticky(new PostCommentEvent(getUiID(),false));
    }


}
