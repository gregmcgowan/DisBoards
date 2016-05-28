package com.drownedinsound.data;

import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.network.DisBoardsApi;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.ui.base.Event;
import com.drownedinsound.ui.base.EventUtils;
import com.drownedinsound.ui.base.Status;
import com.jakewharton.rxrelay.BehaviorRelay;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by gregmcgowan on 19/05/2016.
 */
public class DisBoardRepoImpl2 implements DisRepo2 {

    private static final long MAX_BOARD_POST_LIST_AGE_MINUTES = 15;

    private static final long MAX_BOARD_POST_AGE_MINUTES = 15;

    private DisBoardsApi disApi;

    private DisBoardsLocalRepo disBoardsLocalRepo;

    private UserSessionRepo userSessionRepo;

    private BehaviorRelay<Event<LoginResponse>> loginResponseRelay =
            BehaviorRelay.create(new Event<LoginResponse>(null, Status.IDLE,null));

    public DisBoardRepoImpl2(DisBoardsApi disApi,
            DisBoardsLocalRepo disBoardsDatabase,
            UserSessionRepo userSessionRepo) {
        this.disApi = disApi;
        this.disBoardsLocalRepo = disBoardsDatabase;
        this.userSessionRepo = userSessionRepo;
    }

    @Override
    public void loginUser(String username, String password) {
        disApi.loginUser(username, password)
                .doOnNext(new Action1<LoginResponse>() {
                    @Override
                    public void call(LoginResponse loginResponse) {
                        userSessionRepo.setAuthenticityToken(loginResponse.getAuthenticationToken());
                    }
                })
                .subscribeOn(Schedulers.io())
                .compose(EventUtils.<LoginResponse>transformToEvent())
                .subscribe(loginResponseRelay);
    }

    @Override
    public Observable<Event<LoginResponse>> getLoginEventObservable() {
        return loginResponseRelay.asObservable().distinctUntilChanged();
    }

    @Override
    public Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, boolean forceUpdate) {
        return null;
    }

    @Override
    public Observable<List<BoardPostList>> getAllBoardPostLists() {
        return null;
    }

    @Override
    public Observable<List<BoardPostSummary>> getBoardPostSummaryList(
            @BoardPostList.BoardPostListType String boardListType, int pageNumber,
            boolean forceUpdate) {
        return null;
    }

    @Override
    public Observable<BoardPostSummary> getBoardPostSummary(
            @BoardPostList.BoardPostListType String boardListType, String boardPostId) {
        return null;
    }

    @Override
    public Observable<BoardPost> postComment(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, String commentId, String title, String content) {
        return null;
    }

    @Override
    public Observable<BoardPost> addNewPost(@BoardPostList.BoardPostListType String boardListType,
            String title, String content) {
        return null;
    }

    @Override
    public Observable<BoardPost> thisAComment(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, String commentId) {
        return null;
    }

    @Override
    public Observable<Void> setBoardPostSummary(BoardPostSummary boardPostSummary) {
        return null;
    }

    @Override
    public boolean isUserLoggedIn() {
        return false;
    }

    @Override
    public boolean userSelectedLurk() {
        return false;
    }

    @Override
    public void setUserSelectedLurk(boolean lurk) {

    }

    @Override
    public void clearUserSession() {

    }

}
