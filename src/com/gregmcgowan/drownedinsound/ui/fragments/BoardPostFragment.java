package com.gregmcgowan.drownedinsound.ui.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.network.service.DisWebServiceConstants;
import com.gregmcgowan.drownedinsound.ui.activity.BoardPostActivity;
import com.gregmcgowan.drownedinsound.utils.UiUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

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
    private String boardTypeId;

    private boolean inDualPaneMode;

    public BoardPostFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	EventBus.getDefault().register(this);
	setRetainInstance(true);
	setHasOptionsMenu(true);
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
	    inDualPaneMode = getArguments().getBoolean(
		    DisBoardsConstants.DUAL_PANE_MODE);
	    boardTypeId = (String) getArguments().getString(
		    DisBoardsConstants.BOARD_TYPE_ID);
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
	    boardTypeId = savedInstanceState
		    .getString(DisBoardsConstants.BOARD_TYPE_ID);

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
	    int listVisibility = visible ? View.INVISIBLE : View.VISIBLE;
	    progressBar.setVisibility(progressBarVisiblity);
	    getListView().setVisibility(listVisibility);
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
	if (!unattachedFragment && !requestingPost) {
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
	    disWebServiceIntent.putExtra(DisBoardsConstants.BOARD_TYPE_ID,
		    boardTypeId);
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
	outState.putBoolean(DisBoardsConstants.DUAL_PANE_MODE, inDualPaneMode);
	outState.putString(DisBoardsConstants.BOARD_TYPE_ID, boardTypeId);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	if (!inDualPaneMode) {
	    inflater.inflate(R.menu.board_post_menu, menu);
	}

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	int itemId = item.getItemId();
	switch (itemId) {
	case android.R.id.home:
	    hideFragment();
	    return true;
	case R.id.menu_post_refresh:
	    doRefreshAction();
	    return true;
	case R.id.menu_post_reply:
	    doReplyAction();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    private void hideFragment() {
	((BoardPostActivity) getSherlockActivity()).removeBoardPostFragment();
    }

    private void doReplyAction() {
	Toast.makeText(getSherlockActivity(), "Reply to post",
		Toast.LENGTH_SHORT).show();
	Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Reply to  post");
    }

    private void doRefreshAction() {
	fetchBoardPost();
	Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Refresh  post");
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
		    boardPostCommentHolder = inflateBoardPostCommentHolder(
			    boardPostSummaryRowView, position);
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
		String content = comment.getContent();
		String dateAndTime = comment.getDateAndTimeOfComment();

		if (!isFirstComment) {
		    boardPostCommentHolder.commentAuthorTextView
			    .setText(author);
		    boardPostCommentHolder.commentTitleTextView.setText(title);
		    if (content != null) {
			boardPostCommentHolder.commentContentTextView
				.setText(Html.fromHtml(content));
		    }

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
		    // Set ActionVisibility
		    boardPostSummaryRowView.setOnClickListener(null);
		    boardPostSummaryRowView
			    .setOnClickListener(new CommentSectionClickListener(
				    position,
				    new AllCommentClickListener(
					    boardPostCommentHolder.actionRelativeLayout)));

		    boolean actionSectionVisible = comment
			    .isActionSectionVisible();
		    Log.d(TAG, "Setting postion " + position + " to "
			    + actionSectionVisible);
		    if (actionSectionVisible) {
			boardPostCommentHolder.actionRelativeLayout
				.setVisibility(View.VISIBLE);
		    } else {
			boardPostCommentHolder.actionRelativeLayout
				.setVisibility(View.GONE);
		    }

		} else {
		    dateAndTime = boardPost.getDateOfPost();
		    String numberOfReplies = boardPost.getNumberOfReplies() +" replies";
		    boardPostInitialHolder.commentAuthorTextView
			    .setText(author);
		    boardPostInitialHolder.commentTitleTextView.setText(title);
		    boardPostInitialHolder.commentContentTextView.setText(Html
			    .fromHtml(content));
		    boardPostInitialHolder.commentDateTimeTextView
			    .setText(dateAndTime);
		    boardPostInitialHolder.noOfCommentsTextView
			    .setText(numberOfReplies);
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
	boardPostCommentHolder.actionRelativeLayout = (RelativeLayout) rowView
		.findViewById(R.id.board_post_comment_action_section);

	rowView.setTag(boardPostCommentHolder);
	rowView.setOnClickListener(null);
	rowView.setOnClickListener(new CommentSectionClickListener(
		listPosition, new AllCommentClickListener(
			boardPostCommentHolder.actionRelativeLayout)));
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
	boardPostInitialHolder.commentDateTimeTextView = (TextView) rowView
		.findViewById(R.id.board_post_initial_comment_date_time_subheading);
	boardPostInitialHolder.noOfCommentsTextView = (TextView) rowView
		.findViewById(R.id.board_post_initial_comment_replies_subheading);
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
	private RelativeLayout actionRelativeLayout;
	private TextView thisTextView;
	private TextView replyTextView;
    }

    private class BoardPostInitialCommentHolder {
	private TextView commentTitleTextView;
	private TextView commentContentTextView;
	private TextView commentAuthorTextView;
	private TextView commentDateTimeTextView;
	private TextView noOfCommentsTextView;

    }

    private void animateActionLayout(final RelativeLayout actionLayout,
	    int position, final boolean setVisible) {
	BoardPostComment comment = adapter.getItem(position);
	if (comment != null) {
	    comment.setActionSectionVisible(setVisible);
	}

	float[] offset = setVisible ? new float[] { 0, 0.5f, 1 } : new float[] {
		1, 0.5f, 0 };

	actionLayout.setVisibility(View.VISIBLE);
	ObjectAnimator removeObjectAnimator = ObjectAnimator.ofFloat(
		actionLayout, "scaleY", offset);
	removeObjectAnimator.setDuration(500);
	removeObjectAnimator.setInterpolator(new AccelerateInterpolator());
	removeObjectAnimator.addListener(new AnimatorListener() {

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

    public class AllCommentClickListener implements CommentSectionClickHandler {

	RelativeLayout actionLayout;

	public AllCommentClickListener(RelativeLayout actionLayout) {
	    this.actionLayout = actionLayout;
	}

	@Override
	public void doCommentClickAction(View parentView, int position) {
	    if (actionLayout != null) {
		boolean initallyVisible = actionLayout.getVisibility() == View.VISIBLE;
		animateActionLayout(actionLayout, position, !initallyVisible);
	    }
	}
    }

    private class CommentSectionClickListener implements OnClickListener {

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
