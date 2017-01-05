package com.drownedinsound.data.database;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public interface DisBoardsLocalRepo {

    Observable<BoardPost> getBoardPost(String postId);

    void setBoardPost(BoardPost boardPost) throws Exception;

    Observable<Void> setBoardPostSummary(BoardPostSummary boardPostSummary);

    Observable<Void> setBoardPostObservable(BoardPost boardPost);

    Observable<List<BoardPostSummary>> getBoardPostSummaryListObservable(@BoardPostList.BoardPostListType String boardListType);

    List<BoardPostSummary> getBoardPostSummaryList(@BoardPostList.BoardPostListType String boardListType);
    /**
     * Cannot be called from the main thread or will throw a run time expcetion
     *
     * @param boardPosts
     */
    void setBoardPostSummaries(List<BoardPostSummary> boardPosts);

    Observable<BoardPostList> getBoardPostList(@BoardPostList.BoardPostListType String boardListType);

    /**
     * Cannot be called from the main thread or will throw a run time expcetion
     *
     * @param boardPostListInfo
     */
    void setBoardPostList(BoardPostList boardPostListInfo);

    Observable<List<BoardPostList>> getAllBoardPostLists();
}
