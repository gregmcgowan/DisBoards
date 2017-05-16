package com.drownedinsound.ui.post;

import com.drownedinsound.Comment;
import com.drownedinsound.R;
import com.drownedinsound.ReplyComment;
import com.drownedinsound.utils.UiUtils;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;

class BoardPostCommentHolder extends BaseBoardPostHolder<ReplyComment> {

    @BindView(R.id.board_post_comment_content_title)
    TextView commentTitleTextView;

    @BindView(R.id.board_post_comment_content)
    TextView commentContentTextView;

    @BindView(R.id.board_post_comment_author_text_view)
    TextView commentAuthorTextView;

    @BindView(R.id.board_post_comment_date_time_text_view)
    TextView commentDateTimeTextView;

    @BindView(R.id.board_post_comment_this_view)
    TextView commentThisSectionTextView;

    @BindView(R.id.board_post_comment_whitespace_section)
    LinearLayout whitespaceLayout;

    @BindView(R.id.board_post_comment_this)
    TextView thisTextView;

    @BindView(R.id.board_post_comment_reply)
    TextView replyTextView;

    @BindView(R.id.board_post_comment_content_section)
    LinearLayout commentSection;

    BoardPostCommentHolder(View itemView) {
        super(itemView);
    }

    @Override
    void bind(ReplyComment replyComment) {
        Comment comment = replyComment.getComment();

        commentAuthorTextView
                .setText(comment.getAuthor());

        commentTitleTextView.setText(comment.getTitle());

        commentContentTextView.setText(comment.getContent());

        String usersWhoThised = replyComment.getUsersWhoHaveThisdThis();
        if (!TextUtils.isEmpty(usersWhoThised)) {
            commentThisSectionTextView
                    .setText(usersWhoThised);
            commentThisSectionTextView
                    .setVisibility(View.VISIBLE);
        } else {
            commentThisSectionTextView
                    .setVisibility(View.GONE);
        }

        commentDateTimeTextView
                .setText(comment.getDateAndTime());

        int level = replyComment.getCommentLevel();
        whitespaceLayout.removeAllViews();

        int commentLevelIndentPx = UiUtils.convertDpToPixels(
                itemView.getContext().getResources(), 4);

        for (int i = 0; i < level; i++) {
            View spacer = new View(itemView.getContext());
            spacer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                    commentLevelIndentPx, LinearLayout.LayoutParams.MATCH_PARENT));
            whitespaceLayout.addView(spacer,
                    i);
        }

        itemView.setOnClickListener(
                new AllCommentClickListener(
                        (ViewGroup) itemView,
                        thisTextView,
                        replyTextView));
        //TODO
//        thisTextView.setOnClickListener(
//                new ThisACommentClickListener(comment, thisACommentActionListener));
//
//        replyTextView.setOnClickListener(
//                new ReplyToCommentClickListener(comment, replyToCommentActionListener)
//        );
//
//        boolean actionSectionVisible = comment
//                .isActionSectionVisible();
//        if (actionSectionVisible) {
//            thisTextView
//                    .setVisibility(View.VISIBLE);
//            replyTextView
//                    .setVisibility(View.VISIBLE);
//        } else {
//            thisTextView
//                    .setVisibility(View.GONE);
//            replyTextView
//                    .setVisibility(View.GONE);
//        }
    }

}
