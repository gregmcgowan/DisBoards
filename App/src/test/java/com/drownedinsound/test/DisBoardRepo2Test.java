package com.drownedinsound.test;

import com.drownedinsound.data.DisBoardRepoImpl2;
import com.drownedinsound.data.DisRepo2;
import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.ui.base.Event;
import com.drownedinsound.ui.base.Status;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by gregmcgowan on 19/05/2016.
 */
public class DisBoardRepo2Test {

    @Mock
    DisApiClient disApiClient;

    @Mock
    DisBoardsLocalRepo disBoardsLocalRepo;

    @Mock
    UserSessionRepo userSessionRepo;

    private DisRepo2 disRepo2;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        disRepo2 = new DisBoardRepoImpl2(disApiClient,disBoardsLocalRepo,userSessionRepo);
    }

    @Test
    public void testLogin() {
        when(disApiClient.loginUser(anyString(), anyString()))
                .thenReturn(Observable.just(new LoginResponse("auth token")));

        disRepo2.loginUser(anyString(), anyString());

        verify(disApiClient, times(1)).loginUser(anyString(), anyString());
        verify(userSessionRepo,times(1)).setAuthenticityToken(anyString());
    }


    @Test
    public void testLoginObservable() throws Exception {
        when(disApiClient.loginUser(anyString(), anyString()))
                .thenReturn(Observable.just(new LoginResponse("auth token")));

        Observable<Event<LoginResponse>> loginEventObservable = disRepo2.getLoginEventObservable();

        final CountDownLatch latch = new CountDownLatch(3);

        final Status[] expectedStatus = new Status[]{Status.IDLE, Status.LOADING, Status.IDLE};
        final boolean[] expectedData = new boolean[]{false, false, true};

        loginEventObservable.subscribe(new Action1<Event<LoginResponse>>() {
            @Override
            public void call(Event<LoginResponse> loginResponseEvent) {
                int count = ((int) latch.getCount() - 3) * -1;
                System.out.println(" status "+ loginResponseEvent.getStatus() + " data "+ (loginResponseEvent.getData()
                != null));
                Assert.assertEquals(expectedStatus[count],
                        loginResponseEvent.getStatus());
                Assert.assertEquals(expectedData[count],
                        loginResponseEvent.getData() != null);
                latch.countDown();
            }
        });
        disRepo2.loginUser(anyString(), anyString());

        verify(disApiClient, times(1)).loginUser(anyString(), anyString());
        verify(userSessionRepo, times(1)).setAuthenticityToken(anyString());

        latch.await();
    }

    @Test
    public void testLoginAndObservableResubscribe() throws Exception {
        when(disApiClient.loginUser(anyString(), anyString()))
                .thenReturn(Observable.just(new LoginResponse("authToken")).delay(1, TimeUnit.SECONDS));

        Observable<Event<LoginResponse>> loginEventObservable = disRepo2.getLoginEventObservable();

        Subscription subscription = loginEventObservable.subscribe(new Action1<Event<LoginResponse>>() {
            @Override
            public void call(Event<LoginResponse> loginResponseEvent) {
            }
        });
        disRepo2.loginUser(anyString(), anyString());

        subscription.unsubscribe();

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        subscription = loginEventObservable.subscribe(new Action1<Event<LoginResponse>>() {
            @Override
            public void call(Event<LoginResponse> loginResponseEvent) {
                System.out.println("Response "+loginResponseEvent.getStatus() +
                 " data "+ (loginResponseEvent.getData() != null));
            }
        });

        subscription.unsubscribe();

        verify(disApiClient, times(1)).loginUser(anyString(), anyString());
        verify(userSessionRepo, times(1)).setAuthenticityToken("authToken");
    }
}
