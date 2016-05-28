package com.drownedinsound.ui.start;

import com.drownedinsound.R;
import com.drownedinsound.core.SingleIn;
import com.drownedinsound.data.DisRepo2;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.qualifiers.ForIoScheduler;
import com.drownedinsound.qualifiers.ForMainThreadScheduler;
import com.drownedinsound.ui.base.BasePresenter;
import com.drownedinsound.ui.base.Event;
import com.drownedinsound.ui.base.EventObserver;
import com.drownedinsound.ui.base.Ui;
import com.drownedinsound.utils.StringUtils;

import javax.inject.Inject;

import rx.Scheduler;
import rx.Subscription;

/**
 * Created by gregmcgowan on 19/05/2016.
 */
@SingleIn(LoginComponent.class)
public class LoginPresenter extends BasePresenter {

    private DisRepo2 disBoardRepo;

    private Subscription subscription;

    private LoginUi loginUi;

    @Inject
    public LoginPresenter(DisRepo2 disRepo2,
            @ForMainThreadScheduler Scheduler mainThreadScheduler,
            @ForIoScheduler Scheduler backgroundThreadScheduler) {
        super(mainThreadScheduler, backgroundThreadScheduler);
        this.disBoardRepo = disRepo2;
    }

    @Override
    public void onUiAttached(Ui ui) {
        if (ui instanceof LoginUi) {
            loginUi = (LoginUi) ui;

            subscription =
                    disBoardRepo.getLoginEventObservable()
                            .compose(this.<Event<LoginResponse>>defaultTransformer())
                            .subscribe(new EventObserver<LoginResponse>() {
                                @Override
                                public void loading(LoginResponse data) {
                                    if (loginUi != null) {
                                        loginUi.showLoadingProgress(true);
                                    }
                                }

                                @Override
                                public void idle(LoginResponse loginResponse) {
                                    if (loginResponse != null) {
                                        boolean userLurked = disBoardRepo.userSelectedLurk();
                                        if (!userLurked) {
                                            getDisplay().showMainScreen();
                                        }
                                        getDisplay().hideCurrentScreen();
                                    } else {
                                        loginUi.showLoadingProgress(false);
                                    }
                                }

                                @Override
                                public void error(Throwable throwable) {
                                    if (loginUi != null) {
                                        loginUi.showLoadingProgress(false);
                                        loginUi.handleLoginFailure();
                                    }
                                    if (getDisplay() != null) {
                                        getDisplay().showErrorMessageDialog(R.string.login_failed);
                                    }
                                }
                            });

        }
    }

    public void login(LoginUi loginUi, String username, String password) {
        boolean usernameEntered = !StringUtils.isEmpty(username);
        boolean passwordEntered = !StringUtils.isEmpty(password);
        if (usernameEntered && passwordEntered) {
            disBoardRepo.loginUser(username, password);
        } else {
            if (getDisplay() != null) {
                getDisplay().showErrorMessageDialog(
                        R.string.please_enter_both_username_and_password);
            }
            loginUi.handleLoginFailure();
        }
    }

    public void lurk() {
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

    @Override
    public void onUiDetached(Ui ui) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
