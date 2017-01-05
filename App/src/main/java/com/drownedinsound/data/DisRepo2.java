package com.drownedinsound.data;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 19/05/2016.
 */
public interface DisRepo2 {

    Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, boolean forceUpdate);

    Observable<List<BoardPostList>> getAllBoardPostLists();

    Observable<List<BoardPostSummary>> getBoardPostSummaryList(
            @BoardPostList.BoardPostListType String boardListType, int pageNumber,
            boolean forceUpdate);

    Observable<BoardPostSummary> getBoardPostSummary(@BoardPostList.BoardPostListType final String boardListType,
            String boardPostId);

    Observable<Void> setBoardPostSummary(BoardPostSummary boardPostSummary);


}
