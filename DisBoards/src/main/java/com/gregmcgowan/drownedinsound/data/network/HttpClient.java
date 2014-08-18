package com.gregmcgowan.drownedinsound.data.network;

import android.content.Context;
import android.util.Log;

import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;

import java.util.HashSet;

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


    private static HashSet<String> requestsInProgress = new HashSet<String>();
    private static final boolean useFakeData = false;

    public static void initialiseRedirectClient(final Context appContext) {
//        asyncHttpClient.setRedirectHandler(new DefaultRedirectHandler(){
//
//            @Override
//            public URI getLocationURI(HttpResponse response, HttpContext context)
//                    throws ProtocolException {
//                //First check we are not redirected to login page if so we need to
//                Header locationHeader = response.getFirstHeader("location");
//                if(locationHeader != null) {
//                    String locationValue = locationHeader.getValue();
//                    if("http://drownedinsound.com/login".equals(locationValue)) {
//                        EventBus.getDefault().post(new UserIsNotLoggedInEvent());
//                        //DisBoardsApp.getApplication(appContext).clearCookies();
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



}
