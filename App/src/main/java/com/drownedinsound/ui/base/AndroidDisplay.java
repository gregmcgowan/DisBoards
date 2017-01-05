package com.drownedinsound.ui.base;

import com.drownedinsound.R;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.post.BoardPostActivity;
import com.drownedinsound.ui.postList.BoardPostListParentActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by gregmcgowan on 17/01/16.
 */
public class AndroidDisplay implements Display {

    private final Activity activity;

    AndroidDisplay(Activity activity){
        this.activity = activity;
    }


    @Override
    public void showBoardPost(@BoardPostList.BoardPostListType String boardListType,
            String boardPostId) {
        activity.startActivity(BoardPostActivity.getIntent(activity,boardPostId,boardListType));
    }

    @Override
    public void showNotLoggedInUI() {
        if (activity != null) {
            String notLoggedInMessage = activity.getString(R.string.not_logged_in);
            showSnackbar(notLoggedInMessage);
        }
    }

    private void showSnackbar(String message) {
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
            snackbar.show();
        }
    }


    @Override
    public void showFeatureExpiredUI() {
        showSnackbar(activity.getString(R.string.cannot_use_this_feature_now));
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
