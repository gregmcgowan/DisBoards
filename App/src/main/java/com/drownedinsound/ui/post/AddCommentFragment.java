package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.BaseControllerFragment;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;

import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gregmcgowan on 14/11/15.
 */
public class AddCommentFragment extends BaseControllerFragment<BoardPostController> implements ReplyToCommentUi {

    @InjectView(R.id.loading_layout)
    DisBoardsLoadingLayout loadingLayout;

    @InjectView(R.id.content_container)
    ViewGroup contentConatiner;

    @InjectView(R.id.heading)
    TextView replyToTextView;

    @InjectView(R.id.add_content_title)
    EditText commentTitleEditView;

    @InjectView(R.id.add_content_main)
    EditText commentContentEditView;

    private String boardPostId;

    private String boardListType;

    private String replyToCommentID;

    private String replyToAuthor;

    @Inject
    BoardPostController boardPostController;

    public static AddCommentFragment newInstance(@BoardPostList.BoardPostListType String boardListType,
            String postId, String replyToAuthor, String replyToCommentId ) {
        Bundle arguments = new Bundle();
        arguments.putString(DisBoardsConstants.REPLY_TO_AUTHOR,
                replyToAuthor);
        arguments.putString(DisBoardsConstants.BOARD_COMMENT_ID,
                replyToCommentId);
        arguments.putString(DisBoardsConstants.BOARD_POST_ID,
                postId);
        arguments.putSerializable(DisBoardsConstants.BOARD_TYPE,
                boardListType);
        AddCommentFragment postReplyFragment = new AddCommentFragment();
        postReplyFragment.setArguments(arguments);

        return postReplyFragment;
    }

    @Override
    protected BoardPostController getController() {
        return boardPostController;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        this.replyToCommentID = arguments.getString(DisBoardsConstants.BOARD_COMMENT_ID);
        this.boardListType = arguments.getString(DisBoardsConstants.BOARD_TYPE);
        this.boardPostId = arguments.getString(DisBoardsConstants.BOARD_POST_ID);
        this.replyToAuthor = arguments.getString(DisBoardsConstants.REPLY_TO_AUTHOR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_content_layout, container, false);

        ButterKnife.inject(this, view);

        loadingLayout.setContentView(contentConatiner);

        if (!TextUtils.isEmpty(replyToAuthor)) {
            replyToTextView.setText(getString(R.string.post_reply_title, replyToAuthor));
            replyToTextView.setVisibility(View.VISIBLE);
        } else {
            replyToTextView.setVisibility(View.GONE);
        }

        return view;
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

    public void doReplyAction() {
        String commentTitle = commentTitleEditView.getText().toString();
        String commentContent = commentContentEditView.getText().toString();

        boardPostController
                .replyToComment(this, boardListType, boardPostId, replyToCommentID, commentTitle,
                        commentContent);
    }

    @Override
    public void handlePostCommentFailure() {
        Toast.makeText(getActivity(),
                "Failed to post comment. Please try again later",
                Toast.LENGTH_SHORT).show();
    }
}
