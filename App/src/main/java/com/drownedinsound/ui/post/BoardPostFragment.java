package com.drownedinsound.ui.post;


import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardPostComment;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.database.DatabaseService;
import com.drownedinsound.events.BoardPostCommentSentEvent;
import com.drownedinsound.events.FailedToPostCommentEvent;
import com.drownedinsound.events.FailedToThisThisEvent;
import com.drownedinsound.events.SetBoardPostFavouriteStatusResultEvent;
import com.drownedinsound.events.UserIsNotLoggedInEvent;
import com.drownedinsound.ui.base.BaseControllerFragment;
import com.drownedinsound.ui.controls.AutoScrollListView;
import com.drownedinsound.ui.controls.SvgAnimatePathView;
import com.drownedinsound.utils.SimpleAnimatorListener;
import com.drownedinsound.utils.UiUtils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
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
import java.util.concurrent.atomic.AtomicBoolean;

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

    private BoardPostListAdapter adapter;

    private String boardPostUrl;

    private String boardPostId;

    private BoardType boardType;

    private boolean inDualPaneMode;

    private boolean animatingScrollToLastCommentView;

    protected
    @InjectView(R.id.animated_logo_progress_bar)
    SvgAnimatePathView animatedLogo;

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

    public static BoardPostFragment newInstance(String boardPostUrl,
            String boardPostID,
            boolean inDualPaneMode, BoardType boardType) {
        BoardPostFragment boardPostFragment = new BoardPostFragment();
        Bundle arguments = new Bundle();
        arguments.putString(DisBoardsConstants.BOARD_POST_URL, boardPostUrl);
        arguments.putString(DisBoardsConstants.BOARD_POST_ID, boardPostID);
        arguments.putBoolean(DisBoardsConstants.DUAL_PANE_MODE, inDualPaneMode);
        arguments.putSerializable(DisBoardsConstants.BOARD_TYPE, boardType);
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

        adapter = new BoardPostListAdapter(getActivity());
        adapter.setThisACommentActionListener(new ThisACommentActionListener() {
            @Override
            public void doThisACommentAction(BoardPostComment boardPostComment) {
                thisAComment(boardPostComment);
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
//        floatingReplyButton
//                .attachToListView(commentsList, new FloatingActionButton.FabOnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(AbsListView view, int scrollState) {
//                        super.onScrollStateChanged(view, scrollState);
//                        displayScrollToHiddenCommentOption(false);
//                    }
//                });

        animatedLogo.setSvgResource(R.raw.logo);
        return rootView;
    }

    private void thisAComment(BoardPostComment boardPostComment) {
        String commentID = boardPostComment.getId();
        boardPostController.thisAComment(this,boardPostUrl,boardType,boardPostId,commentID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialise(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (animatedLogo != null) {
            animatedLogo.stopAnimation();
        }
    }

    private void initialise(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            boardPostUrl = (String) getArguments().get(
                    DisBoardsConstants.BOARD_POST_URL);
            boardPostId = (String) getArguments().get(
                    DisBoardsConstants.BOARD_POST_ID);
            inDualPaneMode = getArguments().getBoolean(
                    DisBoardsConstants.DUAL_PANE_MODE);
            boardType = (BoardType) getArguments().getSerializable(
                    DisBoardsConstants.BOARD_TYPE);
        } else {
            boardPostUrl = savedInstanceState
                    .getString(DisBoardsConstants.BOARD_POST_URL);
            boardPostId = savedInstanceState
                    .getString(DisBoardsConstants.BOARD_POST_ID);
            boardPost = savedInstanceState
                    .getParcelable(DisBoardsConstants.BOARD_POST_KEY);
            inDualPaneMode = savedInstanceState
                    .getBoolean(DisBoardsConstants.DUAL_PANE_MODE);
            boardType = (BoardType) savedInstanceState
                    .getSerializable(DisBoardsConstants.BOARD_TYPE);
            if (boardPost != null) {
                adapter.setComments(new ArrayList<>(boardPost.getComments()));
            }
        }

        if (boardPostUrl == null) {
            Log.d(TAG, "Board post url is null");
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
        hideAnimatedLogoAndShowList(new OnListShownHandler() {
            @Override
            public void doOnListShownAction() {
                //TODO animate
                //floatingReplyButton.setVisibility();
            }
        });
    }

    @Override
    public void showLoadingView(IBinder hideSoftKeyboardToken) {
        showAnimatedLogoAndHideList();
    }

    @Override
    public void showBoardPost(BoardPost boardPost, int commentIDToShow) {
        this.boardPost = boardPost;
        connectionErrorTextView.setVisibility(View.GONE);
        adapter.setBoardPost(boardPost);
        adapter.setComments(new ArrayList<>(boardPost.getComments()));
    }

    @Override
    public void showErrorView() {
        hideAnimatedLogoAndShowList(new OnListShownHandler() {
            @Override
            public void doOnListShownAction() {
                connectionErrorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    public String getBoardPostId() {
        String boardPostId = null;
        if (boardPost != null) {
            boardPostId = boardPost.getId();
        }
        return boardPostId;
    }


    public void onEventMainThread(FailedToThisThisEvent event) {
        hideAnimatedLogoAndShowList();
        Toast.makeText(getActivity(),
                "Failed to this this. You could try again", Toast.LENGTH_SHORT)
                .show();
    }

    public void onEventMainThread(FailedToPostCommentEvent event) {
        hideAnimatedLogoAndShowList();
        Toast.makeText(getActivity(),
                "Failed to post comment. Please try again later",
                Toast.LENGTH_SHORT).show();
    }

    public void onEventMainThread(UserIsNotLoggedInEvent event) {
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG, "recieved  not logged in ");
        }

        hideAnimatedLogoAndShowList();
        Toast.makeText(getActivity(),
                "User is not logged in", Toast.LENGTH_SHORT)
                .show();
    }


    public void onEventMainThread(BoardPostCommentSentEvent event) {
        showAnimatedLogoAndHideList();
    }

    public void onEventMainThread(SetBoardPostFavouriteStatusResultEvent event) {
        if (event.isSuccess()) {
            boardPost.setFavourited(event.isNewStatus());
            updateFavouriteMenuItemStatus();
        } else {
            Toast.makeText(getActivity(), "Could not save to favourites",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void displayIsCachedPopup() {
        Toast.makeText(getActivity(), "This is an cached version",
                Toast.LENGTH_SHORT).show();
    }

    public void showAnimatedLogoAndHideList() {
        if (commentsList.getVisibility() == View.VISIBLE) {
            commentsList.setVisibility(View.GONE);
            animatedLogo.setVisibility(View.VISIBLE);
            animatedLogo.startAnimation();
//            ObjectAnimator hideList = ObjectAnimator.ofFloat(commentsList, "alpha", 1f, 0f);
//            hideList.addListener(new SimpleAnimatorListener() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    commentsList.setVisibility(View.INVISIBLE);
//                    moveToFirstOrLastCommentLayout.setVisibility(View.INVISIBLE);
//                    animatedLogo.startAnimation();
//                }
//            });
//            hideList.start();

        } else {
            if (!animatedLogo.animationInProgress()) {
                animatedLogo.startAnimation();
            }
        }
    }

    public void hideAnimatedLogoAndShowList() {
        hideAnimatedLogoAndShowList(null);
    }

    public void hideAnimatedLogoAndShowList(final OnListShownHandler onlistShownListener) {
        if (animatedLogo.getVisibility() == View.VISIBLE) {
            animatedLogo.setAnimationListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ObjectAnimator showList = ObjectAnimator.ofFloat(commentsList, "alpha", 0f, 1f);
                    showList.addListener(new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            commentsList.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animatedLogo.setVisibility(View.GONE);
                            if (onlistShownListener != null) {
                                onlistShownListener.doOnListShownAction();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            animatedLogo.setVisibility(View.GONE);
                            commentsList.setVisibility(View.VISIBLE);
                        }
                    });
                    showList.start();
                }
            });
            animatedLogo.stopAnimationOnceFinished();
        } else {
            animatedLogo.stopAnimationOnceFinished();
            animatedLogo.setVisibility(View.GONE);
            commentsList.setVisibility(View.VISIBLE);
            if (onlistShownListener != null) {
                onlistShownListener.doOnListShownAction();
            }
        }
        //  }
    }

    @Override
    public void onResume() {
        super.onResume();
        boardPostController.loadBoardPost(this, boardPostUrl, boardPostId, boardType);
    }

    @Override
    public void showCachedPopup() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DisBoardsConstants.BOARD_POST_KEY, boardPost);
        outState.putString(DisBoardsConstants.BOARD_POST_ID, boardPostId);
        outState.putString(DisBoardsConstants.BOARD_POST_URL, boardPostUrl);
        outState.putBoolean(DisBoardsConstants.DUAL_PANE_MODE, inDualPaneMode);
        outState.putSerializable(DisBoardsConstants.BOARD_TYPE, boardType);
    }

    private void findAndUpdateFavouritesMenuItem(Menu menu) {
        MenuItem favouriteMenuItem = menu.findItem(R.id.menu_favourite);
        if (favouriteMenuItem != null) {
            if (boardPost != null && boardPost.isFavourited()) {
                //favouriteMenuItem.setIcon(R.drawable.favourite_selected);
            } else {
                //favouriteMenuItem.setIcon(R.drawable.favourite_not_selected);
            }
        }
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
        if (boardPost != null) {
            boolean existingFavouriteStatus = boardPost.isFavourited();
            Bundle serviceBundle = new Bundle();
            serviceBundle.putParcelable(DisBoardsConstants.BOARD_POST_KEY, boardPost);
            serviceBundle.putInt(DatabaseService.DATABASE_SERVICE_REQUESTED_KEY,
                    DatabaseService.SET_BOARD_POST_FAVOURITE_STATUS);
            serviceBundle.putBoolean(DisBoardsConstants.IS_FAVOURITE, !existingFavouriteStatus);

            Intent databaseServiceIntent = new Intent(getActivity(), DatabaseService.class);
            databaseServiceIntent.putExtras(serviceBundle);
            getActivity().startService(databaseServiceIntent);
        }
    }

    @OnClick(R.id.floating_reply_button)
    public void doReplyAction() {
        Bundle replyDetails = new Bundle();
        String replyToAuthor = boardPost.getAuthorUsername();
        replyDetails.putString(DisBoardsConstants.REPLY_TO_AUTHOR,
                replyToAuthor);
        replyDetails.putString(DisBoardsConstants.BOARD_POST_ID,
                boardPost.getId());
        replyDetails.putSerializable(DisBoardsConstants.BOARD_TYPE, boardType);
        PostReplyFragment.newInstance(replyDetails).show(getFragmentManager(),
                "REPLY-DIALOG");
    }

    public void doRefreshAction() {
        Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Refresh  post");
    }


    public void scrollToLatestComment() {
        String latestCommentId = boardPost.getLatestCommentId();
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
            BoardType boardType = UrlConstants.getBoardType(url);
            startActivity(BoardPostActivity.getIntent(getActivity(), url, postId, boardType));
        }


    }


}
