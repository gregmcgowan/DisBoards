package com.drownedinsound.data;

import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.network.DisBoardsApi;

import android.text.format.DateUtils;

import java.util.Collections;
import java.util.List;

import rx.Completable;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by gregmcgowan on 05/12/15.
 */
public class DisBoardRepoImpl implements DisBoardRepo {

    private static final long MAX_BOARD_POST_LIST_AGE_MINUTES = 15;

    private static final long MAX_BOARD_POST_AGE_MINUTES = 15;

    private DisBoardsApi disApi;

    private DisBoardsLocalRepo disBoardsLocalRepo;

    public DisBoardRepoImpl(DisBoardsApi disApi,
            DisBoardsLocalRepo disBoardsDatabase) {
        this.disApi = disApi;
        this.disBoardsLocalRepo = disBoardsDatabase;
    }

    @Override
    public Observable<List<BoardPostSummary>> getBoardPostSummaryList(
            final @BoardPostList.BoardPostListType String boardListType,
            final int pageNumber, final boolean forceUpdate) {

        return disBoardsLocalRepo.getBoardPostList(boardListType).flatMap(
                new Func1<BoardPostList, Observable<List<BoardPostSummary>>>() {
                    @Override
                    public Observable<List<BoardPostSummary>> call(
                            final BoardPostList boardPostList) {
                        long lastFetchedTime = boardPostList.getLastFetchedMs();
                        long fiveMinutesAgo = System.currentTimeMillis()
                                - (DateUtils.MINUTE_IN_MILLIS
                                * MAX_BOARD_POST_LIST_AGE_MINUTES);

                        boolean recentlyFetched = lastFetchedTime > fiveMinutesAgo;
                        List<BoardPostSummary> posts = boardPostList.getBoardPostSummaries();

                        if (recentlyFetched && posts.size() > 0 && !forceUpdate) {
                            return Observable.just(posts);
                        } else {
                            return disApi
                                    .getBoardPostSummaryList(boardListType, boardPostList.getUrl(),
                                            pageNumber)
                                    .flatMap(
                                            new Func1<List<BoardPostSummary>, Observable<List<BoardPostSummary>>>() {
                                                @Override
                                                public Observable<List<BoardPostSummary>> call(
                                                        List<BoardPostSummary> boardPosts) {

                                                    boardPostList.setLastFetchedMs(
                                                            System.currentTimeMillis());
                                                    disBoardsLocalRepo
                                                            .setBoardPostList(boardPostList);
                                                    disBoardsLocalRepo
                                                            .setBoardPostSummaries(boardPosts);

                                                    return Observable.just(boardPosts);
                                                }
                                            }).onErrorResumeNext(Observable.just(posts));
                        }
                    }
                }).map(new Func1<List<BoardPostSummary>, List<BoardPostSummary>>() {
            @Override
            public List<BoardPostSummary> call(List<BoardPostSummary> boardPostSummaries) {
                if (pageNumber == 1) {
                    Collections.sort(boardPostSummaries, BoardPostSummary.COMPARATOR);
                }

                return boardPostSummaries;
            }
        });
    }

    @Override
    public Observable<List<BoardPostList>> getAllBoardPostLists() {
        return disBoardsLocalRepo.getAllBoardPostLists();
    }

    @Override
    public Observable<BoardPost> getBoardPost(
            @BoardPostList.BoardPostListType final String boardListType,
            final String boardPostId, boolean forceUpdate) {
        if (forceUpdate) {
            return getBoardPostFromNetwork(boardListType, boardPostId).doOnNext(
                    new Action1<BoardPost>() {
                        @Override
                        public void call(BoardPost boardPost) {
                            saveBoardPost(boardPost);
                        }
                    });
        } else {
            return disBoardsLocalRepo.getBoardPost(boardPostId).flatMap(
                    new Func1<BoardPost, Observable<BoardPost>>() {
                        @Override
                        public Observable<BoardPost> call(BoardPost boardPost) {
                            long lastFetchedTime = boardPost != null ? boardPost
                                    .getLastFetchedTime()
                                    : 0l;
                            long fiveMinutesAgo = System.currentTimeMillis()
                                    - (DateUtils.MINUTE_IN_MILLIS
                                    * MAX_BOARD_POST_AGE_MINUTES);

                            boolean recentlyFetched = lastFetchedTime > fiveMinutesAgo;
                            if (recentlyFetched) {
                                return Observable.just(boardPost);
                            } else {
                                return getBoardPostFromNetwork(boardListType, boardPostId);
                            }
                        }
                    }).doOnNext(new Action1<BoardPost>() {
                @Override
                public void call(BoardPost boardPost) {
                        saveBoardPost(boardPost);
                }
            });
        }
    }

    private void saveBoardPost(BoardPost boardPost) {
        try {
            disBoardsLocalRepo.setBoardPost(boardPost);
        } catch (Exception e) {
            Timber.d("Could not cache board post " + boardPost.getBoardPostID() + " exception " + e
                    .getMessage());
        }
    }

    private Observable<BoardPost> getBoardPostFromNetwork(
            @BoardPostList.BoardPostListType final String
                    boardListType, final String boardPostId) {
        return disApi.getBoardPost(boardListType, boardPostId)
                .onErrorResumeNext(disBoardsLocalRepo.getBoardPost(boardPostId))
                .doOnNext(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {
                        saveBoardPost(boardPost);
                    }});
    }

    @Override
    public Observable<BoardPostSummary> getBoardPostSummary(@BoardPostList.BoardPostListType final String boardListType,
                final String boardPostId) {
        return disBoardsLocalRepo.getBoardPostSummaryListObservable(boardListType).flatMap(
                new Func1<List<BoardPostSummary>, Observable<BoardPostSummary>>() {
                    @Override
                    public Observable<BoardPostSummary> call(List<BoardPostSummary> boardPostSummaries) {
                        for(BoardPostSummary boardPostSummary : boardPostSummaries) {
                            if(boardPostId.equals(boardPostSummary.getBoardPostID())) {
                                return Observable.just(boardPostSummary);
                            }
                        }
                        return Observable.just(null);
                    }
                });
    }

    @Override
    public Completable setBoardPostSummary(BoardPostSummary boardPostSummary) {
        return disBoardsLocalRepo.setBoardPostSummary(boardPostSummary);
    }



}
