package com.gregmcgowan.drownedinsound.network.handlers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.BoardPostSummaryListParser;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UrlConstants;

import de.greenrobot.event.EventBus;

public class RetrieveBoardSummaryListHandler extends
	DisBoardAsyncNetworkHandler {

    private BoardType boardType;
    private DatabaseHelper databaseHelper;

    public RetrieveBoardSummaryListHandler(File file, BoardType boardType,
	    boolean updateUI, DatabaseHelper databaseHelper) {
	super(file, boardType.name(), updateUI);
	this.boardType = boardType;
	this.databaseHelper = databaseHelper;
    }

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "RetrieveBoardSummaryListHandler";

    @Override
    public void doSuccessAction(int statusCode, File file) {
	List<BoardPost> boardPostSummaries = null;
	if (DisBoardsConstants.DEBUG) {
	    Log.d(TAG, "Got response");
	}
	if (file != null && file.exists()) {
	    Document document = null;
	    try {
		document = Jsoup.parse(file, HttpClient.CONTENT_ENCODING,
			UrlConstants.BASE_URL);
	    } catch (IOException e) {
		if (DisBoardsConstants.DEBUG) {
		    e.printStackTrace();
		}
	    }
	    if (document != null) {
		BoardPostSummaryListParser parser = new BoardPostSummaryListParser(
			document, boardType);
		boardPostSummaries = parser.parseDocument();
	    }

	    if (boardPostSummaries.size() > 0) {
		databaseHelper.setBoardPosts(boardPostSummaries);
	    }
	    Board board = databaseHelper.getBoard(boardType);
	    if(board != null){
		board.setLastFetchedTime(System.currentTimeMillis());
		databaseHelper.setBoard(board);
	    }
	}
	deleteFile();
	if (isUpdateUI()) {
	    EventBus.getDefault().post(
		    new RetrievedBoardPostSummaryListEvent(boardPostSummaries,
			    boardType, false));
	}
    }

    @Override
    public void doFailureAction(Throwable throwable, File response) {
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
	if (isUpdateUI()) {
	    EventBus.getDefault().post(
		    new RetrievedBoardPostSummaryListEvent(null, boardType,
			    false));
	}
    }
}
