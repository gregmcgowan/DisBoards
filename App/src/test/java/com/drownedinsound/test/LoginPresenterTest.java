package com.drownedinsound.test;

import com.drownedinsound.R;
import com.drownedinsound.data.DisBoardRepoImpl2;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.data.parser.streaming.LoginException;
import com.drownedinsound.ui.base.Display;
import com.drownedinsound.ui.base.Event;
import com.drownedinsound.ui.base.Status;
import com.drownedinsound.ui.start.LoginPresenter;
import com.drownedinsound.ui.start.LoginUi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by gregmcgowan on 19/05/2016.
 */
public class LoginPresenterTest {

    @Mock
    Display display;

    @Mock
    LoginUi loginUi;

    @Mock
    DisBoardRepoImpl2 disRepo2;

    private LoginPresenter loginPresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        loginPresenter = new LoginPresenter(disRepo2, Schedulers.immediate(),
                Schedulers.immediate());
        loginPresenter.attachDisplay(display);

    }

    @Test
    public void testLoginEmptyStrings() {
        when(disRepo2.getLoginEventObservable())
                .thenReturn(Observable.just(new Event<LoginResponse>(null,
                        Status.IDLE, null)));
        loginPresenter.attachUi(loginUi);

        loginPresenter.login(loginUi, "", "");

        verify(display).showErrorMessageDialog(R.string.please_enter_both_username_and_password);
        verify(loginUi).handleLoginFailure();
    }

    @Test
    public void testLoginEmptyUsername() {
        when(disRepo2.getLoginEventObservable())
                .thenReturn(Observable.just(new Event<LoginResponse>(null,
                        Status.IDLE,null)));
        loginPresenter.attachUi(loginUi);

        loginPresenter.login(loginUi, "", "password");

        verify(display).showErrorMessageDialog(R.string.please_enter_both_username_and_password);
        verify(loginUi).handleLoginFailure();
    }

    @Test
    public void testLoginEmptyPassword() {
        when(disRepo2.getLoginEventObservable())
                .thenReturn(Observable.just(new Event<LoginResponse>(null,
                        Status.IDLE,null)));
        loginPresenter.attachUi(loginUi);

        loginPresenter.login(loginUi, "username", "");

        verify(display).showErrorMessageDialog(R.string.please_enter_both_username_and_password);
        verify(loginUi).handleLoginFailure();
    }

    @Test
    public void testSuccessfulLoginFromStart() {
        BehaviorSubject<Event<LoginResponse>> loginSubject =
                BehaviorSubject.create();

        when(disRepo2.getLoginEventObservable()).thenReturn(loginSubject);

        loginSubject.onNext(new Event<LoginResponse>(null,
                Status.IDLE,null));

        loginPresenter.attachUi(loginUi);
        loginPresenter.login(loginUi, "username", "password");


        loginSubject.onNext(new Event<LoginResponse>(null,
                Status.LOADING,null));

        loginSubject.onNext(new Event<>(new LoginResponse("auth"),
                Status.IDLE,null));
        when(disRepo2.userSelectedLurk()).thenReturn(false);

        verify(disRepo2).loginUser("username", "password");
        verify(loginUi).showLoadingProgress(true);
        verify(display).showMainScreen();
        verify(display).hideCurrentScreen();
    }

    @Test
    public void testLoginFailed() {
        BehaviorSubject<Event<LoginResponse>> loginSubject =
                BehaviorSubject.create();

        when(disRepo2.getLoginEventObservable()).thenReturn(loginSubject);

        loginSubject.onNext(new Event<LoginResponse>(null,
                Status.IDLE,null));

        loginPresenter.attachUi(loginUi);

        when(disRepo2.userSelectedLurk()).thenReturn(false);

        loginPresenter.login(loginUi, "username", "password");

        loginSubject.onNext(new Event<LoginResponse>(null,
                Status.LOADING,null));

        loginSubject.onNext(new Event<LoginResponse>(null, Status.ERROR,
                new LoginException()));

        verify(disRepo2).loginUser("username", "password");
        verify(loginUi).showLoadingProgress(true);
        verify(loginUi, times(2)).showLoadingProgress(false);
        verify(display).showErrorMessageDialog(R.string.login_failed);
    }

    @Test
    public void testLurkFromStart() {
        when(disRepo2.getLoginEventObservable())
                .thenReturn(Observable.just(new Event<LoginResponse>(null,
                        Status.IDLE,null)));
        loginPresenter.attachUi(loginUi);

        when(disRepo2.userSelectedLurk()).thenReturn(false);

        loginPresenter.lurk();

        verify(disRepo2).clearUserSession();
        verify(disRepo2).setUserSelectedLurk(true);
        verify(display).showMainScreen();
        verify(display).hideCurrentScreen();

    }

    @Test
    public void testLurkAfterLurk() {
        when(disRepo2.getLoginEventObservable())
                .thenReturn(Observable.just(new Event<LoginResponse>(null,
                        Status.IDLE,null)));
        loginPresenter.attachUi(loginUi);

        when(disRepo2.userSelectedLurk()).thenReturn(true);

        loginPresenter.lurk();

        verify(disRepo2).clearUserSession();
        verify(disRepo2).setUserSelectedLurk(true);
        verify(display).hideCurrentScreen();
    }

}
