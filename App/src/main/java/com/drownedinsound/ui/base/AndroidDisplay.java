package com.drownedinsound.ui.base;

import com.drownedinsound.R;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.post.PostReplyActivity;

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
    public void showReplyUI(@BoardPostList.BoardPostListType String boardListType,
            String postId, String replyToAuthor, String replyToCommentId) {
        activity.startActivity(PostReplyActivity
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
