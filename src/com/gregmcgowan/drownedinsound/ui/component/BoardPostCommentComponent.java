package com.gregmcgowan.drownedinsound.ui.component;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.R;
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;
import com.gregmcgowan.drownedinsound.utils.UiUtils;

/**
 * This will contain the UI for representing a BoardPost comment on screen
 * 
 * @author Greg
 * 
 */
public class BoardPostCommentComponent extends LinearLayout {

    private LinearLayout whitespaceLayout;
    private View topDividerView;
    private TextView commentTitleTextView;
    private TextView commentContentTextView;
    private TextView commentAuthorTextView;
    private TextView commentDateTimeTextView;
    private TextView commentThisSectionTextView;
    private View bottomDividerView;

    private BoardPostComment boardPostComment;

    public BoardPostCommentComponent(Context context) {
	super(context);
	createUI(context);
    }

    public BoardPostCommentComponent(Context context,
	    BoardPostComment boardPostComment) {
	super(context);
	this.boardPostComment = boardPostComment;
	createUI(context);
	setData();
	invalidate();
    }

    public BoardPostCommentComponent(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    public BoardPostCommentComponent(Context context, AttributeSet attrs,
	    int defStyle) {
	super(context, attrs, defStyle);
    }
    
    private void createUI(Context context) {
	View.inflate(context, R.layout.board_post_comment_layout, this);
	setWhitespaceLayout((LinearLayout)findViewById(R.id.board_post_comment_whitespace_section));
	setCommentTitleTextView((TextView) findViewById(R.id.board_post_comment_content_title));
	setCommentContentTextView((TextView) findViewById(R.id.board_post_comment_content));
	setCommentAuthorTextView((TextView) findViewById(R.id.board_post_comment_author_text_view));
	setCommentDateTimeTextView((TextView) findViewById(R.id.board_post_comment_date_time_text_view));
	setCommentThisSectionTextView((TextView) findViewById(R.id.board_post_comment_this_view));
	setTopDividerView(findViewById(R.id.board_post_comment_content_divider_top));
    }

    private void setData() {
	if(boardPostComment != null){
	    int level = boardPostComment.getCommentLevel();
	    int commentLevelIndentPx = UiUtils.convertDpToPixels(getContext().getResources(),4);
	    for (int i = 0; i < level; i++) {
                View spacer = new View(getContext());
                spacer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(commentLevelIndentPx,
                    LayoutParams.MATCH_PARENT));
                whitespaceLayout.addView(spacer, i);
            }
	    
	    setCommentTitle(boardPostComment.getTitle());
	    setContent(boardPostComment.getContent());
	    setAuthor(boardPostComment.getAuthorUsername());
	    setUserWhoThis(boardPostComment.getUsersWhoHaveThissed());
	    //TODO date time and this section
	    //setDateTime(boardPostComment.getDateAndTimeOfComment());
	}
    }
 
    private LinearLayout getWhitespaceLayout() {
	return whitespaceLayout;
    }

    private void setWhitespaceLayout(LinearLayout whitespaceLayout) {
	this.whitespaceLayout = whitespaceLayout;
    }

    private View getTopDividerView() {
	return topDividerView;
    }

    private void setTopDividerView(View topDividerView) {
	this.topDividerView = topDividerView;
    }

    public void setCommentTitle(String title) {
	if (commentTitleTextView != null) {
	    commentTitleTextView.setText(title);
	}
    }

    private void setCommentTitleTextView(TextView commentTitleTextView) {
	this.commentTitleTextView = commentTitleTextView;
    }

    public void setContent(String content) {
	if (commentContentTextView != null) {
	    if(!TextUtils.isEmpty(content)){
		commentContentTextView.setText(Html.fromHtml(content));
	    } else {
		commentContentTextView.setVisibility(View.GONE);
	    }
	}
    }

    private void setCommentContentTextView(TextView commentContentTextView) {
	this.commentContentTextView = commentContentTextView;
    }

    private void setCommentAuthorTextView(TextView commentAuthorTextView) {
	this.commentAuthorTextView = commentAuthorTextView;
    }

    public void setAuthor(String author) {
	if (commentAuthorTextView != null) {
	    commentAuthorTextView.setText(author);
	}
    }

    private void setCommentDateTimeTextView(TextView commentDateTimeTextView) {
	this.commentDateTimeTextView = commentDateTimeTextView;
    }

    public void setDateTime(String dateTime) {
	if (commentDateTimeTextView != null) {
	    commentDateTimeTextView.setText(dateTime);
	}
    }

    private void setCommentThisSectionTextView(
	    TextView commentThisSectionTextView) {
	this.commentThisSectionTextView = commentThisSectionTextView;
    }
    
    public void setUserWhoThis(String[] usersWhoThisd){
	if(usersWhoThisd != null && usersWhoThisd.length > 0){
	    String users = TextUtils.join(",", usersWhoThisd);  
	    users += " this'd this"; 
	    commentThisSectionTextView.setText(users);
	} else {
	    commentThisSectionTextView.setVisibility(View.VISIBLE);
	}
	
    }
    
    public void setBottomDividerView(View bottomDividerView) {
	this.bottomDividerView = bottomDividerView;
    }

    public void setBottomDividerViewVisibility(int visiblity){
	if(bottomDividerView != null) {
	    bottomDividerView.setVisibility(visiblity);
	}
    }
    
    public void setBoardPostComment(BoardPostComment boardPostComment) {
	this.boardPostComment = boardPostComment;
    }
    


}
