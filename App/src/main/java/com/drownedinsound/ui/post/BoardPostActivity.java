package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.AndroidNavigator;
import com.drownedinsound.ui.base.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

public class BoardPostActivity extends BaseActivity {

    @Inject
    protected BoardPostContract.Presenter boardPostPresenter;

    private AndroidNavigator androidNavigator = new AndroidNavigator(this);

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
        boardPostPresenter.onViewCreated();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boardPostPresenter.onViewDestroyed();
        androidNavigator = null;
    }

    @Override
    protected void onSessionComponentCreated(SessionComponent sessionComponent) {
        String postId = getIntent().getStringExtra(DisBoardsConstants.BOARD_POST_ID);
        String boardPostType = getIntent().getStringExtra(DisBoardsConstants.BOARD_TYPE);

        sessionComponent.provideBoardPostBuilder()
                .view(findViewById(R.id.board_post_container))
                .boardPostType(boardPostType)
                .postId(postId)
                .navigator(androidNavigator)
                .build().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.board_post_container;
    }

}
