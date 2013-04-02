package com.gregmcgowan.drownedinsound.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.gregmcgowan.drownedinsound.network.UrlConstants;
import com.gregmcgowan.drownedinsound.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.network.service.DisWebServiceConstants;
import com.gregmcgowan.drownedinsound.ui.activity.BoardPostActivity;
import com.gregmcgowan.drownedinsound.utils.UiUtils;

import de.greenrobot.event.EventBus;

/**
 * A fragment that will represent a different section of the community board
 * 
 * This will be shown as a list of posts.
 * 
 * @author Greg
 * 
 */
public class BoardPostSummaryListFragment extends SherlockListFragment {

    private static final String CURRENTLY_SELECTED_BOARD_POST = "currentlySelectedBoardPost";
    private static final String WAS_IN_DUAL_PANE_MODE = "WasInDualPaneMode";
    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardListFragment";
    private String boardUrl;
    private ProgressBar progressBar;
    private ArrayList<BoardPost> boardPostSummaries = new ArrayList<BoardPost>();
    private BoardPostSummaryListAdapater adapter;
    private View rootView;
    private boolean requestOnStart;
    private boolean loadedList;
    private String boardId;
    private boolean dualPaneMode;
    private boolean wasInDualPaneMode;
    private int currentlySelectedPost;
    private String postId;
    private String postUrl;

    public BoardPostSummaryListFragment() {
    }

    public static BoardPostSummaryListFragment newInstance(String boardUrl,
	    String boardId, boolean requestDataOnStart) {
	BoardPostSummaryListFragment boardListFragment = new BoardPostSummaryListFragment();
	boardListFragment.boardUrl = boardUrl;
	boardListFragment.requestOnStart = requestDataOnStart;
	boardListFragment.boardId = boardId;
	return boardListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	inflater = (LayoutInflater) inflater.getContext().getSystemService(
		Context.LAYOUT_INFLATER_SERVICE);
	rootView = inflater.inflate(R.layout.board_layout, null);
	return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	progressBar = (ProgressBar) rootView
		.findViewById(R.id.board_list_progress_bar);
	adapter = new BoardPostSummaryListAdapater(getSherlockActivity(),
		R.layout.board_list_row, boardPostSummaries);
	setListAdapter(adapter);

	if (requestOnStart && !loadedList) {
	    requestBoardSummaryPage(1);
	}

	// Check to see if we have a frame in which to embed the details
	// fragment directly in the containing UI.
	int screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
	int screenWidthDp = UiUtils.convertPixelsToDp(getResources(),
		screenWidthPixels);
	int currentOrientation = getResources().getConfiguration().orientation;
	dualPaneMode = currentOrientation == Configuration.ORIENTATION_LANDSCAPE
		&& screenWidthDp >= UiUtils.MIN_WITH_DP_FOR_DUAL_MODE;

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

	if (dualPaneMode && loadedList && currentlySelectedPost != -1) {
	    getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
	outState.putString(DisBoardsConstants.BOARD_POST_ID, postUrl);
	outState.putString(DisBoardsConstants.BOARD_POST_URL, postId);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	EventBus.getDefault().unregister(this);
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
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    private void doRefreshAction() {
	requestBoardSummaryPage(1);
    }

    public void loadListIfNotAlready(int page) {
	if (!loadedList) {
	    requestBoardSummaryPage(page);
	}
    }

    public void requestBoardSummaryPage(int page) {
	setProgressBarVisiblity(true);
	Intent disWebServiceIntent = new Intent(getSherlockActivity(),
		DisWebService.class);
	disWebServiceIntent.putExtra(
		DisWebServiceConstants.SERVICE_REQUESTED_ID,
		DisWebServiceConstants.GET_POSTS_SUMMARY_LIST_ID);
	disWebServiceIntent.putExtra(DisBoardsConstants.BOARD_TYPE_ID, boardId);
	disWebServiceIntent.putExtra(DisBoardsConstants.BOARD_URL, boardUrl);
	getSherlockActivity().startService(disWebServiceIntent);
    }

    private void setProgressBarVisiblity(boolean visible) {
	int progressBarVisiblity = visible ? View.VISIBLE : View.INVISIBLE;
	int listVisibility = visible ? View.INVISIBLE : View.VISIBLE;
	progressBar.setVisibility(progressBarVisiblity);
	getListView().setVisibility(listVisibility);
    }

    public void onEventMainThread(RetrievedBoardPostSummaryListEvent event) {
	String eventBoardId = event.getBoardId();
	if (eventBoardId != null && eventBoardId.equals(boardId)) {
	    loadedList = true;
	    boardPostSummaries.clear();
	    boardPostSummaries.addAll(event.getBoardPostSummaryList());
	    adapter.notifyDataSetChanged();
	    setProgressBarVisiblity(false);
	}
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	showBoardPost(position);
    }

    private void showBoardPost(int position) {
	currentlySelectedPost = position;
	BoardPost boardPostSummary = boardPostSummaries.get(position);
	if (boardPostSummary != null) {
	    postId = boardPostSummary.getId();
	    postUrl = UrlConstants.BASE_URL + "/" + postId;

	    if (dualPaneMode) {
		getListView().setItemChecked(position, true);
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
		    arguments.putString(DisBoardsConstants.BOARD_TYPE_ID,
			    boardId);
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
		viewPostIntent.putExtra(DisBoardsConstants.BOARD_POST_URL,
			postUrl);
		viewPostIntent.putExtra(DisBoardsConstants.BOARD_POST_ID,
			postId);
		startActivity(viewPostIntent);
	    }
	}
    }

    private class BoardPostSummaryListAdapater extends ArrayAdapter<BoardPost> {

	private List<BoardPost> summaries;

	public BoardPostSummaryListAdapater(Context context,
		int textViewResourceId, List<BoardPost> boardPostSummaries) {
	    super(context, textViewResourceId);
	    this.summaries = boardPostSummaries;
	}

	@Override
	public BoardPost getItem(int position) {
	    return summaries.get(position);
	}

	@Override
	public long getItemId(int position) {
	    return super.getItemId(position);
	}

	@Override
	public int getCount() {
	    return summaries.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View boardPostSummaryRowView = convertView;
	    if (boardPostSummaryRowView == null) {
		LayoutInflater vi = (LayoutInflater) getActivity()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		boardPostSummaryRowView = vi.inflate(R.layout.board_list_row,
			null);
	    }
	    BoardPost summary = summaries.get(position);
	    if (summary != null) {
		String title = summary.getTitle();
		String authorusername = "by " + summary.getAuthorUsername();
		setTextForTextView(boardPostSummaryRowView,
			R.id.board_post_list_row_title, title);
		setTextForTextView(boardPostSummaryRowView,
			R.id.board_post_list_row_author, authorusername);
	    }
	    return boardPostSummaryRowView;
	}

	// TODO optimise to find the view by id once
	private void setTextForTextView(View parentView, int viewId, String text) {
	    TextView textView = (TextView) parentView.findViewById(viewId);
	    if (textView != null) {
		textView.setText(text);
	    }
	}
    }

}
