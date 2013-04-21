package com.gregmcgowan.drownedinsound.network.handlers;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.BoardPostParser;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.UpdateCachedBoardPostEvent;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UrlConstants;

import de.greenrobot.event.EventBus;

public class RetrieveBoardPostHandler extends DisBoardAsyncNetworkHandler {

    private String boardPostId;
    private BoardType boardPostType;
    private DatabaseHelper databaseHelper;

    public RetrieveBoardPostHandler(File file, String boardPostId,
	    BoardType boardType, boolean updateUI,DatabaseHelper databaseHelper) {
	super(file, boardPostId,updateUI);
	this.boardPostId = boardPostId;
	this.boardPostType = boardType;
	this.databaseHelper = databaseHelper;
    }

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "RetrieveBoardPostHandler";

    @Override
    public void doSuccessAction(int statusCode, File file) {
	BoardPost boardPost = null;
	if (file != null && file.exists()) {
	    Document parsedDocument = null;
	    try {
		parsedDocument = Jsoup.parse(file, HttpClient.CONTENT_ENCODING,
			UrlConstants.BASE_URL);
	    } catch (IOException e) {
		if (DisBoardsConstants.DEBUG) {
		    e.printStackTrace();
		}
	    }
	    if (parsedDocument != null) {
		BoardPostParser boardPostParser = new BoardPostParser(
			parsedDocument, boardPostId, boardPostType);
		boardPost = boardPostParser.parseDocument();
		if (boardPost != null) {
		    databaseHelper.setBoardPost(boardPost);
		}
	    }
	}
	deleteFile();
	if(isUpdateUI())  {
		EventBus.getDefault().post(
			new RetrievedBoardPostEvent(boardPost, false));
	}
	EventBus.getDefault().post(new UpdateCachedBoardPostEvent(boardPost));
    }

    @Override
    public void doFailureAction(Throwable throwable, File response) {
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
	deleteFile();
	if(isUpdateUI())  {
	    EventBus.getDefault().post(new RetrievedBoardPostEvent(null, false));
	}
    }

}
