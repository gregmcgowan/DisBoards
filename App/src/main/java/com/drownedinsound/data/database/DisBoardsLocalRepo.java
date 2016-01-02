package com.drownedinsound.data.database;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import java.util.List;

import rx.Observable;

/**
 * Created by gregmcgowan on 06/12/15.
 */
public interface DisBoardsLocalRepo {

    Observable<BoardPost> getBoardPost(String postId);

    Observable<Void> setBoardPost(BoardPost boardPost);

    Observable<List<BoardPost>> getBoardPostsObservable(@BoardPostList.BoardPostListType String boardListType);

    List<BoardPost> getBoardPosts(@BoardPostList.BoardPostListType String boardListType);

    //Observable<Void> set(List<BoardPost> boardPosts);

    /**
     * Cannot be called from the main thread or will throw a run time expcetion
     *
     * @param boardPosts
     */
    void setBoardPosts(List<BoardPost> boardPosts);

    Observable<BoardPostList> getBoardPostList(@BoardPostList.BoardPostListType String boardListType);

    /**
     * Cannot be called from the main thread or will throw a run time expcetion
     *
     * @param boardPostListInfo
     */
    void setBoardPostList(BoardPostList boardPostListInfo);

    Observable<List<BoardPostList>> getAllBoardPostLists();
}
