package com.drownedinsound.ui.favourites;


import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.events.RetrievedFavouritesEvent;
import com.drownedinsound.events.UserIsNotLoggedInEvent;
import com.drownedinsound.ui.base.BaseFragment;
import com.drownedinsound.ui.post.BoardPostActivity;
import com.drownedinsound.ui.postList.BoardPostListAdapter;
import com.drownedinsound.utils.UiUtils;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by gregmcgowan on 29/10/2013.
 */
public class FavouritesListFragment extends BaseFragment {

    private static final String CURRENTLY_SELECTED_BOARD_POST = "currentlySelectedBoardPost";

    private static final String WAS_IN_DUAL_PANE_MODE = "WasInDualPaneMode";

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "FavouritesListFragment";

    private Drawable readDrawable;

    private Drawable unreadDrawable;

    private ArrayList<BoardPost> favouriteBoardPosts = new ArrayList<BoardPost>();

    private BoardPostListAdapter adapter;

    private boolean isRequesting;

    private boolean dualPaneMode;

    private boolean wasInDualPaneMode;

    private int currentlySelectedPost;

    private String postId;

    protected
    ProgressBar progressBar;

    protected
    //@InjectView(R.id.board_list_connection_error_text_view)
            TextView connectionErrorTextView;

    protected
    @InjectView(R.id.board_post_summary_list)
    RecyclerView boardPostList;

    public FavouritesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.board_list_layout, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        readDrawable = getActivity().getResources().getDrawable(
                R.drawable.white_circle_blue_outline);
        unreadDrawable = getActivity().getResources().getDrawable(
                R.drawable.filled_blue_circle);

        adapter = new BoardPostListAdapter(getActivity());
        boardPostList.setLayoutManager(new LinearLayoutManager(boardPostList.getContext()));
        boardPostList.setAdapter(adapter);
//        boardPostList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View rowView, int position, long id) {
//                BoardPostSummaryHolder holder = (BoardPostSummaryHolder) rowView
//                        .getTag();
//                UiUtils.setBackgroundDrawable(holder.postReadMarkerView, readDrawable);
//
//                showBoardPost(position);
//            }
//        });
        if (!favouritesLoaded()) {
            getFavourites();
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
            Intent viewPostIntent = new Intent(getActivity(),
                    BoardPostActivity.class);
            viewPostIntent.putExtra(DisBoardsConstants.BOARD_POST_ID, postId);
            startActivity(viewPostIntent);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENTLY_SELECTED_BOARD_POST, currentlySelectedPost);
        outState.putBoolean(WAS_IN_DUAL_PANE_MODE, dualPaneMode);
        outState.putString(DisBoardsConstants.BOARD_POST_ID, postId);
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
            isRequesting = true;
        } else {
            setProgressBarVisiblity(true);
        }
    }

    private void setProgressBarVisiblity(boolean visible) {
        int progressBarVisiblity = visible ? View.VISIBLE : View.INVISIBLE;
        int listVisibility = visible ? View.INVISIBLE : View.VISIBLE;
        progressBar.setVisibility(progressBarVisiblity);
        boardPostList.setVisibility(listVisibility);
    }

    public void onEventMainThread(RetrievedFavouritesEvent event) {
        favouriteBoardPosts.addAll(event.getFavourites());
        adapter.notifyDataSetChanged();
        if (!favouritesLoaded()) {
            connectionErrorTextView.setText("No favourites found");
            connectionErrorTextView.setVisibility(View.VISIBLE);

        }
        boardPostList.postDelayed(new Runnable() {
            @Override
            public void run() {
                setProgressBarVisiblity(false);
            }
        }, 1500);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                if (UiUtils.isDualPaneMode(getActivity())) {
                    return false;
                }
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onEventMainThread(UserIsNotLoggedInEvent event) {
        if (DisBoardsConstants.DEBUG) {
            Timber.d("recieved  not logged in ");
        }

        setProgressBarVisiblity(false);
        Toast.makeText(getActivity(),
                "User is not logged in", Toast.LENGTH_SHORT)
                .show();
    }


    private void displayIsCachedPopup() {
        Toast.makeText(getActivity(), "This is an cached version",
                Toast.LENGTH_SHORT).show();
    }


    private void showBoardPost(int position) {

    }
}
