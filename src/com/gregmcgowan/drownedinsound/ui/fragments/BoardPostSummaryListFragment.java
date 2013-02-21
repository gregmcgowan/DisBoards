package com.gregmcgowan.drownedinsound.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.BoardPostSummary;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UrlConstants;
import com.gregmcgowan.drownedinsound.network.handlers.RetrieveBoardSummaryListHandler;
import com.gregmcgowan.drownedinsound.ui.activity.BoardPostActivity;
import com.gregmcgowan.drownedinsound.utils.FileUtils;

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

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX  + "BoardListFragment";
    private String boardUrl;
    private ProgressBar progressBar;
    private ArrayList<BoardPostSummary> boardPostSummaries;
    private BoardPostListAdapater adapter;
    private View rootView;
    private boolean requestOnStart;
    private boolean loadedList; 
    private String boardId;
    
    public BoardPostSummaryListFragment(){
    }
    
    public static BoardPostSummaryListFragment newInstance(String boardUrl,String boardId,boolean requestDataOnStart) {
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
	adapter = new BoardPostListAdapater(getSherlockActivity(),R.layout.board_list_row,boardPostSummaries);
	setListAdapter(adapter);
	setRetainInstance(true);
	
	if(requestOnStart && !loadedList){
	    requestBoardSummaryPage(1);
	}
    }
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	boardPostSummaries = new ArrayList<BoardPostSummary>();
	EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	EventBus.getDefault().unregister(this);
    }

    public void loadListIfNotAlready(int page){
	if(!loadedList){
	    requestBoardSummaryPage(page);
	}
    }
    
    public void requestBoardSummaryPage(int page){
	setProgressBarVisiblity(true);
	HttpClient.requestBoardSummary(getSherlockActivity(), boardUrl,boardId,
		new RetrieveBoardSummaryListHandler(FileUtils.createTempFile(getSherlockActivity()),boardId), 1);
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
	BoardPostSummary boardPostSummary = boardPostSummaries.get(position);
	if(boardPostSummary != null){
	    String postUrlPrefix = boardPostSummary.getPostUrlPostfix();
	    String postUrl = UrlConstants.BASE_URL + postUrlPrefix;
	    Intent viewPostIntent = new Intent(getSherlockActivity(),BoardPostActivity.class);
	    viewPostIntent.putExtra(DisBoardsConstants.BOARD_POST_URL, postUrl);
	    startActivity(viewPostIntent);
	}
    }
    
    private class BoardPostListAdapater extends ArrayAdapter<BoardPostSummary> {

	private List<BoardPostSummary> summaries;

	public BoardPostListAdapater(Context context, int textViewResourceId,
		List<BoardPostSummary> boardPostSummaries) {
	    super(context, textViewResourceId);
	    this.summaries = boardPostSummaries;
	}

	@Override
	public BoardPostSummary getItem(int position) {
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
	    BoardPostSummary summary = summaries.get(position);
	    if (summary != null) {
		String title = summary.getTitle();
		String authorusername = "by " + summary.getAuthorUsername();
		setTextForTextView(boardPostSummaryRowView, R.id.board_post_list_row_title,title);
		setTextForTextView(boardPostSummaryRowView, R.id.board_post_list_row_author,authorusername);
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
