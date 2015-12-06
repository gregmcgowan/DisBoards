package com.drownedinsound.ui.start;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.Ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * Created by gregmcgowan on 01/11/15.
 */
@Singleton
public class LoginController extends BaseUIController {

    private DisBoardRepo disBoardRepo;

    @Inject
    public LoginController(DisBoardRepo disBoardRepo) {
        this.disBoardRepo = disBoardRepo;
    }


    @Override
    public void onUiAttached(Ui ui) {
        if (ui instanceof LoginUi) {
            if (disBoardRepo.isUserLoggedIn()) {
                ((LoginUi) ui).handleLoginSuccess();
            }
        }
    }

    @Override
    public void onUiDetached(Ui ui) {
    }

    public void doLurkAction(LoginUi loginUi) {
        disBoardRepo.clearUserSession();
        loginUi.handleLoginSuccess();
    }

    public void doLoginAction(LoginUi loginUi, String username, String password) {
        final int id = getId(loginUi);
        loginUi.showLoadingProgress(true);
        Observable<LoginResponse> loginResponseObservable = disBoardRepo.loginUser(username,password);
        Observer<LoginResponse> loginResponseObserver = new Subscriber<LoginResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LoginUi loginUi = (LoginUi) findUi(id);
                if(loginUi != null) {
                    loginUi.showLoadingProgress(false);
                    loginUi.handleLoginFailure();
                }
            }

            @Override
            public void onNext(LoginResponse loginResponse) {
                LoginUi loginUi = (LoginUi) findUi(id);
                if(loginUi != null) {
                    loginUi.handleLoginSuccess();
                }
            }
        };
    }

}
