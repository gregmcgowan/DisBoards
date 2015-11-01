package com.drownedinsound.ui.post;

import android.view.View;

/**
 * Created by gregmcgowan on 13/08/15.
 */
class CommentSectionClickListener implements View.OnClickListener {

    private int listPosition;

    private CommentSectionClickHandler commentClickHandler;

    CommentSectionClickListener(int listPosition,
            CommentSectionClickHandler commentClickHandler) {
        this.listPosition = listPosition;
        this.commentClickHandler = commentClickHandler;
    }

    @Override
    public void onClick(View v) {
        if (commentClickHandler != null) {
            //commentClickHandler.doCommentClickAction(v, listPosition);
        }
    }

}
