package com.drownedinsound.ui.post;


import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostComment;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.ui.base.BaseControllerFragment;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;
import com.drownedinsound.ui.controls.ActiveTextView;
import com.drownedinsound.ui.controls.AutoScrollListView;
import com.drownedinsound.utils.UiUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

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

    protected
    @InjectView(R.id.loading_layout)
    DisBoardsLoadingLayout loadingLayout;

    protected
    @InjectView(R.id.board_post_connection_error_text_view)
    TextView connectionErrorTextView;

    protected
    @InjectView(R.id.board_post_comment_list)
    RecyclerView commentsList;

    protected
    LinearLayoutManager commentsListLinearLayoutManager;

    protected
    @InjectView(R.id.floating_reply_button)
    FloatingActionButton floatingReplyButton;

    protected
    @Inject
    BoardPostController boardPostController;


    private DisBoardsLoadingLayout.ContentShownListener onContentShownListener;

    private int firstVisiblePosition;

    private boolean userHasInteractedWithUI;

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
        adapter.setOnLinkClickedListener(new ActiveTextView.OnLinkClickedListener() {
            @Override
            public void onClick(String url) {
                handleLinkClicked(url);
            }
        });

        commentsListLinearLayoutManager = new LinearLayoutManager(commentsList.getContext());
        commentsList.setLayoutManager(commentsListLinearLayoutManager);

        commentsList.setAdapter(adapter);
        commentsList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userHasInteractedWithUI = true;
                return false;
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
        saveListViewPosition();
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
        moveListViewToSavedPosition();
        loadingLayout.setContentShownListener(onContentShownListener);
        loadingLayout.hideAnimatedViewAndShowContent();
    }

    @Override
    public void showLoadingView(IBinder hideSoftKeyboardToken) {
        loadingLayout.showAnimatedViewAndHideContent();
    }

    @Override
    public void showBoardPost(BoardPost boardPost) {
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
                commentsList.setVisibility(View.GONE);
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
            int position = adapter.getIndexOfCommentId(latestCommentId);
            if (position > 0) {
                adapter.getItem(position).setDoHighlightedAnimation(true);
                commentsListLinearLayoutManager.scrollToPosition(position);
            }
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
            } else {
                Intent openBrowser = new Intent(Intent.ACTION_VIEW);
                openBrowser.setData(Uri.parse(url));
                startActivity(openBrowser);
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

    @Override
    public void showGoToLatestCommentOption() {
        if (getView() != null) {
            View parentView = getView().findViewById(R.id.board_post_container);
            if (parentView != null) {

                Snackbar snackbar = Snackbar
                        .make(parentView, R.string.go_to_latest_comment,
                                Snackbar.LENGTH_LONG);

                snackbar.getView().setBackgroundColor(
                        getResources().getColor(R.color.yellow_1));
                snackbar.setAction(R.string.go, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scrollToLatestComment();
                    }
                });
                snackbar.show();
            }
        }
    }

    @Override
    public boolean lastCommentIsVisible() {
        int lastVisiblePosition = commentsListLinearLayoutManager.findLastVisibleItemPosition();
        Timber.d("Last visible position "+lastVisiblePosition + " items "+adapter.getItemCount());
        return lastVisiblePosition == (adapter.getItemCount() - 1);
    }

    @Override
    public void setOnContentShownListener(
            DisBoardsLoadingLayout.ContentShownListener contentShownListener) {
        this.onContentShownListener = contentShownListener;
    }

    @Override
    public boolean userHasInteractedWithUI() {
        return userHasInteractedWithUI;
    }

    private void saveListViewPosition() {
        firstVisiblePosition = commentsListLinearLayoutManager.findFirstVisibleItemPosition();

        if (firstVisiblePosition != AdapterView.INVALID_POSITION && commentsList.getChildCount() > 0) {
            firstVisiblePosition = commentsList.getChildAt(0).getTop();
        }
    }

    protected void moveListViewToSavedPosition() {
        if (firstVisiblePosition != AdapterView.INVALID_POSITION
                && commentsListLinearLayoutManager.findFirstVisibleItemPosition() <= 0) {
            commentsList.post(new Runnable() {
                @Override
                public void run() {
                    commentsListLinearLayoutManager.scrollToPosition(firstVisiblePosition);
                }
            });
        }
    }


}
