package com.drownedinsound.ui.base;

import com.drownedinsound.data.generatered.BoardPostList;

/**
 * Created by gregmcgowan on 17/01/16.
 */
public interface Display {

    void showBoardPost(@BoardPostList.BoardPostListType String boardListType , String boardPostId);

    void showNewPostUI(@BoardPostList.BoardPostListType String boardListType);

    void showReplyUI(@BoardPostList.BoardPostListType String boardListType, String postId, String replyToAuthor,
            String replyToCommentId);

    void showNotLoggedInUI();

    void hideCurrentScreen();
}
