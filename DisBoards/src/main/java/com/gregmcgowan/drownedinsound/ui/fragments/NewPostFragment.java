package com.gregmcgowan.drownedinsound.ui.fragments;

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

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.events.SentNewPostEvent;
import com.gregmcgowan.drownedinsound.events.SentNewPostEvent.SentNewPostState;
import com.gregmcgowan.drownedinsound.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.network.service.DisWebServiceConstants;

import de.greenrobot.event.EventBus;

public class NewPostFragment extends DisBoardsDialogFragment {

    private ImageButton newPostButton;
    private EditText postTitleEditText;
    private EditText postContentEditText;

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

        board = getArguments().getParcelable(DisBoardsConstants.BOARD);

        postTitleEditText = (EditText) view
            .findViewById(R.id.new_board_post_title);
        postContentEditText = (EditText) view
            .findViewById(R.id.new_board_post_content);
        newPostButton = (ImageButton) view
            .findViewById(R.id.new_board_post_add_button);

        newPostButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doNewPostAction();
            }
        });

        return view;
    }

    protected void doNewPostAction() {
        // TODO check there is something entered

        String content = postContentEditText.getText().toString();
        String title = postTitleEditText.getText().toString();

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

        EventBus.getDefault().post(new SentNewPostEvent(SentNewPostState.SENT));

        dismiss();
    }

}
