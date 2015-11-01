package com.drownedinsound.ui.post;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.BoardPostComment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by gregmcgowan on 13/08/15.
 */
public class ReplyToCommentListener {

    private WeakReference<BoardPostListAdapter> adapterWeakReference;

    private WeakReference<FragmentManager> fragmentManagerReference;

    public ReplyToCommentListener(
            WeakReference<BoardPostListAdapter> adapterWeakReference,
            WeakReference<FragmentManager> fragmentManager) {
        this.adapterWeakReference = adapterWeakReference;
        this.fragmentManagerReference = fragmentManager;
    }

    public void doCommentClickAction(View parentView, int position) {
        BoardPostComment comment = null;
        BoardPostListAdapter boardPostListAdapter = adapterWeakReference
                .get();
        if (boardPostListAdapter != null) {
            comment = boardPostListAdapter.getItem(position);
        }

        if (comment != null) {
            String replyToAuthor = comment.getAuthorUsername();
            if (TextUtils.isEmpty(replyToAuthor)) {
                BoardPostComment initalPost = boardPostListAdapter
                        .getItem(0);
                replyToAuthor = initalPost.getAuthorUsername();
            }
            String replyToId = comment.getId();

            Bundle replyDetails = new Bundle();
            replyDetails.putString(DisBoardsConstants.REPLY_TO_AUTHOR,
                    replyToAuthor);
            replyDetails.putString(DisBoardsConstants.BOARD_COMMENT_ID,
                    replyToId);
            replyDetails.putString(DisBoardsConstants.BOARD_POST_ID,
                    comment.getBoardPost().getId());
            replyDetails.putSerializable(DisBoardsConstants.BOARD_TYPE,
                    comment.getBoardPost().getBoardType());
    /*
             * String replyText = comment.getTitle(); if
             * (TextUtils.isEmpty(replyText)) { replyText =
             * comment.getContent(); }
             * replyDetails.putString(DisBoardsConstants.REPLY_TO_TEXT,
             * replyText);
             */
            // TextUtils.ellipsize(text, p, avail, where)

            PostReplyFragment.newInstance(replyDetails)
                    .show(fragmentManagerReference.get(),
                            "REPLY_FRAGMENT_DIALOG");

        } else {

        }
    }

}
