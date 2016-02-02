package com.drownedinsound.ui.base;

import com.drownedinsound.R;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.post.BoardPostActivity;
import com.drownedinsound.ui.post.AddCommentActivity;
import com.drownedinsound.ui.postList.AddPostActivity;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by gregmcgowan on 17/01/16.
 */
public class AndroidDisplay implements Display {

    public Activity activity;

    public AndroidDisplay(Activity activity){
        this.activity = activity;
    }

    @Override
    public void showNewPostUI(@BoardPostList.BoardPostListType String boardListType) {
        activity.startActivity(AddPostActivity.getIntent(activity, boardListType));
    }

    @Override
    public void showBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId) {
        activity.startActivity(BoardPostActivity.getIntent(activity,boardPostId,boardListType));
    }

    @Override
    public void showReplyUI(@BoardPostList.BoardPostListType String boardListType,
            String postId, String replyToAuthor, String replyToCommentId) {
        activity.startActivity(AddCommentActivity
                .getIntent(activity, boardListType, postId, replyToAuthor, replyToCommentId));
    }

    @Override
    public void showNotLoggedInUI() {
        Toast.makeText(activity,R.string.not_logged_in,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideCurrentScreen() {
        activity.finish();
    }
}
