package com.drownedinsound.data.network.handlers;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.parser.streaming.BoardPostParser;
import com.drownedinsound.events.FailedToGetBoardPostEvent;
import com.drownedinsound.events.RetrievedBoardPostEvent;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class RetrieveBoardPostHandler extends ResponseHandler {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "RetrieveBoardPostHandler";

    private String boardPostId;

    @BoardPostList.BoardPostListType String boardListType;

    public RetrieveBoardPostHandler(String boardPostId, @BoardPostList.BoardPostListType String boardListType,
            boolean updateUI, int uiID) {
        this.boardPostId = boardPostId;
        this.boardListType = boardListType;
        setUiID(uiID);
        setUpdateUI(updateUI);
    }

    @Override
    public void handleSuccess(Response response, InputStream inputStream) throws IOException {
//        BoardPost boardPost = null;
//        if (inputStream != null) {
//            BoardPostParser boardPostParser = new BoardPostParser(userSessionManager,
//                    boardPostId, boardListType);
//            boardPost = boardPostParser.parse(inputStream);
//            BoardPost exisitingBoardPost = null;//databaseHelper.getBoardPost(boardPostId);
//            int numberOfTimesRead = 0;
//            if (exisitingBoardPost != null) {
//                numberOfTimesRead = exisitingBoardPost.getNumberOfTimesRead() + 1;
//               // boardPost.setFavourited(exisitingBoardPost.isFavourited());
//            }
//            boardPost.setNumberOfTimesRead(numberOfTimesRead);
//            //databaseHelper.setBoardPost(boardPost);
//        }
//        if (isUpdateUI()) {
//            //eventBus.post(new RetrievedBoardPostEvent(boardPost, false, true, getUiID()));
//        }
    }

    @Override
    public void handleFailure(Request request, Throwable throwable) {
        if (DisBoardsConstants.DEBUG) {
            Timber.d("Throwable "+throwable.getMessage());
        }
        BoardPost exisitingBoardPost = null;//databaseHelper.getBoardPost(boardPostId);
        if (isUpdateUI() && exisitingBoardPost != null) {
            //eventBus.post(new RetrievedBoardPostEvent(exisitingBoardPost, true, true, getUiID()));
        } else {
            //eventBus.post(new FailedToGetBoardPostEvent(getUiID()));
        }
    }
}
