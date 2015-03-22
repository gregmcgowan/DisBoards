package com.drownedinsound.ui.summarylist;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.database.DatabaseHelper;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.DraftBoardPost;
import com.drownedinsound.events.SentNewPostEvent;
import com.drownedinsound.events.SentNewPostEvent.SentNewPostState;
import com.drownedinsound.ui.base.BaseDialogFragment;
import com.drownedinsound.ui.controls.RobotoLightTextView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


public class NewPostFragment extends BaseDialogFragment {

    private ImageButton newPostButton;

    private ImageButton clearButton;

    private EditText postTitleEditText;

    private EditText postContentEditText;

    private RobotoLightTextView heading;

    private Board board;

    public static NewPostFragment newInstance(Bundle passedInData) {
        NewPostFragment newPostFragment = new NewPostFragment();
        newPostFragment.setArguments(passedInData);
        return newPostFragment;
    }

    public NewPostFragment() {

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
        View view = inflater.inflate(R.layout.new_board_post_layout, container,
                false);
        postTitleEditText = (EditText) view
                .findViewById(R.id.new_board_post_title);
        postContentEditText = (EditText) view
                .findViewById(R.id.new_board_post_content);
        newPostButton = (ImageButton) view
                .findViewById(R.id.new_board_post_add_button);
        heading = (RobotoLightTextView) view.findViewById(R.id.new_board_post_heading);
        clearButton = (ImageButton) view.findViewById(R.id.new_board_post_abandon);
        board = getArguments().getParcelable(DisBoardsConstants.BOARD);

        DraftBoardPost existingDraftPost = DatabaseHelper.getInstance(getActivity())
                .getDraftBoardPost(board.getBoardType());
        if (existingDraftPost != null) {
            postTitleEditText.setText(existingDraftPost.getTitle());
            postContentEditText.setText(existingDraftPost.getContent());
        }
        newPostButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doNewPostAction();
            }
        });
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAction();
            }
        });
        postContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                    int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s,
                    int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkForContent();
            }
        });
        postTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkForContent();
            }
        });

        heading.setText("New " + board.getDisplayName() + " Post");

        return view;
    }

    private void checkForContent() {
    }

    private void clearAction() {
        String content = postContentEditText.getText().toString();
        String title = postTitleEditText.getText().toString();
        if (!TextUtils.isEmpty(content) || !TextUtils.isEmpty(title)) {
            postContentEditText.setText("");
            postTitleEditText.setText("");
        } else {
            dismiss();
        }

    }

    protected void doNewPostAction() {
        String content = postContentEditText.getText().toString();
        String title = postTitleEditText.getText().toString();
        if (!TextUtils.isEmpty(title)) {
            //TODO make sure the text is not a ridiculous length
            disApiClient.addNewPost(board,title,content);
            eventBus.post(new SentNewPostEvent(SentNewPostState.SENT));
            dismiss();
        } else {
            Toast.makeText(getActivity(), "You must at least add a title",
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        DraftBoardPost draftBoardPost = new DraftBoardPost();
        draftBoardPost.setTitle(postTitleEditText.getText().toString());
        draftBoardPost.setContent(postContentEditText.getText().toString());
        draftBoardPost.setBoardType(board.getBoardType());
        DatabaseHelper.getInstance(getActivity()).setDraftBoardPost(draftBoardPost);
    }
}
