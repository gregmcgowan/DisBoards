package com.gregmcgowan.drownedinsound.ui.activity;

import java.util.List;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gregmcgowan.drownedinsound.DisBoardsApp;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.events.LoginResponseEvent;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UrlConstants;
import com.gregmcgowan.drownedinsound.network.handlers.LoginResponseHandler;
import com.gregmcgowan.drownedinsound.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.network.service.DisWebServiceConstants;
import com.gregmcgowan.drownedinsound.utils.FileUtils;
import com.gregmcgowan.drownedinsound.utils.UiUtils;
import com.gregmcgowan.drownedinsound.R;

import de.greenrobot.event.EventBus;

/**
 * Allows the user to login to their drowned in sound account. If the login
 * attempt is successful the user will be taken to the social main page.
 * Otherwise an appropriate error message will be displayed.
 * 
 * @author Greg
 * 
 */
public class LoginActivity extends Activity {

    private Button loginButton;
    private EditText usernameField;
    private EditText passwordField;
    private ProgressBar progressBar;
    private List<Cookie> cookies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.login_activity);
	EventBus.getDefault().register(this);
	cookies = DisBoardsApp.getApplication(this).getCookies();

	if (userIsAlreadyLoggedIn()) {
	   // setProgressVisibility(true);
	    Intent startMainActivityIntent = new Intent(this,
		    MainCommunityActivity.class);
	    startActivity(startMainActivityIntent);
	    finish();
	} else {
	    setListeners();
	}
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
	super.onResume();

    }

    private boolean userIsAlreadyLoggedIn() {
	String loggedInCookieValue = getCookieValue(DisBoardsConstants.LOGGED_IN_FIELD_NAME);
	String user_credentialsValue = getCookieValue(DisBoardsConstants.USER_CREDENTIALS_FIELD_NAME);
	String dis_session = getCookieValue(DisBoardsConstants.DIS_SESSION_FIELD_NAME);
	String hashed = getCookieValue(DisBoardsConstants.HASHED_FIELD_NAME);
	return "1".equals(loggedInCookieValue)
		&& !TextUtils.isEmpty(user_credentialsValue)
		&& !TextUtils.isEmpty(dis_session)
		&& !TextUtils.isEmpty(hashed);
    }

    private String getCookieValue(String cookieToFind) {
	String value = null;
	if (!TextUtils.isEmpty(cookieToFind)) {
	    for (Cookie cookie : cookies) {
		if (cookieToFind.equalsIgnoreCase(cookie.getName())) {
		    value = cookie.getValue();
		    break;
		}
	    }
	}
	return value;
    }

    private void setListeners() {
	loginButton = (Button) findViewById(R.id.login_button);
	if (loginButton != null) {
	    loginButton.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		    doLoginAction();
		}
	    });
	}
	usernameField = (EditText) findViewById(R.id.login_activity_username_field);
	passwordField = (EditText) findViewById(R.id.login_password_field);
	progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
    }

    protected void doLoginAction() {
	String username = usernameField.getText().toString();
	String password = passwordField.getText().toString();

	boolean usernameEntered = !TextUtils.isEmpty(username);
	boolean passwordEntered = !TextUtils.isEmpty(password);
	if (usernameEntered && passwordEntered) {
	    attemptLogin(username, password);
	} else {
	    Toast.makeText(this, "Please enter a username and password",
		    Toast.LENGTH_LONG).show();
	    if (!usernameEntered) {
		usernameField.requestFocus();
	    } else {
		passwordField.requestFocus();
	    }
	}

    }

    private void attemptLogin(String username, String password) {
	UiUtils.hideSoftKeyboard(this, loginButton.getApplicationWindowToken());	
	setProgressVisibility(true); 
	Intent disWebServiceIntent = new Intent(this,
		DisWebService.class);
	disWebServiceIntent.putExtra(
		DisWebServiceConstants.SERVICE_REQUESTED_ID,
		DisWebServiceConstants.GET_POSTS_SUMMARY_LIST_ID);
	disWebServiceIntent.putExtra(DisBoardsConstants.USERNAME, username);
	disWebServiceIntent.putExtra(DisBoardsConstants.PASSWORD, password);
	startService(disWebServiceIntent);
    }

    private void setProgressVisibility(boolean visible) {
	int progressBarVisibility = visible ? View.VISIBLE : View.INVISIBLE;
	int otherFieldsVisibility = visible ? View.INVISIBLE : View.VISIBLE;
	progressBar.setVisibility(progressBarVisibility);
	usernameField.setVisibility(otherFieldsVisibility);
	passwordField.setVisibility(otherFieldsVisibility);
	loginButton.setVisibility(otherFieldsVisibility);
    }

    public void onEventMainThread(LoginResponseEvent event) {
	boolean loginSucceeded = event.isSuccess();
	if (loginSucceeded) {
	    handleLoginSucceeded();
	} else {
	    setProgressVisibility(false);
	    handleLoginFailed();
	}
    }

    private void handleLoginSucceeded() {
	Intent startMainActivityIntent = new Intent(this,
		MainCommunityActivity.class);
	startActivity(startMainActivityIntent);
	finish();
    }

    private void handleLoginFailed() {
	Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
	usernameField.setText("");
	passwordField.setText("");
	usernameField.requestFocus();
    }

}
