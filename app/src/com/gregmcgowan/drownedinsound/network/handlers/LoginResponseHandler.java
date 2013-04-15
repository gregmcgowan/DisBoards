package com.gregmcgowan.drownedinsound.network.handlers;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.parser.HtmlConstants;
import com.gregmcgowan.drownedinsound.events.LoginResponseEvent;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UrlConstants;

import de.greenrobot.event.EventBus;

public class LoginResponseHandler extends DisBoardAsyncNetworkHandler {

    private static final String LOGIN_IDENTIFIER = "LOGIN";

    public LoginResponseHandler(File file) {
	super(file, LOGIN_IDENTIFIER,true);
    }

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "LoginResponseHandler";

    @Override
    public void doSuccessAction(int statusCode, File file) {
	boolean loginSucceeded = false;
	if (file != null && file.exists()) {
	    Document parsedDocument = null;
	    try {
		parsedDocument = Jsoup.parse(file, HttpClient.CONTENT_ENCODING,
			UrlConstants.BASE_URL);
	    } catch (IOException e) {
		if (DisBoardsConstants.DEBUG) {
		    e.printStackTrace();
		}
	    }
	    if (parsedDocument != null) {
		Elements titleElements = parsedDocument
			.getElementsByTag(HtmlConstants.TITLE_TAG);
		Element firstAndOnlyElement = titleElements.get(0);
		String titleText = firstAndOnlyElement.text();
		loginSucceeded = DisBoardsConstants.SOCIAL_BOARD_TITLE.trim()
			.equalsIgnoreCase(titleText);

	    }
	}
	deleteFile();
	EventBus.getDefault().post(new LoginResponseEvent(loginSucceeded));
    }

    @Override
    public void doFailureAction(Throwable throwable, File response) {
	if (DisBoardsConstants.DEBUG) {
	    Log.d(TAG, "Response Body " + response);
	    if (throwable instanceof HttpResponseException) {
		HttpResponseException exception = (HttpResponseException) throwable;
		int statusCode = exception.getStatusCode();
		Log.d(TAG, "Status code " + statusCode);
		Log.d(TAG, "Message " + exception.getMessage());
	    } else {
		Log.d(TAG, "Something went really wrong");
	    }
	}
	deleteFile();
	EventBus.getDefault().post(new LoginResponseEvent(false));
    }

};