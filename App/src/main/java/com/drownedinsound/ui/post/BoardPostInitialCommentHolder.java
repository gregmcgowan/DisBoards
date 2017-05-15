package com.drownedinsound.ui.post;


import com.drownedinsound.CommentInfo;
import com.drownedinsound.InitialComment;
import com.drownedinsound.R;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;

class BoardPostInitialCommentHolder extends BaseBoardPostHolder<InitialComment> {

    @BindView(R.id.board_post_initial_comment_title)
    TextView commentTitleTextView;

    @BindView(R.id.board_post_initial_comment_text)
    TextView commentContentTextView;

    @BindView(R.id.board_post_initial_comment_author_subheading)
    TextView commentAuthorTextView;

    @BindView(R.id.board_post_initial_comment_date_time_subheading)
    TextView commentDateTimeTextView;

    @BindView(R.id.board_post_initial_comment_replies_subheading)
    TextView noOfCommentsTextView;

    BoardPostInitialCommentHolder(View itemView) {
        super(itemView);
    }

    @Override
    void bind(InitialComment item) {
        CommentInfo commentInfo = item.getCommentInfo();
        commentAuthorTextView.setText(commentInfo.getAuthor());
        commentTitleTextView.setText(commentInfo.getTitle());
        commentContentTextView.setText(commentInfo.getContent());
        commentDateTimeTextView.setText(commentInfo.getDateAndTime());
        noOfCommentsTextView.setText(item.getNumberOfRepliesText());
    }
}
