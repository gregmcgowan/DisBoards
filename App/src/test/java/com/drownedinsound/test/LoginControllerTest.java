package com.drownedinsound.test;

/**
 * Created by gregmcgowan on 08/12/15.
 */
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.ui.start.LoginController;
import com.drownedinsound.ui.start.LoginUi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginControllerTest {


    @Mock
    LoginUi loginUi;

    @Mock
    DisBoardRepo disBoardRepo;

    private LoginController loginController;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        loginController = new LoginController(disBoardRepo, Schedulers.immediate(), Schedulers.immediate());
    }

    @Test
    public void testSuccesfulLogin() {
        loginController.attachUi(loginUi);

        when(disBoardRepo.loginUser(anyString(), anyString()))
                .thenReturn(Observable.just(new LoginResponse()));

        loginController.doLoginAction(loginUi, "username", "password");

        verify(loginUi).showLoadingProgress(true);
        verify(loginUi).handleLoginSuccess();
    }


    @Test
    public void testLoginFailed() {
        loginController.attachUi(loginUi);

        when(disBoardRepo.loginUser(anyString(), anyString()))
                .thenReturn(Observable.create(new Observable.OnSubscribe<LoginResponse>() {
                    @Override
                    public void call(Subscriber<? super LoginResponse> subscriber) {
                        subscriber.onError(new Exception());
                    }
                }));

        loginController.doLoginAction(loginUi, "username", "password");

        verify(loginUi).showLoadingProgress(true);
        verify(loginUi).showLoadingProgress(false);
        verify(loginUi).handleLoginFailure();
    }

    @Test
    public void testLurk() {
        loginController.attachUi(loginUi);
        loginController.doLurkAction(loginUi);

        verify(disBoardRepo).clearUserSession();
        verify(loginUi).handleLoginSuccess();

    }

}
