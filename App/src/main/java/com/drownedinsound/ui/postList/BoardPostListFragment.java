package com.drownedinsound.ui.postList;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.events.FailedToPostNewThreadEvent;
import com.drownedinsound.events.SentNewPostEvent;
import com.drownedinsound.events.SentNewPostEvent.SentNewPostState;
import com.drownedinsound.events.UserIsNotLoggedInEvent;
import com.drownedinsound.ui.base.BaseControllerFragment;
import com.drownedinsound.ui.controls.SvgAnimatePathView;
import com.drownedinsound.ui.post.BoardPostActivity;
import com.drownedinsound.ui.post.BoardPostFragment;
import com.drownedinsound.utils.SimpleAnimatorListener;
import com.drownedinsound.utils.UiUtils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * A fragment that will represent a different section of the community board
 * <p/>
 * This will be shown as a list of posts.
 *
 * @author Greg
 */
public class BoardPostListFragment
        extends BaseControllerFragment<BoardPostListController> implements BoardPostListUi,
        BoardPostListAdapter.BoardPostListListener {

    private static final String CURRENTLY_SELECTED_BOARD_POST = "currentlySelectedBoardPost";

    private static final String WAS_IN_DUAL_PANE_MODE = "WasInDualPaneMode";

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "BoardListFragment";

    @InjectView(R.id.animated_logo_progress_bar)
    SvgAnimatePathView animatedLogo;

    @InjectView(R.id.board_list_connection_error_text_view)
    TextView connectionErrorTextView;

    @InjectView(R.id.board_post_summary_list)
    RecyclerView listView;

    @InjectView(R.id.swipeToRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    BoardPostListController boardPostListController;

    private Drawable readDrawable;

    private Drawable unreadDrawable;

    private String boardUrl;

    private BoardPostListAdapter adapter;

    private Board board;

    private BoardType boardType;

    private boolean dualPaneMode;

    private boolean wasInDualPaneMode;

    private int currentlySelectedPost;

    private String postId;

    private String postUrl;

    private int lastPageFetched;

    public BoardPostListFragment() {
    }

    public static BoardPostListFragment newInstance(Board board) {
        BoardPostListFragment boardListFragment = new BoardPostListFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(DisBoardsConstants.BOARD, board);
        boardListFragment.setArguments(arguments);
        return boardListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        board = getArguments().getParcelable(DisBoardsConstants.BOARD);
        if (board != null) {
            boardUrl = board.getUrl();
            boardType = board.getBoardType();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (animatedLogo != null) {
            animatedLogo.stopAnimation();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.board_list_layout, container, false);

        ButterKnife.inject(this, rootView);

        swipeRefreshLayout.setColorSchemeResources(R.color.highlighted_blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefreshAction();
            }
        });

        readDrawable = getActivity().getResources().getDrawable(
                R.drawable.white_circle_blue_outline);
        unreadDrawable = getActivity().getResources().getDrawable(
                R.drawable.filled_blue_circle);

        adapter = new BoardPostListAdapter(getActivity());
        adapter.setBoardPostListListner(this);

        listView.setLayoutManager(new LinearLayoutManager(listView.getContext()));
        listView.setAdapter(adapter);

        animatedLogo.setSvgResource(R.raw.logo);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Check to see if we have a frame in which to embed the details
        int currentOrientation = getResources().getConfiguration().orientation;
        dualPaneMode = UiUtils.isDualPaneMode(getActivity());

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            currentlySelectedPost = savedInstanceState.getInt(
                    CURRENTLY_SELECTED_BOARD_POST, -1);
            wasInDualPaneMode = savedInstanceState.getBoolean(
                    WAS_IN_DUAL_PANE_MODE, false);
            postUrl = savedInstanceState
                    .getString(DisBoardsConstants.BOARD_POST_URL);
            postId = savedInstanceState
                    .getString(DisBoardsConstants.BOARD_POST_ID);
            boardType = (BoardType) savedInstanceState
                    .getSerializable(DisBoardsConstants.BOARD_TYPE);
            boardUrl = savedInstanceState
                    .getString(DisBoardsConstants.BOARD_URL);
            board = savedInstanceState
                    .getParcelable(DisBoardsConstants.BOARD);
        }

        if (dualPaneMode && currentlySelectedPost != -1) {
            BoardPost boardPost = (BoardPost) adapter.getItem(currentlySelectedPost);
            boardPostSelected(currentlySelectedPost, boardPost);
        }

        // TODO This does not work at the moment. SavedInstanceState always
        // seems to be null
        if (wasInDualPaneMode
                && currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            Intent viewPostIntent = new Intent(getActivity(),
                    BoardPostActivity.class);
            viewPostIntent.putExtra(DisBoardsConstants.BOARD_POST_URL, postUrl);
            viewPostIntent.putExtra(DisBoardsConstants.BOARD_POST_ID, postId);
            startActivity(viewPostIntent);
        }
        animatedLogo.tag = board.getDisplayName();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENTLY_SELECTED_BOARD_POST, currentlySelectedPost);
        outState.putBoolean(WAS_IN_DUAL_PANE_MODE, dualPaneMode);
        outState.putString(DisBoardsConstants.BOARD_POST_ID, postId);
        outState.putString(DisBoardsConstants.BOARD_POST_URL, postUrl);
        outState.putSerializable(DisBoardsConstants.BOARD_TYPE, boardType);
        outState.putString(DisBoardsConstants.BOARD_URL, boardUrl);
        outState.putParcelable(DisBoardsConstants.BOARD, board);
    }


    public void showAnimatedLogoAndHideList() {
        Timber.d("Board " + board.getDisplayName() + " showAnimatedLogoAndHideList ");
        if (listView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.GONE);
            animatedLogo.setVisibility(View.VISIBLE);
            Timber.d("Board " + board.getDisplayName() + " start animation");
            animatedLogo.startAnimation();
        } else {
            if (!animatedLogo.animationInProgress()) {
                Timber.d("Board " + board.getDisplayName() + " start animation");
                animatedLogo.startAnimation();
            }
        }
    }

    public void hideAnimatedLogoAndShowList() {
        Timber.d("Board " + board.getDisplayName() + " hide logo and show list");
        if (animatedLogo.getVisibility() == View.VISIBLE) {
            Timber.d("Board " + board.getDisplayName() + " fade out animated logo");
            animatedLogo.setAnimationListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Timber.d("Board " + board.getDisplayName() + " Animation end");
                    ObjectAnimator showList = ObjectAnimator.ofFloat(listView, "alpha", 0f, 1f);
                    showList.addListener(new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            listView.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animatedLogo.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            animatedLogo.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }
                    });
                    showList.start();
                }


            });
            animatedLogo.stopAnimationOnceFinished();
        } else {
            animatedLogo.stopAnimationOnceFinished();
            animatedLogo.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

    }


    public void onEventMainThread(FailedToPostNewThreadEvent event) {
        hideAnimatedLogoAndShowList();
        Toast.makeText(getActivity(), "Failed to create post",
                Toast.LENGTH_SHORT).show();
    }

    public void doRefreshAction() {
        requestBoardSummaryPage(1, false, true);
    }

    public void loadListIfNotAlready() {
        requestBoardSummaryPage(1, true, false);
    }

    public void requestBoardSummaryPage(int page, boolean showLoadingProgress,
            boolean forceUpdate) {
        boardPostListController
                .requestBoardSummaryPage(this, board, page, showLoadingProgress, forceUpdate, true);
    }

    public void onEventMainThread(SentNewPostEvent event) {
        SentNewPostState state = event.getState();
        if (state.equals(SentNewPostState.SENT)) {
            showAnimatedLogoAndHideList();
        } else if (state.equals(SentNewPostState.CONFIRMED)) {
            //Refresh the current list
            requestBoardSummaryPage(1, true, true);
        }
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


    @Override
    public void boardPostSelected(int position, BoardPost boardPost) {
        currentlySelectedPost = position;
        if (boardPost != null) {
            postId = boardPost.getId();
            postUrl = boardUrl + "/" + postId;

            if (dualPaneMode) {
                BoardPostFragment boardPostFragment = (BoardPostFragment) getFragmentManager()
                        .findFragmentById(R.id.board_post_details);
                if (boardPostFragment == null
                        || !postId.equals(boardPostFragment.getBoardPostId())) {
                    boardPostFragment = BoardPostFragment
                            .newInstance(postUrl, postId, true, boardType);
                    // Execute a transaction, replacing any existing fragment
                    // with this one inside the frame.x§
                    FragmentTransaction ft = getFragmentManager()
                            .beginTransaction();
                    ft.replace(R.id.board_post_details, boardPostFragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }

            } else {
                startActivity(BoardPostActivity
                        .getIntent(getActivity(), postUrl, postId, boardType));
            }
        }
    }

    @Override
    public Board getBoardList() {
        return board;
    }

    @Override
    public void setBoardPosts(List<BoardPost> boardPosts) {
        requestToHideLoadingView();
        currentlySelectedPost = -1;
        adapter.setBoardPosts(boardPosts);

        swipeRefreshLayout.setRefreshing(false);

        //adapter.restartAppending();
        //listView.requestPositionToScreen(0, true);
    }

    @Override
    public void appendBoardPosts(List<BoardPost> boardPosts) {
        currentlySelectedPost = -1;
        lastPageFetched++;
        //adapter.appendBoardPosts(boardPosts);
        //adapter.restartAppending();
    }

    @Override
    public void showLoadingProgress(boolean show) {
        //connectionErrorTextView.setVisibility(View.GONE);
        Timber.d("Board " + board.getDisplayName() + " showLoadingProgress " + show);
        if (show) {
            requestToShowLoadingView();
            // adapter.stopAppending();
        } else {
            //adapter.restartAppending();
            requestToHideLoadingView();
        }
    }

    @Override
    public void showErrorView() {
        Timber.d("Show Error View");
        requestToHideLoadingView();
        //connectionErrorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getPageIndex() {
        return board.getPageIndex();
    }

    @Override
    protected BoardPostListController getController() {
        return boardPostListController;
    }

    @Override
    public void hideLoadingView() {
        hideAnimatedLogoAndShowList();
    }

    @Override
    public void showLoadingView(IBinder hideSoftKeyboardToken) {
        showAnimatedLogoAndHideList();
    }

    private class BoardPostSummaryListEndlessAdapter extends EndlessAdapter {

        public BoardPostSummaryListEndlessAdapter(ListAdapter wrapped) {
            super(wrapped, false);
            super.setRunInBackground(false);
        }

        @Override
        protected void appendCachedData() {
            // Don't think we need this

        }

        @Override
        protected boolean cacheInBackground() throws Exception {
            int pageToFetch = lastPageFetched + 1;
            requestBoardSummaryPage(pageToFetch, false, false);
            return true;
        }

        @Override
        protected View getPendingView(ViewGroup parent) {
            LayoutInflater vi = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View pendingRow = vi.inflate(R.layout.board_list_row, null);

            RelativeLayout detail = (RelativeLayout) pendingRow
                    .findViewById(R.id.board_list_row_detail);
            detail.setVisibility(View.INVISIBLE);

            ProgressBar progressBar = (ProgressBar) pendingRow
                    .findViewById(R.id.board_list_row_progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            return pendingRow;
        }

        public void setBoardPosts(List<BoardPost> summaries) {
            ((BoardPostListAdapter) getWrappedAdapter()).setBoardPosts(summaries);
        }

        public void appendBoardPosts(List<BoardPost> summaries) {
            ((BoardPostListAdapter) getWrappedAdapter()).appendSummaries(summaries);
            onDataReady();
        }

        public int getNumberOfPosts() {
            return ((BoardPostListAdapter) getWrappedAdapter()).getNumberOfPosts();
        }

        public BoardPost getBoardPost(int position) {
            return (BoardPost) ((BoardPostListAdapter) getWrappedAdapter())
                    .getItem(position);
        }

    }


}
