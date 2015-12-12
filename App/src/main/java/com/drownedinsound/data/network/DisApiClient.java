package com.drownedinsound.data.network;

import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.network.handlers.NewPostHandler;
import com.drownedinsound.data.network.handlers.ResponseHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardPostHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardSummaryListHandler;
import com.drownedinsound.data.network.handlers.ThisACommentHandler;
import com.drownedinsound.data.parser.streaming.DisWebPageParser;
import com.drownedinsound.data.parser.streaming.LoginException;
import com.drownedinsound.utils.NetworkUtils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * A service that will handle all the requests to the website
 *
 * @author Greg
 */
@Singleton
public class DisApiClient implements DisBoardsApi {

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


    public enum REQUEST_TYPE {
        GET_LIST,
        NEW_POST
    }

    private Context applicationContext;

    private OkHttpClient httpClient;;

    private CopyOnWriteArrayList<Object> inProgressRequests;

    private String baseUrl;

    private DisWebPageParser disWebPageParser;

    @Inject
    public DisApiClient(Application applicationContext, OkHttpClient httpClient,
            DisWebPageParser disWebPageParser) {

        this.applicationContext = applicationContext;
        this.httpClient = httpClient;
        this.inProgressRequests = new CopyOnWriteArrayList<>();
        this.disWebPageParser = disWebPageParser;
        this.baseUrl = UrlConstants.BASE_URL;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Observable<LoginResponse> loginUser(String username, String password) {
        RequestBody requestBody = new FormEncodingBuilder().add("user_session[username]", username)
                .add("user_session[password]", password)
                .add("user_session[remember_me]", "1")
                .add("return_to", UrlConstants.SOCIAL_URL)
                .add("commit", "Go!*").build();

        String url = baseUrl + UrlConstants.LOGIN_PATH;

        return makeRequest(RequestMethod.POST, url, requestBody, "LOGIN")
                .flatMap(new Func1<Response, Observable<LoginResponse>>() {
                    @Override
                    public Observable<LoginResponse> call(Response response) {
                        try {
                            LoginResponse loginResponse = parseResponse(response);
                            return Observable.just(loginResponse);
                        } catch (IOException | LoginException e) {
                            return Observable.error(e);
                        }
                    }
                });
    }

    private LoginResponse parseResponse(Response response) throws IOException, LoginException {
        String url = response.priorResponse().header("Location");
        String expected = baseUrl + UrlConstants.BOARD_BASE_PATH
                + UrlConstants.SOCIAL_BOARD_NAME;
        System.out.println("Login response url " + url + " expected " + expected);

        boolean logInSuccess = expected.equals(url);
        if (logInSuccess) {
            String authToken = disWebPageParser.getAuthenticationToken(
                    getInputStreamFromResponse(response));
            if (authToken != null && authToken.length() > 0) {
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setAuthenticationToken(authToken);
                return loginResponse;
            } else {
                throw new LoginException();
            }
        } else {
            throw new LoginException();
        }
    }

    @Override
    public Observable<BoardPost> getBoardPost(String boardPostUrl, String boardPostId,
            BoardType boardType) {
        return null;
    }

    @Override
    public Observable<List<BoardPost>> getBoardPostSummaryList(Object tag, int pageNumber) {
        return null;
    }

    @Override
    public Observable<Void> thisAComment(String boardPostUrl, String boardPostId, String commentId,
            BoardType boardType) {
        return null;
    }

    @Override
    public Observable<Void> addNewPost(Board board, String title, String content,
            ResponseHandler responseHandler) {
        return null;
    }

    @Override
    public Observable<Void> postComment(String boardPostId, String commentId, String title,
            String content, BoardType boardType, ResponseHandler responseHandler) {
        return null;
    }

    private Observable<Response> makeRequest(RequestMethod requestMethod, String url, Object tag) {
        return makeRequest(requestMethod, url, null, null, tag);
    }

    private Observable<Response> makeRequest(RequestMethod requestMethod, String url,
            RequestBody requestBody,
            Object tag) {
        return makeRequest(requestMethod, url, requestBody, null, tag);
    }

    private Observable<Response> makeRequest(RequestMethod requestMethod, String url,
            RequestBody requestBody,
            Headers.Builder extraHeaders, Object tag) {

        Headers.Builder headerBuilder = addMandatoryHeaders(extraHeaders);

        Request.Builder builder = new Request.Builder().
                headers(headerBuilder.build());

        if (RequestMethod.GET.equals(requestMethod)) {
            builder = builder.get();
        } else if (RequestMethod.POST.equals(requestMethod)) {
            builder = builder.post(requestBody);
        } else if (RequestMethod.PUT.equals(requestMethod)) {
            builder = builder.put(requestBody);
        } else if (RequestMethod.DELETE.equals(requestMethod)) {
            builder = builder.delete();
        } else {
            throw new IllegalArgumentException("Invalid request type");
        }

        return makeRequest(builder, url, tag);
    }

    private Observable<Response> makeRequest(final Request.Builder requestBuilder,
            final String url,
            final Object tag) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                inProgressRequests.add(tag);
                Request request = requestBuilder.url(url).build();
                try {
                    Response response = httpClient.newCall(request).execute();
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(response);
                        subscriber.onCompleted();
                    }
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    protected Headers.Builder addMandatoryHeaders(Headers.Builder headers) {
        if (headers == null) {
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

    private InputStream getInputStreamFromResponse(Response response) throws IOException{
        String encodingHeader = response.header("Content-Encoding");
        boolean gzipped = encodingHeader != null && encodingHeader.contains("gzip");
        if (gzipped) {
            return new GZIPInputStream(response.body().byteStream());
        } else {
            return response.body().byteStream();
        }
    }

    public void getBoardPost(String boardPostUrl, final String boardPostId, BoardType boardType,
            final int callerUiId) {
        if (NetworkUtils.isConnected(applicationContext)) {
            String tag = "GET_BOARD_POST_" + boardPostId;

            boolean requestIsInProgress = inProgressRequests.contains(tag);
            if (!requestIsInProgress) {
                RetrieveBoardPostHandler retrieveBoardPostHandler = new
                        RetrieveBoardPostHandler(boardPostId, boardType, true, callerUiId);
                makeRequest(RequestMethod.GET, boardPostUrl, tag);

            } else {
                Timber.d("Board post " + boardPostId + " has already been requested");
            }

        } else {
//            dbExecutorService.execute(new DatabaseRunnable(databaseHelper) {
//                @Override
//                public void run() {
//                    BoardPost cachedPost = dbHelper.getBoardPost(boardPostId);
//                    eventBus.post(
//                            new RetrievedBoardPostEvent(cachedPost, true, true, callerUiId));
//                }
//            });
        }

    }

    public void getBoardPostSummaryList(Object tag, final int callerUiId, int pageNumber,
            final Board board,
            boolean forceUpdate, boolean updateUI) {
        final boolean append = pageNumber > 1;
        final boolean networkConnectionAvailable = NetworkUtils.isConnected(applicationContext);

        Timber.d("networkConnectionAvailable " + networkConnectionAvailable
                + " forceUpdate " + forceUpdate);
        if (networkConnectionAvailable
                && (forceUpdate)) {
            String boardUrl = board.getUrl();
            if (append) {
                boardUrl += "/page/" + pageNumber;
            }

            RetrieveBoardSummaryListHandler retrieveBoardSummaryListHandler =
                    new RetrieveBoardSummaryListHandler(callerUiId,
                            board.getBoardType(),
                            updateUI, append);

            //makeRequest(RequestMethod.GET, tag, boardUrl);

        } else {
//            dbExecutorService.execute(new DatabaseRunnable(databaseHelper) {
//                @Override
//                public void run() {
//                    List<BoardPost> cachedBoardPosts = dbHelper.getBoardPosts(board
//                            .getBoardType());
//                    eventBus.post(
//                            new RetrievedBoardPostSummaryListEvent(cachedBoardPosts,
//                                    board.getBoardType(),
//                                    !networkConnectionAvailable, append, callerUiId));
//                }
//            });
        }
    }


    public void thisAComment(String boardPostUrl, String boardPostId, String commentId,
            BoardType boardType, int callingId) {
        ThisACommentHandler thisACommentHandler = new ThisACommentHandler(callingId, boardPostId,
                boardType);

        String fullUrl = boardPostUrl + "/" + commentId + "/this";
        Timber.d("Going to this with  =" + fullUrl);

        String tag = "THIS" + boardPostId;
        makeRequest(RequestMethod.GET, tag, fullUrl);
    }


    public void addNewPost(Board board, String title, String content, String authToken) {
        Headers.Builder extraHeaders = new Headers.Builder();
        extraHeaders.add("Referer", board.getUrl());
        RequestBody requestBody = new FormEncodingBuilder().add("section_id", String.valueOf(board
                .getSectionId()))
                .add("topic[title]", title)
                .add("topic[content_raw]", content)
                .add("topic[sticky]", "0")
                .add("authenticity_token", authToken).build();

        NewPostHandler newPostHandler = new NewPostHandler(board);

        makeRequest(RequestMethod.POST,
                UrlConstants.NEW_POST_URL, requestBody, extraHeaders,REQUEST_TYPE.NEW_POST);
    }

    public void postComment(String boardPostId, String commentId, String title, String content,
            BoardType boardType, String authToken) {
        if (TextUtils.isEmpty(authToken)) {
            throw new IllegalArgumentException("Auth token cannot be null");
        }

        if (TextUtils.isEmpty(boardPostId)) {
            throw new IllegalArgumentException("BoardPostId cannot be null");
        }

//        PostACommentHandler postACommentHandler = new PostACommentHandler(boardPostId, boardType,
//                callingUiId);
       // inject(postACommentHandler);

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

        makeRequest(RequestMethod.POST, UrlConstants.COMMENTS_URL, requestBody, null, tag);
    }


    @Override
    public boolean requestInProgress(Object tag) {
        return inProgressRequests.contains(tag);
    }


}

