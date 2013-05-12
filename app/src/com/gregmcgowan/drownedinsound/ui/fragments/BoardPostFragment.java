package com.gregmcgowan.drownedinsound.ui.fragments;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.events.RetrievedBoardPostEvent;
import com.gregmcgowan.drownedinsound.network.service.DisWebService;
import com.gregmcgowan.drownedinsound.network.service.DisWebServiceConstants;
import com.gregmcgowan.drownedinsound.ui.activity.BoardPostActivity;
import com.gregmcgowan.drownedinsound.ui.widgets.AutoScrollListView;
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
public class BoardPostFragment extends DisBoardsListFragment {

    private static final int SHOW_GO_TO_LAST_COMMENT_TIMEOUT = 5000;

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardPost";

    private BoardPost boardPost;
    private View rootView;
    private ProgressBar progressBar;
    private TextView connectionErrorTextView;
    private AutoScrollListView commentsList;
    private TextView scrollToLastCommentTextView;
    private List<BoardPostComment> boardPostComments = new ArrayList<BoardPostComment>();
    private BoardPostListAdapter adapter;
    private boolean requestingPost;
    private String boardPostUrl;
    private String boardPostId;
    private BoardType boardType;

    private boolean inDualPaneMode;
    private boolean animatingScrollToLastCommentView;
    
    public BoardPostFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	EventBus.getDefault().register(this);
	setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	// In dual mode the fragment will be recreated but will not be used
	// anywhere
	if (container == null) {
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
	    connectionErrorTextView = (TextView) rootView
		    .findViewById(R.id.board_post_connection_error_text_view);
	    connectionErrorTextView.setVisibility(View.GONE);
	    commentsList = (AutoScrollListView) getListView();
	    scrollToLastCommentTextView = (TextView) rootView
		    .findViewById(R.id.board_post_move_to_last_comment_view);
	    scrollToLastCommentTextView
		    .setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			    scrollToLatestComment();
			    displayScrollToHiddenCommentOption(false);
			}

		    });
	}

	adapter = new BoardPostListAdapter(getSherlockActivity(),
		R.layout.board_post_comment_layout, boardPostComments);
	setListAdapter(adapter);
	initliase(savedInstanceState);
    }

    private void initliase(Bundle savedInstanceState) {
	if (savedInstanceState == null) {
	    boardPostUrl = (String) getArguments().get(
		    DisBoardsConstants.BOARD_POST_URL);
	    boardPostId = (String) getArguments().get(
		    DisBoardsConstants.BOARD_POST_ID);
	    inDualPaneMode = getArguments().getBoolean(
		    DisBoardsConstants.DUAL_PANE_MODE);
	    boardType = (BoardType) getArguments().getSerializable(
		    DisBoardsConstants.BOARD_TYPE);
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
	    boardType = (BoardType) savedInstanceState
		    .getSerializable(DisBoardsConstants.BOARD_TYPE);
	    if (boardPost != null) {
		updateComments(boardPost.getComments());
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
	this.requestingPost = false;
	if (isValid()) {
	    BoardPost boardPost = event.getBoardPost();
	    boolean showGoToLastCommentOption = false;
	    if (shouldShowBoardPost(boardPost)) {
		this.boardPost = boardPost;
		updateComments(boardPost.getComments());
		if (event.isCached()) {
		    displayIsCachedPopup();
		}
		showGoToLastCommentOption = true;
		connectionErrorTextView.setVisibility(View.GONE);
	    } else {
		connectionErrorTextView.setVisibility(View.VISIBLE);
	    }
	    setProgressBarAndFragmentVisibility(false);
	    if(showGoToLastCommentOption){
		displayScrollToHiddenCommentOption(true);
		scrollToLastCommentTextView.postDelayed(new Runnable(){
		    @Override
		    public void run() {
			displayScrollToHiddenCommentOption(false);
		    }
		    
		}, SHOW_GO_TO_LAST_COMMENT_TIMEOUT);
	    }
	}
    }

    private boolean shouldShowBoardPost(BoardPost boardPost) {
	boolean shouldDisplayBoardPost = false;
	if (boardPost != null) {
	    Collection<BoardPostComment> comments = boardPost.getComments();
	    shouldDisplayBoardPost = comments.size() > 0;
	}
	return shouldDisplayBoardPost;
    }

    private void displayIsCachedPopup() {
	Toast.makeText(getSherlockActivity(), "This is an cached version",
		Toast.LENGTH_SHORT).show();
    }

    public void setProgressBarAndFragmentVisibility(boolean visible) {
	if (progressBar != null) {
	    int progressBarVisiblity = visible ? View.VISIBLE : View.INVISIBLE;
	    int listVisibility = visible ? View.INVISIBLE : View.VISIBLE;
	    progressBar.setVisibility(progressBarVisiblity);
	    getListView().setVisibility(listVisibility);
	    scrollToLastCommentTextView.setVisibility(listVisibility);
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
	if (isValid()) {
	    setProgressBarAndFragmentVisibility(true);
	    if (!requestingPost) {
		connectionErrorTextView.setVisibility(View.GONE);
		Intent disWebServiceIntent = new Intent(getSherlockActivity(),
			DisWebService.class);
		Bundle parametersBundle = new Bundle();
		parametersBundle.putString(DisBoardsConstants.BOARD_POST_URL,
			boardPostUrl);
		parametersBundle.putString(DisBoardsConstants.BOARD_POST_ID,
			boardPostId);
		parametersBundle.putSerializable(DisBoardsConstants.BOARD_TYPE,
			boardType);
		parametersBundle.putInt(
			DisWebServiceConstants.SERVICE_REQUESTED_ID,
			DisWebServiceConstants.GET_BOARD_POST_ID);
		disWebServiceIntent.putExtras(parametersBundle);

		getSherlockActivity().startService(disWebServiceIntent);
		requestingPost = true;
	    }
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
	outState.putSerializable(DisBoardsConstants.BOARD_TYPE, boardType);
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
	Bundle replyDetails = new Bundle();
	String replyToAuthor = boardPost.getAuthorUsername();
	replyDetails.putString(DisBoardsConstants.REPLY_TO_AUTHOR,
		replyToAuthor);
	PostReplyFragment.newInstance(replyDetails).show(getFragmentManager(),
		"REPLY-DIALOG");
    }

    private void doRefreshAction() {
	fetchBoardPost();
	Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Refresh  post");
    }

    public boolean showGoToLastCommentOption() {
	// TODO

	return true;
    }

    public void scrollToLatestComment() {
	String latestCommentId = boardPost.getLatestCommentId();
	if (!TextUtils.isEmpty(latestCommentId)) {
	    int index = 0;
	    for (BoardPostComment comment : boardPostComments) {
		if (latestCommentId.equals(comment.getId())) {
		    break;
		}
		index++;
	    }
	    if (index > 0) {
		commentsList.requestPositionToScreen(index, true);
	    }
	}

    }

    private void displayScrollToHiddenCommentOption(final boolean display) {
	if(!animatingScrollToLastCommentView) {
	    animatingScrollToLastCommentView = true;
		float[] offset = new float[3];
		if (display) {
		    offset[0] = 100f;
		    offset[1] = 50f;
		    offset[2] = 0f;
		} else {
		    offset[0] = 0f;
		    offset[1] = 50f;
		    offset[2] = 100f;
		}

		ObjectAnimator animateScrollToLastCommentOption = ObjectAnimator
			.ofFloat(scrollToLastCommentTextView, "translationY", offset);
		animateScrollToLastCommentOption.setDuration(1000);
		animateScrollToLastCommentOption.addListener(new AnimatorListener() {

		    public void onAnimationStart(Animator animation) {
		    }

		    public void onAnimationEnd(Animator animation) {
			if (display) {
			    scrollToLastCommentTextView.setVisibility(View.VISIBLE);
			} else {
			    scrollToLastCommentTextView.setVisibility(View.GONE);
			}
			animatingScrollToLastCommentView = false;
		    }

		    public void onAnimationCancel(Animator animation) {
		    }

		    public void onAnimationRepeat(Animator animation) {
		    }

		});
		animateScrollToLastCommentOption.start();
	}
    }

    private class BoardPostListAdapter extends ArrayAdapter<BoardPostComment> {

	private List<BoardPostComment> comments;

	public BoardPostListAdapter(Context context, int textViewResourceId,
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
		String replyTo = comment.getReplyToUsername();
		if (!TextUtils.isEmpty(replyTo)) {
		    author = author + "\n" + "@ " + replyTo;
		}
		String content = comment.getContent();
		String dateAndTime = comment.getDateAndTimeOfComment();

		if (!isFirstComment) {
		    boardPostCommentHolder.commentAuthorTextView
			    .setText(author);
		    boardPostCommentHolder.commentTitleTextView.setText(title);
		    if (content == null) {
			content = "";
		    }
		    boardPostCommentHolder.commentContentTextView.setText(Html
			    .fromHtml(content));

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
					    new WeakReference<RelativeLayout>(
						    boardPostCommentHolder.actionRelativeLayout),
					    new WeakReference<BoardPostListAdapter>(
						    adapter))));

		    boardPostCommentHolder.replyTextView
			    .setOnClickListener(new CommentSectionClickListener(
				    position,
				    new ReplyToCommentListener(
					    new WeakReference<BoardPostListAdapter>(
						    adapter),
					    new WeakReference<FragmentManager>(
						    getSherlockActivity()
							    .getSupportFragmentManager()))));

		    boolean actionSectionVisible = comment
			    .isActionSectionVisible();
		    if (actionSectionVisible) {
			boardPostCommentHolder.actionRelativeLayout
				.setVisibility(View.VISIBLE);
		    } else {
			boardPostCommentHolder.actionRelativeLayout
				.setVisibility(View.GONE);
		    }

		} else {
		    dateAndTime = boardPost.getDateOfPost();
		    String numberOfReplies = boardPost.getNumberOfReplies()
			    + " replies";
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
	boardPostCommentHolder.replyTextView = (TextView) rowView
		.findViewById(R.id.board_post_comment_reply);

	rowView.setTag(boardPostCommentHolder);
	rowView.setOnClickListener(null);
	rowView.setOnClickListener(new CommentSectionClickListener(
		listPosition, new AllCommentClickListener(
			new WeakReference<RelativeLayout>(
				boardPostCommentHolder.actionRelativeLayout),
			new WeakReference<BoardPostListAdapter>(adapter))));
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

    public static class ReplyToCommentListener implements
	    CommentSectionClickHandler {

	private WeakReference<BoardPostListAdapter> adapterWeakReference;
	private WeakReference<FragmentManager> fragmentManagerReference;

	public ReplyToCommentListener(
		WeakReference<BoardPostListAdapter> adapterWeakReference,
		WeakReference<FragmentManager> fragmentManager) {
	    this.adapterWeakReference = adapterWeakReference;
	    this.fragmentManagerReference = fragmentManager;
	}

	@Override
	public void doCommentClickAction(View parentView, int position) {
	    BoardPostComment comment = null;
	    BoardPostListAdapter boardPostListAdapter = adapterWeakReference
		    .get();
	    if (boardPostListAdapter != null) {
		comment = boardPostListAdapter.getItem(position);
	    }

	    if (comment != null) {
		String replyToAuthor = comment.getAuthorUsername();
		if (TextUtils.isEmpty(replyToAuthor)) {
		    BoardPostComment initalPost = boardPostListAdapter
			    .getItem(0);
		    replyToAuthor = initalPost.getAuthorUsername();
		}
		String replyToId = comment.getId();

		Bundle replyDetails = new Bundle();
		replyDetails.putString(DisBoardsConstants.REPLY_TO_AUTHOR,
			replyToAuthor);
		replyDetails.putString(DisBoardsConstants.REPLY_TO_ID,
			replyToId);
		/*
		 * String replyText = comment.getTitle(); if
		 * (TextUtils.isEmpty(replyText)) { replyText =
		 * comment.getContent(); }
		 * replyDetails.putString(DisBoardsConstants.REPLY_TO_TEXT,
		 * replyText);
		 */
		// TextUtils.ellipsize(text, p, avail, where)

		PostReplyFragment.newInstance(replyDetails)
			.show(fragmentManagerReference.get(),
				"REPLY_FRAGMENT_DIALOG");

	    } else {

	    }
	}

    }

    public static class AllCommentClickListener implements
	    CommentSectionClickHandler {

	private WeakReference<RelativeLayout> actionLayoutWeakReference;
	private WeakReference<BoardPostListAdapter> adapterWeakReference;

	public AllCommentClickListener(
		WeakReference<RelativeLayout> actionLayout,
		WeakReference<BoardPostListAdapter> adapterWeakReference) {
	    this.actionLayoutWeakReference = actionLayout;
	    this.adapterWeakReference = adapterWeakReference;
	}

	@Override
	public void doCommentClickAction(View parentView, int position) {
	    RelativeLayout actionLayout = actionLayoutWeakReference.get();
	    if (actionLayout != null) {
		boolean initallyVisible = actionLayout.getVisibility() == View.VISIBLE;
		animateActionLayout(actionLayout, position, !initallyVisible);
	    }
	}

	private void animateActionLayout(final RelativeLayout actionLayout,
		int position, final boolean setVisible) {
	    BoardPostComment comment = null;
	    BoardPostListAdapter boardPostListAdapter = adapterWeakReference
		    .get();
	    if (boardPostListAdapter != null) {
		comment = boardPostListAdapter.getItem(position);
	    }

	    if (comment != null) {
		comment.setActionSectionVisible(setVisible);
	    }

	    float[] offset = setVisible ? new float[] { 0, 0.5f, 1 }
		    : new float[] { 1, 0.5f, 0 };

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
    }

    private static class CommentSectionClickListener implements OnClickListener {

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
