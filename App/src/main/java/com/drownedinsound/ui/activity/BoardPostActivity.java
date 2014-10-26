package com.drownedinsound.ui.activity;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.ui.fragments.BoardPostFragment;
import com.drownedinsound.utils.UiUtils;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * This will contain a board post fragment. A board post will be made of the
 * original post and comments. This activity will not do much apart from load
 * the fragment
 *
 * @author Greg
 */
public class BoardPostActivity extends DisBoardsActivity {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "BoardPostActivity";

    private BoardPostFragment boardPostFragment;

    public static Intent getIntent(Context context, String postUrl, String postID,
            BoardType boardType) {
        Intent boardPostActivityIntent = new Intent(context, BoardPostActivity.class);

        Bundle parametersBundle = new Bundle();
        parametersBundle.putString(DisBoardsConstants.BOARD_POST_URL,
                postUrl);
        parametersBundle.putString(DisBoardsConstants.BOARD_POST_ID,
                postID);
        parametersBundle.putSerializable(DisBoardsConstants.BOARD_TYPE,
                boardType);
        boardPostActivityIntent.putExtras(parametersBundle);
        return boardPostActivityIntent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
        int screenWidthDp = UiUtils.convertPixelsToDp(getResources(), screenWidthPixels);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE &&
                screenWidthDp >= UiUtils.MIN_WIDTH_DP_FOR_DUAL_MODE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            String postUrl = getIntent().getStringExtra(DisBoardsConstants.BOARD_POST_URL);
            String postID = getIntent().getStringExtra(DisBoardsConstants.BOARD_POST_ID);
            BoardType boardType = (BoardType) getIntent()
                    .getSerializableExtra(DisBoardsConstants.BOARD_TYPE);

            boardPostFragment = BoardPostFragment.newInstance(postUrl, postID, false, boardType);
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, boardPostFragment).commit();
        }

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.board_post_container;
    }

    public void removeBoardPostFragment() {
        finish();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void refreshMenu() {
        this.supportInvalidateOptionsMenu();
    }

}
