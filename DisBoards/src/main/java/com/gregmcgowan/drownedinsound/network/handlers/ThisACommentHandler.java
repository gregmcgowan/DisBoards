package com.gregmcgowan.drownedinsound.network.handlers;

import java.io.InputStream;

import org.apache.http.Header;

import android.util.Log;

import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostParser;
import com.gregmcgowan.drownedinsound.events.FailedToThisThisEvent;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.UpdateCachedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.UserIsNotLoggedInEvent;

import de.greenrobot.event.EventBus;

public class ThisACommentHandler extends DisBoardAsyncInputStreamHandler {

    private String postID;
    private BoardType boardType;
    private DatabaseHelper databaseHelper;

    public ThisACommentHandler(String postID, BoardType boardType,
	    DatabaseHelper databaseHelper) {
	super(postID, true);
	this.postID = postID;
	this.databaseHelper = databaseHelper;
	this.boardType = boardType;
    }

    @Override
    public void doSuccessAction(int statusCode, Header[] headers,
	    InputStream inputStream) {
	    if (headers != null) {
		for (Header header : headers) {
		    Log.d("HEADER ", header.toString() + " " + header.getName());

		}
	    }
	    
	BoardPost boardPost = null;
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
    public void doFailureAction(Throwable throwable) {
	EventBus.getDefault().post(new FailedToThisThisEvent());
    }

}
