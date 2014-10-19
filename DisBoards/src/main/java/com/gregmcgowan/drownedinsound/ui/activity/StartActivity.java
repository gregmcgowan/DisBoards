package com.gregmcgowan.drownedinsound.ui.activity;

import com.crashlytics.android.Crashlytics;
import com.gregmcgowan.drownedinsound.BuildConfig;
import com.gregmcgowan.drownedinsound.annotations.UseDagger;
import com.gregmcgowan.drownedinsound.annotations.UseEventBus;
import com.gregmcgowan.drownedinsound.core.DisBoardsApp;
import com.gregmcgowan.drownedinsound.data.UserSessionManager;
import com.gregmcgowan.drownedinsound.events.LoginSucceededEvent;
import com.gregmcgowan.drownedinsound.events.LurkEvent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

@UseEventBus @UseDagger
public class StartActivity extends DisBoardsActivity {

    @Inject
    UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(BuildConfig.BUILD_TYPE.equals("beta")
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
                MainCommunityActivity.class);
        startActivity(startMainActivityIntent);
        finish();
    }

    public void onEventMainThread(LoginSucceededEvent event) {
        goToMainActivity();
    }

    public void onEventMainThread(LurkEvent event) {
        goToMainActivity();
    }

}
