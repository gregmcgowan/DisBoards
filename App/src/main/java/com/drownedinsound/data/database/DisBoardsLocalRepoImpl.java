package com.drownedinsound.data.database;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostCommentDao;
import com.drownedinsound.data.generatered.BoardPostDao;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostListDao;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.generatered.BoardPostSummaryDao;
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

    private BoardPostSummaryDao boardPostSummaryDao;

    @Inject
    public DisBoardsLocalRepoImpl(DaoSession daoSession) {
        this.boardPostDao = daoSession.getBoardPostDao();
        this.boardPostListDao = daoSession.getBoardPostListDao();
        this.boardPostCommentDao = daoSession.getBoardPostCommentDao();
        this.boardPostSummaryDao = daoSession.getBoardPostSummaryDao();
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
    public Observable<List<BoardPostSummary>> getBoardPostSummaryListObservable(
            @BoardPostList.BoardPostListType final String boardListType) {
        return Observable.create(new Observable.OnSubscribe<List<BoardPostSummary>>() {
            @Override
            public void call(Subscriber<? super List<BoardPostSummary>> subscriber) {
                subscriber.onNext(getBoardPostSummaryList(boardListType));
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void setBoardPostSummaries(List<BoardPostSummary> boardPostSummaries) {
        AssertUtils.checkMainThread();

        if(boardPostSummaries.size() > 0) {
            @BoardPostList.BoardPostListType String type = boardPostSummaries.get(0).getBoardListTypeID();

            List<BoardPostSummary> existingSummaries = getBoardPostSummaryList(type);
            for(BoardPostSummary existingSummary: existingSummaries) {
                int index = boardPostSummaries.indexOf(existingSummary);
                if(index != -1) {
                    BoardPostSummary matchingNewSummary = boardPostSummaries.get(index);
                    //We don't want to overrite this
                    if(matchingNewSummary != null) {
                        existingSummary.setLastViewedTime(matchingNewSummary.getLastViewedTime());
                    }
                }

            }
            boardPostSummaryDao.insertOrReplaceInTx(boardPostSummaries);
        }
    }

    @Override
    public List<BoardPostSummary> getBoardPostSummaryList(
            @BoardPostList.BoardPostListType String boardListType) {
        AssertUtils.checkMainThread();

        return boardPostSummaryDao.queryBuilder().where(
                BoardPostSummaryDao.Properties.BoardListTypeID.eq(boardListType)).list();
    }

    @Override
    public Observable<BoardPostList> getBoardPostList(@BoardPostList.BoardPostListType final String boardListType) {
        return Observable.create(new Observable.OnSubscribe<BoardPostList>() {
            @Override
            public void call(Subscriber<? super BoardPostList> subscriber) {
                BoardPostList boardPostList = boardPostListDao.load(boardListType);
                boardPostList.setBoardPostSummaries(getBoardPostSummaryList(boardListType));
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
