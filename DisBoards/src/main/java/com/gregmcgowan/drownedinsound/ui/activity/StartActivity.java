package com.gregmcgowan.drownedinsound.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import com.gregmcgowan.drownedinsound.CookieManager;
import com.gregmcgowan.drownedinsound.DisBoardsApp;
import com.gregmcgowan.drownedinsound.events.LoginSucceededEvent;
import com.gregmcgowan.drownedinsound.events.LurkEvent;


import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class StartActivity extends Activity {

    @Inject
    CookieManager cookieManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisBoardsApp disBoardsApp = DisBoardsApp.getApplication(this);
        disBoardsApp.inject(this);

        EventBus.getDefault().register(this);
        if (cookieManager.userIsLoggedIn()) {
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
