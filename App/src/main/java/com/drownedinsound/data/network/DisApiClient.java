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
import com.drownedinsound.data.network.requests.AddANewPostRunnable;
import com.drownedinsound.data.network.requests.PostACommentRunnable;
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
        GET_LIST
    }

    private Context applicationContext;

    private OkHttpClient httpClient;

    private DatabaseHelper databaseHelper;

    private UserSessionManager userSessionManager;

    private EventBus eventBus;

    private ExecutorService networkRequestExecutorService;

    private ExecutorService dbExecutorService;

    private CopyOnWriteArrayList<Object> inProgressRequests;

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
    }

    public void loginUser(final String username, final String password) {
        RequestBody requestBody = new FormEncodingBuilder().add("user_session[username]", username)
                .add("user_session[password]", password)
                .add("user_session[remember_me]", "1")
                .add("return_to", UrlConstants.SOCIAL_URL)
                .add("commit", "Go!*").build();

        LoginResponseHandler loginResponseHandler = new LoginResponseHandler();
        DisBoardsApp.getApplication(applicationContext).inject(loginResponseHandler);

        makeRequest(RequestMethod.POST,"LOGIN",UrlConstants.LOGIN_URL,requestBody,loginResponseHandler);
    }

    private void inject(Object object){
        DisBoardsApp.getApplication(applicationContext).inject(object);
    }

    public void getBoardPost(String boardPostUrl, final String boardPostId, BoardType boardType) {
        if (NetworkUtils.isConnected(applicationContext)) {
            String tag = "GET_BOARD_POST_" + boardPostId;

            boolean requestIsInProgress = inProgressRequests.contains(tag);
            if (!requestIsInProgress) {
                RetrieveBoardPostHandler retrieveBoardPostHandler = new
                        RetrieveBoardPostHandler (boardPostId, boardType, true);
                inject(retrieveBoardPostHandler);

                makeRequest(RequestMethod.GET,tag,boardPostUrl,retrieveBoardPostHandler);

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

                makeRequest(RequestMethod.GET ,tag, boardUrl, retrieveBoardSummaryListHandler);

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
                - (DateUtils.MINUTE_IN_MILLIS * 1);

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
        ThisACommentHandler thisACommentHandler = new ThisACommentHandler(applicationContext,
                boardPostId, boardType);

        String fullUrl = boardPostUrl + "/" + commentId + "/this";
        Timber.d("Going to this with  =" + fullUrl);

        String tag = "THIS" +boardPostId;
        makeRequest(RequestMethod.GET, tag, fullUrl,thisACommentHandler);
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

    private void makeRequest(RequestMethod requestMethod, Object tag, String url, OkHttpAsyncResponseHandler
            okHttpAsyncResponseHandler) {
         makeRequest(requestMethod,tag,url,null,okHttpAsyncResponseHandler);
    }

    private void makeRequest(RequestMethod requestMethod, final Object tag, String url, RequestBody requestBody,
            final OkHttpAsyncResponseHandler okHttpAsyncResponseHandler){

        Headers.Builder headerBuilder = getMandatoryDefaultHeaders();
        Request.Builder builder = new Request.Builder().
                headers(headerBuilder.build());

        if (RequestMethod.GET.equals(requestMethod)) {
             makeRequest(builder.get(),tag,url,okHttpAsyncResponseHandler);
        } else if (RequestMethod.POST.equals(requestMethod)) {
             makeRequest(builder.post(requestBody),tag,url,okHttpAsyncResponseHandler);
        } else if (RequestMethod.PUT.equals(requestMethod)) {
             makeRequest(builder.put(requestBody),tag,url,okHttpAsyncResponseHandler);
        }  else if (RequestMethod.DELETE.equals(requestMethod)) {
            makeRequest(builder.delete(),tag,url,okHttpAsyncResponseHandler);
        }
    }

    private void makeRequest(Request.Builder requestBuilder, final Object tag, String url,
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
                Timber.d("Response on "+Thread.currentThread().getName());
                okHttpAsyncResponseHandler.onResponse(response);
            }
        });
    }

    public boolean requestInProgress(Object tag) {
        return inProgressRequests.contains(tag);
    }

    protected Headers.Builder getMandatoryDefaultHeaders() {
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
}

