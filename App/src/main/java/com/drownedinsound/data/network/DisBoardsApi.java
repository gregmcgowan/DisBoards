package com.drownedinsound.data.network;

import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.network.handlers.ResponseHandler;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 05/12/15.
 */
public interface DisBoardsApi {

        Observable<LoginResponse> loginUser(String username, String password);

        Observable<BoardPost> getBoardPost(String boardPostUrl, String boardPostId, BoardType boardType);

        Observable<List<BoardPost>> getBoardPostSummaryList(Board board, int pageNumber);

        Observable<Void> thisAComment(String boardPostUrl, String boardPostId, String commentId,
                BoardType boardType);

        Observable<Void> addNewPost(Board board, String title, String content, ResponseHandler responseHandler);

        Observable<Void> postComment(String boardPostId, String commentId, String title, String content,
                BoardType boardType, ResponseHandler responseHandler);

        boolean requestInProgress(Object tag);
}
