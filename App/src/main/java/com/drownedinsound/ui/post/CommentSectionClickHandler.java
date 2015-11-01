package com.drownedinsound.ui.post;

import com.drownedinsound.data.model.BoardPostComment;

import android.view.View;

/**
 * Created by gregmcgowan on 13/08/15.
 */
interface CommentSectionClickHandler {

    void doCommentClickAction(View parentView, BoardPostComment boardPostComment, int position);
}
