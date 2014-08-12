package com.gregmcgowan.drownedinsound.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import com.gregmcgowan.drownedinsound.data.UserSessionManager;
import com.gregmcgowan.drownedinsound.data.network.CookieManager;
import com.gregmcgowan.drownedinsound.core.DisBoardsApp;
import com.gregmcgowan.drownedinsound.events.LoginSucceededEvent;
import com.gregmcgowan.drownedinsound.events.LurkEvent;


import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class StartActivity extends Activity {

    @Inject
    UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisBoardsApp disBoardsApp = DisBoardsApp.getApplication(this);
        disBoardsApp.inject(this);

        EventBus.getDefault().register(this);
        if (userSessionManager.isUserLoggedIn()){
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    
}
