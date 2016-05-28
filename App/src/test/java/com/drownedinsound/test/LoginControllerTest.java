package com.drownedinsound.test;

/**
 * Created by gregmcgowan on 08/12/15.
 */
import com.drownedinsound.R;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.ui.base.Display;
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
    Display display;

    @Mock
    LoginUi loginUi;

    @Mock
    DisBoardRepo disBoardRepo;

    private LoginController loginController;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        loginController = new LoginController(disBoardRepo, Schedulers.immediate(), Schedulers.immediate());
        loginController.attachDisplay(display);
        loginController.attachUi(loginUi);
    }

    @Test
    public void testLoginEmptyStrings(){
        loginController.loginButtonPressed(loginUi, "", "");

        verify(display).showErrorMessageDialog(R.string.please_enter_both_username_and_password);
        verify(loginUi).handleLoginFailure();
    }

    @Test
    public void testLoginEmptyUsername(){
        loginController.loginButtonPressed(loginUi, "", "password");

        verify(display).showErrorMessageDialog(R.string.please_enter_both_username_and_password);
        verify(loginUi).handleLoginFailure();
    }

    @Test
    public void testLoginEmptyPassword(){
        loginController.loginButtonPressed(loginUi, "username", "");

        verify(display).showErrorMessageDialog(R.string.please_enter_both_username_and_password);
        verify(loginUi).handleLoginFailure();
    }

    @Test
    public void testSuccessfulLoginFromStart() {
        when(disBoardRepo.loginUser(anyString(), anyString()))
                .thenReturn(Observable.just(new LoginResponse()));

        when(disBoardRepo.userSelectedLurk()).thenReturn(false);

        loginController.loginButtonPressed(loginUi, "username", "password");

        verify(loginUi).showLoadingProgress(true);
        verify(display).showMainScreen();
        verify(display).hideCurrentScreen();
    }

    @Test
    public void testSuccesfulLoginAfterLurk() {
        when(disBoardRepo.loginUser(anyString(), anyString()))
                .thenReturn(Observable.just(new LoginResponse()));

        when(disBoardRepo.userSelectedLurk()).thenReturn(true);

        loginController.loginButtonPressed(loginUi, "username", "password");

        verify(loginUi).showLoadingProgress(true);
        verify(display).hideCurrentScreen();
    }


    @Test
    public void testLoginFailed() {
        when(disBoardRepo.loginUser(anyString(), anyString()))
                .thenReturn(Observable.create(new Observable.OnSubscribe<LoginResponse>() {
                    @Override
                    public void call(Subscriber<? super LoginResponse> subscriber) {
                        subscriber.onError(new Exception());
                    }
                }));

        loginController.loginButtonPressed(loginUi, "username", "password");

        verify(loginUi).showLoadingProgress(true);
        verify(loginUi).showLoadingProgress(false);
        verify(display).showErrorMessageDialog(R.string.login_failed);
    }

    @Test
    public void testLurkFromStart() {
        when(disBoardRepo.userSelectedLurk()).thenReturn(false);

        loginController.doLurkAction(loginUi);

        verify(disBoardRepo).clearUserSession();
        verify(disBoardRepo).setUserSelectedLurk(true);
        verify(display).showMainScreen();
        verify(display).hideCurrentScreen();

    }

    @Test
    public void testLurkAfterLurk() {
        when(disBoardRepo.userSelectedLurk()).thenReturn(true);

        loginController.doLurkAction(loginUi);

        verify(disBoardRepo).clearUserSession();
        verify(disBoardRepo).setUserSelectedLurk(true);
        verify(display).hideCurrentScreen();
    }


}
