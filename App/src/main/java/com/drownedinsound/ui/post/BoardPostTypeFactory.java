package com.drownedinsound.ui.post;

import com.drownedinsound.InitialComment;
import com.drownedinsound.R;
import com.drownedinsound.ReplyComment;
import com.drownedinsound.BordPostCommentListTypeFactory;

import org.jetbrains.annotations.NotNull;

import android.view.View;

import javax.inject.Inject;

class BoardPostTypeFactory implements BordPostCommentListTypeFactory {

    @Inject
    BoardPostTypeFactory() {
    }

    @Override
    public int getType(@NotNull InitialComment initialComment) {
        return R.layout.board_post_initial_comment_layout;
    }

    @Override
    public int getType(@NotNull ReplyComment replyComment) {
        return R.layout.board_post_comment_layout;
    }

    @NotNull
    @Override
    public BaseBoardPostHolder<?> createViewHolder(@NotNull View view, int viewType) {
        switch (viewType) {
            case R.layout.board_post_initial_comment_layout:
                return new BoardPostInitialCommentHolder(view);
            case R.layout.board_post_comment_layout:
                return new BoardPostCommentHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }
}
