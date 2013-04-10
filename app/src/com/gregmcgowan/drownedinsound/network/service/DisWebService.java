package com.gregmcgowan.drownedinsound.network.service;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
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

	if(NetworkUtils.isConnected(this)) {
		HttpClient.requestBoardPost(this, boardPostUrl,
			new RetrieveBoardPostHandler(FileUtils.createTempFile(this),
				boardPostId, boardType,databaseHelper));	    
	} else {
	    BoardPost cachedPost = databaseHelper.getBoardPost(boardPostId);
	    EventBus.getDefault().post(new RetrievedBoardPostEvent(cachedPost,true));
	}

    }

    private void handleGetPostSummaryList(Intent intent) {
	String boardUrl = intent.getStringExtra(DisBoardsConstants.BOARD_URL);
	BoardType boardType = (BoardType) intent
		.getSerializableExtra(DisBoardsConstants.BOARD_TYPE);
	
	if(NetworkUtils.isConnected(this)) {
		HttpClient.requestBoardSummary(
			this,
			boardUrl,
			boardType,
			new RetrieveBoardSummaryListHandler(FileUtils
				.createTempFile(this), boardType,databaseHelper), 1);	    
	} else {
	    List<BoardPost> cachedBoardPosts = databaseHelper.getBoardPosts(boardType);
		EventBus.getDefault().post(
			new RetrievedBoardPostSummaryListEvent(cachedBoardPosts, boardType,true));
	}

    }

    private void handleLoginRequest(Intent intent) {
	String username = intent.getStringExtra(DisBoardsConstants.USERNAME);
	String password = intent.getStringExtra(DisBoardsConstants.PASSWORD);
	HttpClient.makeLoginRequest(this, username, password,
		UrlConstants.SOCIAL_URL,
		new LoginResponseHandler(FileUtils.createTempFile(this)));
    }

}
