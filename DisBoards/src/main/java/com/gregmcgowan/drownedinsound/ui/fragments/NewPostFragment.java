package com.gregmcgowan.drownedinsound.ui.fragments;

import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.data.model.DraftBoardPost;
import com.gregmcgowan.drownedinsound.data.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.data.network.service.DisWebServiceConstants;
import com.gregmcgowan.drownedinsound.events.SentNewPostEvent;
import com.gregmcgowan.drownedinsound.events.SentNewPostEvent.SentNewPostState;
import com.gregmcgowan.drownedinsound.ui.view.RobotoLightTextView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import de.greenrobot.event.EventBus;

public class NewPostFragment extends DisBoardsDialogFragment {

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

        DraftBoardPost existingDraftPost = DatabaseHelper.getInstance(getSherlockActivity())
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

        heading.setText("New " + board.getDisplayName() + " Post");

        return view;
    }

    private void clearAction() {
        String content = postContentEditText.getText().toString();
        String title = postTitleEditText.getText().toString();
        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(title)) {
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
            Intent disWebService = new Intent(getSherlockActivity(),
                    DisWebService.class);
            Bundle parametersBundle = new Bundle();
            parametersBundle.putParcelable(DisBoardsConstants.BOARD, board);
            parametersBundle
                    .putString(DisBoardsConstants.NEW_POST_CONTENT, content);
            parametersBundle.putString(DisBoardsConstants.NEW_POST_TITLE, title);
            parametersBundle.putInt(DisWebServiceConstants.SERVICE_REQUESTED_ID,
                    DisWebServiceConstants.NEW_POST);
            disWebService.putExtras(parametersBundle);

            getSherlockActivity().startService(disWebService);
            eventBus.post(new SentNewPostEvent(SentNewPostState.SENT));
            dismiss();
        } else {
            Toast.makeText(getSherlockActivity(), "You must at least add a title",
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
        DatabaseHelper.getInstance(getSherlockActivity()).setDraftBoardPost(draftBoardPost);
    }
}
