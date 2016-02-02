package com.drownedinsound.ui.postList;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.BaseControllerFragment;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;

import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class AddPostFragment extends BaseControllerFragment<BoardPostListController> implements
        AddPostUI {

    @InjectView(R.id.loading_layout)
    DisBoardsLoadingLayout loadingLayout;

    @InjectView(R.id.content_container)
    ViewGroup contentContainer;

    @InjectView(R.id.heading)
    TextView heading;

    @InjectView(R.id.add_content_title)
    EditText newPostTitleEditText;

    @InjectView(R.id.add_content_main)
    EditText newPostContentEditText;

    @Inject
    BoardPostListController boardPostListController;

    private String boardListType;

    public static AddPostFragment newInstance(@BoardPostList.BoardPostListType String boardListType) {
        AddPostFragment newPostFragment = new AddPostFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(DisBoardsConstants.BOARD_TYPE,
                boardListType);
        newPostFragment.setArguments(arguments);
        return newPostFragment;
    }

    public AddPostFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        this.boardListType = arguments.getString(DisBoardsConstants.BOARD_TYPE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_content_layout, container,
                false);
        ButterKnife.inject(this, view);

        loadingLayout.setContentView(contentContainer);

        newPostTitleEditText.addTextChangedListener(new TextWatcher() {
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
        newPostContentEditText.addTextChangedListener(new TextWatcher() {
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

        String title = getString(R.string.new_post_title, boardListType);
        heading.setText(title);

        return view;
    }

    private void checkForContent() {
    }

    protected void doNewPostAction() {
        String title = newPostTitleEditText.getText().toString();
        String content = newPostContentEditText.getText().toString();

        boardPostListController.addNewPost(this, boardListType, title, content);
    }


    @Override
    public void showLoadingProgress(boolean show) {
        if(show) {
            requestToShowLoadingView();
        } else {
            requestToHideLoadingView();
        }
    }

    @Override
    public void hideLoadingView() {
        loadingLayout.hideAnimatedViewAndShowContent();
    }

    @Override
    public void showLoadingView(IBinder hideSoftKeyboardToken) {
        loadingLayout.showAnimatedViewAndHideContent();
    }

    @Override
    public void handleNewPostFailure() {
        Toast.makeText(getActivity(),
                "Failed to create post. Please try again later",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected BoardPostListController getController() {
        return boardPostListController;
    }
}
