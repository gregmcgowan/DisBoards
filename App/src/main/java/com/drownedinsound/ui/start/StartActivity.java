package com.drownedinsound.ui.start;

import com.crashlytics.android.Crashlytics;
import com.drownedinsound.BuildConfig;
import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.annotations.UseEventBus;
import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.events.LoginSucceededEvent;
import com.drownedinsound.events.LurkEvent;
import com.drownedinsound.ui.base.BaseActivity;

import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

@UseEventBus
@UseDagger
public class StartActivity extends BaseActivity {

    @Inject
    UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.BUILD_TYPE.equals("beta")
                || BuildConfig.BUILD_TYPE.equals("release")) {
            Crashlytics.start(this);
        }

        if (userSessionManager.isUserLoggedIn()) {
            goToMainActivity();
        } else {
            goToLoginActivity();
        }
        finish();
    }

    private void goToLoginActivity() {
        Intent startLoginActivity = new Intent(this,
                LoginActivity.class);
        startActivity(startLoginActivity);
    }


    private void goToMainActivity() {
        Intent startMainActivityIntent = new Intent(this,
                BoardPostListActivity.class);
        startActivity(startMainActivityIntent);
        finish();
    }

    public void onEventMainThread(LoginSucceededEvent event) {
        goToMainActivity();
    }

    public void onEventMainThread(LurkEvent event) {
        goToMainActivity();
    }

    @Override
    protected int getLayoutResource() {
        return 0;
    }
}
