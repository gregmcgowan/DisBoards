package com.drownedinsound.test;

import com.drownedinsound.data.DisBoardRepoImpl;
import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.network.LoginResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by gregmcgowan on 09/12/15.
 */
public class DisBoardsRepoTest {

    @Mock
    DisApiClient disApiClient;

    @Mock
    DisBoardsLocalRepo disBoardsLocalRepo;

    @Mock
    UserSessionRepo userSessionRepo;

    DisBoardRepoImpl disBoardRepo;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        disBoardRepo = new DisBoardRepoImpl(disApiClient,disBoardsLocalRepo,
                userSessionRepo);
    }

    @Test
    public void testLogin() {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAuthenticationToken("token");

        when(disApiClient.loginUser("username", "password"))
                .thenReturn(Observable.just(loginResponse));

        disBoardRepo.loginUser("username", "password")
                .subscribeOn(Schedulers.immediate())
                .subscribe(new Action1<LoginResponse>() {
            @Override
            public void call(LoginResponse loginResponse) {

            }
        });

        verify(disApiClient).loginUser("username", "password");
        verify(userSessionRepo).setAuthenticityToken("token");
    }

}
