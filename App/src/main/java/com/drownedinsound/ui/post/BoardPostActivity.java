package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This will contain a board post fragment. A board post will be made of the
 * original post and comments. This activity will not do much apart from load
 * the fragment
 *
 * @author Greg
 */
public class BoardPostActivity extends BaseActivity
        implements BoardPostParentUi {

    private BoardPostFragment boardPostFragment;

    @Inject
    protected BoardPostController boardPostController;

    public static Intent getIntent(Context context, String postID,
            @BoardPostList.BoardPostListType String boardListType) {
        Intent boardPostActivityIntent = new Intent(context, BoardPostActivity.class);

        Bundle parametersBundle = new Bundle();
        parametersBundle.putString(DisBoardsConstants.BOARD_POST_ID,
                postID);
        parametersBundle.putString(DisBoardsConstants.BOARD_TYPE,
                boardListType);
        boardPostActivityIntent.putExtras(parametersBundle);
        return boardPostActivityIntent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            String postID = getIntent().getStringExtra(DisBoardsConstants.BOARD_POST_ID);
            @BoardPostList.BoardPostListType String boardListType  = getIntent()
                    .getStringExtra(DisBoardsConstants.BOARD_TYPE);

            boardPostFragment = BoardPostFragment.newInstance(postID, false, boardListType);
            fragmentManager.beginTransaction()
                    .add(R.id.board_post_fragment_holder, boardPostFragment).commit();
        }

    }

    @Override
    protected void onSessionComponentCreated(SessionComponent sessionComponent) {

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.board_post_container;
    }


    @OnClick(R.id.back_button)
    public void backAction() {
        finish();
    }

    @OnClick(R.id.refresh_board_posts_button)
    public void refreshButtonAction() {
        if (boardPostFragment != null) {
            boardPostFragment.doRefreshAction();
        }
    }

    public void refreshMenu() {
        this.supportInvalidateOptionsMenu();
    }

}
