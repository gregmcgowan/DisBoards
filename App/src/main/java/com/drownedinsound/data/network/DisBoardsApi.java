package com.drownedinsound.data.network;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 05/12/15.
 */
public interface DisBoardsApi {

    Observable<LoginResponse> loginUser(String username, String password);

    Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId);

    Observable<List<BoardPostSummary>> getBoardPostSummaryList(
            @BoardPostList.BoardPostListType String boardListType, String boardPostUrl,
            int pageNumber);

    Observable<BoardPost> postComment(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, String commentId, String title, String content, String authToken);

    Observable<BoardPost> thisAComment(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, String commentId, String authToken);

    Observable<BoardPost> addNewPost(@BoardPostList.BoardPostListType String boardListType,
            String title, String content, String authToken, String sectionID);


    boolean requestInProgress(Object tag);
}
