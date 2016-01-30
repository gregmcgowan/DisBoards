package com.drownedinsound.data;

import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.network.DisBoardsApi;
import com.drownedinsound.data.network.LoginResponse;

import android.text.format.DateUtils;

import java.util.Collections;
import java.util.List;

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
    private UserSessionRepo userSessionRepo;

    public DisBoardRepoImpl(DisBoardsApi disApi,
            DisBoardsLocalRepo disBoardsDatabase,
            UserSessionRepo userSessionRepo) {
        this.disApi = disApi;
        this.disBoardsLocalRepo = disBoardsDatabase;
        this.userSessionRepo = userSessionRepo;
    }

    @Override
    public Observable<LoginResponse> loginUser(String username, String password) {
        return disApi.loginUser(username, password)
                .doOnNext(new Action1<LoginResponse>() {
                    @Override
                    public void call(LoginResponse loginResponse) {
                        userSessionRepo
                                .setAuthenticityToken(loginResponse.getAuthenticationToken());
                    }
                });
    }

    @Override
    public Observable<List<BoardPostSummary>> getBoardPostSummaryList(final @BoardPostList.BoardPostListType String boardListType,
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
    public Observable<BoardPost> getBoardPost(@BoardPostList.BoardPostListType final String boardListType,
            final String boardPostId,boolean forceUpdate) {
        if(forceUpdate) {
            return getBoardPostFromNetwork(boardListType,boardPostId);
        } else  {
            return disBoardsLocalRepo.getBoardPost(boardPostId).flatMap(
                    new Func1<BoardPost, Observable<BoardPost>>() {
                        @Override
                        public Observable<BoardPost> call(BoardPost boardPost) {
                                     long lastFetchedTime = boardPost != null ? boardPost.getLastFetchedTime()
                                    : 0l;
                            long fiveMinutesAgo = System.currentTimeMillis()
                                    - (DateUtils.MINUTE_IN_MILLIS
                                    * MAX_BOARD_POST_AGE_MINUTES);

                            boolean recentlyFetched = lastFetchedTime > fiveMinutesAgo;
                            if(recentlyFetched) {
                                return Observable.just(boardPost);
                            } else {
                                return getBoardPostFromNetwork(boardListType,boardPostId);
                            }
                        }
                    });
        }
    }

    private Observable<BoardPost> getBoardPostFromNetwork(@BoardPostList.BoardPostListType final String
            boardListType, final String boardPostId) {
        return disApi.getBoardPost(boardListType,boardPostId)
                .onErrorResumeNext(disBoardsLocalRepo.getBoardPost(boardPostId))
                .doOnNext(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {

                        try {
                            disBoardsLocalRepo.setBoardPost(boardPost);
                        } catch (Exception e) {
                            Timber.d("Could not cache board post "+boardPostId + " exception "+e.getMessage());
                        }
                    }
                });
    }


    @Override
    public Observable<BoardPost> postComment(@BoardPostList.BoardPostListType String boardListType,
            final String boardPostId, String commentId, String title, String content) {
        return disApi.postComment(boardListType, boardPostId, commentId, title, content,
                userSessionRepo.getAuthenticityToken())
                .doOnNext(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {
                        try {
                            disBoardsLocalRepo.setBoardPost(boardPost);
                        } catch (Exception e) {
                            Timber.d("Could not cache board post " + boardPostId + " exception " + e
                                    .getMessage());
                        }
                    }
                });
    }

    @Override
    public Observable<BoardPost> thisAComment(@BoardPostList.BoardPostListType String boardListType,
            final String boardPostId, String commentId) {
        return disApi.thisAComment(boardListType, boardPostId, commentId,
                userSessionRepo.getAuthenticityToken())
                .doOnNext(new Action1<BoardPost>() {
                    @Override
                    public void call(BoardPost boardPost) {
                        try {
                            disBoardsLocalRepo.setBoardPost(boardPost);
                        } catch (Exception e) {
                            Timber.d("Could not cache board post " + boardPostId + " exception " + e
                                    .getMessage());
                        }
                    }
                });
    }


    @Override
    public Observable<BoardPost> addNewPost(
            @BoardPostList.BoardPostListType final String boardListType,
            final String title, final String content) {
        return disBoardsLocalRepo.getBoardPostList(boardListType)
                .flatMap(new Func1<BoardPostList, Observable<BoardPost>>() {
                    @Override
                    public Observable<BoardPost> call(final BoardPostList boardPostList) {
                        String sectionId = String.valueOf(boardPostList.getSectionId());
                        String authToken = userSessionRepo.getAuthenticityToken();

                        return disApi
                                .addNewPost(boardListType, title, content, authToken, sectionId)
                                .doOnNext(new Action1<BoardPost>() {
                                    @Override
                                    public void call(BoardPost boardPost) {

                                        boardPostList.setLastFetchedMs(0);
                                        disBoardsLocalRepo.setBoardPostList(boardPostList);

                                        try {
                                            disBoardsLocalRepo.setBoardPost(boardPost);
                                        } catch (Exception e) {
                                            Timber.d("Could not cache board post " + boardPost
                                                    .getBoardPostID() + " exception " + e
                                                    .getMessage());
                                        }
                                    }
                                });
                    }
                });
    }


    @Override
    public boolean isUserLoggedIn() {
        return userSessionRepo.isUserLoggedIn();
    }

    @Override
    public void clearUserSession() {
        userSessionRepo.clearSession();
    }



}
