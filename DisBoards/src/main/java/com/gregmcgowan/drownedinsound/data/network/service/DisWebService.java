package com.gregmcgowan.drownedinsound.data.network.service;

import com.gregmcgowan.drownedinsound.core.DisBoardsApp;
import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.UserSessionManager;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.network.HttpClient;
import com.gregmcgowan.drownedinsound.data.network.UrlConstants;
import com.gregmcgowan.drownedinsound.data.network.handlers.LoginResponseHandler;
import com.gregmcgowan.drownedinsound.data.network.handlers.NewPostHandler;
import com.gregmcgowan.drownedinsound.data.network.handlers.PostACommentHandler;
import com.gregmcgowan.drownedinsound.data.network.handlers.RetrieveBoardPostHandler;
import com.gregmcgowan.drownedinsound.data.network.handlers.RetrieveBoardSummaryListHandler;
import com.gregmcgowan.drownedinsound.data.network.handlers.ThisACommentHandler;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.gregmcgowan.drownedinsound.utils.NetworkUtils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import android.app.IntentService;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * A service that will handle all the requests to the website
 *
 * @author Greg
 */
public class DisWebService extends IntentService {

    private static final String SERVICE_NAME = "DisWebService";

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "DisWebService";

    @Inject
    OkHttpClient httpClient;

    @Inject
    DatabaseHelper databaseHelper;

    @Inject
    UserSessionManager userSessionManager;

    @Inject
    EventBus eventBus;

    public DisWebService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DisBoardsApp disBoardsApp = DisBoardsApp.getApplication(this);
        disBoardsApp.inject(this);

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
            case DisWebServiceConstants.THIS_A_COMMENT_ID:
                handleThisAComment(intent);
                break;
            case DisWebServiceConstants.POST_A_COMMENT:
                postComment(intent);
                break;
            case DisWebServiceConstants.NEW_POST:
                newPost(intent);
                break;
            default:
                break;
        }
    }

    private void handleLoginRequest(Intent intent) {
        String username = intent.getStringExtra(DisBoardsConstants.USERNAME);
        String password = intent.getStringExtra(DisBoardsConstants.PASSWORD);
        RequestBody requestBody = new FormEncodingBuilder().add("user_session[username]", username)
                .add("user_session[password]", password)
                .add("user_session[remember_me]", "1")
                .add("return_to", UrlConstants.SOCIAL_URL)
                .add("commit", "Go!*").build();
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(requestBody).url(UrlConstants.LOGIN_URL).build();

        httpClient.newCall(request).enqueue(new LoginResponseHandler(this));

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
                if (DisBoardsConstants.DEBUG) {
                    Log.d(TAG, "Going to request = " + boardPostUrl);
                }
                Headers.Builder headerBuilder = getMandatoryDefaultHeaders();
                Request.Builder requestBuilder = new Request.Builder();
                Request request = requestBuilder.get().url(boardPostUrl)
                        .headers(headerBuilder.build()).build();
                httpClient.newCall(request).enqueue(new
                        RetrieveBoardPostHandler(this, boardPostId, boardType, true));
            }

        } else {
            eventBus.post(
                    new RetrievedBoardPostEvent(cachedPost, true, true));
        }

    }

    private Headers.Builder getMandatoryDefaultHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        headerBuilder.add("Cache-Control", "max-age=0");
        headerBuilder.add("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
        headerBuilder
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headerBuilder.add("Accept-Encoding", "gzip,deflate,sdch");
        headerBuilder.add("Accept-Language", "en-US,en;q=0.8,en-GB;q=0.6");
        headerBuilder.add("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
        return headerBuilder;
    }

    private void handleGetPostSummaryList(Intent intent) {
        Board board = intent.getParcelableExtra(DisBoardsConstants.BOARD);
        boolean forceFetch = intent.getBooleanExtra(
                DisBoardsConstants.FORCE_FETCH, false);
        int pageNumber = intent.getIntExtra(
                DisBoardsConstants.BOARD_PAGE_NUMBER, 1);
        fetchBoardPostSummaryList(pageNumber, board, true, forceFetch);
    }

    private void fetchBoardPostSummaryList(int pageNumber, Board board,
            boolean updateUI, boolean forceFetch) {
        List<BoardPost> cachedBoardPosts = databaseHelper.getBoardPosts(board
                .getBoardType());
        boolean requestIsInProgress = HttpClient.requestIsInProgress(board
                .getBoardType().name());
        boolean append = pageNumber > 1;

        if (!requestIsInProgress) {
            if (NetworkUtils.isConnected(this)) {
                String boardUrl = board.getUrl();
                if (append) {
                    boardUrl += "/page/" + pageNumber;
                }
                if (DisBoardsConstants.DEBUG) {
                    Log.d(TAG, "Going to request = " + boardUrl);
                }

                Request.Builder requestBuilder = new Request.Builder();
                Headers.Builder headerBuilder = getMandatoryDefaultHeaders();
                Request request = requestBuilder.get().url(boardUrl)
                        .headers(headerBuilder.build()).build();
                httpClient.newCall(request).enqueue(
                        new RetrieveBoardSummaryListHandler(this, board.getBoardType(),
                                updateUI, append));
            } else {
                if (updateUI) {
                    eventBus.post(
                            new RetrievedBoardPostSummaryListEvent(
                                    cachedBoardPosts, board.getBoardType(),
                                    true, false));
                }
            }
        }
    }

    private void handleThisAComment(Intent intent) {
        String boardPostUrl = intent
                .getStringExtra(DisBoardsConstants.BOARD_POST_URL);
        String boardPostId = intent
                .getStringExtra(DisBoardsConstants.BOARD_POST_ID);
        String commentId = intent
                .getStringExtra(DisBoardsConstants.BOARD_COMMENT_ID);
        BoardType boardType = (BoardType) intent
                .getSerializableExtra(DisBoardsConstants.BOARD_TYPE);

        String fullUrl = boardPostUrl + "/" + commentId + "/this";
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG, "Going to request  =" + fullUrl);
        }

        Headers.Builder headerBuilder = getMandatoryDefaultHeaders();
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.get().url(fullUrl)
                .headers(headerBuilder.build()).build();
        httpClient.newCall(request)
                .enqueue(new ThisACommentHandler(this,boardPostId, boardType));
    }

    private void newPost(Intent intent) {
        Board board = intent.getParcelableExtra(DisBoardsConstants.BOARD);
        String title = intent.getStringExtra(DisBoardsConstants.NEW_POST_TITLE);
        String content = intent
                .getStringExtra(DisBoardsConstants.NEW_POST_CONTENT);

        Headers.Builder headerBuilder = getMandatoryDefaultHeaders();
        headerBuilder.add("Referer", board.getUrl());
        RequestBody requestBody = new FormEncodingBuilder().add("section_id", String.valueOf(board
                .getSectionId()))
                .add("topic[title]", title)
                .add("topic[content_raw]", content)
                .add("topic[sticky]", "0")
                .add("authenticity_token", userSessionManager.getAuthenticityToken())
                .add("commit", "Post it").build();
        Request.Builder requestBuilder = new Request.Builder();

        Request request = requestBuilder.post(requestBody).headers(headerBuilder.build())
                .url(UrlConstants.NEW_POST_URL).build();

        httpClient.newCall(request).enqueue(new NewPostHandler(this,board));
    }

    private void postComment(Intent intent) {
        String boardPostId = intent
                .getStringExtra(DisBoardsConstants.BOARD_POST_ID);
        String commentId = intent
                .getStringExtra(DisBoardsConstants.BOARD_COMMENT_ID);
        if (commentId == null) {
            commentId = "";
        }
        BoardType boardType = (BoardType) intent
                .getSerializableExtra(DisBoardsConstants.BOARD_TYPE);
        String title = intent.getStringExtra(DisBoardsConstants.COMMENT_TITLE);
        String content = intent
                .getStringExtra(DisBoardsConstants.COMMENT_CONTENT);

        Headers.Builder headerBuilder = getMandatoryDefaultHeaders();
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

        httpClient.newCall(request).enqueue(new PostACommentHandler(this, boardPostId, boardType));
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
}
