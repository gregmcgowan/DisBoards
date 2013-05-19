package com.gregmcgowan.drownedinsound.network.handlers;

import java.io.InputStream;

import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostParser;
import com.gregmcgowan.drownedinsound.events.FailedToThisThisEvent;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.UpdateCachedBoardPostEvent;

import de.greenrobot.event.EventBus;

public class ThisACommentHandler extends DisBoardAsyncInputStreamHandler {

    private String postID;
    private BoardType boardType;
    private DatabaseHelper databaseHelper;

    public ThisACommentHandler( String postID,
	    BoardType boardType,  DatabaseHelper databaseHelper) {
	super(postID, true);
	this.postID = postID;
	this.databaseHelper = databaseHelper;
    }

    @Override
    public void doSuccessAction(int statusCode, InputStream inputStream) {
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
		    new RetrievedBoardPostEvent(boardPost, false));
	}
	EventBus.getDefault().post(new UpdateCachedBoardPostEvent(boardPost));
    }

    @Override
    public void doFailureAction(Throwable throwable) {
	    EventBus.getDefault()
	    .post(new FailedToThisThisEvent());
    }

}
