package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.BaseControllerActivity;
import com.drownedinsound.utils.UiUtils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
public class BoardPostActivity extends BaseControllerActivity<BoardPostController>
        implements BoardPostParentUi {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "BoardPostActivity";

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

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
        int screenWidthDp = UiUtils.convertPixelsToDp(getResources(), screenWidthPixels);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE &&
                screenWidthDp >= UiUtils.MIN_WIDTH_DP_FOR_DUAL_MODE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

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
    protected BoardPostController getController() {
        return boardPostController;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.board_post_container;
    }


    @OnClick(R.id.back_button)
    public void backAction() {
        if (!UiUtils.isDualPaneMode(this)) {
            finish();
        }
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
