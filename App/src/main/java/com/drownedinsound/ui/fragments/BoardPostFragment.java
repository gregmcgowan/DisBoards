package com.drownedinsound.ui.fragments;


import com.drownedinsound.R;
import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.annotations.UseEventBus;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.DatabaseService;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardPostComment;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.data.network.service.DisWebService;
import com.drownedinsound.data.network.service.DisWebServiceConstants;
import com.drownedinsound.events.BoardPostCommentSentEvent;
import com.drownedinsound.events.FailedToPostCommentEvent;
import com.drownedinsound.events.FailedToThisThisEvent;
import com.drownedinsound.events.RetrievedBoardPostEvent;
import com.drownedinsound.events.SetBoardPostFavouriteStatusResultEvent;
import com.drownedinsound.events.UserIsNotLoggedInEvent;
import com.drownedinsound.ui.activity.BoardPostActivity;
import com.drownedinsound.ui.view.ActiveTextView;
import com.drownedinsound.ui.widgets.AutoScrollListView;
import com.drownedinsound.utils.UiUtils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Represents a board fragment. This will consist of the board post and all the
 * comments made against that post.
 *
 * @author Greg
 */
@UseDagger
@UseEventBus
public class BoardPostFragment extends DisBoardsFragment {

    private static final int SHOW_GO_TO_LAST_COMMENT_TIMEOUT = 5000;

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "BoardPost";

    private static final String DUAL_PANE_MODE = "DUAL_PANE_MODE";

    private BoardPost boardPost;

    private List<BoardPostComment> boardPostComments = new ArrayList<>();

    private BoardPostListAdapter adapter;

    private boolean requestingPost;

    private String boardPostUrl;

    private String boardPostId;

    private BoardType boardType;

    private boolean inDualPaneMode;

    private boolean animatingScrollToLastCommentView;

    protected
    @InjectView(R.id.board_post_progress_bar)
    ProgressBar progressBar;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setHasOptionsMenu(true);
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

        adapter = new BoardPostListAdapter(getActivity(),
                R.layout.board_post_comment_layout, boardPostComments,
                new WeakReference<>(this));
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialise(savedInstanceState);
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
            requestingPost = savedInstanceState
                    .getBoolean(DisBoardsConstants.REQUESTING_POST);
            boardPost = savedInstanceState
                    .getParcelable(DisBoardsConstants.BOARD_POST_KEY);
            inDualPaneMode = savedInstanceState
                    .getBoolean(DisBoardsConstants.DUAL_PANE_MODE);
            boardType = (BoardType) savedInstanceState
                    .getSerializable(DisBoardsConstants.BOARD_TYPE);
            if (boardPost != null) {
                updateComments(boardPost.getComments());
            }
        }

        if (boardPostUrl == null) {
            Log.d(TAG, "Board post url is null");
        }
    }

    private void updateComments(Collection<BoardPostComment> comments) {
        boardPostComments.clear();
        boardPostComments.addAll(comments);
        adapter.notifyDataSetChanged();
    }

    public String getBoardPostId() {
        String boardPostId = null;
        if (boardPost != null) {
            boardPostId = boardPost.getId();
        }
        return boardPostId;
    }

    public void onEventMainThread(RetrievedBoardPostEvent event) {
        this.requestingPost = false;
        if (isValid()) {
            BoardPost boardPost = event.getBoardPost();
            if (shouldShowBoardPost(boardPost)) {
                this.boardPost = boardPost;
                boolean showGoToLastCommentOption = event
                        .isDisplayGotToLatestCommentOption()
                        && showGoToLastCommentOption();
                updateComments(boardPost.getComments());
                if (event.isCached()) {
                    displayIsCachedPopup();
                }
                connectionErrorTextView.setVisibility(View.GONE);
                Log.d(TAG, "Show got to last comment option ="
                        + showGoToLastCommentOption);
                if (showGoToLastCommentOption) {
                    displayScrollToHiddenCommentOption(true);
                }
            } else {
                connectionErrorTextView.setVisibility(View.VISIBLE);
            }
            updateFavouriteMenuItemStatus();
            setProgressBarAndFragmentVisibility(false);
        }
    }

    public void onEventMainThread(FailedToThisThisEvent event) {
        setProgressBarAndFragmentVisibility(false);
        Toast.makeText(getActivity(),
                "Failed to this this. You could try again", Toast.LENGTH_SHORT)
                .show();
    }

    public void onEventMainThread(FailedToPostCommentEvent event) {
        setProgressBarAndFragmentVisibility(false);
        Toast.makeText(getActivity(),
                "Failed to post comment. You could try again",
                Toast.LENGTH_SHORT).show();
    }

    public void onEventMainThread(UserIsNotLoggedInEvent event) {
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG, "recieved  not logged in ");
        }

        setProgressBarAndFragmentVisibility(false);
        Toast.makeText(getActivity(),
                "User is not logged in", Toast.LENGTH_SHORT)
                .show();
    }


    public void onEventMainThread(BoardPostCommentSentEvent event) {
        setProgressBarAndFragmentVisibility(true);
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

    private boolean shouldShowBoardPost(BoardPost boardPost) {
        boolean shouldDisplayBoardPost = false;
        if (boardPost != null) {
            Collection<BoardPostComment> comments = boardPost.getComments();
            shouldDisplayBoardPost = comments.size() > 0;
        }
        return shouldDisplayBoardPost;
    }

    private void displayIsCachedPopup() {
        Toast.makeText(getActivity(), "This is an cached version",
                Toast.LENGTH_SHORT).show();
    }

    public void setProgressBarAndFragmentVisibility(boolean progressBarVisible) {
        if (progressBar != null) {
            int progressBarVisiblity = progressBarVisible ? View.VISIBLE
                    : View.INVISIBLE;
            int listVisibility = progressBarVisible ? View.INVISIBLE
                    : View.VISIBLE;
            progressBar.setVisibility(progressBarVisiblity);
            commentsList.setVisibility(listVisibility);
            if (progressBarVisible) {
                moveToFirstOrLastCommentLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (boardPost == null) {
            fetchBoardPost();
        }
    }

    private void fetchBoardPost() {
        if (isValid()) {
            setProgressBarAndFragmentVisibility(true);
            if (!requestingPost) {
                connectionErrorTextView.setVisibility(View.GONE);

                Intent disWebServiceIntent = new Intent(getActivity(),
                        DisWebService.class);
                Bundle parametersBundle = new Bundle();
                parametersBundle.putString(DisBoardsConstants.BOARD_POST_URL,
                        boardPostUrl);
                parametersBundle.putString(DisBoardsConstants.BOARD_POST_ID,
                        boardPostId);
                parametersBundle.putSerializable(DisBoardsConstants.BOARD_TYPE,
                        boardType);
                parametersBundle.putInt(
                        DisWebServiceConstants.SERVICE_REQUESTED_ID,
                        DisWebServiceConstants.GET_BOARD_POST_ID);
                disWebServiceIntent.putExtras(parametersBundle);

                getActivity().startService(disWebServiceIntent);
                requestingPost = true;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DisBoardsConstants.REQUESTING_POST, requestingPost);
        outState.putParcelable(DisBoardsConstants.BOARD_POST_KEY, boardPost);
        outState.putString(DisBoardsConstants.BOARD_POST_ID, boardPostId);
        outState.putString(DisBoardsConstants.BOARD_POST_URL, boardPostUrl);
        outState.putBoolean(DisBoardsConstants.DUAL_PANE_MODE, inDualPaneMode);
        outState.putSerializable(DisBoardsConstants.BOARD_TYPE, boardType);
    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
////        if (!inDualPaneMode) {
////            inflater.inflate(R.menu.board_post_menu, menu);
////        }
//        findAndUpdateFavouritesMenuItem(menu);
//    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        findAndUpdateFavouritesMenuItem(menu);
//    }

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

    private void doReplyAction() {
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

    private void doRefreshAction() {
        fetchBoardPost();
        Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Refresh  post");
    }

    public boolean showGoToLastCommentOption() {
        // TODO

        return boardPost != null && boardPost.getNumberOfTimesRead() > 1;
    }

    public void scrollToLatestComment() {
        String latestCommentId = boardPost.getLatestCommentId();
        if (!TextUtils.isEmpty(latestCommentId)) {
            int index = 0;
            for (BoardPostComment comment : boardPostComments) {
                if (latestCommentId.equals(comment.getId())) {
                    break;
                }
                index++;
            }
            if (index > 0) {
                boardPostComments.get(index).setDoHighlightedAnimation(true);
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
            Intent viewPostIntent = new Intent(getActivity(),
                    BoardPostActivity.class);
            Bundle parametersBundle = new Bundle();
            parametersBundle.putString(DisBoardsConstants.BOARD_POST_URL,
                    url);
            parametersBundle.putString(DisBoardsConstants.BOARD_POST_ID,
                    postId);
            parametersBundle.putSerializable(DisBoardsConstants.BOARD_TYPE,
                    getBoardTypeFromUrl(url));
            viewPostIntent.putExtras(parametersBundle);

            startActivity(viewPostIntent);
        }


    }

    private BoardType getBoardTypeFromUrl(String url) {
        BoardType boardtype = null;
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith(UrlConstants.MUSIC_URL)) {
                boardtype = BoardType.MUSIC;
            } else if (url.startsWith(UrlConstants.SOCIAL_URL)) {
                boardtype = BoardType.SOCIAL;
            }
            //TODO the rest
        }
        return boardtype;
    }

    private class BoardPostListAdapter extends ArrayAdapter<BoardPostComment> {

        private static final int HIGHLIGHTED_COMMENT_ANIMATION_LENGTH = 2000;

        private List<BoardPostComment> comments;

        private WeakReference<BoardPostFragment> boardPostFragmentWeakReference;

        public BoardPostListAdapter(Context context, int textViewResourceId,
                List<BoardPostComment> boardPostSummaries,
                WeakReference<BoardPostFragment> boardPostFragmentWeakReference) {
            super(context, textViewResourceId);
            this.comments = boardPostSummaries;
            this.boardPostFragmentWeakReference = boardPostFragmentWeakReference;
        }

        @Override
        public BoardPostComment getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BoardPostComment comment = comments.get(position);
            View boardPostSummaryRowView = convertView;

            boolean isFirstComment = position == 0;
            BoardPostCommentHolder boardPostCommentHolder = null;
            BoardPostInitialCommentHolder boardPostInitialHolder = null;

            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (boardPostSummaryRowView == null) {
                if (!isFirstComment) {
                    boardPostSummaryRowView = inflater.inflate(
                            R.layout.board_post_comment_layout, null);
                    boardPostCommentHolder = inflateBoardPostCommentHolder(
                            boardPostSummaryRowView, position);
                } else {
                    boardPostSummaryRowView = inflater.inflate(
                            R.layout.board_post_initial_comment_layout, null);
                    boardPostInitialHolder = inflateBoardPostInitialCommentHolder(
                            boardPostSummaryRowView);
                }

            } else {
                if (isFirstComment) {
                    if (boardPostSummaryRowView.getTag() instanceof BoardPostCommentHolder) {
                        boardPostSummaryRowView = inflater.inflate(
                                R.layout.board_post_initial_comment_layout,
                                null);
                        boardPostInitialHolder = inflateBoardPostInitialCommentHolder(
                                boardPostSummaryRowView);
                    } else {
                        boardPostInitialHolder
                                = (BoardPostInitialCommentHolder) boardPostSummaryRowView
                                .getTag();
                    }
                } else {
                    if (boardPostSummaryRowView.getTag() instanceof BoardPostInitialCommentHolder) {
                        boardPostSummaryRowView = inflater.inflate(
                                R.layout.board_post_comment_layout, null);
                        boardPostCommentHolder = inflateBoardPostCommentHolder(
                                boardPostSummaryRowView, position);
                    } else {
                        boardPostCommentHolder = (BoardPostCommentHolder) boardPostSummaryRowView
                                .getTag();
                    }
                }
            }

            if (comment != null) {
                String title = comment.getTitle();
                String author = comment.getAuthorUsername();
                String replyTo = comment.getReplyToUsername();
                if (!TextUtils.isEmpty(replyTo)) {
                    author = author + "\n" + "@ " + replyTo;
                }
                String content = comment.getContent();
                if (content == null) {
                    content = "";
                }
                if (!TextUtils.isEmpty(content)) {
                    content = Html.fromHtml(content).toString();
                    int lastLineFeed = content.lastIndexOf("\n");
                    if (lastLineFeed != -1 && (lastLineFeed == content.length() - 1)) {
                        content = content.substring(0, content.length() - 2);
                    }
                }

                String dateAndTime = comment.getDateAndTimeOfComment();

                if (!isFirstComment) {
                    boardPostCommentHolder.commentAuthorTextView
                            .setText(author);
                    boardPostCommentHolder.commentTitleTextView.setText(title);

                    boardPostCommentHolder.commentContentTextView.setText(content);

                    String usersWhoThised = comment.getUsersWhoHaveThissed();
                    if (!TextUtils.isEmpty(usersWhoThised)) {
                        boardPostCommentHolder.commentThisSectionTextView
                                .setText(usersWhoThised);
                        boardPostCommentHolder.commentThisSectionTextView
                                .setVisibility(View.VISIBLE);
                    } else {
                        boardPostCommentHolder.commentThisSectionTextView
                                .setVisibility(View.GONE);
                    }

                    if (TextUtils.isEmpty(dateAndTime)) {
                        dateAndTime = "Unknown";
                    }
                    boardPostCommentHolder.commentDateTimeTextView
                            .setText(dateAndTime);

                    int level = comment.getCommentLevel();
                    boardPostCommentHolder.whitespaceLayout.removeAllViews();

                    int commentLevelIndentPx = UiUtils.convertDpToPixels(
                            getContext().getResources(), 4);
                    for (int i = 0; i < level; i++) {
                        View spacer = new View(getContext());
                        spacer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                                commentLevelIndentPx, LayoutParams.MATCH_PARENT));
                        boardPostCommentHolder.whitespaceLayout.addView(spacer,
                                i);
                    }
                    LinearLayout commentLayout = (LinearLayout) boardPostSummaryRowView
                            .findViewById(R.id.board_post_comment_comment_section);
                    // Set ActionVisibility
                    //boardPostSummaryRowView.setOnClickListener(null);

                    boardPostSummaryRowView
                            .setOnClickListener(new CommentSectionClickListener(
                                    position,
                                    new AllCommentClickListener(
                                            new WeakReference<RelativeLayout>(
                                                    boardPostCommentHolder.actionRelativeLayout),
                                            new WeakReference<View>(commentLayout),
                                            new WeakReference<BoardPostListAdapter>(
                                                    adapter))));

                    boardPostCommentHolder.replyTextView
                            .setOnClickListener(new CommentSectionClickListener(
                                    position,
                                    new ReplyToCommentListener(
                                            new WeakReference<BoardPostListAdapter>(
                                                    adapter),
                                            new WeakReference<FragmentManager>(
                                                    getActivity()
                                                            .getFragmentManager()))));
                    boardPostCommentHolder.thisTextView
                            .setOnClickListener(new CommentSectionClickListener(
                                    position,
                                    new ThisACommentListener(
                                            boardPostUrl,
                                            boardType,
                                            boardPostId,
                                            new WeakReference<BoardPostListAdapter>(
                                                    this),
                                            boardPostFragmentWeakReference)));

                    boolean actionSectionVisible = comment
                            .isActionSectionVisible();
                    if (actionSectionVisible) {
                        boardPostCommentHolder.actionRelativeLayout
                                .setVisibility(View.VISIBLE);
                    } else {
                        boardPostCommentHolder.actionRelativeLayout
                                .setVisibility(View.GONE);
                    }

                    if (comment.isDoHighlightedAnimation()) {
                        final TransitionDrawable transitionDrawable
                                = (TransitionDrawable) boardPostCommentHolder.commentSection
                                .getBackground();
                        transitionDrawable
                                .startTransition(HIGHLIGHTED_COMMENT_ANIMATION_LENGTH / 2);
                        boardPostCommentHolder.commentSection.postDelayed(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        transitionDrawable
                                                .reverseTransition(
                                                        HIGHLIGHTED_COMMENT_ANIMATION_LENGTH / 2);

                                    }

                                }, HIGHLIGHTED_COMMENT_ANIMATION_LENGTH / 2);
                        comment.setDoHighlightedAnimation(false);
                    }
                    boardPostCommentHolder.commentContentTextView
                            .setLinkClickedListener(new ActiveTextView.OnLinkClickedListener() {
                                @Override
                                public void onClick(String url) {
                                    handleLinkClicked(url);
                                }
                            });
                } else {
                    dateAndTime = boardPost.getDateOfPost();
                    String numberOfReplies = boardPost.getNumberOfReplies()
                            + " replies";
                    boardPostInitialHolder.commentAuthorTextView
                            .setText(author);
                    boardPostInitialHolder.commentTitleTextView.setText(title);
                    boardPostInitialHolder.commentContentTextView.setText(content);
                    boardPostInitialHolder.commentDateTimeTextView
                            .setText(dateAndTime);
                    boardPostInitialHolder.noOfCommentsTextView
                            .setText(numberOfReplies);
                    boardPostInitialHolder.commentContentTextView
                            .setLinkClickedListener(new ActiveTextView.OnLinkClickedListener() {
                                @Override
                                public void onClick(String url) {
                                    handleLinkClicked(url);
                                }
                            });
                }


            }
            return boardPostSummaryRowView;
        }

    }

    private BoardPostCommentHolder inflateBoardPostCommentHolder(View rowView,
            int listPosition) {

        BoardPostCommentHolder boardPostCommentHolder = new BoardPostCommentHolder();
        boardPostCommentHolder.commentTitleTextView = (TextView) rowView
                .findViewById(R.id.board_post_comment_content_title);
        boardPostCommentHolder.commentContentTextView = (ActiveTextView) rowView
                .findViewById(R.id.board_post_comment_content);
        boardPostCommentHolder.commentAuthorTextView = (TextView) rowView
                .findViewById(R.id.board_post_comment_author_text_view);
        boardPostCommentHolder.commentDateTimeTextView = (TextView) rowView
                .findViewById(R.id.board_post_comment_date_time_text_view);
        boardPostCommentHolder.commentThisSectionTextView = (TextView) rowView
                .findViewById(R.id.board_post_comment_this_view);
        boardPostCommentHolder.whitespaceLayout = (LinearLayout) rowView
                .findViewById(R.id.board_post_comment_whitespace_section);
        boardPostCommentHolder.actionRelativeLayout = (RelativeLayout) rowView
                .findViewById(R.id.board_post_comment_action_section);
        boardPostCommentHolder.replyTextView = (TextView) rowView
                .findViewById(R.id.board_post_comment_reply);
        boardPostCommentHolder.thisTextView = (TextView) rowView
                .findViewById(R.id.board_post_comment_this);
        boardPostCommentHolder.commentSection = (LinearLayout) rowView
                .findViewById(R.id.board_post_comment_content_section);
        LinearLayout commentLayout = (LinearLayout) rowView
                .findViewById(R.id.board_post_comment_comment_section);
        rowView.setTag(boardPostCommentHolder);
        rowView.setOnClickListener(null);
        rowView.setOnClickListener(new CommentSectionClickListener(
                listPosition, new AllCommentClickListener(
                new WeakReference<RelativeLayout>(
                        boardPostCommentHolder.actionRelativeLayout),
                new WeakReference<View>(commentLayout),
                new WeakReference<BoardPostListAdapter>(adapter))));
        return boardPostCommentHolder;
    }

    private BoardPostInitialCommentHolder inflateBoardPostInitialCommentHolder(
            View rowView) {

        BoardPostInitialCommentHolder boardPostInitialHolder = new BoardPostInitialCommentHolder();

        boardPostInitialHolder.commentTitleTextView = (TextView) rowView
                .findViewById(R.id.board_post_initial_comment_title);
        boardPostInitialHolder.commentAuthorTextView = (TextView) rowView
                .findViewById(R.id.board_post_initial_comment_author_subheading);
        boardPostInitialHolder.commentContentTextView = (ActiveTextView) rowView
                .findViewById(R.id.board_post_initial_comment_text);
        boardPostInitialHolder.commentDateTimeTextView = (TextView) rowView
                .findViewById(R.id.board_post_initial_comment_date_time_subheading);
        boardPostInitialHolder.noOfCommentsTextView = (TextView) rowView
                .findViewById(R.id.board_post_initial_comment_replies_subheading);
        rowView.setTag(boardPostInitialHolder);
        return boardPostInitialHolder;
    }

    private class BoardPostCommentHolder {

        private TextView commentTitleTextView;

        private ActiveTextView commentContentTextView;

        private TextView commentAuthorTextView;

        private TextView commentDateTimeTextView;

        private TextView commentThisSectionTextView;

        private LinearLayout whitespaceLayout;

        private RelativeLayout actionRelativeLayout;

        private TextView thisTextView;

        private TextView replyTextView;

        private LinearLayout commentSection;
    }

    private class BoardPostInitialCommentHolder {

        private TextView commentTitleTextView;

        private ActiveTextView commentContentTextView;

        private TextView commentAuthorTextView;

        private TextView commentDateTimeTextView;

        private TextView noOfCommentsTextView;

    }

    public static class ThisACommentListener implements
            CommentSectionClickHandler {

        private WeakReference<BoardPostListAdapter> adapterWeakReference;

        private WeakReference<BoardPostFragment> boardPostFragmentWeakReference;

        private String postUrl;

        private BoardType boardType;

        private String postID;

        public ThisACommentListener(String postUrl, BoardType boardType,
                String postID,
                WeakReference<BoardPostListAdapter> adapterWeakReference,
                WeakReference<BoardPostFragment> fragment) {
            this.adapterWeakReference = adapterWeakReference;
            this.boardPostFragmentWeakReference = fragment;
            this.postUrl = postUrl;
            this.postID = postID;
            this.boardType = boardType;
        }

        @Override
        public void doCommentClickAction(View parentView, int position) {
            BoardPostComment comment = null;
            BoardPostListAdapter boardPostListAdapter = adapterWeakReference
                    .get();
            if (boardPostListAdapter != null) {
                comment = boardPostListAdapter.getItem(position);
            }
            String commentID = comment.getId();
            BoardPostListAdapter adapter = adapterWeakReference.get();
            BoardPostFragment fragment = boardPostFragmentWeakReference.get();
            if (adapter != null && fragment != null) {
                fragment.setProgressBarAndFragmentVisibility(true);

                Intent thisCommentIntent = new Intent(
                        fragment.getActivity(), DisWebService.class);
                Bundle parametersBundle = new Bundle();
                parametersBundle.putString(DisBoardsConstants.BOARD_POST_URL,
                        postUrl);
                parametersBundle.putString(DisBoardsConstants.BOARD_POST_ID,
                        postID);
                parametersBundle.putSerializable(DisBoardsConstants.BOARD_TYPE,
                        boardType);
                parametersBundle.putString(DisBoardsConstants.BOARD_COMMENT_ID,
                        commentID);
                parametersBundle.putInt(
                        DisWebServiceConstants.SERVICE_REQUESTED_ID,
                        DisWebServiceConstants.THIS_A_COMMENT_ID);
                thisCommentIntent.putExtras(parametersBundle);

                fragment.getActivity().startService(thisCommentIntent);

            }
        }

    }

    public static class ReplyToCommentListener implements
            CommentSectionClickHandler {

        private WeakReference<BoardPostListAdapter> adapterWeakReference;

        private WeakReference<FragmentManager> fragmentManagerReference;

        public ReplyToCommentListener(
                WeakReference<BoardPostListAdapter> adapterWeakReference,
                WeakReference<FragmentManager> fragmentManager) {
            this.adapterWeakReference = adapterWeakReference;
            this.fragmentManagerReference = fragmentManager;
        }

        @Override
        public void doCommentClickAction(View parentView, int position) {
            BoardPostComment comment = null;
            BoardPostListAdapter boardPostListAdapter = adapterWeakReference
                    .get();
            if (boardPostListAdapter != null) {
                comment = boardPostListAdapter.getItem(position);
            }

            if (comment != null) {
                String replyToAuthor = comment.getAuthorUsername();
                if (TextUtils.isEmpty(replyToAuthor)) {
                    BoardPostComment initalPost = boardPostListAdapter
                            .getItem(0);
                    replyToAuthor = initalPost.getAuthorUsername();
                }
                String replyToId = comment.getId();

                Bundle replyDetails = new Bundle();
                replyDetails.putString(DisBoardsConstants.REPLY_TO_AUTHOR,
                        replyToAuthor);
                replyDetails.putString(DisBoardsConstants.BOARD_COMMENT_ID,
                        replyToId);
                replyDetails.putString(DisBoardsConstants.BOARD_POST_ID,
                        comment.getBoardPost().getId());
                replyDetails.putSerializable(DisBoardsConstants.BOARD_TYPE,
                        comment.getBoardPost().getBoardType());
        /*
                 * String replyText = comment.getTitle(); if
		 * (TextUtils.isEmpty(replyText)) { replyText =
		 * comment.getContent(); }
		 * replyDetails.putString(DisBoardsConstants.REPLY_TO_TEXT,
		 * replyText);
		 */
                // TextUtils.ellipsize(text, p, avail, where)

                PostReplyFragment.newInstance(replyDetails)
                        .show(fragmentManagerReference.get(),
                                "REPLY_FRAGMENT_DIALOG");

            } else {

            }
        }

    }

    public static class AllCommentClickListener implements
            CommentSectionClickHandler {

        private WeakReference<RelativeLayout> actionLayoutWeakReference;

        private WeakReference<View> parentLayoutWeakReference;

        private WeakReference<BoardPostListAdapter> adapterWeakReference;

        public AllCommentClickListener(
                WeakReference<RelativeLayout> actionLayout,
                WeakReference<View> parentLayoutWeakReference,
                WeakReference<BoardPostListAdapter> adapterWeakReference) {
            this.actionLayoutWeakReference = actionLayout;
            this.parentLayoutWeakReference = parentLayoutWeakReference;
            this.adapterWeakReference = adapterWeakReference;
        }

        @Override
        public void doCommentClickAction(View parentView, int position) {
            RelativeLayout actionLayout = actionLayoutWeakReference.get();
            if (actionLayout != null) {
                boolean initallyVisible = actionLayout.getVisibility() == View.VISIBLE;
                animateActionLayout(actionLayout, position, !initallyVisible);
            }
        }

        private void animateActionLayout(final RelativeLayout actionLayout,
                int position, final boolean setVisible) {
            BoardPostComment comment = null;
            BoardPostListAdapter boardPostListAdapter = adapterWeakReference
                    .get();
            if (boardPostListAdapter != null) {
                comment = boardPostListAdapter.getItem(position);
            }

            if (comment != null) {
                comment.setActionSectionVisible(setVisible);
            }

            float[] offset = setVisible ? new float[]{0, 0.5f, 1}
                    : new float[]{1, 0.5f, 0};

            actionLayout.setVisibility(View.VISIBLE);
            // parentLayoutWeakReference.get().bringToFront();
            //parentLayoutWeakReference.get().requestLayout();
            ObjectAnimator removeObjectAnimator = ObjectAnimator.ofFloat(
                    actionLayout, "scaleY", offset);
            removeObjectAnimator.setDuration(500);
            removeObjectAnimator.setInterpolator(new AccelerateInterpolator());
            removeObjectAnimator.addListener(new Animator.AnimatorListener() {

                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    if (setVisible) {
                        actionLayout.setVisibility(View.VISIBLE);
                    } else {
                        actionLayout.setVisibility(View.GONE);
                    }

                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }

            });
            removeObjectAnimator.start();
        }
    }

    private static class CommentSectionClickListener implements OnClickListener {

        private int listPosition;

        private CommentSectionClickHandler commentClickHandler;

        CommentSectionClickListener(int listPosition,
                CommentSectionClickHandler commentClickHandler) {
            this.listPosition = listPosition;
            this.commentClickHandler = commentClickHandler;
        }

        @Override
        public void onClick(View v) {
            if (commentClickHandler != null) {
                commentClickHandler.doCommentClickAction(v, listPosition);
            }
        }

    }

    interface CommentSectionClickHandler {

        public void doCommentClickAction(View parentView, int position);
    }

}
