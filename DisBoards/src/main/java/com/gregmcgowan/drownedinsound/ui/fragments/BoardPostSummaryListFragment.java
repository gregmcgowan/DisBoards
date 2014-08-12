package com.gregmcgowan.drownedinsound.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.commonsware.cwac.endless.EndlessAdapter;
import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.events.FailedToPostNewThreadEvent;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.gregmcgowan.drownedinsound.events.SentNewPostEvent;
import com.gregmcgowan.drownedinsound.events.SentNewPostEvent.SentNewPostState;
import com.gregmcgowan.drownedinsound.events.UpdateCachedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.UserIsNotLoggedInEvent;
import com.gregmcgowan.drownedinsound.data.network.HttpClient;
import com.gregmcgowan.drownedinsound.data.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.data.network.service.DisWebServiceConstants;
import com.gregmcgowan.drownedinsound.ui.activity.BoardPostActivity;
import com.gregmcgowan.drownedinsound.ui.adapter.BoardPostSummaryHolder;
import com.gregmcgowan.drownedinsound.ui.adapter.BoardPostSummaryListAdapter;
import com.gregmcgowan.drownedinsound.ui.widgets.AutoScrollListView;
import com.gregmcgowan.drownedinsound.utils.NetworkUtils;
import com.gregmcgowan.drownedinsound.utils.UiUtils;

import de.greenrobot.event.EventBus;

/**
 * A fragment that will represent a different section of the community board
 * <p/>
 * This will be shown as a list of posts.
 *
 * @author Greg
 */
public class BoardPostSummaryListFragment extends DisBoardsListFragment {

    private static final String CURRENTLY_SELECTED_BOARD_POST = "currentlySelectedBoardPost";
    private static final String WAS_IN_DUAL_PANE_MODE = "WasInDualPaneMode";
    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
        + "BoardListFragment";

    private Drawable readDrawable;
    private Drawable unreadDrawable;
    private String boardUrl;
    private ProgressBar progressBar;
    private TextView connectionErrorTextView;
    private ArrayList<BoardPost> boardPostSummaries = new ArrayList<BoardPost>();
    private BoardPostSummaryListEndlessAdapter adapter;
    private View rootView;
    private AutoScrollListView listView;
    private boolean requestOnStart;
    private Board board;
    private BoardType boardType;
    private boolean dualPaneMode;
    private boolean wasInDualPaneMode;
    private int currentlySelectedPost;
    private String postId;
    private String postUrl;

    private int lastPageFetched;

    public BoardPostSummaryListFragment() {
    }

    public static BoardPostSummaryListFragment newInstance(Board boardTypeInfo,
                                                           boolean requestDataOnStart) {
        BoardPostSummaryListFragment boardListFragment = new BoardPostSummaryListFragment();
        boardListFragment.board = boardTypeInfo;
        boardListFragment.boardUrl = boardTypeInfo.getUrl();
        boardListFragment.requestOnStart = requestDataOnStart;
        boardListFragment.boardType = boardTypeInfo.getBoardType();
        return boardListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater = (LayoutInflater) inflater.getContext().getSystemService(
            Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.board_list_layout, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressBar = (ProgressBar) rootView
            .findViewById(R.id.board_list_progress_bar);
        connectionErrorTextView = (TextView) rootView
            .findViewById(R.id.board_list_connection_error_text_view);

        readDrawable = getSherlockActivity().getResources().getDrawable(
            R.drawable.white_circle_blue_outline);
        unreadDrawable = getSherlockActivity().getResources().getDrawable(
            R.drawable.filled_blue_circle);

        listView = (AutoScrollListView) getListView();
        // connectionErrorTextView.setVisibility(View.GONE);
        adapter = new BoardPostSummaryListEndlessAdapter(getSherlockActivity(),
            new BoardPostSummaryListAdapter(getSherlockActivity(),
                R.layout.board_list_row, boardPostSummaries));
        setListAdapter(adapter);

        if (requestOnStart && !summariesLoaded()) {
            requestBoardSummaryPage(1, false);
        }

        // Check to see if we have a frame in which to embed the details

        int currentOrientation = getResources().getConfiguration().orientation;
        dualPaneMode = UiUtils.isDualPaneMode(getSherlockActivity());

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
            board = (Board) savedInstanceState
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
            Intent viewPostIntent = new Intent(getSherlockActivity(),
                BoardPostActivity.class);
            viewPostIntent.putExtra(DisBoardsConstants.BOARD_POST_URL, postUrl);
            viewPostIntent.putExtra(DisBoardsConstants.BOARD_POST_ID, postId);
            startActivity(viewPostIntent);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
            .isConnected(getSherlockActivity());
        return !haveNetworkConnection && boardPostSummaries.size() == 0
            && !isBoardBeingRequested();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_community_activity_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_list_refresh:
                doRefreshAction();
                return true;
            case R.id.menu_new_post:
                doNewPostAction();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doNewPostAction() {
        Bundle newPostDetails = new Bundle();
        newPostDetails.putParcelable(DisBoardsConstants.BOARD, board);

        NewPostFragment.newInstance(newPostDetails).show(getFragmentManager(),
            "NEW_POST_DIALOG");
    }


    public void onEventMainThread(FailedToPostNewThreadEvent event) {
        this.setProgressBarVisiblity(false);
        Toast.makeText(getSherlockActivity(), "Failed to create post",
            Toast.LENGTH_SHORT).show();
    }

    private boolean isBoardBeingRequested() {
        boolean requested = false;
        if(boardType != null) {
            HttpClient.requestIsInProgress(boardType.name());
        }
        return requested;
    }

    private void doRefreshAction() {
        requestBoardSummaryPage(1, true);
    }

    private boolean summariesLoaded() {
        return boardPostSummaries != null && boardPostSummaries.size() > 0;
    }

    public void loadListIfNotAlready() {
        if (isValid()) {
            connectionErrorTextView.setVisibility(View.GONE);
            if (!summariesLoaded()) {
                requestBoardSummaryPage(1, false);
            } else {
                if (isBoardBeingRequested()) {
                    setProgressBarVisiblity(true);
                } else {
                    adapter.notifyDataSetChanged();
                    setProgressBarVisiblity(false);
                }
            }
        }
    }

    public void requestBoardSummaryPage(int page, boolean forceUpdate) {
        if (!isBoardBeingRequested()) {
            if (page <= 1) {
                connectionErrorTextView.setVisibility(View.GONE);
                setProgressBarVisiblity(true);
            }


            Intent disWebServiceIntent = new Intent(getSherlockActivity(),
                DisWebService.class);
            Bundle parametersBundle = new Bundle();

            parametersBundle.putInt(
                DisWebServiceConstants.SERVICE_REQUESTED_ID,
                DisWebServiceConstants.GET_POSTS_SUMMARY_LIST_ID);
            parametersBundle.putParcelable(DisBoardsConstants.BOARD, board);
            parametersBundle.putBoolean(DisBoardsConstants.FORCE_FETCH,
                forceUpdate);
            parametersBundle.putInt(DisBoardsConstants.BOARD_PAGE_NUMBER, page);
            disWebServiceIntent.putExtras(parametersBundle);
            getSherlockActivity().startService(disWebServiceIntent);
        } else {
            Log.d(TAG, "Already requesting " + boardType);
            setProgressBarVisiblity(true);
        }
    }

    private void setProgressBarVisiblity(boolean visible) {
        int progressBarVisiblity = visible ? View.VISIBLE : View.INVISIBLE;
        int listVisibility = visible ? View.INVISIBLE : View.VISIBLE;
        progressBar.setVisibility(progressBarVisiblity);
        listView.setVisibility(listVisibility);
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
                } else {
                    connectionErrorTextView.setVisibility(View.VISIBLE);
                }
                setProgressBarVisiblity(false);
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
    }

    public void onEventMainThread(SentNewPostEvent event) {
        SentNewPostState state = event.getState();
        if (state.equals(SentNewPostState.SENT)) {
            this.setProgressBarVisiblity(true);
        } else if (state.equals(SentNewPostState.CONFIRMED)) {
            //Refresh the current list
            requestBoardSummaryPage(1, true);
        }

    }

    public void onEventMainThread(UserIsNotLoggedInEvent event) {
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG, "recieved  not logged in ");
        }

        setProgressBarVisiblity(false);
        Toast.makeText(getSherlockActivity(),
            "User is not logged in", Toast.LENGTH_SHORT)
            .show();
    }


    private void displayIsCachedPopup() {
        Toast.makeText(getSherlockActivity(), "This is an cached version",
            Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NewApi")
    @Override
    public void onListItemClick(ListView l, View rowView, int position, long id) {
        BoardPostSummaryHolder holder = (BoardPostSummaryHolder) rowView
            .getTag();
        if (Build.VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN) {
            holder.postReadMarkerView.setBackground(readDrawable);
        } else {
            holder.postReadMarkerView.setBackgroundDrawable(readDrawable);
        }

        showBoardPost(position);
    }

    private void showBoardPost(int position) {
        currentlySelectedPost = position;
        BoardPost boardPostSummary = boardPostSummaries.get(position);
        if (boardPostSummary != null) {
            postId = boardPostSummary.getId();
            postUrl = boardUrl + "/" + postId;

            if (dualPaneMode) {
               // listView.setItemChecked(position, true);
                BoardPostFragment boardPostFragment = (BoardPostFragment) getFragmentManager()
                    .findFragmentById(R.id.board_post_details);
                if (boardPostFragment == null
                    || !postId.equals(boardPostFragment.getBoardPostId())) {
                    boardPostFragment = new BoardPostFragment();
                    Bundle arguments = new Bundle();
                    arguments.putString(DisBoardsConstants.BOARD_POST_ID,
                        postId);
                    arguments.putString(DisBoardsConstants.BOARD_POST_URL,
                        postUrl);
                    arguments.putBoolean(DisBoardsConstants.DUAL_PANE_MODE,
                        true);
                    arguments.putSerializable(DisBoardsConstants.BOARD_TYPE,
                        boardType);
                    boardPostFragment.setArguments(arguments);
                    // Execute a transaction, replacing any existing fragment
                    // with this one inside the frame.
                    FragmentTransaction ft = getFragmentManager()
                        .beginTransaction();
                    ft.replace(R.id.board_post_details, boardPostFragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }

            } else {
                Intent viewPostIntent = new Intent(getSherlockActivity(),
                    BoardPostActivity.class);
                Bundle parametersBundle = new Bundle();
                parametersBundle.putString(DisBoardsConstants.BOARD_POST_URL,
                    postUrl);
                parametersBundle.putString(DisBoardsConstants.BOARD_POST_ID,
                    postId);
                parametersBundle.putSerializable(DisBoardsConstants.BOARD_TYPE,
                    boardType);
                viewPostIntent.putExtras(parametersBundle);

                startActivity(viewPostIntent);
            }
        }
    }

    private class BoardPostSummaryListEndlessAdapter extends EndlessAdapter {

        public BoardPostSummaryListEndlessAdapter(Context context,
                                                  ListAdapter wrapped) {
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
            Log.d(TAG, "Cache in background called");
            requestBoardSummaryPage(pageToFetch, false);
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
