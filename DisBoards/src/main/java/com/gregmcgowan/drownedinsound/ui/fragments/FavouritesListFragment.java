package com.gregmcgowan.drownedinsound.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.DatabaseService;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.events.RetrievedFavouritesEvent;
import com.gregmcgowan.drownedinsound.events.UpdateCachedBoardPostEvent;
import com.gregmcgowan.drownedinsound.events.UserIsNotLoggedInEvent;
import com.gregmcgowan.drownedinsound.ui.activity.BoardPostActivity;
import com.gregmcgowan.drownedinsound.ui.adapter.BoardPostSummaryHolder;
import com.gregmcgowan.drownedinsound.ui.adapter.BoardPostSummaryListAdapter;
import com.gregmcgowan.drownedinsound.ui.widgets.AutoScrollListView;

import com.gregmcgowan.drownedinsound.utils.UiUtils;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by gregmcgowan on 29/10/2013.
 */
public class FavouritesListFragment extends DisBoardsListFragment {

    private static final String CURRENTLY_SELECTED_BOARD_POST = "currentlySelectedBoardPost";
    private static final String WAS_IN_DUAL_PANE_MODE = "WasInDualPaneMode";
    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
        + "FavouritesListFragment";

    private Drawable readDrawable;
    private Drawable unreadDrawable;
    private ProgressBar progressBar;
    private TextView connectionErrorTextView;
    private ArrayList<BoardPost> favouriteBoardPosts = new ArrayList<BoardPost>();
    private BoardPostSummaryListAdapter adapter;
    private View rootView;
    private AutoScrollListView listView;

    private boolean isRequesting;
    private boolean dualPaneMode;
    private boolean wasInDualPaneMode;
    private int currentlySelectedPost;
    private String postId;
    private String postUrl;


    public FavouritesListFragment() {
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
        adapter = new BoardPostSummaryListAdapter(getSherlockActivity(),
                R.layout.board_list_row, favouriteBoardPosts);
        setListAdapter(adapter);

        if (!favouritesLoaded()) {
            getFavourites();
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
        }

        if (dualPaneMode && favouritesLoaded() && currentlySelectedPost != -1) {
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private boolean favouritesLoaded() {
        return favouriteBoardPosts != null && favouriteBoardPosts.size() > 0;
    }

    public void loadListIfNotAlready() {
        if (isValid()) {
            connectionErrorTextView.setVisibility(View.GONE);
            if (!favouritesLoaded()) {
                getFavourites();
            }
        }
    }

    public void getFavourites() {
        if (!isRequesting) {
            Intent disWebServiceIntent = new Intent(getSherlockActivity(),
                DatabaseService.class);
            Bundle parametersBundle = new Bundle();

            parametersBundle.putInt(
                DatabaseService.DATABASE_SERVICE_REQUESTED_KEY,
                DatabaseService.GET_FAVOURITE_BOARD_POSTS);
            disWebServiceIntent.putExtras(parametersBundle);
            getSherlockActivity().startService(disWebServiceIntent);
            setProgressBarVisiblity(true);
            isRequesting = true;
        } else {
            setProgressBarVisiblity(true);
        }
    }

    private void setProgressBarVisiblity(boolean visible) {
        int progressBarVisiblity = visible ? View.VISIBLE : View.INVISIBLE;
        int listVisibility = visible ? View.INVISIBLE : View.VISIBLE;
        progressBar.setVisibility(progressBarVisiblity);
        listView.setVisibility(listVisibility);
    }

    public void onEventMainThread(RetrievedFavouritesEvent event) {
        favouriteBoardPosts.addAll(event.getFavourites());
        adapter.notifyDataSetChanged();
        if(!favouritesLoaded()) {
            connectionErrorTextView.setText("No favourites found");
            connectionErrorTextView.setVisibility(View.VISIBLE);

        }
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setProgressBarVisiblity(false);
            }
        },1500);
    }

    public void onEventBackgroundThread(UpdateCachedBoardPostEvent event) {
        if (favouriteBoardPosts != null) {
            BoardPost boardPostToUpdate = event.getBoardPost();
            if (boardPostToUpdate != null) {
                String postId = boardPostToUpdate.getId();
                for (BoardPost boardPost : favouriteBoardPosts) {
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                if(UiUtils.isDualPaneMode(getSherlockActivity())) {
                    return false;
                }
                getSherlockActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            holder.postReadMarkerView.setBackground(readDrawable);
        } else {
            holder.postReadMarkerView.setBackgroundDrawable(readDrawable);
        }

        showBoardPost(position);
    }

    private void showBoardPost(int position) {
        currentlySelectedPost = position;
        BoardPost boardPostSummary = favouriteBoardPosts.get(position);
        if (boardPostSummary != null) {
            BoardType boardType = boardPostSummary.getBoardType();
            Board board = DatabaseHelper.getInstance(getSherlockActivity()).getBoard(boardType);
            postId = boardPostSummary.getId();

            postUrl = board.getUrl() + "/" + postId;

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
}
