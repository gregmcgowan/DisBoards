package com.drownedinsound.data;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;

import java.util.List;

import rx.Completable;
import rx.Observable;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public interface DisBoardRepo {

    Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, boolean forceUpdate);

    Observable<List<BoardPostList>> getAllBoardPostLists();

    Observable<List<BoardPostSummary>> getBoardPostSummaryList(
            @BoardPostList.BoardPostListType String boardListType, int pageNumber,
            boolean forceUpdate);

    Observable<BoardPostSummary> getBoardPostSummary(@BoardPostList.BoardPostListType final String boardListType,
            String boardPostId);

    Completable setBoardPostSummary(BoardPostSummary boardPostSummary);

}
