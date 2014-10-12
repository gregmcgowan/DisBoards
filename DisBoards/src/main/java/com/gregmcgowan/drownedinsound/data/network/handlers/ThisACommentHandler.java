package com.gregmcgowan.drownedinsound.data.network.handlers;

import com.gregmcgowan.drownedinsound.core.DisBoardsApp;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostParser;
import com.gregmcgowan.drownedinsound.events.FailedToThisThisEvent;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.UpdateCachedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.UserIsNotLoggedInEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ThisACommentHandler extends OkHttpAsyncResponseHandler {

    private String postID;

    private BoardType boardType;

    @Inject
    protected DatabaseHelper databaseHelper;

    @Inject
    protected EventBus eventBus;

    public ThisACommentHandler(Context context, String postID, BoardType boardType) {
        DisBoardsApp.getApplication(context).inject(this);
        this.postID = postID;
        this.boardType = boardType;
        setUpdateUI(true);
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        BoardPost boardPost;
        if (inputStream != null) {
            BoardPostParser boardPostParser = new BoardPostParser(inputStream,
                    postID, boardType);
            boardPost = boardPostParser.parse();
            if (boardPost != null) {
                databaseHelper.setBoardPost(boardPost);
                if (isUpdateUI()) {
                    eventBus.post(
                            new RetrievedBoardPostEvent(boardPost, false, false));
                }
                eventBus.post(
                        new UpdateCachedBoardPostEvent(boardPost));
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
