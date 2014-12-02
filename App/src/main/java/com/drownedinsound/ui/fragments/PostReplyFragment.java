package com.drownedinsound.ui.fragments;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.events.BoardPostCommentSentEvent;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class PostReplyFragment extends DisBoardsDialogFragment {

    private TextView replyToTextView;

    private ImageButton replyButton;

    // private TextView originalCommentTextView;
    private EditText commentTitleEditView;

    private EditText commentContentEditView;

    private String boardPostId;

    private BoardType boardType;

    private String replyToCommentID;

    public static PostReplyFragment newInstance(Bundle passedInData) {
        PostReplyFragment postReplyFragment = new PostReplyFragment();
        postReplyFragment.setArguments(passedInData);
        return postReplyFragment;
    }

    public PostReplyFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_post_reply, container,
                false);

        commentTitleEditView = (EditText) view
                .findViewById(R.id.board_post_reply_subject);
        commentContentEditView = (EditText) view
                .findViewById(R.id.board_post_reply_reply);
        replyToTextView = (TextView) view
                .findViewById(R.id.board_post_reply_to_author);

        replyButton = (ImageButton) view
                .findViewById(R.id.board_post_reply_button);
        replyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doReplyAction();
            }
        });
    /*
         * originalCommentTextView = (TextView) view
	 * .findViewById(R.id.board_post_reply_original_comment);
	 */

        String originalComment = getArguments().getString(
                DisBoardsConstants.REPLY_TO_TEXT);
        String replyToAuthor = getArguments().getString(
                DisBoardsConstants.REPLY_TO_AUTHOR);
        String replyToText = "In reply to " + replyToAuthor;

        // originalCommentTextView.setText(Html.fromHtml(originalComment));
        replyToTextView.setText(replyToText);

        this.boardPostId = getArguments().getString(
                DisBoardsConstants.BOARD_POST_ID);
        this.boardType = (BoardType) getArguments().getSerializable(
                DisBoardsConstants.BOARD_TYPE);
        this.replyToCommentID = getArguments().getString(
                DisBoardsConstants.BOARD_COMMENT_ID);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        commentTitleEditView.requestFocus();
    }

    private void doReplyAction() {
        // TODO check to see if data has been entered
        disApiClient.postComment(boardPostId,
                replyToCommentID,
                commentTitleEditView.getText().toString(),
                commentContentEditView.getText().toString(),boardType);

        dismiss();
        eventBus.post(new BoardPostCommentSentEvent());
    }

}
