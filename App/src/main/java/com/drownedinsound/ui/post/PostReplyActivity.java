package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.BaseActivity;
import com.drownedinsound.ui.base.BaseControllerActivity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class PostReplyActivity extends BaseControllerActivity<BoardPostController> {


    public static Intent getIntent(Context context, @BoardPostList.BoardPostListType String boardListType,
            String postId, String replyToAuthor, String replyToCommentId) {

        Intent intent = new Intent(context,PostReplyActivity.class);

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

    @Inject
    BoardPostController boardPostController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        Intent intent = getIntent();

        String replyToCommentID = intent.getStringExtra(DisBoardsConstants.BOARD_COMMENT_ID);
        @BoardPostList.BoardPostListType String boardListType = intent.getStringExtra(DisBoardsConstants.BOARD_TYPE);
        String boardPostId = intent.getStringExtra(DisBoardsConstants.BOARD_POST_ID);
        String replyToAuthor = intent.getStringExtra(DisBoardsConstants.REPLY_TO_AUTHOR);

        PostReplyFragment postReplyFragment = PostReplyFragment.newInstance(boardListType,boardPostId,
                replyToAuthor,replyToCommentID);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container,postReplyFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.board_post_reply_container;
    }

    @Override
    protected BoardPostController getController() {
        return boardPostController;
    }
}
