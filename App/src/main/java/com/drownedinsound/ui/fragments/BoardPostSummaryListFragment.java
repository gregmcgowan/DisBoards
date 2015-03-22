package com.drownedinsound.ui.fragments;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.drownedinsound.R;
import com.drownedinsound.annotations.UseDagger;
import com.drownedinsound.annotations.UseEventBus;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.events.FailedToPostNewThreadEvent;
import com.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.drownedinsound.events.SentNewPostEvent;
import com.drownedinsound.events.SentNewPostEvent.SentNewPostState;
import com.drownedinsound.events.UpdateCachedBoardPostEvent;
import com.drownedinsound.events.UserIsNotLoggedInEvent;
import com.drownedinsound.ui.activity.BoardPostActivity;
import com.drownedinsound.ui.adapter.BoardPostSummaryHolder;
import com.drownedinsound.ui.adapter.BoardPostSummaryListAdapter;
import com.drownedinsound.ui.view.SvgAnimatePathView;
import com.drownedinsound.ui.widgets.AutoScrollListView;
import com.drownedinsound.utils.NetworkUtils;
import com.drownedinsound.utils.SimpleAnimatorListener;
import com.drownedinsound.utils.UiUtils;
import com.melnykov.fab.FloatingActionButton;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A fragment that will represent a different section of the community board
 * <p/>
 * This will be shown as a list of posts.
 *
 * @author Greg
 */
@UseEventBus
@UseDagger
public class BoardPostSummaryListFragment extends DisBoardsFragment {

    private static final String CURRENTLY_SELECTED_BOARD_POST = "currentlySelectedBoardPost";

    private static final String WAS_IN_DUAL_PANE_MODE = "WasInDualPaneMode";

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "BoardListFragment";

    private static final String REQUEST_ON_START = "REQUEST_ON_START";

    @InjectView(R.id.animated_logo_progress_bar)
    SvgAnimatePathView animatedLogo;

    @InjectView(R.id.board_list_connection_error_text_view)
    TextView connectionErrorTextView;

    @InjectView(R.id.board_post_summary_list)
    AutoScrollListView listView;

    @InjectView(R.id.floating_add_button)
    FloatingActionButton floatingAddButton;

    @InjectView(R.id.swipeToRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private Drawable readDrawable;

    private Drawable unreadDrawable;

    private String boardUrl;

    private ArrayList<BoardPost> boardPostSummaries = new ArrayList<BoardPost>();

    private BoardPostSummaryListEndlessAdapter adapter;

    private boolean requestOnStart;

    private Board board;

    private BoardType boardType;

    private boolean dualPaneMode;

    private boolean wasInDualPaneMode;

    private int currentlySelectedPost;

    private String postId;

    private String postUrl;

    private int lastPageFetched;

    private AtomicBoolean animatingTransiton = new AtomicBoolean(false);

    public BoardPostSummaryListFragment() {
    }

    public static BoardPostSummaryListFragment newInstance(Board board,
            boolean requestDataOnStart) {
        BoardPostSummaryListFragment boardListFragment = new BoardPostSummaryListFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(DisBoardsConstants.BOARD, board);
        arguments.putBoolean(REQUEST_ON_START, requestDataOnStart);

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

        this.requestOnStart = getArguments().getBoolean(REQUEST_ON_START);
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

        adapter = new BoardPostSummaryListEndlessAdapter(
                new BoardPostSummaryListAdapter(getActivity(),
                        R.layout.board_list_row, boardPostSummaries));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BoardPostSummaryHolder holder = (BoardPostSummaryHolder) view
                        .getTag();
                UiUtils.setBackgroundDrawable(holder.postReadMarkerView,readDrawable);

                showBoardPost(position);
            }
        });
        floatingAddButton.attachToListView(listView);

        animatedLogo.setSvgResource(R.raw.logo);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (requestOnStart && !summariesLoaded()) {
            requestBoardSummaryPage(1, false, true);
        }

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

        if (dualPaneMode && summariesLoaded() && currentlySelectedPost != -1) {
            // listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            showBoardPost(currentlySelectedPost);
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

    @Override
    public void onResume() {
        super.onResume();

        int errorTextVisibility = showNetworkConnectionErrorText() ? View.VISIBLE
                : View.GONE;
        connectionErrorTextView.setVisibility(errorTextVisibility);
    }

    private boolean showNetworkConnectionErrorText() {
        boolean haveNetworkConnection = NetworkUtils
                .isConnected(getActivity());
        return !haveNetworkConnection && boardPostSummaries.size() == 0;
    }

    @OnClick(R.id.floating_add_button)
    public void doNewPostAction() {
        Bundle newPostDetails = new Bundle();
        newPostDetails.putParcelable(DisBoardsConstants.BOARD, board);

        NewPostFragment.newInstance(newPostDetails).show(getFragmentManager(),
                "NEW_POST_DIALOG");
    }

    public void showAnimatedLogoAndHideList(){
      //  if(!animatingTransiton.get()) {
            animatedLogo.setAnimationListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    animatedLogo.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    animatingTransiton.set(false);
                }
            });

            if(listView.getVisibility() == View.VISIBLE) {
                ObjectAnimator hideList = ObjectAnimator.ofFloat(listView, "alpha",1f,0f);
                hideList.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        listView.setVisibility(View.INVISIBLE);
                        animatedLogo.startAnimation();
                    }
                });
                hideList.start();
            } else {
                ObjectAnimator fadeInLogo = ObjectAnimator.ofFloat(animatedLogo,"alpha",0f,1f);
                fadeInLogo.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animatedLogo.startAnimation();
                        animatingTransiton.set(false);
                    }
                });
                fadeInLogo.start();
            }
            animatingTransiton.set(true);
       // }
    }

    public void hideAnimatedLogoAndShowList(){
//        if(!animatingTransiton.get()) {
            if(animatedLogo.getVisibility() == View.VISIBLE) {
                animatingTransiton.set(true);
                animatedLogo.setAnimationListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ObjectAnimator showList = ObjectAnimator.ofFloat(listView, "alpha", 0f, 1f);
                        showList.addListener(new SimpleAnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                listView.setVisibility(View.VISIBLE);
                                animatedLogo.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                animatingTransiton.set(false);
                                animatedLogo.setVisibility(View.INVISIBLE);
                            }
                        });
                        showList.start();
                    }
                });
                animatedLogo.stopAnimationOnceFinished();
            } else {
                listView.setVisibility(View.VISIBLE);
                animatingTransiton.set(false);
            }
    //    }
    }


    public void onEventMainThread(FailedToPostNewThreadEvent event) {
        hideAnimatedLogoAndShowList();
        Toast.makeText(getActivity(), "Failed to create post",
                Toast.LENGTH_SHORT).show();
    }

    public void doRefreshAction() {
        requestBoardSummaryPage(1, true, false);
    }

    private boolean summariesLoaded() {
        return boardPostSummaries != null && boardPostSummaries.size() > 0;
    }

    public void loadListIfNotAlready() {
        if (isValid()) {
            connectionErrorTextView.setVisibility(View.GONE);
            if (!summariesLoaded()) {
                requestBoardSummaryPage(1, false, true);
            } else {
                 adapter.notifyDataSetChanged();
                hideAnimatedLogoAndShowList();
            }
        }
    }

    public void requestBoardSummaryPage(int page, boolean forceUpdate, boolean showProgressDialog) {
        if (showProgressDialog) {
            connectionErrorTextView.setVisibility(View.GONE);
            showAnimatedLogoAndHideList();
        }
        getDisApiClient().getBoardPostSummaryList(page, board, forceUpdate, true);
    }


    public void onEventBackgroundThread(UpdateCachedBoardPostEvent event) {
        if (boardPostSummaries != null) {
            BoardPost boardPostToUpdate = event.getBoardPost();
            if (boardPostToUpdate != null) {
                String postId = boardPostToUpdate.getId();
                for (BoardPost boardPost : boardPostSummaries) {
                    if (postId != null && postId.equals(boardPost.getId())) {
                        boardPost.setLastViewedTime(boardPostToUpdate
                                .getLastViewedTime());
                        Log.d(TAG, "Setting post " + postId + " to "
                                + boardPostToUpdate.getLastViewedTime());
                    }
                }
            }
        }
    }

    public void onEventMainThread(RetrievedBoardPostSummaryListEvent event) {
        BoardType eventBoardType = event.getBoardType();
        currentlySelectedPost = -1;
        if (eventBoardType != null && eventBoardType.equals(boardType)) {
            if (isValid()) {
                Log.d(TAG, "Event for board type " + eventBoardType
                        + " current board Type " + boardType);
                List<BoardPost> summaries = event.getBoardPostSummaryList();
                boolean append = event.isAppend();
                if (summaries != null && summaries.size() > 0) {
                    if (!append) {
                        boardPostSummaries.clear();
                        lastPageFetched = 1;
                    } else {
                        lastPageFetched++;
                        // TODO may need to optimize this
                        for (BoardPost boardPost : summaries) {
                            if (boardPostSummaries.contains(boardPost)) {
                                boardPostSummaries.remove(boardPost);
                            }
                        }
                    }
                    boardPostSummaries.addAll(summaries);
                    adapter.onDataReady();
                }

                Log.d(TAG, "Updated UI for " + eventBoardType);
                if (event.isCached()) {
                    displayIsCachedPopup();
                }
                if (summariesLoaded()) {
                    connectionErrorTextView.setVisibility(View.GONE);
                    adapter.restartAppending();
                } else {
                    connectionErrorTextView.setVisibility(View.VISIBLE);
                    adapter.stopAppending();
                }
                hideAnimatedLogoAndShowList();
                if (!append) {
                    listView.requestPositionToScreen(0, true);
                }
            } else {
                Log.d(TAG, "Board type " + boardType
                        + " was not attached to a activity");
            }

        } else {
            Log.d(TAG, "Event for wrong board type");
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public void onEventMainThread(SentNewPostEvent event) {
        SentNewPostState state = event.getState();
        if (state.equals(SentNewPostState.SENT)) {
            showAnimatedLogoAndHideList();
        } else if (state.equals(SentNewPostState.CONFIRMED)) {
            //Refresh the current list
            requestBoardSummaryPage(1, true, false);
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


    private void displayIsCachedPopup() {
        Toast.makeText(getActivity(), "This is an cached version",
                Toast.LENGTH_SHORT).show();
    }

    private void showBoardPost(int position) {
        currentlySelectedPost = position;
        BoardPost boardPostSummary = boardPostSummaries.get(position);
        if (boardPostSummary != null) {
            postId = boardPostSummary.getId();
            postUrl = boardUrl + "/" + postId;

            if (dualPaneMode) {
                BoardPostFragment boardPostFragment = (BoardPostFragment) getFragmentManager()
                        .findFragmentById(R.id.board_post_details);
                if (boardPostFragment == null
                        || !postId.equals(boardPostFragment.getBoardPostId())) {
                    boardPostFragment = BoardPostFragment
                            .newInstance(postUrl, postId, true, boardType);
                    // Execute a transaction, replacing any existing fragment
                    // with this one inside the frame.
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

    private class BoardPostSummaryListEndlessAdapter extends EndlessAdapter {

        public BoardPostSummaryListEndlessAdapter(ListAdapter wrapped) {
            super(wrapped);
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

    }


}
