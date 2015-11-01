package com.drownedinsound.data.network;

import com.drownedinsound.core.DisBoardsApp;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.network.handlers.LoginResponseHandler;
import com.drownedinsound.data.network.handlers.NewPostHandler;
import com.drownedinsound.data.network.handlers.OkHttpAsyncResponseHandler;
import com.drownedinsound.data.network.handlers.PostACommentHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardPostHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardSummaryListHandler;
import com.drownedinsound.data.network.handlers.ThisACommentHandler;
import com.drownedinsound.database.DatabaseHelper;
import com.drownedinsound.database.DatabaseRunnable;
import com.drownedinsound.events.RetrievedBoardPostEvent;
import com.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.drownedinsound.qualifiers.ForDatabase;
import com.drownedinsound.qualifiers.ForNetworkRequests;
import com.drownedinsound.utils.NetworkUtils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * A service that will handle all the requests to the website
 *
 * @author Greg
 */
@Singleton
public class DisApiClient {

    private static final long MAX_BOARD_POST_LIST_AGE_MINUTES = 5;

    public enum RequestMethod {
        GET(false),
        POST(true),
        PUT(true),
        DELETE(false);

        public final boolean hasRequestBody;

        private RequestMethod(boolean hasRequestBody) {
            this.hasRequestBody = hasRequestBody;
        }
    }


    public enum REQUEST_TYPE  {
        GET_LIST,
        NEW_POST
    }

    private Context applicationContext;

    private OkHttpClient httpClient;

    private DatabaseHelper databaseHelper;

    private UserSessionManager userSessionManager;

    private EventBus eventBus;

    private ExecutorService dbExecutorService;

    private CopyOnWriteArrayList<Object> inProgressRequests;

    @Inject
    public DisApiClient(Application applicationContext, OkHttpClient httpClient,
            DatabaseHelper databaseHelper,
            UserSessionManager userSessionManager, EventBus eventBus,
            @ForDatabase ExecutorService dbExecutorService) {

        this.applicationContext = applicationContext;
        this.httpClient = httpClient;
        this.databaseHelper = databaseHelper;
        this.userSessionManager = userSessionManager;
        this.eventBus = eventBus;
        this.dbExecutorService = dbExecutorService;
        this.inProgressRequests = new CopyOnWriteArrayList<>();
    }

    public void loginUser(final String username, final String password) {
        RequestBody requestBody = new FormEncodingBuilder().add("user_session[username]", username)
                .add("user_session[password]", password)
                .add("user_session[remember_me]", "1")
                .add("return_to", UrlConstants.SOCIAL_URL)
                .add("commit", "Go!*").build();

        LoginResponseHandler loginResponseHandler = new LoginResponseHandler();
        inject(loginResponseHandler);

        makeRequest(RequestMethod.POST, "LOGIN", UrlConstants.LOGIN_URL, requestBody,
                null, loginResponseHandler);
    }

    private void inject(Object object){
        DisBoardsApp.getApplication(applicationContext).inject(object);
    }

    public void getBoardPost(String boardPostUrl, final String boardPostId, BoardType boardType, final int callerUiId) {
        if (NetworkUtils.isConnected(applicationContext)) {
            String tag = "GET_BOARD_POST_" + boardPostId;

            boolean requestIsInProgress = inProgressRequests.contains(tag);
            if (!requestIsInProgress) {
                RetrieveBoardPostHandler retrieveBoardPostHandler = new
                        RetrieveBoardPostHandler (boardPostId, boardType, true,callerUiId);
                inject(retrieveBoardPostHandler);

                makeRequest(RequestMethod.GET, tag, boardPostUrl, retrieveBoardPostHandler);

            } else {
                Timber.d("Board post " + boardPostId + " has already been requested");
            }

        } else {
            dbExecutorService.execute(new DatabaseRunnable(databaseHelper) {
                @Override
                public void run() {
                    BoardPost cachedPost = dbHelper.getBoardPost(boardPostId);
                    eventBus.post(
                            new RetrievedBoardPostEvent(cachedPost, true, true, callerUiId));
                }
            });
        }

    }

    public void getBoardPostSummaryList(Object tag, final int callerUiId, int pageNumber, final Board board,
            boolean forceUpdate, boolean updateUI) {
        final boolean append = pageNumber > 1;
        final boolean requestedRecently = recentlyFetched(board);
        final boolean networkConnectionAvailable = NetworkUtils.isConnected(applicationContext);

            Timber.d("networkConnectionAvailable " + networkConnectionAvailable
                    + " forceUpdate " + forceUpdate + " requestedRecently " + requestedRecently);
            if (networkConnectionAvailable
                    && (forceUpdate || !requestedRecently)) {
                String boardUrl = board.getUrl();
                if (append) {
                    boardUrl += "/page/" + pageNumber;
                }

                RetrieveBoardSummaryListHandler retrieveBoardSummaryListHandler =
                        new RetrieveBoardSummaryListHandler(callerUiId,
                                board.getBoardType(),
                                updateUI, append);

                inject(retrieveBoardSummaryListHandler);

                makeRequest(RequestMethod.GET, tag, boardUrl, retrieveBoardSummaryListHandler);

            } else {
                dbExecutorService.execute(new DatabaseRunnable(databaseHelper) {
                    @Override
                    public void run() {
                        List<BoardPost> cachedBoardPosts = dbHelper.getBoardPosts(board
                                .getBoardType());
                        eventBus.post(
                                new RetrievedBoardPostSummaryListEvent(cachedBoardPosts,
                                        board.getBoardType(),
                                        !networkConnectionAvailable, append, callerUiId));
                    }
                });
            }
    }

    private boolean recentlyFetched(Board cachedBoard) {
        BoardType type = cachedBoard.getBoardType();
        Board board = databaseHelper.getBoard(type);
        long lastFetchedTime = board.getLastFetchedTime();
        long fiveMinutesAgo = System.currentTimeMillis()
                - (DateUtils.MINUTE_IN_MILLIS * MAX_BOARD_POST_LIST_AGE_MINUTES);

        boolean recentlyFetched = lastFetchedTime > fiveMinutesAgo;

        if (DisBoardsConstants.DEBUG) {
            Timber.d("type " + type + " fetched " + (((System.currentTimeMillis() - lastFetchedTime)
                    / 1000))
                    + " seconds ago "
                    + (recentlyFetched ? "recently fetched"
                    : "not recently fetched"));
        }

        return recentlyFetched;
    }

    public void thisAComment(String boardPostUrl, String boardPostId, String commentId,
            BoardType boardType) {
        ThisACommentHandler thisACommentHandler = new ThisACommentHandler(boardPostId, boardType);
        inject(thisACommentHandler);

        String fullUrl = boardPostUrl + "/" + commentId + "/this";
        Timber.d("Going to this with  =" + fullUrl);

        String tag = "THIS" +boardPostId;
        makeRequest(RequestMethod.GET, tag, fullUrl, thisACommentHandler);
    }


    public void addNewPost(Board board, String title, String content) {
        String authToken = userSessionManager.getAuthenticityToken();

        Headers.Builder extraHeaders = new Headers.Builder();
        extraHeaders.add("Referer", board.getUrl());
        RequestBody requestBody = new FormEncodingBuilder().add("section_id", String.valueOf(board
                .getSectionId()))
                .add("topic[title]", title)
                .add("topic[content_raw]", content)
                .add("topic[sticky]", "0")
                .add("authenticity_token", authToken).build();

        NewPostHandler newPostHandler = new NewPostHandler(board);
        inject(newPostHandler);

        makeRequest(RequestMethod.POST, REQUEST_TYPE.NEW_POST,
                UrlConstants.NEW_POST_URL, requestBody, extraHeaders, newPostHandler);
    }

    public void postComment(String boardPostId, String commentId, String title, String content,
            BoardType boardType) {
        String authToken = userSessionManager.getAuthenticityToken();

        if (TextUtils.isEmpty(authToken)) {
            throw new IllegalArgumentException("Auth token cannot be null");
        }

        if (TextUtils.isEmpty(boardPostId)) {
            throw new IllegalArgumentException("BoardPostId cannot be null");
        }

        PostACommentHandler postACommentHandler = new PostACommentHandler(boardPostId, boardType);
        inject(postACommentHandler);

        if (commentId == null) {
            commentId = "";
        }

        RequestBody requestBody = new FormEncodingBuilder()
                .add("comment[commentable_id]", boardPostId)
                .add("comment[title]", title)
                .add("comment[commentable_type]", "Topic")
                .add("comment[content_raw]", content)
                .add("parent_id", commentId)
                .add("authenticity_token", authToken)
                .add("commit", "Post reply").build();

        String tag = boardPostId + "COMMENT" + commentId;

        makeRequest(RequestMethod.POST,tag,UrlConstants.COMMENTS_URL,requestBody,null,postACommentHandler);
    }

    private void makeRequest(RequestMethod requestMethod, Object tag, String url,
            OkHttpAsyncResponseHandler
                    okHttpAsyncResponseHandler) {
         makeRequest(requestMethod, tag, url, null, null, okHttpAsyncResponseHandler);
    }


    private void makeRequest(RequestMethod requestMethod, final Object tag, String url,
            RequestBody requestBody,
            Headers.Builder extraHeaders,
            final OkHttpAsyncResponseHandler okHttpAsyncResponseHandler){

        Headers.Builder headerBuilder = addMandatoryHeaders(extraHeaders);

        Request.Builder builder = new Request.Builder().
                headers(headerBuilder.build());

        if (RequestMethod.GET.equals(requestMethod)) {
             performRequest(builder.get(), tag, url, okHttpAsyncResponseHandler);
        } else if (RequestMethod.POST.equals(requestMethod)) {
             performRequest(builder.post(requestBody), tag, url, okHttpAsyncResponseHandler);
        } else if (RequestMethod.PUT.equals(requestMethod)) {
             performRequest(builder.put(requestBody), tag, url, okHttpAsyncResponseHandler);
        }  else if (RequestMethod.DELETE.equals(requestMethod)) {
             performRequest(builder.delete(), tag, url, okHttpAsyncResponseHandler);
        }
    }

    private void performRequest(Request.Builder requestBuilder, final Object tag, String url,
            final OkHttpAsyncResponseHandler okHttpAsyncResponseHandler) {

        inProgressRequests.add(tag);

        Request request = requestBuilder.url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                inProgressRequests.remove(tag);
                okHttpAsyncResponseHandler.onFailure(request,e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                inProgressRequests.remove(tag);
                okHttpAsyncResponseHandler.onResponse(response);
            }
        });
    }

    public boolean requestInProgress(Object tag) {
        return inProgressRequests.contains(tag);
    }


    protected Headers.Builder addMandatoryHeaders(Headers.Builder headers) {
        if(headers == null) {
            headers = new Headers.Builder();
        }

        headers.add("Cache-Control", "max-age=0");
        headers.add("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
        headers
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.add("Accept-Encoding", "gzip,deflate,sdch");
        headers.add("Accept-Language", "en-US,en;q=0.8,en-GB;q=0.6");
        headers.add("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
        return headers;
    }
}

