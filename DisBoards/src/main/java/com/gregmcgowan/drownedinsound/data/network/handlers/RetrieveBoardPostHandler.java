package com.gregmcgowan.drownedinsound.data.network.handlers;

import android.content.Context;
import android.util.Log;

import com.gregmcgowan.drownedinsound.core.DisBoardsApp;
import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostParser;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.UpdateCachedBoardPostEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.client.HttpResponseException;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class RetrieveBoardPostHandler extends OkHttpAsyncResponseHandler {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
        + "RetrieveBoardPostHandler";

    private String boardPostId;
    private BoardType boardPostType;

    @Inject
    DatabaseHelper databaseHelper;

    @Inject
    EventBus eventBus;

    public RetrieveBoardPostHandler(Context context, String boardPostId, BoardType boardType,
                                    boolean updateUI) {
        DisBoardsApp.getApplication(context).inject(this);
        this.boardPostId = boardPostId;
        this.boardPostType = boardType;
        setUpdateUI(updateUI);
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        BoardPost boardPost = null;
        if (inputStream != null) {
            BoardPostParser boardPostParser = new BoardPostParser(inputStream,
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
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        if (DisBoardsConstants.DEBUG) {
            if (throwable instanceof HttpResponseException) {
                HttpResponseException exception = (HttpResponseException) throwable;
                int statusCode = exception.getStatusCode();
                Log.d(TAG, "Status code " + statusCode);
                Log.d(TAG, "Message " + exception.getMessage());
            } else {
                Log.d(TAG, "Something went really wrong");
            }
        }

        if (isUpdateUI()) {
            eventBus.post(new RetrievedBoardPostEvent(null, false, true));
        }
    }
}
