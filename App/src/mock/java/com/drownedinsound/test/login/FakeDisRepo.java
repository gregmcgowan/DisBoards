package com.drownedinsound.test.login;

import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.data.model.BoardTypeConstants;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.data.network.UrlConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public class FakeDisRepo implements DisBoardRepo {


    private static List<BoardPostSummary> boardPosts;

    private static boolean loginSuccess;

    private static long networkDelaySeconds;

    public static void setBoardPostSummariesToReturn(List<BoardPostSummary> boardPostSummariesToReturn) {
        boardPosts = boardPostSummariesToReturn;
    }

    public static void setLoginSuccess(boolean success) {
        loginSuccess = success;
    }

    public static void setNetworkDelaySeconds(long seconds) {
        networkDelaySeconds = seconds;
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
            String boardPostId, boolean forceUpdate) {
        BoardPost expectedBoardPost = new BoardPost();
        expectedBoardPost.setBoardPostID("4471118");
        expectedBoardPost.setAuthorUsername("shitty_zombies");
        expectedBoardPost.setTitle("Charlie Brooker&#x27;s 2015 Wipe");
        expectedBoardPost.setContent("<p>Christ, that was bleak, wasn&#x27;t it?</p>");
        expectedBoardPost.setDateOfPost("22:55, 30 December '15");
        expectedBoardPost.setBoardListTypeID(BoardListTypes.SOCIAL);
        expectedBoardPost.setNumberOfReplies(5);

        return Observable.just(expectedBoardPost);
    }

    @Override
    public Observable<List<BoardPostList>> getAllBoardPostLists() {
        List<BoardPostList> boardPostListInfos = new ArrayList<>();
        boardPostListInfos.add(new BoardPostList(BoardListTypes.MUSIC,
                BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL, 0, 19, 0));


        return Observable.just(boardPostListInfos);
    }

    @Override
    public Observable<List<BoardPostSummary>> getBoardPostSummaryList(
            @BoardPostList.BoardPostListType final String boardListType, int pageNumber,
            boolean forceUpdate) {
        return Observable.just(boardPosts)
                .filter(new Func1<List<BoardPostSummary>, Boolean>() {
                    @Override
                    public Boolean call(List<BoardPostSummary> boardPostSummaries) {
                        BoardPostSummary boardPostSummary = boardPostSummaries.get(0);
                        @BoardPostList.BoardPostListType String thisBoardPostListType
                                = boardPostSummary.getBoardListTypeID();
                        return thisBoardPostListType.equals(boardListType);
                    }
                }).delay(networkDelaySeconds, TimeUnit.SECONDS);
    }

    @Override
    public Observable<BoardPost> postComment(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, String commentId, String title, String content) {
        return null;
    }

    @Override
    public Observable<BoardPost> addNewPost(@BoardPostList.BoardPostListType String boardListType,
            String title, String content) {
        BoardPost expectedBoardPost = new BoardPost();
        expectedBoardPost.setBoardPostID("4471118");
        expectedBoardPost.setAuthorUsername("shitty_zombies");
        expectedBoardPost.setTitle("Charlie Brooker&#x27;s 2015 Wipe");
        expectedBoardPost.setContent("<p>Christ, that was bleak, wasn&#x27;t it?</p>");
        expectedBoardPost.setDateOfPost("22:55, 30 December '15");
        expectedBoardPost.setBoardListTypeID(BoardListTypes.SOCIAL);
        expectedBoardPost.setNumberOfReplies(5);

        return Observable.just(expectedBoardPost);
    }

    @Override
    public Observable<BoardPost> thisAComment(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, String commentId) {
        return null;
    }

    @Override
    public boolean isUserLoggedIn() {
        return loginSuccess;
    }

    @Override
    public void clearUserSession() {

    }

    @Override
    public Observable<Void> setBoardPostSummary(BoardPostSummary boardPostSummary) {
        return null;
    }

    @Override
    public boolean userSelectedLurk() {
        return false;
    }

    @Override
    public void setUserSelectedLurk(boolean lurk) {

    }
}
