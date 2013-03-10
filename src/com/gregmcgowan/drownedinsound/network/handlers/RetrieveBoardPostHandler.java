package com.gregmcgowan.drownedinsound.network.handlers;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.parser.BoardPostParser;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UrlConstants;
import com.loopj.android.http.FileAsyncBackgroundThreadHttpResponseHandler;

import de.greenrobot.event.EventBus;

public class RetrieveBoardPostHandler extends
	FileAsyncBackgroundThreadHttpResponseHandler {
    
    private String boardPostId;
    
    public RetrieveBoardPostHandler(File file,String boardPostId) {
	super(file);
	this.boardPostId = boardPostId;
    }

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "RetrieveBoardPostHandler";

    @Override
    public void onSuccess(int statusCode, File file) {
	BoardPost boardPost = null;
	if (file != null && file.exists()) {
	    Document parsedDocument = null;
	    try {
		parsedDocument = Jsoup.parse(file,HttpClient.CONTENT_ENCODING, UrlConstants.BASE_URL);
	    } catch (IOException e) {
		if (DisBoardsConstants.DEBUG) {
		    e.printStackTrace();
		}
	    }
	    if (parsedDocument != null) {
		BoardPostParser boardPostParser = new BoardPostParser(parsedDocument,boardPostId);
		boardPost = boardPostParser.parseDocument();

	    }
	}
	deleteFile();
	EventBus.getDefault().post(new RetrievedBoardPostEvent(boardPost));

    }

    @Override
    public void onFailure(Throwable throwable, File response) {
	if (DisBoardsConstants.DEBUG) {
	    Log.d(TAG, "Response Body " + response);
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
	EventBus.getDefault().post(new RetrievedBoardPostEvent(null));
    }

}
