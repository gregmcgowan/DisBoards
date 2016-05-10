package com.drownedinsound.ui.base;

import com.drownedinsound.R;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.post.AddCommentActivity;
import com.drownedinsound.ui.post.BoardPostActivity;
import com.drownedinsound.ui.addPost.AddPostActivity;
import com.drownedinsound.ui.postList.BoardPostListParentActivity;
import com.drownedinsound.ui.start.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;
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
        if (activity != null) {
            //TODO improve this. Maybe only have a single activity
            View parentView = activity.findViewById(R.id.parent_layout);
            if (parentView == null) {
                parentView = activity.findViewById(R.id.board_post_container);
            }

            if (parentView != null) {
                Snackbar snackbar = Snackbar
                        .make(parentView, R.string.not_logged_in,
                                Snackbar.LENGTH_LONG);

                snackbar.getView().setBackgroundColor(
                        activity.getResources().getColor(R.color.yellow_1));
                snackbar.setAction(R.string.login, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoginUi();
                    }
                });
                snackbar.show();
            }

        }
    }

    @Override
    public void showLoginUi() {
        if(activity != null) {
            activity.startActivity(LoginActivity.getIntent(activity));
        }
    }

    @Override
    public void hideCurrentScreen() {
        activity.finish();
    }

    @Override
    public void showErrorMessageDialog(int stringID) {
        if(activity != null) {
            Toast.makeText(activity, stringID, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMainScreen() {
        if(activity != null) {
            Intent intent = BoardPostListParentActivity.getIntent(activity);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        }
    }
}
