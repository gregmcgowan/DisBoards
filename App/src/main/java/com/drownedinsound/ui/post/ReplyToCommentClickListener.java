package com.drownedinsound.ui.post;

import com.drownedinsound.data.generatered.BoardPostComment;

import android.view.View;

/**
 * Created by gregmcgowan on 08/11/15.
 */
public class ReplyToCommentClickListener implements View.OnClickListener{

    private ReplyToCommentActionListener replyToCommentActionListener;

    private BoardPostComment boardPostComment;

    public ReplyToCommentClickListener(
            BoardPostComment boardPostComment,
            ReplyToCommentActionListener replyToCommentActionListener) {
        this.replyToCommentActionListener = replyToCommentActionListener;
        this.boardPostComment = boardPostComment;
    }

    @Override
    public void onClick(View v) {
        if(replyToCommentActionListener != null) {
            replyToCommentActionListener.doReplyToCommentAction(boardPostComment);
        }
    }
}
