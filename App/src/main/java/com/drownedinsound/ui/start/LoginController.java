package com.drownedinsound.ui.start;

import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.events.LoginResponseEvent;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.Ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.greenrobot.event.EventBus;

/**
 * Created by gregmcgowan on 01/11/15.
 */
@Singleton
public class LoginController extends BaseUIController {


    private DisApiClient disApiClient;

    private EventBus eventBus;

    private UserSessionManager userSessionManager;

    @Inject
    public LoginController(DisApiClient disApiClient, EventBus eventBus,
            UserSessionManager userSessionManager) {
        this.disApiClient = disApiClient;
        this.eventBus = eventBus;
        this.userSessionManager = userSessionManager;
    }


    @Override
    public void onUiAttached(Ui ui) {
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }

        if (ui instanceof LoginUi) {
            if (userSessionManager.isUserLoggedIn()) {
                ((LoginUi) ui).handleLoginSuccess();
            }
        }
    }

    @Override
    public void onUiDetached(Ui ui) {
        if (eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
    }

    public void doLurkAction(LoginUi loginUi) {
        userSessionManager.clearSession();
        loginUi.handleLoginSuccess();
    }

    public void doLoginAction(LoginUi loginUi, String username, String password) {
        int id = getId(loginUi);
        loginUi.showLoadingProgress(true);
        disApiClient.loginUser(username, password, id);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(LoginResponseEvent event) {
        int callingID = event.getLoginUiId();

        LoginUi loginUi = (LoginUi) findUi(callingID);
        if (loginUi != null) {
            boolean loginSucceeded = event.isSuccess();
            if (loginSucceeded) {
                loginUi.handleLoginSuccess();
            } else {
                loginUi.showLoadingProgress(false);
                loginUi.handleLoginFailure();
            }
        }
    }
}
