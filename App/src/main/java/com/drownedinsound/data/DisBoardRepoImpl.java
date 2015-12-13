package com.drownedinsound.data;

import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
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
    public Observable<List<BoardPost>> getBoardPostSummaryList(BoardType boardType,
            Object tag, int pageNumber, final boolean forceUpdate) {

        Observable<List<BoardPost>> getCachedBoardPost = Observable.zip(
                disBoardsLocalRepo.getBoard(boardType)
                , disBoardsLocalRepo.getBoardPosts(boardType),
                new Func2<Board, List<BoardPost>, List<BoardPost>>() {
                    @Override
                    public List<BoardPost> call(Board board, List<BoardPost> boardPosts) {
                        long lastFetchedTime = board.getLastFetchedTime();
                        long fiveMinutesAgo = System.currentTimeMillis()
                                - (DateUtils.MINUTE_IN_MILLIS * MAX_BOARD_POST_LIST_AGE_MINUTES);

                        boolean recentlyFetched = lastFetchedTime > fiveMinutesAgo;
                        return recentlyFetched && !forceUpdate ? boardPosts : null;
                    }
                });

        return Observable.concat(getCachedBoardPost,
                disApi.getBoardPostSummaryList(null,pageNumber))
                .takeFirst(
                new Func1<List<BoardPost>, Boolean>() {
                    @Override
                    public Boolean call(List<BoardPost> boardPosts) {
                        return boardPosts != null;
                    }
                })
                .onErrorResumeNext(disBoardsLocalRepo.getBoardPosts(boardType))
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

    @Override
    public Observable<BoardPost> getBoardPost(String boardPostUrl, String boardPostId,
            BoardType boardType) {
        return null;
    }

    @Override
    public Observable<Void> thisAComment(String boardPostUrl, String boardPostId, String commentId,
            BoardType boardType) {
        return null;
    }

    public Observable<Void> addNewPost(Board board, String title, String content) {
        return null;
    }

    public Observable<Void> postComment(String boardPostId, String commentId, String title, String content,
            BoardType boardType) {
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
