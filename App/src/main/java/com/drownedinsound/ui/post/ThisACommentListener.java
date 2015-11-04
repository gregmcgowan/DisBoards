package com.drownedinsound.ui.post;

import com.drownedinsound.data.model.BoardPostComment;
import com.drownedinsound.data.model.BoardType;

import android.view.View;

import javax.inject.Inject;

/**
 * Created by gregmcgowan on 13/08/15.
 */
public class ThisACommentListener {

    private String postUrl;

    private BoardType boardType;

    private String postID;

    @Inject
    protected BoardPostController boardPostController;

    public ThisACommentListener(String postUrl, BoardType boardType,
            String postID) {
        this.postUrl = postUrl;
        this.postID = postID;
        this.boardType = boardType;
    }

    public void doCommentClickAction(View parentView, BoardPostComment boardPostComment,
            int position) {
        String commentID = boardPostComment.getId();
        // boardPostController.thisAComment(postUrl,boardType,postID,commentID);
    }
}
