package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.BaseControllerActivity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCommentActivity extends BaseControllerActivity<BoardPostController> {

    @Inject
    BoardPostController boardPostController;

    public static Intent getIntent(Context context,
            @BoardPostList.BoardPostListType String boardListType,
            String postId, String replyToAuthor, String replyToCommentId) {

        Intent intent = new Intent(context, AddCommentActivity.class);

        intent.putExtra(DisBoardsConstants.REPLY_TO_AUTHOR,
                replyToAuthor);
        intent.putExtra(DisBoardsConstants.BOARD_COMMENT_ID,
                replyToCommentId);
        intent.putExtra(DisBoardsConstants.BOARD_POST_ID,
                postId);
        intent.putExtra(DisBoardsConstants.BOARD_TYPE,
                boardListType);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        Intent intent = getIntent();

        String replyToCommentID = intent.getStringExtra(DisBoardsConstants.BOARD_COMMENT_ID);
        @BoardPostList.BoardPostListType String boardListType = intent
                .getStringExtra(DisBoardsConstants.BOARD_TYPE);
        String boardPostId = intent.getStringExtra(DisBoardsConstants.BOARD_POST_ID);
        String replyToAuthor = intent.getStringExtra(DisBoardsConstants.REPLY_TO_AUTHOR);

        AddCommentFragment postReplyFragment = AddCommentFragment
                .newInstance(boardListType, boardPostId,
                        replyToAuthor, replyToCommentID);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container, postReplyFragment, "POST_REPLY_FRAGMENT");
        fragmentTransaction.commit();
    }

    @Override
    protected void onSessionComponentCreated(SessionComponent sessionComponent) {
        sessionComponent.boardPostComponent().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.add_content_container_layout;
    }


    @OnClick(R.id.send_button)
    public void handleSendButtonPressed() {
        AddCommentFragment postReplyFragment =
                (AddCommentFragment) getFragmentManager().findFragmentByTag("POST_REPLY_FRAGMENT");
        if (postReplyFragment != null) {
            postReplyFragment.doReplyAction();
        }
    }

    @OnClick(R.id.back_button)
    protected void doBackAction() {
        finish();
    }


    @Override
    protected BoardPostController getController() {
        return boardPostController;
    }
}
