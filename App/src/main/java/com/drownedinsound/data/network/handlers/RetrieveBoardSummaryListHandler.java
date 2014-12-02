package com.drownedinsound.data.network.handlers;

import com.drownedinsound.core.DisBoardsApp;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.parser.streaming.BoardPostSummaryListParser;
import com.drownedinsound.events.RequestCompletedEvent;
import com.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.client.HttpResponseException;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RetrieveBoardSummaryListHandler extends
        OkHttpAsyncResponseHandler {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "RetrieveBoardSummaryListHandler";

    private BoardType boardType;

    private boolean append;

    public RetrieveBoardSummaryListHandler(Context context,
            BoardType boardType,
            boolean updateUI,
            boolean append) {
        super(context);
        DisBoardsApp.getApplication(context).inject(this);
        this.boardType = boardType;
        this.append = append;
        setUpdateUI(updateUI);
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        List<BoardPost> boardPostSummaries = new ArrayList<BoardPost>();
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG, "Got response");
        }
        if (inputStream != null) {
            BoardPostSummaryListParser parser = new BoardPostSummaryListParser(
                    userSessionManager, inputStream, boardType, databaseHelper);
            boardPostSummaries = parser.parse();
            if (boardPostSummaries.size() > 0) {
                databaseHelper.setBoardPosts(boardPostSummaries);
            }
            Board board = databaseHelper.getBoard(boardType);
            if (board != null) {
                board.setLastFetchedTime(System.currentTimeMillis());
                databaseHelper.setBoard(board);
            }
        }

        if (isUpdateUI()) {
            eventBus.post(
                    new RetrievedBoardPostSummaryListEvent(boardPostSummaries,
                            boardType, false, append));
        }
        eventBus.post(new RequestCompletedEvent(boardType.name()));
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
            List<BoardPost> cachedBoardPosts = databaseHelper.getBoardPosts(boardType);
            if(cachedBoardPosts.size() > 0) {
                eventBus.post(
                        new RetrievedBoardPostSummaryListEvent(cachedBoardPosts, boardType,
                                true, append));
            } else {
                eventBus.post(
                        new RetrievedBoardPostSummaryListEvent(null, boardType,
                                false, append));
            }

        }
        eventBus.post(new RequestCompletedEvent(boardType.name()));
    }

}
