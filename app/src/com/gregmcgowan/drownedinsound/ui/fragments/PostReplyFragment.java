package com.gregmcgowan.drownedinsound.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;

public class PostReplyFragment extends DisBoardsDialogFragment {

    private TextView replyToTextView;
  //  private TextView originalCommentTextView;
    private EditText subjectEditView;
    private EditText replyEditView;
    
    private String replyToId;
    
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
	// TODO set some stuff
	dialog.getWindow().setSoftInputMode(
		WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.board_post_reply, container,
		false);

	subjectEditView = (EditText) view
		.findViewById(R.id.board_post_reply_subject);
	replyEditView = (EditText) view
		.findViewById(R.id.board_post_reply_reply);
	replyToTextView = (TextView) view
		.findViewById(R.id.board_post_reply_to_author);
/*	originalCommentTextView = (TextView) view
		.findViewById(R.id.board_post_reply_original_comment);*/

	String originalComment = getArguments().getString(
		DisBoardsConstants.REPLY_TO_TEXT);
	String replyToAuthor = getArguments().getString(
		DisBoardsConstants.REPLY_TO_AUTHOR);
	String replyToText = "In reply to " + replyToAuthor;

	//originalCommentTextView.setText(Html.fromHtml(originalComment));
	replyToTextView.setText(replyToText);

	return view;
    }

    @Override
    public void onResume() {
	super.onResume();
	subjectEditView.requestFocus();

    }

}
