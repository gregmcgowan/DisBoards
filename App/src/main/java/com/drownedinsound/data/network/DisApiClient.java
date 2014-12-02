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
import com.drownedinsound.database.DatabaseHelper;
import com.drownedinsound.data.network.requests.GetBoardPostRunnable;
import com.drownedinsound.data.network.requests.GetBoardPostSummaryListRunnable;
import com.drownedinsound.data.network.requests.LoginRunnable;
import com.drownedinsound.database.DatabaseRunnable;
import com.drownedinsound.events.RequestCompletedEvent;
import com.drownedinsound.events.RetrievedBoardPostEvent;
import com.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.drownedinsound.qualifiers.ForDatabase;
import com.drownedinsound.qualifiers.ForNetworkRequests;
import com.drownedinsound.utils.NetworkUtils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import android.app.Application;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

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

    private static final String SERVICE_NAME = "DisWebService";

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "DisWebService";

    private Context applicationContext;

    private OkHttpClient httpClient;

    private DatabaseHelper databaseHelper;

    private UserSessionManager userSessionManager;

    private EventBus eventBus;

    private ExecutorService networkRequestExecutorService;

    private ExecutorService dbExecutorService;

    private CopyOnWriteArrayList<String> inProgressRequests;

    @Inject
    public DisApiClient(Application applicationContext, OkHttpClient httpClient, DatabaseHelper databaseHelper,
            UserSessionManager userSessionManager, EventBus eventBus, @ForNetworkRequests ExecutorService networkExecutorService,
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

    public void getBoardPostSummaryList(int pageNumber,final Board board, boolean forceUpdate, boolean updateUI) {
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

    public void thisAComment(String boardPostUrl, String boardPostId, String commentId, BoardType boardType) {

        String fullUrl = boardPostUrl + "/" + commentId + "/this";
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG, "Going to request  =" + fullUrl);
        }

        Headers.Builder headerBuilder = null;// getMandatoryDefaultHeaders();
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.get().url(fullUrl)
                .headers(headerBuilder.build()).build();
        httpClient.newCall(request)
                .enqueue(new ThisACommentHandler(applicationContext, boardPostId, boardType));
    }

    public void addNewPost(Board board, String title, String content) {
        Headers.Builder headerBuilder = null;//getMandatoryDefaultHeaders();
        headerBuilder.add("Referer", board.getUrl());
        RequestBody requestBody = new FormEncodingBuilder().add("section_id", String.valueOf(board
                .getSectionId()))
                .add("topic[title]", title)
                .add("topic[content_raw]", content)
                .add("topic[sticky]", "0")
                .add("authenticity_token", userSessionManager.getAuthenticityToken()).build();
        Request.Builder requestBuilder = new Request.Builder();

        Request request = requestBuilder.post(requestBody).headers(headerBuilder.build())
                .url(UrlConstants.NEW_POST_URL).build();

        httpClient.newCall(request).enqueue(new NewPostHandler(applicationContext, board));
    }

    public void postComment(String boardPostId, String commentId, String title, String content, BoardType boardType) {
        Headers.Builder headerBuilder = null;// getMandatoryDefaultHeaders();
        if (commentId == null) {
            commentId = "";
        }

        RequestBody requestBody = new FormEncodingBuilder()
                .add("comment[commentable_id]", boardPostId)
                .add("comment[title]", title)
                .add("comment[commentable_type]", "Topic")
                .add("comment[content_raw]", content)
                .add("parent_id", commentId)
                .add("authenticity_token", userSessionManager.getAuthenticityToken())
                .add("commit", "Post reply").build();
        Request.Builder requestBuilder = new Request.Builder();

        Request request = requestBuilder.post(requestBody).headers(headerBuilder.build())
                .url(UrlConstants.COMMENTS_URL).build();

        httpClient.newCall(request)
                .enqueue(new PostACommentHandler(applicationContext, boardPostId, boardType));
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

    public void onEvent(RequestCompletedEvent requestCompletedEvent) {
        String idenfifer = requestCompletedEvent.getIdentifier();
        Timber.d("Completed request for " + idenfifer);
        if (inProgressRequests != null && inProgressRequests.contains(idenfifer)) {
            inProgressRequests.remove(idenfifer);
        }
    }

}
    // TODO Not sure if this just causes more problems
//    private static class FetchBoardRunnable implements Runnable {
//
//        private DatabaseHelper databaseHelper;
//        private WeakReference<Context> context;
//        private ArrayList<Board> boards;
//        private boolean forceFetch;
//
//        FetchBoardRunnable(ArrayList<Board> boards,
//                           WeakReference<Context> context, DatabaseHelper databaseHelper,
//                           boolean forceFetch) {
//            this.boards = boards;
//            this.context = context;
//            this.databaseHelper = databaseHelper;
//            this.forceFetch = forceFetch;
//        }
//
//        @Override
//        public void run() {
//            for (Board boardToFetch : boards) {
//                fetchBoardType(boardToFetch);
//            }
//        }
//
//        private void fetchBoardType(Board board) {
//            boolean requestIsInProgress = HttpClient.requestIsInProgress(board
//                .getBoardType().name());
//            if (!requestIsInProgress) {
//                if (NetworkUtils.isConnected(context.get())) {
//                    if (forceFetch || !recentlyFetched(board)) {
//                        HttpClient.requestBoardSummary(
//                            context.get(),
//                            board.getUrl(),
//                            board.getBoardType(),
//                            new RetrieveBoardSummaryListHandler(board
//                                .getBoardType(), forceFetch,
//                                databaseHelper, false), 1);
//                    }
//                }
//            }
//        }
//
//        private boolean recentlyFetched(Board cachedBoard) {
//            boolean recentlyFetched = false;
//            BoardType type = cachedBoard.getBoardType();
//            Board board = databaseHelper.getBoard(type);
//            long lastFetchedTime = board.getLastFetchedTime();
//            long oneMinuteAgo = System.currentTimeMillis()
//                - (DateUtils.MINUTE_IN_MILLIS);
//
//            recentlyFetched = lastFetchedTime > oneMinuteAgo;
//
//            if (DisBoardsConstants.DEBUG) {
//                Log.d(TAG, " last fetched time =  "
//                    + lastFetchedTime
//                    + " one  minute ago =  "
//                    + oneMinuteAgo
//                    + " so it has been "
//                    + (recentlyFetched ? "recently fetched"
//                    : "not recently fetched"));
//            }
//
//            return recentlyFetched;
//        }
//    }

