package com.gregmcgowan.drownedinsound.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.handlers.RetrieveBoardPostHandler;
import com.gregmcgowan.drownedinsound.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.network.service.DisWebServiceConstants;
import com.gregmcgowan.drownedinsound.utils.FileUtils;
import com.gregmcgowan.drownedinsound.utils.UiUtils;

import de.greenrobot.event.EventBus;

/**
 * Represents a board fragment. This will consist of the board post and all the
 * comments made against that post.
 * 
 * @author Greg
 * 
 */
public class BoardPostFragment extends SherlockListFragment {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardPost";

    private BoardPost boardPost;
    private View rootView;
    private ProgressBar progressBar;
    private List<BoardPostComment> boardPostComments = new ArrayList<BoardPostComment>();
    private BoardPostListAdapater adapter;
    private boolean unattachedFragment;
    private boolean requestingPost;
    private String boardPostUrl;
    private String boardPostId;

    public BoardPostFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	// In dual mode the fragment will be recreated but will not be used
	// anywhere
	if (container == null) {
	    unattachedFragment = true;
	    return null;
	}
	inflater = (LayoutInflater) inflater.getContext().getSystemService(
		Context.LAYOUT_INFLATER_SERVICE);
	rootView = inflater.inflate(R.layout.board_post_layout, null);
	return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	if (rootView != null) {
	    progressBar = (ProgressBar) rootView
		    .findViewById(R.id.board_post_progress_bar);
	}
	adapter = new BoardPostListAdapater(getSherlockActivity(),
		R.layout.board_post_comment_layout, boardPostComments);
	setListAdapter(adapter);
	initliase(savedInstanceState);
    }

    private void initliase(Bundle savedInstanceState) {
	if (savedInstanceState == null) {
	    boardPostUrl = (String) getArguments().get(
		    DisBoardsConstants.BOARD_POST_URL);
	    boardPostId = (String) getArguments().get(
		    DisBoardsConstants.BOARD_POST_URL);
	} else {
	    boardPostUrl = savedInstanceState
		    .getString(DisBoardsConstants.BOARD_POST_URL);
	    boardPostId = savedInstanceState
		    .getString(DisBoardsConstants.BOARD_POST_ID);
	    requestingPost = savedInstanceState
		    .getBoolean(DisBoardsConstants.REQUESTING_POST);
	    boardPost = savedInstanceState
		    .getParcelable(DisBoardsConstants.BOARD_POST_KEY);
	    if (boardPost != null) {
		updateComments(boardPost.getComments());
	    }
	    if (requestingPost) {
		setProgressBarAndFragmentVisibility(true);
	    }

	}

	if (boardPostUrl == null) {
	    Log.d(TAG, "Board post url is null");
	}
    }

    private void updateComments(List<BoardPostComment> comments) {
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
	BoardPost boardPost = event.getBoardPost();
	if (boardPost != null && !unattachedFragment) {
	    this.boardPost = boardPost;
	    this.requestingPost = false;
	    updateComments(boardPost.getComments());
	}
	setProgressBarAndFragmentVisibility(false);
    }

    public void setProgressBarAndFragmentVisibility(boolean visible) {
	if (progressBar != null) {
	    int progressBarVisiblity = visible ? View.VISIBLE : View.INVISIBLE;
	    progressBar.setVisibility(progressBarVisiblity);
	}
    }

    @Override
    public void onResume() {
	super.onResume();
	if (!unattachedFragment && !requestingPost && boardPost == null) {
	    setProgressBarAndFragmentVisibility(true);
	    Intent disWebServiceIntent = new Intent(getSherlockActivity(),
		    DisWebService.class);
	    disWebServiceIntent.putExtra(
		    DisWebServiceConstants.SERVICE_REQUESTED_ID,
		    DisWebServiceConstants.GET_BOARD_POST_ID);
	    disWebServiceIntent.putExtra(DisBoardsConstants.BOARD_POST_ID,
		    boardPostId);
	    disWebServiceIntent.putExtra(DisBoardsConstants.BOARD_POST_URL,
		    boardPostUrl);
	    getSherlockActivity().startService(disWebServiceIntent);
	    requestingPost = true;
	}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	outState.putBoolean(DisBoardsConstants.REQUESTING_POST, requestingPost);
	outState.putParcelable(DisBoardsConstants.BOARD_POST_KEY, boardPost);
	outState.putString(DisBoardsConstants.BOARD_POST_ID, boardPostId);
	outState.putString(DisBoardsConstants.BOARD_POST_URL, boardPostUrl);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	EventBus.getDefault().unregister(this);
    }

    private class BoardPostListAdapater extends ArrayAdapter<BoardPostComment> {

	private List<BoardPostComment> comments;

	public BoardPostListAdapater(Context context, int textViewResourceId,
		List<BoardPostComment> boardPostSummaries) {
	    super(context, textViewResourceId);
	    this.comments = boardPostSummaries;
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
		    boardPostCommentHolder = inflateBoardPostCommentHolder(boardPostSummaryRowView);
		} else {
		    boardPostSummaryRowView = inflater.inflate(
			    R.layout.board_post_initial_comment_layout, null);
		    boardPostInitialHolder = inflateBoardPostInitialCommentHolder(boardPostSummaryRowView);
		}

	    } else {
		if (isFirstComment) {
		    if (boardPostSummaryRowView.getTag() instanceof BoardPostCommentHolder) {
			boardPostSummaryRowView = inflater.inflate(
				R.layout.board_post_initial_comment_layout,
				null);
			boardPostInitialHolder = inflateBoardPostInitialCommentHolder(boardPostSummaryRowView);
		    } else {
			boardPostInitialHolder = (BoardPostInitialCommentHolder) boardPostSummaryRowView
				.getTag();
		    }
		} else {
		    if (boardPostSummaryRowView.getTag() instanceof BoardPostInitialCommentHolder) {
			boardPostSummaryRowView = inflater.inflate(
				R.layout.board_post_comment_layout, null);
			boardPostCommentHolder = inflateBoardPostCommentHolder(boardPostSummaryRowView);
		    } else {
			boardPostCommentHolder = (BoardPostCommentHolder) boardPostSummaryRowView
				.getTag();
		    }
		}
	    }

	    if (comment != null) {
		String title = comment.getTitle();
		String author = comment.getAuthorUsername();
		String content = comment.getContent();
		// TODO date and time
		// String dateTime = summary.getDateAndTimeOfComment()

		if (!isFirstComment) {
		    boardPostCommentHolder.commentAuthorTextView
			    .setText(author);
		    boardPostCommentHolder.commentTitleTextView.setText(title);
		    if (content != null) {
			boardPostCommentHolder.commentContentTextView
				.setText(Html.fromHtml(content));
		    }

		    String usersWhoThised = getUserWhoThisString(comment
			    .getUsersWhoHaveThissed());
		    boardPostCommentHolder.commentThisSectionTextView
			    .setText(usersWhoThised);

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

		} else {
		    boardPostInitialHolder.commentAuthorTextView
			    .setText(author);
		    boardPostInitialHolder.commentTitleTextView.setText(title);
		    boardPostInitialHolder.commentContentTextView.setText(Html
			    .fromHtml(content));
		}

	    }
	    return boardPostSummaryRowView;
	}

	public String getUserWhoThisString(String[] usersWhoThisd) {
	    String userWhoThisString = "";
	    if (usersWhoThisd != null && usersWhoThisd.length > 0) {
		String users = TextUtils.join(",", usersWhoThisd);
		userWhoThisString = users + " this'd this";

	    }
	    return userWhoThisString;
	}
    }

    private BoardPostCommentHolder inflateBoardPostCommentHolder(View rowView) {
	BoardPostCommentHolder boardPostCommentHolder = new BoardPostCommentHolder();
	boardPostCommentHolder.commentTitleTextView = (TextView) rowView
		.findViewById(R.id.board_post_comment_content_title);
	boardPostCommentHolder.commentContentTextView = (TextView) rowView
		.findViewById(R.id.board_post_comment_content);
	boardPostCommentHolder.commentAuthorTextView = (TextView) rowView
		.findViewById(R.id.board_post_comment_author_text_view);
	boardPostCommentHolder.commentDateTimeTextView = (TextView) rowView
		.findViewById(R.id.board_post_comment_date_time_text_view);
	boardPostCommentHolder.commentThisSectionTextView = (TextView) rowView
		.findViewById(R.id.board_post_comment_this_view);
	boardPostCommentHolder.whitespaceLayout = (LinearLayout) rowView
		.findViewById(R.id.board_post_comment_whitespace_section);

	rowView.setTag(boardPostCommentHolder);
	return boardPostCommentHolder;
    }

    private BoardPostInitialCommentHolder inflateBoardPostInitialCommentHolder(
	    View rowView) {

	BoardPostInitialCommentHolder boardPostInitialHolder = new BoardPostInitialCommentHolder();

	boardPostInitialHolder.commentTitleTextView = (TextView) rowView
		.findViewById(R.id.board_post_initial_comment_title);
	boardPostInitialHolder.commentAuthorTextView = (TextView) rowView
		.findViewById(R.id.board_post_initial_comment_author_subheading);
	boardPostInitialHolder.commentContentTextView = (TextView) rowView
		.findViewById(R.id.board_post_initial_comment_text);
	// TODO
	// Board date and time and number of comments
	rowView.setTag(boardPostInitialHolder);
	return boardPostInitialHolder;
    }

    private class BoardPostCommentHolder {
	private TextView commentTitleTextView;
	private TextView commentContentTextView;
	private TextView commentAuthorTextView;
	private TextView commentDateTimeTextView;
	private TextView commentThisSectionTextView;
	private LinearLayout whitespaceLayout;
    }

    private class BoardPostInitialCommentHolder {
	private TextView commentTitleTextView;
	private TextView commentContentTextView;
	private TextView commentAuthorTextView;
	private TextView commentDateTimeTextView;
	private TextView noOfCommentsTextView;

    }

}
