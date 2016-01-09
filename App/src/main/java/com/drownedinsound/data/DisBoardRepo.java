package com.drownedinsound.data;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.network.LoginResponse;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public interface DisBoardRepo {

    Observable<LoginResponse> loginUser(String username, String password);

    Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, boolean forceUpdate);

    Observable<List<BoardPostList>> getAllBoardPostLists();

    Observable<List<BoardPostSummary>> getBoardPostSummaryList(
            @BoardPostList.BoardPostListType String boardListType, int pageNumber,
            boolean forceUpdate);

    Observable<Void> thisAComment(String boardPostId, String commentId,
            @BoardPostList.BoardPostListType String boardListType);

    boolean isUserLoggedIn();

    void clearUserSession();
}
