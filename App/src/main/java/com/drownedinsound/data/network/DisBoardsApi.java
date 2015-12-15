package com.drownedinsound.data.network;

import com.drownedinsound.data.model.BoardPostListInfo;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardListType;
import com.drownedinsound.data.network.handlers.ResponseHandler;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 05/12/15.
 */
public interface DisBoardsApi {

        Observable<LoginResponse> loginUser(String username, String password);

        Observable<BoardPost> getBoardPost(String boardPostUrl, String boardPostId, BoardListType boardListType);

        Observable<List<BoardPost>> getBoardPostSummaryList(BoardListType boardListType, String boardPostUrl, int pageNumber);

        Observable<Void> thisAComment(String boardPostUrl, String boardPostId, String commentId,
                BoardListType boardListType);

        Observable<Void> addNewPost(BoardPostListInfo boardPostListInfo, String title, String content, ResponseHandler responseHandler);

        Observable<Void> postComment(String boardPostId, String commentId, String title, String content,
                BoardListType boardListType, ResponseHandler responseHandler);

        boolean requestInProgress(Object tag);
}
