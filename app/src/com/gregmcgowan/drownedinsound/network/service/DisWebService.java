package com.gregmcgowan.drownedinsound.network.service;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.model.BoardTypeInfo;
import com.gregmcgowan.drownedinsound.data.model.BoardTypeInfoConstants;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UrlConstants;
import com.gregmcgowan.drownedinsound.network.handlers.LoginResponseHandler;
import com.gregmcgowan.drownedinsound.network.handlers.RetrieveBoardPostHandler;
import com.gregmcgowan.drownedinsound.network.handlers.RetrieveBoardSummaryListHandler;
import com.gregmcgowan.drownedinsound.utils.FileUtils;
import com.gregmcgowan.drownedinsound.utils.NetworkUtils;

import de.greenrobot.event.EventBus;

/**
 * A service that will handle all the requests to the website
 * 
 * @author Greg
 * 
 */
public class DisWebService extends IntentService {

    private static final String SERVICE_NAME = "DisWebService";

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "DisWebService";

    private DatabaseHelper databaseHelper;

    public DisWebService() {
	super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
	databaseHelper = DatabaseHelper.getInstance(getApplicationContext());

	int requestedService = intent.getIntExtra(
		DisWebServiceConstants.SERVICE_REQUESTED_ID, 0);

	switch (requestedService) {
	case DisWebServiceConstants.LOGIN_SERVICE_ID:
	    handleLoginRequest(intent);
	    break;
	case DisWebServiceConstants.GET_POSTS_SUMMARY_LIST_ID:
	    handleGetPostSummaryList(intent);
	    break;
	case DisWebServiceConstants.GET_BOARD_POST_ID:
	    handleGetBoardPost(intent);
	    break;
	default:
	    break;
	}
    }

    private void handleGetBoardPost(Intent intent) {
	String boardPostUrl = intent
		.getStringExtra(DisBoardsConstants.BOARD_POST_URL);
	String boardPostId = intent
		.getStringExtra(DisBoardsConstants.BOARD_POST_ID);
	BoardType boardType = (BoardType) intent
		.getSerializableExtra(DisBoardsConstants.BOARD_TYPE);
	BoardPost cachedPost = databaseHelper.getBoardPost(boardPostId);

	if (NetworkUtils.isConnected(this)) {
	    boolean requestIsInProgress = HttpClient
		    .requestIsInProgress(boardPostId);
	    if (!requestIsInProgress) {
		HttpClient.requestBoardPost(
			this,
			boardPostUrl,
			new RetrieveBoardPostHandler(FileUtils
				.createTempFile(this), boardPostId, boardType,
				true, databaseHelper));
	    }

	} else {
	    EventBus.getDefault().post(
		    new RetrievedBoardPostEvent(cachedPost, true));
	}

    }

    private void handleGetPostSummaryList(Intent intent) {
	BoardTypeInfo passedInBoardTypeInfo = intent
		.getParcelableExtra(DisBoardsConstants.BOARD_TYPE_INFO);
	boolean forceFetch = intent.getBooleanExtra(
		DisBoardsConstants.FORCE_FETCH, false);
	ArrayList<BoardTypeInfo> boardTypeInfos = BoardTypeInfoConstants
		.geBoardsToFetch(passedInBoardTypeInfo);
	int requestNumber = 0;
	for (BoardTypeInfo boardTypeInfo : boardTypeInfos) {
	    boolean updateUI = requestNumber == 0;
	    fetchBoardType(boardTypeInfo, updateUI, forceFetch);
	    requestNumber++;
	}

    }

    private void fetchBoardType(BoardTypeInfo boardTypeInfo, boolean updateUI,
	    boolean forceFetch) {
	List<BoardPost> cachedBoardPosts = databaseHelper
		.getBoardPosts(boardTypeInfo.getBoardType());
	if (NetworkUtils.isConnected(this)) {
	    if (forceFetch || !recentlyFetched(cachedBoardPosts)) {
		boolean requestIsInProgress = HttpClient
			.requestIsInProgress(boardTypeInfo.getBoardType()
				.name());
		if (!requestIsInProgress) {
		    HttpClient.requestBoardSummary(
			    this,
			    boardTypeInfo.getUrl(),
			    boardTypeInfo.getBoardType(),
			    new RetrieveBoardSummaryListHandler(FileUtils
				    .createTempFile(this), boardTypeInfo
				    .getBoardType(), updateUI, databaseHelper),
			    1);
		}
	    } else {
		if (updateUI) {
		    EventBus.getDefault().post(
			    new RetrievedBoardPostSummaryListEvent(
				    cachedBoardPosts, boardTypeInfo
					    .getBoardType(), false));
		}
	    }
	} else {
	    if (updateUI) {
		EventBus.getDefault().post(
			new RetrievedBoardPostSummaryListEvent(
				cachedBoardPosts, boardTypeInfo.getBoardType(),
				true));	    
		}
	}
    }

    private boolean recentlyFetched(List<BoardPost> boardPosts) {
	boolean recentlyFetched = false;
	if (boardPosts != null && boardPosts.size() > 0) {
	    long latestLastRecentyFetchedTIme = boardPosts.get(0)
		    .getLastFetchedTime();
	    for (BoardPost boardPost : boardPosts) {
		long lastRecentlyFetchedTime = boardPost.getLastFetchedTime();
		if (lastRecentlyFetchedTime < latestLastRecentyFetchedTIme) {
		    latestLastRecentyFetchedTIme = lastRecentlyFetchedTime;
		}
	    }

	    long oneMinuteAgo = System.currentTimeMillis()
		    - (DateUtils.MINUTE_IN_MILLIS);

	    if (DisBoardsConstants.DEBUG) {
		Log.d(TAG, " latest last recently fetched time =  "
			+ latestLastRecentyFetchedTIme + " one  minute ago =  "
			+ oneMinuteAgo);
	    }
	    recentlyFetched = latestLastRecentyFetchedTIme > oneMinuteAgo;
	}
	return recentlyFetched;
    }

    private void handleLoginRequest(Intent intent) {
	String username = intent.getStringExtra(DisBoardsConstants.USERNAME);
	String password = intent.getStringExtra(DisBoardsConstants.PASSWORD);
	HttpClient.makeLoginRequest(this, username, password,
		UrlConstants.SOCIAL_URL,
		new LoginResponseHandler(FileUtils.createTempFile(this)));
    }

}
