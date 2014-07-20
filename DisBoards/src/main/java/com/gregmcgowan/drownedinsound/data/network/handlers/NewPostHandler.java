package com.gregmcgowan.drownedinsound.data.network.handlers;

import java.io.InputStream;

import org.apache.http.Header;

import android.util.Log;

import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.events.FailedToPostNewThreadEvent;
import com.gregmcgowan.drownedinsound.events.SentNewPostEvent;
import com.gregmcgowan.drownedinsound.events.SentNewPostEvent.SentNewPostState;

import de.greenrobot.event.EventBus;

public class NewPostHandler extends DisBoardAsyncInputStreamHandler {

    private Board board;
    private DatabaseHelper databaseHelper;

    public NewPostHandler(Board board, DatabaseHelper databaseHelper) {
        super(board.getDisplayName(), true);
        this.board = board;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public void doSuccessAction(int statusCode, Header[] headers,
                                InputStream inputStream) {
        String postID = "";
        if (headers != null) {
            for (Header header : headers) {
                Log.d("NEWPOST", header.toString());
                String name = header.getName();
                if ("location".equals(name)) {
                    String value = header.getValue();
                    int lastIndexOfForwardSlash = value.lastIndexOf("/");
                    int lastIndexOfQuestion = value.lastIndexOf("?");
                    postID = value.substring(lastIndexOfForwardSlash,
                        lastIndexOfQuestion + 1);
                    Log.d("NEWPOST", "postID " + postID);
                }
            }

//	    BoardPostParser boardPostParser = new BoardPostParser(inputStream,
//		    postID, board.getBoardType());
//	    BoardPost boardPost = boardPostParser.parse();
//	    if (boardPost != null) {
//		databaseHelper.setBoardPost(boardPost);
//	    }
//
//	    if (isUpdateUI()) {
//		EventBus.getDefault().post(
//			new RetrievedBoardPostEvent(boardPost, false, false));
//	    }
//	    EventBus.getDefault().post(
//		    new UpdateCachedBoardPostEvent(boardPost));
            databaseHelper.removeDraftBoardPost(board.getBoardType());
            EventBus.getDefault().post(new SentNewPostEvent(SentNewPostState.CONFIRMED));
        }

    }

    @Override
    public void doFailureAction(Throwable throwable) {
        EventBus.getDefault().post(new FailedToPostNewThreadEvent());
    }

}
