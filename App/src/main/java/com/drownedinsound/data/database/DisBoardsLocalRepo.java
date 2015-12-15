package com.drownedinsound.data.database;

import com.drownedinsound.data.model.BoardPostListInfo;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardListType;
import com.drownedinsound.data.model.DraftBoardPost;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public interface DisBoardsLocalRepo {

    Observable<BoardPost> getBoardPost(String postId);

    Observable<Void> setBoardPost(BoardPost boardPost);

    Observable<DraftBoardPost> getDraftBoardPost(BoardListType boardListType);

    Observable<Void> setDraftBoardPost(DraftBoardPost draftBoardPost);

    Observable<Void> removeDraftBoardPost(BoardListType boardListType);

    Observable<List<BoardPost>> getBoardPosts(BoardListType boardListType);

    Observable<Void> setBoardPosts(List<BoardPost> boardPosts);

    Observable<BoardPostListInfo> getBoard(BoardListType boardListType);

    Observable<Void> setBoard(BoardPostListInfo boardPostListInfo);

    Observable<List<BoardPostListInfo>> getAllBoardPostInfos();
}
