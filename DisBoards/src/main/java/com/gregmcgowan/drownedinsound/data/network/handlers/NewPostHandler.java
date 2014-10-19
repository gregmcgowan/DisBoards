package com.gregmcgowan.drownedinsound.data.network.handlers;

import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.events.FailedToPostNewThreadEvent;
import com.gregmcgowan.drownedinsound.events.SentNewPostEvent;
import com.gregmcgowan.drownedinsound.events.SentNewPostEvent.SentNewPostState;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class NewPostHandler extends OkHttpAsyncResponseHandler {

    private Board board;


    public NewPostHandler(Context context,Board board) {
        super(context);
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
