package com.drownedinsound.ui.start;

import com.drownedinsound.R;
import com.drownedinsound.ui.base.BaseControllerActivity;
import com.drownedinsound.ui.postList.BoardPostListParentActivity;
import com.drownedinsound.utils.EspressoIdlingResource;
import com.drownedinsound.utils.UiUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Allows the user to login to their drowned in sound account. If the login
 * attempt is successful the user will be taken to the social main page.
 * Otherwise an appropriate error message will be displayed.
 *
 * @author Greg
 */

public class LoginActivity extends BaseControllerActivity<LoginController> implements LoginUi {

    @Inject
    LoginController loginController;

    @InjectView(R.id.login_button)
    Button loginButton;

    @InjectView(R.id.lurk_button)
    Button lurkButton;

    @InjectView(R.id.login_activity_username_field)
    EditText usernameField;

    @InjectView(R.id.login_password_field)
    EditText passwordField;

    @InjectView(R.id.login_progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.login_activity;
    }

    @Override
    protected LoginController getController() {
        return loginController;
    }

    @Override
    public void showLoadingProgress(boolean visible) {
        int progressBarVisibility = visible ? View.VISIBLE : View.INVISIBLE;
        int otherFieldsVisibility = visible ? View.INVISIBLE : View.VISIBLE;
        progressBar.setVisibility(progressBarVisibility);
        usernameField.setVisibility(otherFieldsVisibility);
        passwordField.setVisibility(otherFieldsVisibility);
        loginButton.setVisibility(otherFieldsVisibility);
        lurkButton.setVisibility(otherFieldsVisibility);
    }

    @Override
    public void handleLoginSuccess() {
        Intent startMainActivityIntent = new Intent(this,
                BoardPostListParentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMainActivityIntent);
        finish();
    }

    @Override
    public void handleLoginFailure() {
        Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
        passwordField.setText("");
        passwordField.requestFocus();
    }

    @OnClick(R.id.lurk_button)
    protected void doLurkAction() {
        loginController.doLurkAction(this);
    }

    @OnClick(R.id.login_button)
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
        loginController.doLoginAction(this, username, password);
    }

}
