package com.drownedinsound.data.database;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostCommentDao;
import com.drownedinsound.data.generatered.BoardPostDao;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostListDao;
import com.drownedinsound.data.generatered.DaoSession;
import com.drownedinsound.utils.AssertUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by gregmcgowan on 29/12/15.
 */
public class DisBoardsLocalRepoImpl implements DisBoardsLocalRepo {

    private BoardPostDao boardPostDao;

    private BoardPostListDao boardPostListDao;

    private BoardPostCommentDao boardPostCommentDao;

    @Inject
    public DisBoardsLocalRepoImpl(DaoSession daoSession) {
        this.boardPostDao = daoSession.getBoardPostDao();
        this.boardPostListDao = daoSession.getBoardPostListDao();
        this.boardPostCommentDao = daoSession.getBoardPostCommentDao();
    }

    @Override
    public Observable<BoardPost> getBoardPost(String postId) {
        return null;
    }

    @Override
    public Observable<Void> setBoardPost(BoardPost boardPost) {
        return null;
    }

    @Override
    public Observable<List<BoardPost>> getBoardPostsObservable(
            @BoardPostList.BoardPostListType final String boardListType) {
        return Observable.create(new Observable.OnSubscribe<List<BoardPost>>() {
            @Override
            public void call(Subscriber<? super List<BoardPost>> subscriber) {
                subscriber.onNext(getBoardPosts(boardListType));
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void setBoardPosts(List<BoardPost> boardPosts) {
        AssertUtils.checkMainThread();

        boardPostDao.insertOrReplaceInTx(boardPosts);
    }

    public List<BoardPost> getBoardPosts(@BoardPostList.BoardPostListType String boardListType) {
        AssertUtils.checkMainThread();

        List<BoardPost> boardPosts = new ArrayList<>();
        BoardPostList boardPostList = boardPostListDao.load(boardListType);
        if (boardPostList != null) {
            boardPosts = boardPostList.getPosts();
        }

        return boardPosts;
    }

    @Override
    public Observable<BoardPostList> getBoardPostList(
            @BoardPostList.BoardPostListType final String boardListType) {
        return Observable.create(new Observable.OnSubscribe<BoardPostList>() {
            @Override
            public void call(Subscriber<? super BoardPostList> subscriber) {
                BoardPostList boardPostList = boardPostListDao.load(boardListType);
                Collections.sort(boardPostList.getPosts(),BoardPost.COMPARATOR);
                boardPostList.getPosts();
                subscriber.onNext(boardPostList);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void setBoardPostList(final BoardPostList boardPostListInfo) {
        AssertUtils.checkMainThread();
        boardPostListDao.insertOrReplace(boardPostListInfo);
    }

    @Override
    public Observable<List<BoardPostList>> getAllBoardPostLists() {
        return Observable.create(new Observable.OnSubscribe<List<BoardPostList>>() {
            @Override
            public void call(Subscriber<? super List<BoardPostList>> subscriber) {
                List<BoardPostList> boardPostList = boardPostListDao.queryBuilder()
                        .orderAsc(BoardPostListDao.Properties.PageIndex).list();
                subscriber.onNext(boardPostList);
                subscriber.onCompleted();
            }
        });
    }
}
