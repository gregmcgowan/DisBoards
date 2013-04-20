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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.gregmcgowan.drownedinsound.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.network.service.DisWebServiceConstants;
import com.gregmcgowan.drownedinsound.ui.activity.BoardPostActivity;
import com.gregmcgowan.drownedinsound.utils.NetworkUtils;
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
    private  Drawable readDrawable; 
    private Drawable unreadDrawable; 
    private String boardUrl;
    private ProgressBar progressBar;
    private TextView connectionErrorTextView;
    private ArrayList<BoardPost> boardPostSummaries = new ArrayList<BoardPost>();
    private BoardPostSummaryListAdapater adapter;
    private View rootView;
    private boolean requestOnStart;
    private boolean loadedList;
    private Board boardTypeInfo;
    private BoardType boardType;
    private boolean dualPaneMode;
    private boolean wasInDualPaneMode;
    private int currentlySelectedPost;
    private String postId;
    private String postUrl;
    private boolean requestingBoardList;

    public BoardPostSummaryListFragment() {
    }

    public static BoardPostSummaryListFragment newInstance(
	    Board boardTypeInfo, boolean requestDataOnStart) {
	BoardPostSummaryListFragment boardListFragment = new BoardPostSummaryListFragment();
	boardListFragment.boardTypeInfo = boardTypeInfo;
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
	
	readDrawable = getSherlockActivity().getResources()
		    .getDrawable(R.drawable.white_circle_blue_outline);
	unreadDrawable = getSherlockActivity()
		    .getResources().getDrawable(R.drawable.filled_blue_circle);
	
	// connectionErrorTextView.setVisibility(View.GONE);
	adapter = new BoardPostSummaryListAdapater(getSherlockActivity(),
		R.layout.board_list_row, boardPostSummaries);
	setListAdapter(adapter);

	if (requestOnStart && !loadedList) {
	    requestBoardSummaryPage(1, false);
	}

	// Check to see if we have a frame in which to embed the details
	// fragment directly in the containing UI.
	int screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
	int screenWidthDp = UiUtils.convertPixelsToDp(getResources(),
		screenWidthPixels);
	int currentOrientation = getResources().getConfiguration().orientation;
	dualPaneMode = currentOrientation == Configuration.ORIENTATION_LANDSCAPE
		&& screenWidthDp >= UiUtils.MIN_WIDTH_DP_FOR_DUAL_MODE;

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
	outState.putString(DisBoardsConstants.BOARD_POST_ID, postId);
	outState.putString(DisBoardsConstants.BOARD_POST_URL, postUrl);
	outState.putSerializable(DisBoardsConstants.BOARD_TYPE, boardType);
	outState.putString(DisBoardsConstants.BOARD_URL, boardUrl);
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
		&& !requestingBoardList;
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
	requestBoardSummaryPage(1, true);
    }

    public void loadListIfNotAlready(int page) {
	if (!loadedList) {
	    requestBoardSummaryPage(page, false);
	}
    }

    public void requestBoardSummaryPage(int page, boolean forceUpdate) {
	if (!requestingBoardList) {
	    requestingBoardList = true;
	    connectionErrorTextView.setVisibility(View.GONE);
	    setProgressBarVisiblity(true);
	    Intent disWebServiceIntent = new Intent(getSherlockActivity(),
		    DisWebService.class);
	    Bundle parametersBundle = new Bundle();

	    parametersBundle.putInt(
		    DisWebServiceConstants.SERVICE_REQUESTED_ID,
		    DisWebServiceConstants.GET_POSTS_SUMMARY_LIST_ID);
	    parametersBundle.putParcelable(DisBoardsConstants.BOARD_TYPE_INFO,
		    boardTypeInfo);
	    parametersBundle.putBoolean(DisBoardsConstants.FORCE_FETCH,
		    forceUpdate);

	    disWebServiceIntent.putExtras(parametersBundle);
	    getSherlockActivity().startService(disWebServiceIntent);
	}
    }

    private void setProgressBarVisiblity(boolean visible) {
	int progressBarVisiblity = visible ? View.VISIBLE : View.INVISIBLE;
	int listVisibility = visible ? View.INVISIBLE : View.VISIBLE;
	progressBar.setVisibility(progressBarVisiblity);
	getListView().setVisibility(listVisibility);
    }

    public void onEventMainThread(RetrievedBoardPostSummaryListEvent event) {
	BoardType eventBoardType = event.getBoardType();
	requestingBoardList = false;
	if (eventBoardType != null && eventBoardType.equals(boardType)) {
	    List<BoardPost> summaries = event.getBoardPostSummaryList();
	    if (summaries != null && summaries.size() > 0) {
		loadedList = true;
		boardPostSummaries.clear();
		boardPostSummaries.addAll(event.getBoardPostSummaryList());
		adapter.notifyDataSetChanged();
		final ListView listView = getListView();
		listView.postDelayed(new Runnable() {
		    @Override
		    public void run() {
			listView.smoothScrollToPosition(0);
		    }

		}, 250);

		if (event.isCached()) {
		    displayIsCachedPopup();
		}
		connectionErrorTextView.setVisibility(View.GONE);
	    } else {
		connectionErrorTextView.setVisibility(View.VISIBLE);
		loadedList = false;
	    }
	    setProgressBarVisiblity(false);
	}
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
	if(Build.VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN) {
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

    private class BoardPostSummaryListAdapater extends ArrayAdapter<BoardPost> {

	private List<BoardPost> summaries;

	public BoardPostSummaryListAdapater(Context context,
		int textViewResourceId, List<BoardPost> boardPostSummaries) {
	    super(context, textViewResourceId,boardPostSummaries);
	    this.summaries = boardPostSummaries;
	}

	@Override
	public int getCount() {
	    return summaries.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View boardPostSummaryRowView = convertView;
	    BoardPost summary = getItem(position);
	    BoardPostSummaryHolder holder = null;
	    if (boardPostSummaryRowView == null) {
		LayoutInflater vi = (LayoutInflater) getActivity()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		boardPostSummaryRowView = vi.inflate(R.layout.board_list_row,
			null);
		holder = new BoardPostSummaryHolder();
		holder.titleTextView = (TextView) boardPostSummaryRowView
			.findViewById(R.id.board_post_list_row_title);
		holder.authorTextView = (TextView) boardPostSummaryRowView
			.findViewById(R.id.board_post_list_row_author);
		holder.numberOfRepliesTextView = (TextView) boardPostSummaryRowView
			.findViewById(R.id.board_post_list_row_number_of_replies);
		holder.stickyTextView = (TextView) boardPostSummaryRowView
			.findViewById(R.id.board_post_list_row_sticky);
		holder.lastUpdatedTextView = (TextView) boardPostSummaryRowView
			.findViewById(R.id.board_post_list_row_last_updated);
		holder.postReadMarkerView = boardPostSummaryRowView
			.findViewById(R.id.board_post_list_row_read_marker);
		boardPostSummaryRowView.setTag(holder);
	    } else {
		holder = (BoardPostSummaryHolder) boardPostSummaryRowView
			.getTag();
	    }

	    if (summary != null) {
		String title = summary.getTitle();
		String authorusername = "by " + summary.getAuthorUsername();
		int numberOfReplies = summary.getNumberOfReplies();
		String numberOfRepliesText = null;
		if (numberOfReplies > 0) {
		    numberOfRepliesText = numberOfReplies
			    + (numberOfReplies > 1 ? " replies " : "  reply");
		} else {
		    numberOfRepliesText = "No replies";
		}
		String lastUpdatedText = summary
			.getLastUpdatedInReadableString();
		int stickyVisible = summary.isSticky() ? View.VISIBLE
			: View.GONE;

		long lastViewedTime = summary.getLastViewedTime();
		long lastUpdatedTime = summary.getLastUpdatedTime();
		boolean markAsRead = lastViewedTime > 0 && lastViewedTime >= lastUpdatedTime;

		holder.titleTextView.setText(title);
		holder.authorTextView.setText(authorusername);
		holder.numberOfRepliesTextView.setText(numberOfRepliesText);
		holder.lastUpdatedTextView.setText(lastUpdatedText);
		holder.stickyTextView.setVisibility(stickyVisible);
		if (markAsRead) {
		    holder.postReadMarkerView
			    .setBackgroundDrawable(readDrawable);
		} else {
		    holder.postReadMarkerView
			    .setBackgroundDrawable(unreadDrawable);
		}
	    }
	    return boardPostSummaryRowView;
	}
    }

    private class BoardPostSummaryHolder {
	View postReadMarkerView;
	TextView titleTextView;
	TextView authorTextView;
	TextView stickyTextView;
	TextView numberOfRepliesTextView;
	TextView lastUpdatedTextView;

    }

}
