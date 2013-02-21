package com.gregmcgowan.drownedinsound.ui.fragments;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;
import com.gregmcgowan.drownedinsound.ui.component.BoardPostCommentComponent;

/**
 * Represents a board fragment. This will consist of the board post and all the
 * comments made against that post.
 * 
 * @author Greg
 * 
 */
public class BoardPostFragment extends SherlockFragment {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardPost";

    private TextView boardPostTitleTextView;
    private TextView boardPostTextTextView;
    private TextView boardPostAuthorSubHeadingTextView;
    private LinearLayout boardPostCommentsSection;
    private ScrollView parentScrollView;
    private BoardPost boardPost;

    private View rootView;

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

	boardPost = getArguments().getParcelable(
		DisBoardsConstants.BOARD_POST_KEY);

	boardPostTitleTextView = (TextView) rootView
		.findViewById(R.id.board_post_heading);
	boardPostAuthorSubHeadingTextView = (TextView) rootView
		.findViewById(R.id.board_post_author_subheading);
	boardPostTextTextView = (TextView) rootView
		.findViewById(R.id.board_post_text);
	parentScrollView = (ScrollView) rootView
		.findViewById(R.id.board_post_scroll_view);
	parentScrollView.setSmoothScrollingEnabled(true);
	boardPostCommentsSection = (LinearLayout) rootView
		.findViewById(R.id.board_post_comment_section);

	if (boardPost != null) {
	    String title = boardPost.getTitle();
	    String text = boardPost.getContent();
	    String author = boardPost.getAuthorUsername();
	    boardPostTitleTextView.setText(title);
	    boardPostAuthorSubHeadingTextView.setText(author);
	    boardPostTextTextView.setText(Html.fromHtml(text));
	    List<BoardPostComment> comments = boardPost.getComments();
	    int noOfComments = comments.size();
	    //int currentComment = 0;
	    //Log.d("GREG", "number of comments ="+noOfComments);
	    boardPostCommentsSection.removeAllViews();
	    for (BoardPostComment comment : comments) {
		//currentComment++;
		BoardPostCommentComponent boardPostUIComponent = new BoardPostCommentComponent(
			getSherlockActivity(), comment);
		boardPostCommentsSection.addView(boardPostUIComponent);
	    }
	} else {

	}

    }

}
