package com.drownedinsound.ui.start;

import com.drownedinsound.R;
import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.annotations.UseEventBus;
import com.drownedinsound.data.network.CookieManager;
import com.drownedinsound.events.LoginResponseEvent;
import com.drownedinsound.events.LoginSucceededEvent;
import com.drownedinsound.events.LurkEvent;
import com.drownedinsound.ui.base.BaseActivity;
import com.drownedinsound.ui.summarylist.BoardPostSummaryListActivity;
import com.drownedinsound.utils.UiUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;

/**
 * Allows the user to login to their drowned in sound account. If the login
 * attempt is successful the user will be taken to the social main page.
 * Otherwise an appropriate error message will be displayed.
 *
 * @author Greg
 */
@UseDagger
@UseEventBus
public class LoginActivity extends BaseActivity {

    @Inject
    CookieManager cookieManager;

    private Button loginButton;

    private Button lurkButton;

    private EditText usernameField;

    private EditText passwordField;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListeners();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.login_activity;
    }

    private void goToMainActivity() {
        Intent startMainActivityIntent = new Intent(this,
                BoardPostSummaryListActivity.class);
        startActivity(startMainActivityIntent);
        finish();
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
        cookieManager.clearCookies();
        eventBus.post(new LurkEvent());
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
        disApiClient.loginUser(username, password);
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
            eventBus.post(new LoginSucceededEvent());
            Intent startMainActivityIntent = new Intent(this,
                    BoardPostSummaryListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMainActivityIntent);
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
