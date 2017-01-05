package com.drownedinsound.data;

import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.network.DisBoardsApi;

import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 19/05/2016.
 */
public class DisBoardRepoImpl2 implements DisRepo2 {

    private static final long MAX_BOARD_POST_LIST_AGE_MINUTES = 15;

    private static final long MAX_BOARD_POST_AGE_MINUTES = 15;

    private DisBoardsApi disApi;

    private DisBoardsLocalRepo disBoardsLocalRepo;

    public DisBoardRepoImpl2(DisBoardsApi disApi,
            DisBoardsLocalRepo disBoardsDatabase) {
        this.disApi = disApi;
        this.disBoardsLocalRepo = disBoardsDatabase;
    }

    @Override
    public Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId, boolean forceUpdate) {
        return null;
    }

    @Override
    public Observable<List<BoardPostList>> getAllBoardPostLists() {
        return null;
    }

    @Override
    public Observable<List<BoardPostSummary>> getBoardPostSummaryList(
            @BoardPostList.BoardPostListType String boardListType, int pageNumber,
            boolean forceUpdate) {
        return null;
    }

    @Override
    public Observable<BoardPostSummary> getBoardPostSummary(
            @BoardPostList.BoardPostListType String boardListType, String boardPostId) {
        return null;
    }

    @Override
    public Observable<Void> setBoardPostSummary(BoardPostSummary boardPostSummary) {
        return null;
    }


}
