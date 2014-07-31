package com.gregmcgowan.drownedinsound.data.network.handlers;

import java.io.InputStream;

import org.apache.http.Header;

import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostParser;
import com.gregmcgowan.drownedinsound.events.FailedToPostCommentEvent;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.UpdateCachedBoardPostEvent;

import de.greenrobot.event.EventBus;

public class PostACommentHandler extends DisBoardAsyncInputStreamHandler {

    private String postID;
    private BoardType boardType;
    private DatabaseHelper databaseHelper;

    public PostACommentHandler(String boardPostId, BoardType boardType,
                               DatabaseHelper databaseHelper) {
        super(boardPostId, true);
        this.postID = boardPostId;
        this.boardType = boardType;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public void doSuccessAction(int statusCode, Header[] headers, InputStream inputStream) {
        BoardPost boardPost = null;
        if (inputStream != null) {
            BoardPostParser boardPostParser = new BoardPostParser(inputStream,
                postID, boardType);
            boardPost = boardPostParser.parse();
            if (boardPost != null) {
                databaseHelper.setBoardPost(boardPost);
            }
        }
        if (isUpdateUI()) {
            EventBus.getDefault().post(
                new RetrievedBoardPostEvent(boardPost, false, false));
        }
        EventBus.getDefault().post(new UpdateCachedBoardPostEvent(boardPost));
    }

    @Override
    public void doFailureAction(Throwable throwable) {
        EventBus.getDefault().post(new FailedToPostCommentEvent());
    }
}