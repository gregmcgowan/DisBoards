package com.drownedinsound.data;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.network.LoginResponse;
import com.drownedinsound.ui.base.Event;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 19/05/2016.
 */
public interface DisRepo2 {

    Observable<Event<LoginResponse>> getLoginEventObservable();

    void loginUser(String username, String password);

    Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, boolean forceUpdate);

    Observable<List<BoardPostList>> getAllBoardPostLists();

    Observable<List<BoardPostSummary>> getBoardPostSummaryList(
            @BoardPostList.BoardPostListType String boardListType, int pageNumber,
            boolean forceUpdate);

    Observable<BoardPostSummary> getBoardPostSummary(@BoardPostList.BoardPostListType final String boardListType,
            String boardPostId);

    Observable<BoardPost> postComment(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, String commentId, String title, String content);

    Observable<BoardPost> addNewPost(@BoardPostList.BoardPostListType String boardListType,
            String title, String content);

    Observable<BoardPost> thisAComment(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, String commentId);

    Observable<Void> setBoardPostSummary(BoardPostSummary boardPostSummary);

    boolean isUserLoggedIn();

    boolean userSelectedLurk();

    void setUserSelectedLurk(boolean lurk);

    void clearUserSession();

}
