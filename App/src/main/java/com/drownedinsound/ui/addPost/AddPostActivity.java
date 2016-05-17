package com.drownedinsound.ui.addPost;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gregmcgowan on 31/01/16.
 */
public class AddPostActivity extends BaseActivity {


    public static Intent getIntent(Context context,
            @BoardPostList.BoardPostListType String boardListType) {
        Intent intent = new Intent(context, AddPostActivity.class);
        intent.putExtra(DisBoardsConstants.BOARD_TYPE, boardListType);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        Intent intent = getIntent();

        @BoardPostList.BoardPostListType String boardListType = intent
                .getStringExtra(DisBoardsConstants.BOARD_TYPE);

        AddPostFragment newPostFragment = AddPostFragment.newInstance(boardListType);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, newPostFragment, "NEW_POST_FRAGMENT");
        fragmentTransaction.commit();
    }

    @Override
    protected void onSessionComponentCreated(SessionComponent sessionComponent) {
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.add_content_container_layout;
    }


    @OnClick(R.id.send_button)
    public void handleSendButtonPressed() {
        AddPostFragment newPostFragment =
                (AddPostFragment) fragmentManager.findFragmentByTag("NEW_POST_FRAGMENT");
        if (newPostFragment != null) {
            newPostFragment.doNewPostAction();
        }
    }

    @OnClick(R.id.back_button)
    protected void doBackAction() {
        finish();
    }

}
