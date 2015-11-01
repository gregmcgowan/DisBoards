package com.drownedinsound.data.network.handlers;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.events.FailedToPostNewThreadEvent;
import com.drownedinsound.events.SentNewPostEvent;
import com.drownedinsound.events.SentNewPostEvent.SentNewPostState;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;


public class NewPostHandler extends OkHttpAsyncResponseHandler {

    private Board board;


    public NewPostHandler(Board board) {
        this.board = board;
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        String postID = "";
        String locationHeader = response.header("location");
        Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Location Header " + locationHeader);
        databaseHelper.removeDraftBoardPost(board.getBoardType());
        eventBus.post(new SentNewPostEvent(SentNewPostState.CONFIRMED));
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        eventBus.post(new FailedToPostNewThreadEvent());
    }


}
