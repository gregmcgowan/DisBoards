package com.gregmcgowan.drownedinsound.network.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.Board;
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
			new RetrieveBoardPostHandler(boardPostId, boardType,
				true, databaseHelper));
	    }

	} else {
	    EventBus.getDefault().post(
		    new RetrievedBoardPostEvent(cachedPost, true));
	}

    }

    private void handleGetPostSummaryList(Intent intent) {
	Board board = intent
		.getParcelableExtra(DisBoardsConstants.BOARD);
	boolean forceFetch = intent.getBooleanExtra(
		DisBoardsConstants.FORCE_FETCH, false);
	fetchBoardPostSummaryList(board, true, forceFetch);	
	//TODO not sure if we actually gain anything from doing this
	// Fetch the two nearest as well
/*	if(!forceFetch) {
		ArrayList<Board> nextTwoBoards = Board.getBoardsToFetch(board, this);
		DisBoardsApp disApp = DisBoardsApp.getApplication(this);
		FetchBoardRunnable fetchBoardRunnable = new FetchBoardRunnable(
			nextTwoBoards, new WeakReference<Context>(disApp),
			databaseHelper,forceFetch);
		disApp.getMultiThreadedExecutorService().execute(fetchBoardRunnable); 
	}*/

    }

    private void fetchBoardPostSummaryList(Board board, boolean updateUI,
	    boolean forceFetch) {
	List<BoardPost> cachedBoardPosts = databaseHelper.getBoardPosts(board
		.getBoardType());
	boolean requestIsInProgress = HttpClient.requestIsInProgress(board
		.getBoardType().name());
	if (!requestIsInProgress) {
	    if (NetworkUtils.isConnected(this)) {
		if (forceFetch || !recentlyFetched(board)) {
		    HttpClient.requestBoardSummary(
			    this,
			    board.getUrl(),
			    board.getBoardType(),
			    new RetrieveBoardSummaryListHandler(
				    board.getBoardType(), updateUI,
				    databaseHelper), 1);
		} else {
		    if (updateUI) {
			EventBus.getDefault().post(
				new RetrievedBoardPostSummaryListEvent(
					cachedBoardPosts, board.getBoardType(),
					false));
		    }
		}
	    } else {
		if (updateUI) {
		    EventBus.getDefault().post(
			    new RetrievedBoardPostSummaryListEvent(
				    cachedBoardPosts, board.getBoardType(),
				    true));
		}
	    }
	}
    }

    private boolean recentlyFetched(Board cachedBoard) {
	boolean recentlyFetched = false;
	BoardType type = cachedBoard.getBoardType();
	Board board = databaseHelper.getBoard(type);
	long lastFetchedTime = board.getLastFetchedTime();
	long oneMinuteAgo = System.currentTimeMillis()
		- (DateUtils.MINUTE_IN_MILLIS);

	recentlyFetched = lastFetchedTime > oneMinuteAgo;

	if (DisBoardsConstants.DEBUG) {
	    Log.d(TAG, " last fetched time =  "
		    + lastFetchedTime
		    + " one  minute ago =  "
		    + oneMinuteAgo
		    + " so it has been "
		    + (recentlyFetched ? "recently fetched"
			    : "not recently fetched"));
	}

	return recentlyFetched;
    }
    
    
    //TODO Not sure if this just causes more problems
    private static class FetchBoardRunnable implements Runnable {

	private DatabaseHelper databaseHelper;
	private WeakReference<Context> context;
	private ArrayList<Board> boards;
	private boolean forceFetch;
	
	FetchBoardRunnable(ArrayList<Board> boards,
		WeakReference<Context> context, DatabaseHelper databaseHelper,boolean forceFetch) {
	    this.boards = boards;
	    this.context = context;
	    this.databaseHelper = databaseHelper;
	    this.forceFetch = forceFetch;
	}

	@Override
	public void run() {
	    for (Board boardToFetch : boards) {
		fetchBoardType(boardToFetch);
	    }
	}

	private void fetchBoardType(Board board) {
	    boolean requestIsInProgress = HttpClient.requestIsInProgress(board
		    .getBoardType().name());
	    if (!requestIsInProgress) {
		if (NetworkUtils.isConnected(context.get())) {
		    if (forceFetch || !recentlyFetched(board)) {
			HttpClient.requestBoardSummary(
				context.get(),
				board.getUrl(),
				board.getBoardType(),
				new RetrieveBoardSummaryListHandler(board
					.getBoardType(), forceFetch,
					databaseHelper), 1);
		    } 
		} 
	    }
	}

	private boolean recentlyFetched(Board cachedBoard) {
	    boolean recentlyFetched = false;
	    BoardType type = cachedBoard.getBoardType();
	    Board board = databaseHelper.getBoard(type);
	    long lastFetchedTime = board.getLastFetchedTime();
	    long oneMinuteAgo = System.currentTimeMillis()
		    - (DateUtils.MINUTE_IN_MILLIS);

	    recentlyFetched = lastFetchedTime > oneMinuteAgo;

	    if (DisBoardsConstants.DEBUG) {
		Log.d(TAG, " last fetched time =  "
			+ lastFetchedTime
			+ " one  minute ago =  "
			+ oneMinuteAgo
			+ " so it has been "
			+ (recentlyFetched ? "recently fetched"
				: "not recently fetched"));
	    }

	    return recentlyFetched;
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
