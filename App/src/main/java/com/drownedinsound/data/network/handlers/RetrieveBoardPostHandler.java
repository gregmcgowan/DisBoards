package com.drownedinsound.data.network.handlers;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.parser.streaming.BoardPostParser;
import com.drownedinsound.events.RequestCompletedEvent;
import com.drownedinsound.events.RetrievedBoardPostEvent;
import com.drownedinsound.events.UpdateCachedBoardPostEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.client.HttpResponseException;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class RetrieveBoardPostHandler extends OkHttpAsyncResponseHandler {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "RetrieveBoardPostHandler";

    private String boardPostId;

    private BoardType boardPostType;

    public RetrieveBoardPostHandler(Context context, String boardPostId, BoardType boardType,
            boolean updateUI) {
        super(context);
        this.boardPostId = boardPostId;
        this.boardPostType = boardType;
        setUpdateUI(updateUI);
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        BoardPost boardPost = null;
        if (inputStream != null) {
            BoardPostParser boardPostParser = new BoardPostParser(userSessionManager, inputStream,
                    boardPostId, boardPostType);
            boardPost = boardPostParser.parse();
            BoardPost exisitingBoardPost = databaseHelper.getBoardPost(boardPost.getId());
            int numberOfTimesRead = 0;
            if (exisitingBoardPost != null) {
                numberOfTimesRead = exisitingBoardPost.getNumberOfTimesRead() + 1;
                boardPost.setFavourited(exisitingBoardPost.isFavourited());
            }
            boardPost.setNumberOfTimesRead(numberOfTimesRead);
            if (boardPost != null) {
                databaseHelper.setBoardPost(boardPost);
            }
        }
        if (isUpdateUI()) {
            eventBus.post(new RetrievedBoardPostEvent(boardPost, false, true));
        }
        eventBus.post(new UpdateCachedBoardPostEvent(boardPost));
        eventBus.post(new RequestCompletedEvent(boardPostId));
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        if (DisBoardsConstants.DEBUG) {
            if (throwable instanceof HttpResponseException) {
                HttpResponseException exception = (HttpResponseException) throwable;
                int statusCode = exception.getStatusCode();
                Timber.d("Status code " + statusCode);
                Timber.d("Message " + exception.getMessage());
            } else {
                Timber.d("Something went really wrong throwable = " + throwable);
            }
        }

        if (isUpdateUI()) {
            eventBus.post(new RetrievedBoardPostEvent(null, false, true));
        }
        eventBus.post(new RequestCompletedEvent(boardPostId));
    }
}
