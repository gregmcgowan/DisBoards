package com.gregmcgowan.drownedinsound.ui.fragments;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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
import com.gregmcgowan.drownedinsound.utils.UiUtils;

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
    private List<BoardPostComment> boardPostComments;

    private BoardPostListAdapater adapter;

    public BoardPostFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	inflater = (LayoutInflater) inflater.getContext().getSystemService(
		Context.LAYOUT_INFLATER_SERVICE);
	rootView = inflater.inflate(R.layout.board_post_layout, null);
	return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	setRetainInstance(true);
	boardPost = getArguments().getParcelable(
		DisBoardsConstants.BOARD_POST_KEY);
	if (boardPost != null) {
	    boardPostComments = boardPost.getComments();
	    adapter = new BoardPostListAdapater(getSherlockActivity(),
		    R.layout.board_post_comment_layout, boardPostComments);
	    setListAdapter(adapter);
	} else {

	}

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
		    if(content != null){
			boardPostCommentHolder.commentContentTextView.setText(Html
				    .fromHtml(content));
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
	    String userWhoThis = "";
	    if (usersWhoThisd != null && usersWhoThisd.length > 0) {
		String users = TextUtils.join(",", usersWhoThisd);
		users += " this'd this";

	    }
	    return userWhoThis;
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
