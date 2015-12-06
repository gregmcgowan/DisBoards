package com.drownedinsound.data;

import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.network.DisBoardsApi;
import com.drownedinsound.data.network.LoginResponse;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by gregmcgowan on 05/12/15.
 */
public class DisBoardRepoImpl implements DisBoardRepo {

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
        //Make login request
        return disApi.loginUser(username,password)
                .doOnNext(new Action1<LoginResponse>() {
            @Override
            public void call(LoginResponse loginResponse) {
                userSessionRepo.setAuthenticityToken(loginResponse.getAuthenticationToken());
            }
        });
    }

    @Override
    public Observable<BoardPost> getBoardPost(String boardPostUrl, String boardPostId,
            BoardType boardType) {
        return null;
    }

    @Override
    public Observable<List<BoardPost>> getBoardPostSummaryList(Object tag, int pageNumber) {
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


//    private boolean recentlyFetched(Board cachedBoard) {
//        BoardType type = cachedBoard.getBoardType();
//        Board board = disBoardsLocalRepo.getBoard(type);
//        long lastFetchedTime = board.getLastFetchedTime();
//        long fiveMinutesAgo = System.currentTimeMillis()
//                - (DateUtils.MINUTE_IN_MILLIS * MAX_BOARD_POST_LIST_AGE_MINUTES);
//
//        boolean recentlyFetched = lastFetchedTime > fiveMinutesAgo;
//
//        if (DisBoardsConstants.DEBUG) {
//            Timber.d("type " + type + " fetched " + (((System.currentTimeMillis() - lastFetchedTime)
//                    / 1000))
//                    + " seconds ago "
//                    + (recentlyFetched ? "recently fetched"
//                    : "not recently fetched"));
//        }
//
//        return recentlyFetched;
//    }
}
