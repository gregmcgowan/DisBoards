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

    Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId);

    Observable<List<BoardPostSummary>> getBoardPostSummaryList(
            @BoardPostList.BoardPostListType String boardListType, String boardPostUrl,
            int pageNumber);


    boolean requestInProgress(Object tag);
}
