package com.gregmcgowan.drownedinsound.network;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import de.greenrobot.event.EventBus;

/**
 * Http client for making requests to the drowned in sound website
 * 
 * @author Greg
 * 
 */
public class HttpClient {

    public final static String CONTENT_ENCODING = "UTF-8";
    private final static String REQUEST_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final static String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "HttpClient";

    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private static HashSet<String> requestsInProgress = new HashSet<String>();
    private static final boolean useFakeData = false;

    public static void setTimeout(int timeout) {
	asyncHttpClient.setTimeout(timeout);
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
	asyncHttpClient.cancelRequests(context, true);
    }

    /**
     * Set the cookies that will be sent with any requests made to the drowned
     * in sound website
     * 
     * @param cookieStore
     */
    public synchronized static void setCookies(CookieStore cookieStore) {
	asyncHttpClient.setCookieStore(cookieStore);
    }

    /**
     * Attempts to login to the drowned in sound social website
     * 
     * @param username
     *            the username to use in the login request
     * @param password
     *            the password for the given user name
     * @param forwardPage
     *            the page to go to after the login
     * @param responseHandler
     */
    public static void makeLoginRequest(Context context, String username,
	    String password, String forwardPage,
	    AsyncHttpResponseHandler responseHandler) {
	BasicHeader[] headers = getMandatoryDefaultHeaders();

	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
	pairs.add(new BasicNameValuePair("user_session[username]", username));
	pairs.add(new BasicNameValuePair("user_session[password]", password));
	pairs.add(new BasicNameValuePair("user_session[remember_me]", "1"));
	pairs.add(new BasicNameValuePair("return_to", forwardPage));
	pairs.add(new BasicNameValuePair("commit", "Go!*"));

	HttpEntity entity = null;
	try {
	    entity = new UrlEncodedFormEntity(pairs, CONTENT_ENCODING);
	} catch (UnsupportedEncodingException e) {
	    if (DisBoardsConstants.DEBUG) {
		e.printStackTrace();
	    }
	}
	asyncHttpClient.post(context, UrlConstants.LOGIN_URL, headers, entity,
		REQUEST_CONTENT_TYPE, responseHandler);

    }

    public static void postComment(Context context, String title,
	    String content, String postID, String replyToCommentID,
	    AsyncHttpResponseHandler responseHandler) {
	BasicHeader[] headers = getMandatoryDefaultHeaders();

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
	asyncHttpClient.post(context, UrlConstants.COMMENTS_URL, headers,
		entity, REQUEST_CONTENT_TYPE, responseHandler);
    }

    /**
     * Get the headers that are required for each request to the drowned in
     * sound website
     * 
     * @return
     */
    private static BasicHeader[] getMandatoryDefaultHeaders() {
	// TODO tidy this up
	BasicHeader cacheControl = new BasicHeader("Cache-Control", "max-age=0");
	BasicHeader userAgent = new BasicHeader(
		"User-Agent",
		"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
	BasicHeader accept = new BasicHeader("Accept",
		"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	BasicHeader acceptEncoding = new BasicHeader("Accept-Encoding",
		"gzip,deflate,sdch");
	BasicHeader acceptLangague = new BasicHeader("Accept-Language",
		"en-US,en;q=0.8,en-GB;q=0.6");
	BasicHeader acceptCharset = new BasicHeader("Accept-Charset",
		"ISO-8859-1,utf-8;q=0.7,*;q=0.3");
	return new BasicHeader[] { cacheControl, userAgent, accept,
		acceptEncoding, acceptLangague, acceptCharset };
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
	if(append) {
	    boardUrl += "/page/" + pageNumber;
	}
	if (DisBoardsConstants.DEBUG) {
	    Log.d(TAG, "Going to request = " + boardUrl);
	}
	addRequest(responseHandler.getIdentifier());
	if (useFakeData) {
	    makeFakeRequest(new RetrievedBoardPostSummaryListEvent(
		    FakeDataFactory.generateRandomBoardPostSummaryList(),
		    boardType, false,append));
	} else {
	    asyncHttpClient.get(context, boardUrl,
		    getMandatoryDefaultHeaders(), null, responseHandler);
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
		    FakeDataFactory.generateRandomBoardPost(), false,true));
	} else {
	    asyncHttpClient.get(context, boardPostUrl,
		    getMandatoryDefaultHeaders(), null, responseHandler);
	}

    }

    public static void thisAComment(Context context, String commentId,
	    String boardPostUrl, AsyncHttpResponseHandler responseHandler) {
	String fullUrl = boardPostUrl + "/" + commentId + "/this";
	if (DisBoardsConstants.DEBUG) {
	    Log.d(TAG, "Going to request  =" + fullUrl);
	}
	addRequest(responseHandler.getIdentifier());
	asyncHttpClient.get(context, fullUrl, getMandatoryDefaultHeaders(),
		null, responseHandler);

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
