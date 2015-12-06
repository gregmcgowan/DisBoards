    package com.drownedinsound.data.network.handlers;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.parser.streaming.BoardPostSummaryListParser;
import com.drownedinsound.events.RequestCompletedEvent;
import com.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class RetrieveBoardSummaryListHandler extends
        ResponseHandler {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "RetrieveBoardSummaryListHandler";

    private BoardType boardType;

    private boolean append;

    public RetrieveBoardSummaryListHandler(
            int uiID,
            BoardType boardType,
            boolean updateUI,
            boolean append) {
        setUiID(uiID);
        this.boardType = boardType;
        this.append = append;
        setUpdateUI(updateUI);
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
        List<BoardPost> boardPostSummaries = new ArrayList<BoardPost>();
        if (DisBoardsConstants.DEBUG) {
            Timber.d("Got response");
        }
        if (inputStream != null) {
            BoardPostSummaryListParser parser = new BoardPostSummaryListParser(
                    userSessionManager, inputStream, boardType, databaseHelper);
            boardPostSummaries = parser.parse();
            if (boardPostSummaries.size() > 0) {
                databaseHelper.setBoardPosts(boardPostSummaries);
            }
            Board board = null;//databaseHelper.getBoard(boardType);
            if (board != null) {
                board.setLastFetchedTime(System.currentTimeMillis());
                databaseHelper.setBoard(board);
            }
        }

        if (isUpdateUI()) {
            eventBus.post(
                    new RetrievedBoardPostSummaryListEvent(boardPostSummaries,
                            boardType, false, append, getUiID()));
        }
        eventBus.post(new RequestCompletedEvent(boardType.name()));
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        if (DisBoardsConstants.DEBUG) {
            Timber.d("Something went really wrong throwable = " + throwable);
        }

        if (isUpdateUI()) {
            List<BoardPost> cachedBoardPosts = null;//databaseHelper.getBoardPosts(boardType);
            if (cachedBoardPosts.size() > 0) {
                eventBus.post(
                        new RetrievedBoardPostSummaryListEvent(cachedBoardPosts, boardType,
                                true, append, getUiID()));
            } else {
                eventBus.post(
                        new RetrievedBoardPostSummaryListEvent(cachedBoardPosts, boardType,
                                false, append, getUiID()));
            }
        }
    }

}
