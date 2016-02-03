package com.drownedinsound.ui.post;


import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostComment;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.ui.base.BaseControllerFragment;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;
import com.drownedinsound.ui.controls.AutoScrollListView;
import com.drownedinsound.utils.UiUtils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Represents a board fragment. This will consist of the board post and all the
 * comments made against that post.
 *
 * @author Greg
 */
public class BoardPostFragment extends BaseControllerFragment<BoardPostController>
        implements BoardPostUI {

    private static final int SHOW_GO_TO_LAST_COMMENT_TIMEOUT = 5000;

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "BoardPost";

    private static final String DUAL_PANE_MODE = "DUAL_PANE_MODE";

    private BoardPost boardPost;

    private BoardPostAdapter adapter;

    private String boardPostId;

    private String boardListType;

    private boolean inDualPaneMode;

    private boolean animatingScrollToLastCommentView;

    protected
    @InjectView(R.id.loading_layout)
    DisBoardsLoadingLayout loadingLayout;

    protected
    @InjectView(R.id.board_post_connection_error_text_view)
    TextView connectionErrorTextView;

    protected
    @InjectView(R.id.board_post_comment_list)
    AutoScrollListView commentsList;

    protected
    @InjectView(R.id.board_post_move_to_first_or_last_comment_layout)
    RelativeLayout moveToFirstOrLastCommentLayout;

    protected
    @InjectView(R.id.board_post_move_to_last_comment_text_view)
    TextView scrollToLastCommentTextView;

    protected
    @InjectView(R.id.floating_reply_button)
    FloatingActionButton floatingReplyButton;

    protected
    @Inject
    BoardPostController boardPostController;

    public static BoardPostFragment newInstance(String boardPostID, boolean inDualPaneMode, @BoardPostList.BoardPostListType String boardListType) {
        BoardPostFragment boardPostFragment = new BoardPostFragment();
        Bundle arguments = new Bundle();
        arguments.putString(DisBoardsConstants.BOARD_POST_ID, boardPostID);
        arguments.putBoolean(DisBoardsConstants.DUAL_PANE_MODE, inDualPaneMode);
        arguments.putString(DisBoardsConstants.BOARD_TYPE, boardListType);
        boardPostFragment.setArguments(arguments);
        return boardPostFragment;
    }

    public BoardPostFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // In dual mode the fragment will be recreated but will not be used
        // anywhere
        if (container == null) {
            return null;
        }
        View rootView = inflater.inflate(R.layout.board_post_layout, container, false);
        ButterKnife.inject(this, rootView);

        loadingLayout.setContentView(commentsList);

        adapter = new BoardPostAdapter(getActivity());
        adapter.setThisACommentActionListener(new ThisACommentActionListener() {
            @Override
            public void doThisACommentAction(BoardPostComment boardPostComment) {
                thisAComment(boardPostComment);
            }
        });
        adapter.setReplyToCommentActionListener(new ReplyToCommentActionListener() {
            @Override
            public void doReplyToCommentAction(BoardPostComment boardPostComment) {
                displayReplyDialog(boardPostComment);
            }
        });
        commentsList.setAdapter(adapter);
        moveToFirstOrLastCommentLayout
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scrollToLatestComment();
                        displayScrollToHiddenCommentOption(false);
                    }

                });

        return rootView;
    }

    private void displayReplyDialog(BoardPostComment boardPostComment) {
        String replyToAuthor = boardPostComment.getAuthorUsername();
        String commentId = boardPostComment.getCommentID();
        boardPostController.showReplyUI(boardListType, boardPostId, replyToAuthor, commentId);
    }

    private void thisAComment(BoardPostComment boardPostComment) {
        String commentID = boardPostComment.getCommentID();
        boardPostController.thisAComment(this, boardListType, boardPostId, commentID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialise(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (loadingLayout != null) {
            loadingLayout.stopAnimation();
        }
    }

    private void initialise(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            boardPostId = (String) getArguments().get(
                    DisBoardsConstants.BOARD_POST_ID);
            inDualPaneMode = getArguments().getBoolean(
                    DisBoardsConstants.DUAL_PANE_MODE);
            boardListType =
                    getArguments().getString(DisBoardsConstants.BOARD_TYPE);
        } else {
            boardPostId = savedInstanceState
                    .getString(DisBoardsConstants.BOARD_POST_ID);
            boardPost = savedInstanceState
                    .getParcelable(DisBoardsConstants.BOARD_POST_KEY);
            inDualPaneMode = savedInstanceState
                    .getBoolean(DisBoardsConstants.DUAL_PANE_MODE);
            boardListType =  savedInstanceState
                    .getString(DisBoardsConstants.BOARD_TYPE);
            if (boardPost != null) {
                adapter.setComments(new ArrayList<>(boardPost.getComments()));
            }
        }
    }

    @Override
    protected BoardPostController getController() {
        return boardPostController;
    }

    @Override
    public void showLoadingProgress(boolean show) {
        if (show) {
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
    public void showBoardPost(BoardPost boardPost, int commentIDToShow) {
        this.boardPost = boardPost;
        connectionErrorTextView.setVisibility(View.GONE);
        adapter.setBoardPost(boardPost);

        BoardPostComment initialPost = new BoardPostComment();
        initialPost.setBoardPostID(boardPostId);
        initialPost
                .setAuthorUsername(boardPost
                        .getAuthorUsername());
        initialPost
                .setDateAndTime(boardPost
                        .getDateOfPost());
        initialPost.setContent(boardPost.getContent());
        initialPost.setTitle(boardPost
                .getTitle());
        initialPost.setBoardPost(boardPost);

        ArrayList<BoardPostComment> commentList = new ArrayList<>();
        commentList.add(initialPost);
        commentList.addAll(boardPost.getComments());

        adapter.setComments(commentList);
    }

    @Override
    public void showErrorView() {
        loadingLayout.setContentShownListener(new DisBoardsLoadingLayout.ContentShownListener() {
            @Override
            public void onContentShown() {
                loadingLayout.setContentShownListener(null);
                connectionErrorTextView.setVisibility(View.VISIBLE);
            }
        });
        loadingLayout.hideAnimatedViewAndShowContent();
    }

    public String getBoardPostId() {
        String boardPostId = null;
        if (boardPost != null) {
            boardPostId = boardPost.getBoardPostID();
        }
        return boardPostId;
    }

    @Override
    public void onResume() {
        super.onResume();
        boardPostController.loadBoardPost(this, boardListType, boardPostId, false);
    }

    @Override
    public void showCachedPopup() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DisBoardsConstants.BOARD_POST_ID, boardPostId);
        outState.putBoolean(DisBoardsConstants.DUAL_PANE_MODE, inDualPaneMode);
        outState.putSerializable(DisBoardsConstants.BOARD_TYPE, boardListType);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_post_refresh:
                doRefreshAction();
                return true;
            case R.id.menu_post_reply:
                doReplyAction();
                return true;
            case R.id.menu_favourite:
                doFavouriteAction();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doFavouriteAction() {
        //TODO
    }

    @OnClick(R.id.floating_reply_button)
    public void doReplyAction() {
        String replyToAuthor = boardPost.getAuthorUsername();
        boardPostController.showReplyUI(boardListType, boardPostId, replyToAuthor, null);
    }


    public void doRefreshAction() {
        boardPostController.loadBoardPost(this, boardListType, boardPostId, true);
    }


    public void scrollToLatestComment() {
        String latestCommentId = boardPost.getLatestCommentID();
        if (!TextUtils.isEmpty(latestCommentId)) {
            int index = adapter.getIndexOfCommentId(latestCommentId);
            if (index > 0) {
                adapter.getItem(index).setDoHighlightedAnimation(true);
                commentsList.requestPositionToScreen(index, true);
            }
        }

    }

    private void displayScrollToHiddenCommentOption(final boolean display) {
        boolean alreadyHidden = moveToFirstOrLastCommentLayout.getVisibility() != View.VISIBLE;
        if (!display && alreadyHidden) {
            return;
        }
        if (!animatingScrollToLastCommentView) {
            animatingScrollToLastCommentView = true;
            float[] offset = new float[3];
            if (display) {
                offset[0] = 100f;
                offset[1] = 50f;
                offset[2] = 0f;
            } else {
                offset[0] = 0f;
                offset[1] = 50f;
                offset[2] = 100f;
            }
            moveToFirstOrLastCommentLayout.setVisibility(View.VISIBLE);
            ObjectAnimator animateScrollToLastCommentOption = ObjectAnimator
                    .ofFloat(moveToFirstOrLastCommentLayout, "translationY",
                            offset);
            animateScrollToLastCommentOption.setDuration(1000);
            animateScrollToLastCommentOption
                    .addListener(new Animator.AnimatorListener() {

                        public void onAnimationStart(Animator animation) {
                        }

                        public void onAnimationEnd(Animator animation) {
                            if (display) {
                                moveToFirstOrLastCommentLayout
                                        .setVisibility(View.VISIBLE);
                            } else {
                                moveToFirstOrLastCommentLayout
                                        .setVisibility(View.GONE);
                            }
                            animatingScrollToLastCommentView = false;
                        }

                        public void onAnimationCancel(Animator animation) {
                        }

                        public void onAnimationRepeat(Animator animation) {
                        }

                    });
            animateScrollToLastCommentOption.start();
        }
    }

    private void updateFavouriteMenuItemStatus() {
        if (!UiUtils.isDualPaneMode(getActivity())) {
            ((BoardPostActivity) (getActivity())).refreshMenu();
        }
    }

    private void handleLinkClicked(String url) {
        Log.d(TAG, "URL CLICKED " + url);
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith(UrlConstants.BOARD_BASE_URL)) {
                handleBoardPostClicked(url);
            }
        }
    }

    private void handleBoardPostClicked(String url) {
        if (!TextUtils.isEmpty(url)) {
            String postId = null;
            int indexOfLastForwardSlash = url.lastIndexOf("/");
            if (indexOfLastForwardSlash != -1) {
                postId = url.substring(indexOfLastForwardSlash + 1);
            }
                //TODO remove comment if there is one
            Log.d(TAG, "PostID = " + postId);
            @BoardPostList.BoardPostListType String boardListType = UrlConstants.getBoardType(url);
            startActivity(BoardPostActivity.getIntent(getActivity(), postId, boardListType));
        }
    }


    @Override
    public void showThisACommentFailed() {
        Toast.makeText(getActivity(),
                "Failed to this this. You could try again", Toast.LENGTH_SHORT)
                .show();
    }
}
