package com.drownedinsound.ui.start;

import com.drownedinsound.R;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.Ui;
import com.drownedinsound.utils.StringUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;

/**
 * Created by gregmcgowan on 01/11/15.
 */
@Singleton
public class LoginController extends BaseUIController {

    private DisBoardRepo disBoardRepo;

    @Inject
    public LoginController(DisBoardRepo disBoardRepo,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        super(mainThreadScheduler, backgroundThreadScheduler);
        this.disBoardRepo = disBoardRepo;
    }


    @Override
    public void onUiAttached(Ui ui) {
        if (ui instanceof LoginUi) {
            if (disBoardRepo.isUserLoggedIn()) {
                loggedInSuccessful();
            }
        }
    }

    @Override
    public void onUiDetached(Ui ui) {
    }

    public void doLurkAction(LoginUi loginUi) {
        boolean userLurked = disBoardRepo.userSelectedLurk();
        disBoardRepo.clearUserSession();
        disBoardRepo.setUserSelectedLurk(true);
        if (userLurked) {
            getDisplay().hideCurrentScreen();
        } else {
            getDisplay().showMainScreen();
            getDisplay().hideCurrentScreen();
        }

    }

    public void loginButtonPressed(LoginUi loginUi, String username, String password) {
        boolean usernameEntered = !StringUtils.isEmpty(username);
        boolean passwordEntered = !StringUtils.isEmpty(password);
        if (usernameEntered && passwordEntered) {
            attemptLogin(loginUi, username, password);
        } else {
            if (getDisplay() != null) {
                getDisplay().showErrorMessageDialog(
                        R.string.please_enter_both_username_and_password);
            }
            loginUi.handleLoginFailure();
        }
    }

    private void attemptLogin(LoginUi loginUi, String username, String password) {
        final int id = getId(loginUi);
        loginUi.showLoadingProgress(true);

        Observable<LoginResponse> loginResponseObservable = disBoardRepo
                .loginUser(username, password)
                .compose(this.<LoginResponse>defaultTransformer());

        Observer<LoginResponse> loginResponseObserver = new Subscriber<LoginResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LoginUi loginUi = (LoginUi) findUi(id);
                if (loginUi != null) {
                    loginUi.showLoadingProgress(false);
                    loginUi.handleLoginFailure();
                }
                if(getDisplay() != null) {
                    getDisplay().showErrorMessageDialog(R.string.login_failed);
                }
            }

            @Override
            public void onNext(LoginResponse loginResponse) {
                loggedInSuccessful();
            }
        };
        subscribeAndCache(loginUi, "LOGIN", loginResponseObserver, loginResponseObservable);
    }

    private void loggedInSuccessful() {
        if(getDisplay() != null) {
            boolean userLurked = disBoardRepo.userSelectedLurk();
            if (!userLurked) {
                getDisplay().showMainScreen();
            }
            getDisplay().hideCurrentScreen();
        }
    }

}
