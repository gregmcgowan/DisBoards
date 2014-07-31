package com.gregmcgowan.drownedinsound.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.gregmcgowan.drownedinsound.DisBoardsApp;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.events.LoginResponseEvent;
import com.gregmcgowan.drownedinsound.events.LoginSucceededEvent;
import com.gregmcgowan.drownedinsound.events.LurkEvent;
import com.gregmcgowan.drownedinsound.data.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.data.network.service.DisWebServiceConstants;
import com.gregmcgowan.drownedinsound.utils.UiUtils;

import de.greenrobot.event.EventBus;

/**
 * Allows the user to login to their drowned in sound account. If the login
 * attempt is successful the user will be taken to the social main page.
 * Otherwise an appropriate error message will be displayed.
 *
 * @author Greg
 */
public class LoginActivity extends SherlockActivity {

    private Button loginButton;
    private Button lurkButton;
    private EditText usernameField;
    private EditText passwordField;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        EventBus.getDefault().register(this);
        setListeners();
    }

    private void goToMainActivity() {
        Intent startMainActivityIntent = new Intent(this,
            MainCommunityActivity.class);
        startActivity(startMainActivityIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setListeners() {
        lurkButton = (Button) findViewById(R.id.lurk_button);
        loginButton = (Button) findViewById(R.id.login_button);
        if (loginButton != null) {
            loginButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    doLoginAction();
                }
            });
        }
        lurkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doLurkAction();
            }
        });
        usernameField = (EditText) findViewById(R.id.login_activity_username_field);
        passwordField = (EditText) findViewById(R.id.login_password_field);
        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
    }

    protected void doLurkAction() {
        DisBoardsApp.getApplication(this).clearCookies();
        EventBus.getDefault().post(new LurkEvent());
        goToMainActivity();
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
        Intent disWebServiceIntent = new Intent(this, DisWebService.class);
        disWebServiceIntent.putExtra(
            DisWebServiceConstants.SERVICE_REQUESTED_ID,
            DisWebServiceConstants.LOGIN_SERVICE_ID);
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
        lurkButton.setVisibility(otherFieldsVisibility);
    }

    public void onEventMainThread(LoginResponseEvent event) {
        boolean loginSucceeded = event.isSuccess();
        if (loginSucceeded) {
            EventBus.getDefault().post(new LoginSucceededEvent());
            finish();
        } else {
            setProgressVisibility(false);
            handleLoginFailed();
        }
    }



    private void handleLoginFailed() {
        Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
    }

}
