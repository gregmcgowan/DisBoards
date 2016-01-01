package com.drownedinsound.ui.post;

import com.drownedinsound.data.generatered.BoardPostComment;

import android.view.View;

/**
 * Created by gregmcgowan on 13/08/15.
 */
public class ThisACommentClickListener implements View.OnClickListener{

    private BoardPostComment boardPostComment;
    private ThisACommentActionListener thisACommentActionListener;

    public ThisACommentClickListener(BoardPostComment boardPostComment,
            ThisACommentActionListener thisACommentActionListener) {
        this.boardPostComment = boardPostComment;
        this.thisACommentActionListener = thisACommentActionListener;
    }

    @Override
    public void onClick(View v) {
        if(thisACommentActionListener !=  null) {
            thisACommentActionListener.doThisACommentAction(boardPostComment);
        }
    }
}
