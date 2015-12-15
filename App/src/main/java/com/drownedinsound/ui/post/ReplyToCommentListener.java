package com.drownedinsound.ui.post;

import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardPostComment;
import com.drownedinsound.data.model.BoardListType;

import android.text.TextUtils;
import android.view.View;


/**
 * Created by gregmcgowan on 13/08/15.
 */
public class ReplyToCommentListener implements View.OnClickListener {

    private BoardPostComment comment;

    private BoardPost boardPost;


    public ReplyToCommentListener(
            BoardPost boardPost,
            BoardPostComment boardPostComment) {
        this.comment = boardPostComment;
        this.boardPost = boardPost;
    }

    @Override
    public void onClick(View v) {
        if (comment != null) {
            String replyToAuthor = comment.getAuthorUsername();
            if (TextUtils.isEmpty(replyToAuthor)) {
                replyToAuthor = boardPost.getAuthorUsername();
            }
            String replyToId = comment.getId();
            String postId = comment.getBoardPost().getId();
            BoardListType boardListType = comment.getBoardPost().getBoardListType();


    /*
             * String replyText = comment.getTitle(); if
             * (TextUtils.isEmpty(replyText)) { replyText =
             * comment.getContent(); }
             * replyDetails.putString(DisBoardsConstants.REPLY_TO_TEXT,
             * replyText);
             */
            // TextUtils.ellipsize(text, p, avail, where)

//            PostReplyFragment.newInstance(replyToAuthor,replyToId,postId,boardType)
//                    .show(fragmentManagerReference.get(),
//                            "REPLY_FRAGMENT_DIALOG");

        }
    }

}
