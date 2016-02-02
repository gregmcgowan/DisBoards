package com.drownedinsound.ui.postList;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.BaseControllerActivity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gregmcgowan on 31/01/16.
 */
public class AddPostActivity extends BaseControllerActivity<BoardPostListController> {


    public static Intent getIntent(Context context,
            @BoardPostList.BoardPostListType String boardListType) {
        Intent intent = new Intent(context, AddPostActivity.class);
        intent.putExtra(DisBoardsConstants.BOARD_TYPE, boardListType);
        return intent;
    }

    @Inject
    BoardPostListController boardPostListController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        Intent intent = getIntent();

        @BoardPostList.BoardPostListType String boardListType = intent
                .getStringExtra(DisBoardsConstants.BOARD_TYPE);

        AddPostFragment newPostFragment = AddPostFragment.newInstance(boardListType);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, newPostFragment, "NEW_POST_FRAGMENT");
        fragmentTransaction.commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.add_content_container_layout;
    }


    @OnClick(R.id.send_button)
    public void handleSendButtonPressed() {
        AddPostFragment newPostFragment =
                (AddPostFragment) getFragmentManager().findFragmentByTag("NEW_POST_FRAGMENT");
        if (newPostFragment != null) {
            newPostFragment.doNewPostAction();
        }
    }

    @OnClick(R.id.back_button)
    protected void doBackAction() {
        finish();
    }

    @Override
    protected BoardPostListController getController() {
        return boardPostListController;
    }
}
