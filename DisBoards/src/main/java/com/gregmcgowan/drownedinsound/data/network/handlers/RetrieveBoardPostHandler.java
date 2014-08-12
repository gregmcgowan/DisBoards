package com.gregmcgowan.drownedinsound.data.network.handlers;

import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;

import android.util.Log;

import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostParser;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.UpdateCachedBoardPostEvent;

import de.greenrobot.event.EventBus;

public class RetrieveBoardPostHandler extends DisBoardAsyncInputStreamHandler {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
        + "RetrieveBoardPostHandler";

    private String boardPostId;
    private BoardType boardPostType;
    private DatabaseHelper databaseHelper;

    public RetrieveBoardPostHandler(String boardPostId, BoardType boardType,
                                    boolean updateUI, DatabaseHelper databaseHelper) {
        super(boardPostId, updateUI);
        this.boardPostId = boardPostId;
        this.boardPostType = boardType;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public void doSuccessAction(int statusCode, Header[] headers, InputStream inputStream) {
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
            EventBus.getDefault().post(
                new RetrievedBoardPostEvent(boardPost, false, true));
        }
        EventBus.getDefault().post(new UpdateCachedBoardPostEvent(boardPost));

    }

    @Override
    public void doFailureAction(Throwable throwable) {
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
            EventBus.getDefault()
                .post(new RetrievedBoardPostEvent(null, false, true));
        }
    }

}
