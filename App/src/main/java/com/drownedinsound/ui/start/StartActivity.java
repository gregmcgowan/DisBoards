package com.drownedinsound.ui.start;


import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.ui.base.BaseActivity;
import com.drownedinsound.ui.postList.BoardPostListParentActivity;

import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;


public class StartActivity extends BaseActivity {

    @Inject
    UserSessionRepo userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (userSessionManager.isUserLoggedIn()
                || userSessionManager.userSelectedLurk()) {
            goToMainActivity();
        } else {
            goToLoginActivity();
        }
        finish();
    }

    @Override
    protected void onSessionComponentCreated(SessionComponent sessionComponent) {
        sessionComponent.inject(this);
    }

    private void goToLoginActivity() {
        Intent startLoginActivity = new Intent(this,
                LoginActivity.class);
        startActivity(startLoginActivity);
    }


    private void goToMainActivity() {
        Intent startMainActivityIntent = new Intent(this,
                BoardPostListParentActivity.class);
        startActivity(startMainActivityIntent);
    }

    @Override
    protected int getLayoutResource() {
        return 0;
    }
}
