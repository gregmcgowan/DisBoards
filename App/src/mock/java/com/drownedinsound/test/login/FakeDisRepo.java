package com.drownedinsound.test.login;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardListType;
import com.drownedinsound.data.model.BoardPostListInfo;
import com.drownedinsound.data.network.LoginResponse;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public class FakeDisRepo implements DisBoardRepo {


    private static List<BoardPost> boardPosts;

    private static boolean loginSuccess;

    public static void setBoardPostSummariesToReturn(List<BoardPost> boardPostSummariesToReturn) {
        boardPosts = boardPostSummariesToReturn;
    }

    public static void setLoginSuccess(boolean success) {
        loginSuccess = success;
    }

    @Override
    public Observable<LoginResponse> loginUser(String username, String password) {
        if(loginSuccess) {
            return Observable.just(new LoginResponse());
        } else {
            return Observable.error(new Exception());
        }
    }

    @Override
    public Observable<BoardPost> getBoardPost( String boardPostId, BoardListType boardListType) {
        return null;
    }

    @Override
    public Observable<List<BoardPost>> getBoardPostSummaryList(BoardListType board, int pageNumber,
            boolean forceUpdate) {
        return Observable.just(boardPosts);
    }

    @Override
    public Observable<List<BoardPostListInfo>> getBoardPostListInfo() {
        return null;
    }

    @Override
    public Observable<Void> thisAComment(String boardPostUrl, String boardPostId, String commentId,
            BoardListType boardListType) {
        return null;
    }

    @Override
    public boolean isUserLoggedIn() {
        return false;
    }

    @Override
    public void clearUserSession() {

    }
}
