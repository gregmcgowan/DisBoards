package com.drownedinsound.data;

import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardListType;
import com.drownedinsound.data.model.BoardPostListInfo;
import com.drownedinsound.data.network.LoginResponse;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public interface DisBoardRepo {

    Observable<LoginResponse> loginUser(String username, String password);

    Observable<BoardPost> getBoardPost(String boardPostId,
            BoardListType boardListType);

    Observable<List<BoardPost>> getBoardPostSummaryList(BoardListType board, int pageNumber,boolean forceUpdate);

    Observable<List<BoardPostListInfo>> getBoardPostListInfo();

    Observable<Void> thisAComment(String boardPostUrl, String boardPostId, String commentId,
            BoardListType boardListType);

    boolean isUserLoggedIn();

    void clearUserSession();
}
