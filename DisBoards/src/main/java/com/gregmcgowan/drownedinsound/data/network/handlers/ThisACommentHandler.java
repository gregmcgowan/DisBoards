package com.gregmcgowan.drownedinsound.data.network.handlers;

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

import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

public class ThisACommentHandler extends OkHttpAsyncResponseHandler {

    private String postID;

    private BoardType boardType;

    private DatabaseHelper databaseHelper;

    public ThisACommentHandler(String postID, BoardType boardType,
            DatabaseHelper databaseHelper) {
        this.postID = postID;
        this.databaseHelper = databaseHelper;
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
                    EventBus.getDefault().post(
                            new RetrievedBoardPostEvent(boardPost, false, false));
                }
                EventBus.getDefault().post(
                        new UpdateCachedBoardPostEvent(boardPost));
            }
        } else {
            EventBus.getDefault().post(new UserIsNotLoggedInEvent());
        }
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        EventBus.getDefault().post(new FailedToThisThisEvent());
    }
}
