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

    private SessionComponent sessionComponent;

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

        String postId = getIntent().getStringExtra(DisBoardsConstants.BOARD_POST_ID);
        String boardPostType = getIntent().getStringExtra(DisBoardsConstants.BOARD_TYPE);

        sessionComponent.boardPostComponent(
                new BoardPostModule(androidNavigator,
                        new BoardPostView(findViewById(R.id.board_post_container),
                                new BoardPostTypeFactory()),
                        postId, boardPostType)).inject(this);

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
        this.sessionComponent = sessionComponent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.board_post_container;
    }

}
