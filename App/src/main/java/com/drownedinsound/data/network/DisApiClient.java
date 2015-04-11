package com.drownedinsound.data.network;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.network.handlers.LoginResponseHandler;
import com.drownedinsound.data.network.handlers.NewPostHandler;
import com.drownedinsound.data.network.handlers.PostACommentHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardPostHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardSummaryListHandler;
import com.drownedinsound.data.network.handlers.ThisACommentHandler;
import com.drownedinsound.data.network.requests.AddANewPostRunnable;
import com.drownedinsound.data.network.requests.GetBoardPostRunnable;
import com.drownedinsound.data.network.requests.GetBoardPostSummaryListRunnable;
import com.drownedinsound.data.network.requests.LoginRunnable;
import com.drownedinsound.data.network.requests.PostACommentRunnable;
import com.drownedinsound.data.network.requests.ThisACommentRunnable;
import com.drownedinsound.database.DatabaseHelper;
import com.drownedinsound.database.DatabaseRunnable;
import com.drownedinsound.events.RequestCompletedEvent;
import com.drownedinsound.events.RetrievedBoardPostEvent;
import com.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.drownedinsound.qualifiers.ForDatabase;
import com.drownedinsound.qualifiers.ForNetworkRequests;
import com.drownedinsound.utils.NetworkUtils;
import com.squareup.okhttp.OkHttpClient;

import android.app.Application;
import android.content.Context;
import android.text.format.DateUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * A service that will handle all the requests to the website
 *
 * @author Greg
 */
public class DisApiClient {

    private Context applicationContext;

    private OkHttpClient httpClient;

    private DatabaseHelper databaseHelper;

    private UserSessionManager userSessionManager;

    private EventBus eventBus;

    private ExecutorService networkRequestExecutorService;

    private ExecutorService dbExecutorService;

    private CopyOnWriteArrayList<String> inProgressRequests;

    @Inject
    public DisApiClient(Application applicationContext, OkHttpClient httpClient,
            DatabaseHelper databaseHelper,
            UserSessionManager userSessionManager, EventBus eventBus,
            @ForNetworkRequests ExecutorService networkExecutorService,
            @ForDatabase ExecutorService dbExecutorService) {

        this.applicationContext = applicationContext;
        this.httpClient = httpClient;
        this.databaseHelper = databaseHelper;
        this.userSessionManager = userSessionManager;
        this.eventBus = eventBus;
        this.networkRequestExecutorService = networkExecutorService;
        this.dbExecutorService = dbExecutorService;
        this.inProgressRequests = new CopyOnWriteArrayList<>();

        eventBus.register(this);
    }

    public void loginUser(final String username, final String password) {
        networkRequestExecutorService.execute(
                new LoginRunnable(httpClient, new LoginResponseHandler(applicationContext),
                        username, password));
    }

    public void getBoardPost(String boardPostUrl, final String boardPostId, BoardType boardType) {
        if (NetworkUtils.isConnected(applicationContext)) {
            boolean requestIsInProgress = inProgressRequests.contains(boardPostId);
            if (!requestIsInProgress) {
                RetrieveBoardPostHandler retrieveBoardPostHandler = new
                        RetrieveBoardPostHandler(applicationContext, boardPostId, boardType, true);
                networkRequestExecutorService.execute(
                        new GetBoardPostRunnable(retrieveBoardPostHandler, httpClient,
                                boardPostUrl));
                inProgressRequests.add(boardPostId);
            } else {
                Timber.d("Board post " + boardPostId + " has already been requested");
            }

        } else {
            dbExecutorService.execute(new DatabaseRunnable(databaseHelper) {
                @Override
                public void run() {
                    BoardPost cachedPost = dbHelper.getBoardPost(boardPostId);
                    eventBus.post(
                            new RetrievedBoardPostEvent(cachedPost, true, true));
                }
            });
        }

    }

    public void getBoardPostSummaryList(int pageNumber, final Board board, boolean forceUpdate,
            boolean updateUI) {
        String boardListName = board
                .getBoardType().name();
        final boolean requestIsInProgress = inProgressRequests.contains(boardListName);
        final boolean append = pageNumber > 1;
        final boolean requestedRecently = recentlyFetched(board);
        final boolean networkConnectionAvailable = NetworkUtils.isConnected(applicationContext);

        if (!requestIsInProgress) {
            if (networkConnectionAvailable
                    && (forceUpdate || !requestedRecently)) {
                String boardUrl = board.getUrl();
                if (append) {
                    boardUrl += "/page/" + pageNumber;
                }

                RetrieveBoardSummaryListHandler retrieveBoardSummaryListHandler =
                        new RetrieveBoardSummaryListHandler(applicationContext,
                                board.getBoardType(),
                                updateUI, append);
                networkRequestExecutorService.execute(
                        new GetBoardPostSummaryListRunnable(retrieveBoardSummaryListHandler,
                                httpClient, boardUrl));
                inProgressRequests.add(boardListName);
            } else {
                dbExecutorService.execute(new DatabaseRunnable(databaseHelper) {
                    @Override
                    public void run() {
                        List<BoardPost> cachedBoardPosts = dbHelper.getBoardPosts(board
                                .getBoardType());
                        eventBus.post(
                                new RetrievedBoardPostSummaryListEvent(cachedBoardPosts,
                                        board.getBoardType(),
                                        !networkConnectionAvailable, append));
                    }
                });
            }
        } else {
            Timber.d("Request for " + boardListName + " is already in progress ");
        }
    }

    private boolean recentlyFetched(Board cachedBoard) {
        BoardType type = cachedBoard.getBoardType();
        Board board = databaseHelper.getBoard(type);
        long lastFetchedTime = board.getLastFetchedTime();
        long oneMinuteAgo = System.currentTimeMillis()
                - (DateUtils.MINUTE_IN_MILLIS);

        boolean recentlyFetched = lastFetchedTime > oneMinuteAgo;

        if (DisBoardsConstants.DEBUG) {
            Timber.d(" last fetched time =  "
                    + lastFetchedTime
                    + " one  minute ago =  "
                    + oneMinuteAgo
                    + " so it has been "
                    + (recentlyFetched ? "recently fetched"
                    : "not recently fetched"));
        }

        return recentlyFetched;
    }

    public void thisAComment(String boardPostUrl, String boardPostId, String commentId,
            BoardType boardType) {
        ThisACommentHandler thisACommentHandler = new ThisACommentHandler(applicationContext,
                boardPostId, boardType);
        networkRequestExecutorService.execute(
                new ThisACommentRunnable(boardPostUrl, commentId, thisACommentHandler, httpClient));
    }

    public void addNewPost(Board board, String title, String content) {
        NewPostHandler newPostHandler = new NewPostHandler(applicationContext, board);
        String authToken = userSessionManager.getAuthenticityToken();
        networkRequestExecutorService.execute(
                new AddANewPostRunnable(newPostHandler, httpClient, board, title, content,
                        authToken));
    }

    public void postComment(String boardPostId, String commentId, String title, String content,
            BoardType boardType) {
        String authToken = userSessionManager.getAuthenticityToken();

        PostACommentHandler postACommentHandler = new PostACommentHandler(applicationContext,
                boardPostId, boardType);
        networkRequestExecutorService.execute(
                new PostACommentRunnable(postACommentHandler, httpClient, boardPostId, commentId,
                        title, content, authToken));
    }

    public void onEvent(RequestCompletedEvent requestCompletedEvent) {
        String idenfifer = requestCompletedEvent.getIdentifier();
        Timber.d("Completed request for " + idenfifer);
        if (inProgressRequests != null && inProgressRequests.contains(idenfifer)) {
            inProgressRequests.remove(idenfifer);
        }
    }

}

