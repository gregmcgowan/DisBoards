package com.drownedinsound.data.network;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.network.handlers.ResponseHandler;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 05/12/15.
 */
public interface DisBoardsApi {

        Observable<LoginResponse> loginUser(String username, String password);

        Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType String boardListType, String boardPostUrl, String boardPostId);

        Observable<List<BoardPost>> getBoardPostSummaryList(@BoardPostList.BoardPostListType String boardListType, String boardPostUrl, int pageNumber);

        Observable<Void> thisAComment(@BoardPostList.BoardPostListType String boardListType, String boardPostUrl, String boardPostId, String commentId);

        Observable<Void> addNewPost(@BoardPostList.BoardPostListType String boardListType, String title, String content, ResponseHandler responseHandler);

        Observable<Void> postComment(@BoardPostList.BoardPostListType String boardListType, String boardPostId, String commentId, String title, String content);

        boolean requestInProgress(Object tag);
}
