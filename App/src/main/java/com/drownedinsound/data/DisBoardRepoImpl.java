package com.drownedinsound.data;

import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.model.BoardPostListInfo;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardListType;
import com.drownedinsound.data.network.DisBoardsApi;
import com.drownedinsound.data.network.LoginResponse;

import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

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
        return disApi.loginUser(username,password)
                .doOnNext(new Action1<LoginResponse>() {
            @Override
            public void call(LoginResponse loginResponse) {
                userSessionRepo.setAuthenticityToken(loginResponse.getAuthenticationToken());
            }
        });
    }

    @Override
    public Observable<List<BoardPost>> getBoardPostSummaryList(final BoardListType boardListType,
            final int pageNumber, final boolean forceUpdate) {

        final Observable<List<BoardPost>> getCachedBoardPost = Observable.zip(
                disBoardsLocalRepo.getBoard(boardListType)
                , disBoardsLocalRepo.getBoardPosts(boardListType),
                new Func2<BoardPostListInfo, List<BoardPost>, List<BoardPost>>() {
                    @Override
                    public List<BoardPost> call(BoardPostListInfo board, List<BoardPost> boardPosts) {
                        long lastFetchedTime = board.getLastFetchedTime();
                        long fiveMinutesAgo = System.currentTimeMillis()
                                - (DateUtils.MINUTE_IN_MILLIS * MAX_BOARD_POST_LIST_AGE_MINUTES);

                        boolean recentlyFetched = lastFetchedTime > fiveMinutesAgo;
                        return recentlyFetched && !forceUpdate ? boardPosts : null;
                    }
                });
        return disBoardsLocalRepo.getBoard(boardListType).flatMap(
                new Func1<BoardPostListInfo, Observable<List<BoardPost>>>() {
                    @Override
                    public Observable<List<BoardPost>> call(final BoardPostListInfo boardPostListInfo) {
                        return Observable.concat(getCachedBoardPost,
                                disApi.getBoardPostSummaryList(boardListType,
                                        boardPostListInfo.getUrl(),pageNumber))
                                .takeFirst(
                                        new Func1<List<BoardPost>, Boolean>() {
                                            @Override
                                            public Boolean call(List<BoardPost> boardPosts) {
                                                return boardPosts != null;
                                            }
                                        })
                                .onErrorResumeNext(disBoardsLocalRepo.getBoardPosts(boardListType))
                                .map(new Func1<List<BoardPost>, List<BoardPost>>() {
                                    @Override
                                    public List<BoardPost> call(List<BoardPost> boardPosts) {
                                        if(boardPosts == null) {
                                            boardPosts = new ArrayList<>();
                                        }
                                        return boardPosts;
                                    }
                                });
                    }
                });
    }

    @Override
    public Observable<List<BoardPostListInfo>> getBoardPostListInfo() {
        return disBoardsLocalRepo.getAllBoardPostInfos();
    }

    @Override
    public Observable<BoardPost> getBoardPost(String boardPostId,
            BoardListType boardListType) {
        return null;
    }

    @Override
    public Observable<Void> thisAComment(String boardPostUrl, String boardPostId, String commentId,
            BoardListType boardListType) {
        return null;
    }

    public Observable<Void> addNewPost(BoardPostListInfo boardPostListInfo, String title, String content) {
        return null;
    }

    public Observable<Void> postComment(String boardPostId, String commentId, String title, String content,
            BoardListType boardListType) {
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
