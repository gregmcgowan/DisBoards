package com.drownedinsound.data.database;

import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.model.DraftBoardPost;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public interface DisBoardsLocalRepo {

    Observable<BoardPost> getBoardPost(String postId);

    Observable<Void> setBoardPost(BoardPost boardPost);

    Observable<DraftBoardPost> getDraftBoardPost(BoardType boardType);

    Observable<Void> setDraftBoardPost(DraftBoardPost draftBoardPost);

    Observable<Void> removeDraftBoardPost(BoardType boardType);

    Observable<List<BoardPost>> getBoardPosts(BoardType boardType);

    Observable<Void> setBoardPosts(List<BoardPost> boardPosts);

    Observable<Board> getBoard(BoardType boardType);

    Observable<Void> setBoard(Board board);
}
