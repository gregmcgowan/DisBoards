package com.gregmcgowan.drownedinsound.data.network;

import android.content.Context;
import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.greenrobot.event.EventBus;



/**
 * Http client for making requests to the drowned in sound website
 *
 * @author Greg
 */
public class HttpClient {

    public final static MediaType URL_ENCODED_CONTENT = MediaType.parse("application/x-www-form-urlencoded");
    public final static String CONTENT_ENCODING = "UTF-8";
    private final static String REQUEST_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final static String TAG = DisBoardsConstants.LOG_TAG_PREFIX
        + "HttpClient";

    private static OkHttpClient asyncHttpClient = new OkHttpClient();

    private static HashSet<String> requestsInProgress = new HashSet<String>();
    private static final boolean useFakeData = false;

    public static void setTimeout(int timeout) {
     //   asyncHttpClient.setTimeout(timeout);
    }

    public static void initialiseRedirectClient(final Context appContext) {
//        asyncHttpClient.setRedirectHandler(new DefaultRedirectHandler() {
//
//            @Override
//            public URI getLocationURI(HttpResponse response, HttpContext context)
//                throws ProtocolException {
//                //First check we are not redirected to login page if so we need to
//                Header locationHeader = response.getFirstHeader("location");
//                if (locationHeader != null) {
//                    String locationValue = locationHeader.getValue();
//                    if ("http://drownedinsound.com/login".equals(locationValue)) {
//                        EventBus.getDefault().post(new UserIsNotLoggedInEvent());
//                        DisBoardsApp.getApplication(appContext).clearCookies();
//                        throw new UserNotLoggedInException();
//                    }
//                }
//
//                context.setAttribute(AsyncHttpClient.REDIRECT_LOCATIONS, new RedirectLocations());
//                return super.getLocationURI(response, context);
//            }
//
//        });
    }

    /**
     * Indicates that the request represented by identifier has compelted
     *
     * @param identifier
     */
    public synchronized static void requestHasCompleted(String identifier) {
        boolean removed = requestsInProgress.remove(identifier);
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG, (removed ? "Removed" : "Did not remove")
                + "  the request with identfifier " + identifier);
        }
    }

    /**
     * Identicates if the request is still in progress
     *
     * @param identifier
     * @return
     */
    public synchronized static boolean requestIsInProgress(String identifier) {
        boolean inProgress = requestsInProgress.contains(identifier);
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG, "Request " + identifier + " is "
                + (inProgress ? " in progress " : "not in progress"));
        }
        return inProgress;
    }

    private static synchronized void addRequest(String identifier) {
        requestsInProgress.add(identifier);
    }

    /**
     * Cancels all requests associated with the context provided. This should
     * only really be used on the onDestory method
     *
     * @param context
     */
    public synchronized static void cancelAllRequests(Context context) {
        //asyncHttpClient.cancelRequests(context, true);
    }

    /**
     * Set the cookies that will be sent with any requests made to the drowned
     * in sound website
     *
     * @param cookieStore
     */
    public synchronized static void setCookies(CookieStore cookieStore) {
       // asyncHttpClient.setCookieStore(cookieStore);
    }

    /**
     * Attempts to login to the drowned in sound social website
     *
     * @param username        the username to use in the login request
     * @param password        the password for the given user name
     * @param forwardPage     the page to go to after the login
     * @param responseHandler
     */
    public static void makeLoginRequest(Context context, String username,
                                        String password, String forwardPage,
                                        AsyncHttpResponseHandler responseHandler) {


        RequestBody requestBody = new FormEncodingBuilder().add("user_session[username]", username)
                .add("user_session[password]", password)
                .add("user_session[remember_me]", "1")
                .add("return_to", forwardPage)
                .add("commit", "Go!*").build();
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(requestBody).url(UrlConstants.LOGIN_URL).build();

        asyncHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d(DisBoardsConstants.LOG_TAG_PREFIX, " ioException" + e.getMessage());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.d(DisBoardsConstants.LOG_TAG_PREFIX, " Response = " + response.toString());
                int code = response.code();
                boolean isRedirect = response.isRedirect();
                Log.d(DisBoardsConstants.LOG_TAG_PREFIX, " Code " + code + " isRedirect " + isRedirect);
            }
        });

    }

    public static void postComment(Context context, String title,
                                   String content, String postID, String replyToCommentID,
                                   AsyncHttpResponseHandler responseHandler) {
        //BasicHeader[] headers = getMandatoryDefaultHeaders();

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("comment[commentable_id]", postID));
        pairs.add(new BasicNameValuePair("comment[commentable_type]", "Topic"));
        if (replyToCommentID == null) {
            replyToCommentID = "";
        }
        pairs.add(new BasicNameValuePair("parent_id", replyToCommentID));
        pairs.add(new BasicNameValuePair("comment[title]", title));
        pairs.add(new BasicNameValuePair("comment[content_raw]", content));
        pairs.add(new BasicNameValuePair("commit", "Post reply"));

        HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(pairs, CONTENT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            if (DisBoardsConstants.DEBUG) {
                e.printStackTrace();
            }
        }
//        asyncHttpClient.post(context, UrlConstants.COMMENTS_URL, headers,
//            entity, REQUEST_CONTENT_TYPE, responseHandler);
    }

    public static void makeNewPost(Context context, String title,
                                   String content, Board board,
                                   AsyncHttpResponseHandler responseHandler) {
        BasicHeader[] headers =  null;//getMandatoryDefaultHeaders();

        BasicHeader referer = new BasicHeader("Referer", board.getUrl());

       // headers = Arrays.copyOf(headers, headers.length + 1);
        headers[headers.length - 1] = referer;
        for (Header header : headers) {
            Log.d(TAG, "Header " + header);
        }
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();

        pairs.add(new BasicNameValuePair("section_id", String.valueOf(board
            .getSectionId())));
        pairs.add(new BasicNameValuePair("topic[title]", title));
        pairs.add(new BasicNameValuePair("topic[content_raw]", content));
        pairs.add(new BasicNameValuePair("topic[sticky]", "0"));
        pairs.add(new BasicNameValuePair("commit", "Post it"));

        for (NameValuePair pair : pairs) {
            Log.d(TAG, "Pair " + pair);
        }

        HttpEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(pairs, CONTENT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            if (DisBoardsConstants.DEBUG) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, entity.toString());
//        asyncHttpClient.post(context, UrlConstants.NEW_POST_URL, headers,
//            entity, REQUEST_CONTENT_TYPE, responseHandler);

    }

    /**
     * Get the headers that are required for each request to the drowned in
     * sound website
     *
     * @return
     */
    private static Request.Builder getMandatoryDefaultHeaders(Request.Builder requestBuilder) {
        // TODO tidy this up

        requestBuilder.addHeader("Cache-Control", "max-age=0");
        requestBuilder.addHeader(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
        requestBuilder.addHeader("Accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        requestBuilder.addHeader("Accept-Encoding",
            "gzip,deflate,sdch");
        requestBuilder.addHeader("Accept-Language",
            "en-US,en;q=0.8,en-GB;q=0.6");
        requestBuilder.addHeader("Accept-Charset",
            "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
        return  requestBuilder;
    }

    /**
     * Make a request for a board summary page
     *
     * @param contxt
     * @param boardUrl
     * @param responseHandler
     */
    public static void requestBoardSummary(Context context, String boardUrl,
                                           BoardType boardType, AsyncHttpResponseHandler responseHandler,
                                           int pageNumber) {

        boolean append = pageNumber > 1;
        if (append) {
            boardUrl += "/page/" + pageNumber;
        }
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG, "Going to request = " + boardUrl);
        }
        addRequest(responseHandler.getIdentifier());
        if (useFakeData) {
            makeFakeRequest(new RetrievedBoardPostSummaryListEvent(
                FakeDataFactory.generateRandomBoardPostSummaryList(),
                boardType, false, append));
        } else {
//            asyncHttpClient.get(context, boardUrl,
//                getMandatoryDefaultHeaders(), null, responseHandler);
        }
    }

    /**
     * This makes a request for a specfic board post
     *
     * @param context
     * @param boardPostUrl
     * @param responseHandler
     */
    public static void requestBoardPost(Context context, String boardPostUrl,
                                        AsyncHttpResponseHandler responseHandler) {
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG, "Going to request = " + boardPostUrl);
        }
        addRequest(responseHandler.getIdentifier());
        if (useFakeData) {
            makeFakeRequest(new RetrievedBoardPostEvent(
                FakeDataFactory.generateRandomBoardPost(), false, true));
        } else {
//            asyncHttpClient.get(context, boardPostUrl,
//                getMandatoryDefaultHeaders(), null, responseHandler);
        }

    }

    public static void thisAComment(Context context, String commentId,
                                    String boardPostUrl, AsyncHttpResponseHandler responseHandler) {
        String fullUrl = boardPostUrl + "/" + commentId + "/this";
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG, "Going to request  =" + fullUrl);
        }
        addRequest(responseHandler.getIdentifier());
//        asyncHttpClient.get(context, fullUrl, getMandatoryDefaultHeaders(),
//            null, responseHandler);

    }

    private static void makeFakeRequest(final Object returnEvent) {
        new Thread() {

            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EventBus.getDefault().post(returnEvent);

            }

        }.start();
    }

}
