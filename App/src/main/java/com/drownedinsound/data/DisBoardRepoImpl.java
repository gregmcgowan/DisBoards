package com.drownedinsound.data;

import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.network.DisBoardsApi;
import com.drownedinsound.data.network.LoginResponse;

import android.text.format.DateUtils;


import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by gregmcgowan on 05/12/15.
 */
public class DisBoardRepoImpl implements DisBoardRepo {

    private static final long MAX_BOARD_POST_LIST_AGE_MINUTES = 5;

    private DisBoardsApi disApi;
    private DisBoardsLocalRepo disBoardsLocalRepo;
    private UserSessionRepo userSessionRepo;

    public DisBoardRepoImpl(DisBoardsApi disApi,
            DisBoardsLocalRepo disBoardsDatabase,
            UserSessionRepo userSessionRepo) {
        this.disApi = disApi;
        this.disBoardsLocalRepo = disBoardsDatabase;
        this.userSessionRepo = userSessionRepo;
    }

    @Override
    public Observable<LoginResponse> loginUser(String username, String password) {
        return disApi.loginUser(username, password)
                .doOnNext(new Action1<LoginResponse>() {
                    @Override
                    public void call(LoginResponse loginResponse) {
                        userSessionRepo
                                .setAuthenticityToken(loginResponse.getAuthenticationToken());
                    }
                });
    }

    @Override
    public Observable<List<BoardPost>> getBoardPostList(final @BoardPostList.BoardPostListType String boardListType,
            final int pageNumber, final boolean forceUpdate) {

        return disBoardsLocalRepo.getBoardPostList(boardListType).flatMap(
                new Func1<BoardPostList, Observable<List<BoardPost>>>() {
                    @Override
                    public Observable<List<BoardPost>> call(final BoardPostList boardPostList) {
                        long lastFetchedTime = boardPostList.getLastFetchedMs();
                        long fiveMinutesAgo = System.currentTimeMillis()
                                - (DateUtils.MINUTE_IN_MILLIS
                                * MAX_BOARD_POST_LIST_AGE_MINUTES);

                        boolean recentlyFetched = lastFetchedTime > fiveMinutesAgo;
                        List<BoardPost> posts = boardPostList.getBoardPostSummaries();
                        Timber.d("recentlyFetched "+recentlyFetched + " posts "+posts.size() + " forceUpdate "+forceUpdate);
                        if (recentlyFetched && posts.size() > 0 && !forceUpdate) {
                            Timber.d("Return cached");
                            return Observable.just(posts);
                        } else {
                            return disApi
                                    .getBoardPostSummaryList(boardListType, boardPostList.getUrl(),
                                            pageNumber)
                                    .flatMap(new Func1<List<BoardPost>, Observable<List<BoardPost>>>() {
                                        @Override
                                        public Observable<List<BoardPost>> call(List<BoardPost> boardPosts) {
                                            boardPostList.setLastFetchedMs(System.currentTimeMillis());
                                            disBoardsLocalRepo.setBoardPostList(boardPostList);
                                            disBoardsLocalRepo.setBoardPosts(boardPosts);

                                            return Observable.just(boardPosts);
                                        }
                                    }).onErrorResumeNext(Observable.just(posts));
                        }
                    }
                });
    }

    @Override
    public Observable<List<BoardPostList>> getAllBoardPostLists() {
        return disBoardsLocalRepo.getAllBoardPostLists();
    }

    @Override
    public Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId) {
        return null;
    }


    @Override
    public Observable<Void> thisAComment(String boardPostUrl, String boardPostId, String commentId,
            @BoardPostList.BoardPostListType String boardListType) {
        return null;
    }

    @Override
    public boolean isUserLoggedIn() {
        return userSessionRepo.isUserLoggedIn();
    }

    @Override
    public void clearUserSession() {
        userSessionRepo.clearSession();
    }



}
