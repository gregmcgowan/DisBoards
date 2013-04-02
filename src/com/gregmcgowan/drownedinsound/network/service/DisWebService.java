package com.gregmcgowan.drownedinsound.network.service;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UrlConstants;
import com.gregmcgowan.drownedinsound.network.handlers.LoginResponseHandler;
import com.gregmcgowan.drownedinsound.network.handlers.RetrieveBoardPostHandler;
import com.gregmcgowan.drownedinsound.network.handlers.RetrieveBoardSummaryListHandler;
import com.gregmcgowan.drownedinsound.utils.FileUtils;

import android.app.IntentService;
import android.content.Intent;

/**
 * A service that will handle all the requests to the website
 * 
 * @author Greg
 * 
 */
public class DisWebService extends IntentService {

    private static final String SERVICE_NAME = "DisWebService";

    public DisWebService() {
	super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
	String boardPostTypeIdString = intent.getStringExtra(DisBoardsConstants.BOARD_TYPE_ID);
	
	HttpClient.requestBoardPost(this, boardPostUrl,
		new RetrieveBoardPostHandler(FileUtils.createTempFile(this),
			boardPostId,boardPostTypeIdString));

    }

    private void handleGetPostSummaryList(Intent intent) {
	String boardUrl = intent.getStringExtra(DisBoardsConstants.BOARD_URL);
	String boardId = intent.getStringExtra(DisBoardsConstants.BOARD_TYPE_ID);

	HttpClient.requestBoardSummary(
		this,
		boardUrl,
		boardId,
		new RetrieveBoardSummaryListHandler(FileUtils
			.createTempFile(this), boardId), 1);

    }

    private void handleLoginRequest(Intent intent) {
	String username = intent.getStringExtra(DisBoardsConstants.USERNAME);
	String password = intent.getStringExtra(DisBoardsConstants.PASSWORD);
	HttpClient.makeLoginRequest(this, username, password,
		UrlConstants.SOCIAL_URL, new LoginResponseHandler(FileUtils.createTempFile(this)));
    }

}
