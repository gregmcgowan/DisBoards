package com.gregmcgowan.drownedinsound.network.handlers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.parser.BoardPostSummaryListParser;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UrlConstants;
import com.loopj.android.http.FileAsyncBackgroundThreadHttpResponseHandler;

import de.greenrobot.event.EventBus;

public class RetrieveBoardSummaryListHandler extends
	FileAsyncBackgroundThreadHttpResponseHandler {

    private String boardId;
    
    public RetrieveBoardSummaryListHandler(File file, String boardId) {
	super(file);
	this.boardId = boardId;
    }

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "RetrieveBoardSummaryListHandler";

    @Override
    public void onSuccess(int statusCode, File file) {
	List<BoardPost> boardPostSummaries = null;
	if(DisBoardsConstants.DEBUG){
	    Log.d(TAG, "Got response");
	}
	if (file != null && file.exists()) {
	    Document document = null;
	    try {
		document = Jsoup.parse(file,HttpClient.CONTENT_ENCODING, UrlConstants.BASE_URL);
	    } catch (IOException e) {
		if (DisBoardsConstants.DEBUG) {
		    e.printStackTrace();
		}
	    }
	    if (document != null) {
		BoardPostSummaryListParser parser = new BoardPostSummaryListParser(document,boardId);
		boardPostSummaries = parser.parseDocument();

	    }
	}
	deleteFile();
	EventBus.getDefault().post(new RetrievedBoardPostSummaryListEvent(boardPostSummaries,boardId));

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
	EventBus.getDefault().post(new RetrievedBoardPostSummaryListEvent(null,boardId));
    }
}
