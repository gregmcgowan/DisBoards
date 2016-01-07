package com.drownedinsound.test.login;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.network.LoginResponse;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public class FakeDisRepo implements DisBoardRepo {


    private static List<BoardPostSummary> boardPosts;

    private static boolean loginSuccess;

    public static void setBoardPostSummariesToReturn(List<BoardPostSummary> boardPostSummariesToReturn) {
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
    public Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId) {
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
        return Observable.just(boardPosts);
    }

    @Override
    public Observable<Void> thisAComment(String boardPostId, String commentId,
            @BoardPostList.BoardPostListType String boardListType) {
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
