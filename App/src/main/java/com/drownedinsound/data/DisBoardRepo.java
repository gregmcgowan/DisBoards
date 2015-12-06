package com.drownedinsound.data;

import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.network.LoginResponse;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public interface DisBoardRepo {

    Observable<LoginResponse> loginUser(String username, String password);

    Observable<BoardPost> getBoardPost(String boardPostUrl, String boardPostId,
            BoardType boardType);

    Observable<List<BoardPost>> getBoardPostSummaryList(Object tag, int pageNumber);

    Observable<Void> thisAComment(String boardPostUrl, String boardPostId, String commentId,
            BoardType boardType);

    boolean isUserLoggedIn();

    void clearUserSession();
}
