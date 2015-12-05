package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardPostComment;
import com.drownedinsound.ui.controls.ActiveTextView;
import com.drownedinsound.utils.UiUtils;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

;

/**
 * Created by gregmcgowan on 13/08/15.
 */
public class BoardPostAdapter extends BaseAdapter {

    private static final int HIGHLIGHTED_COMMENT_ANIMATION_LENGTH = 2000;

    private List<BoardPostComment> comments;

    private BoardPost boardPost;

    private LayoutInflater inflater;

    private Context context;

    private ThisACommentActionListener thisACommentActionListener;

    private ReplyToCommentActionListener replyToCommentActionListener;

    public BoardPostAdapter(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.comments = new ArrayList<>();
    }

    @Override
    public BoardPostComment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return comments.size();
    }


    public void setComments(ArrayList<BoardPostComment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void setBoardPost(BoardPost boardPost) {
        this.boardPost = boardPost;
    }

    public int getIndexOfCommentId(String commentId) {
        int index = 0;
        for (BoardPostComment comment : comments) {
            if (commentId.equals(comment.getId())) {
                break;
            }
            index++;
        }
        return index;
    }

    public void setThisACommentActionListener(
            ThisACommentActionListener thisACommentActionListener) {
        this.thisACommentActionListener = thisACommentActionListener;
    }

    public void setReplyToCommentActionListener(
            ReplyToCommentActionListener replyToCommentActionListener) {
        this.replyToCommentActionListener = replyToCommentActionListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BoardPostComment comment = comments.get(position);
        View boardPostSummaryRowView = convertView;

        boolean isFirstComment = position == 0;
        BoardPostCommentHolder boardPostCommentHolder = null;
        BoardPostInitialCommentHolder boardPostInitialHolder = null;

        if (boardPostSummaryRowView == null) {
            if (!isFirstComment) {
                boardPostSummaryRowView = inflater.inflate(
                        R.layout.board_post_comment_layout, null);
                boardPostCommentHolder = inflateBoardPostCommentHolder(
                        boardPostSummaryRowView, position);
            } else {
                boardPostSummaryRowView = inflater.inflate(
                        R.layout.board_post_initial_comment_layout, null);
                boardPostInitialHolder = inflateBoardPostInitialCommentHolder(
                        boardPostSummaryRowView);
            }

        } else {
            if (isFirstComment) {
                if (boardPostSummaryRowView.getTag() instanceof BoardPostCommentHolder) {
                    boardPostSummaryRowView = inflater.inflate(
                            R.layout.board_post_initial_comment_layout,
                            null);
                    boardPostInitialHolder = inflateBoardPostInitialCommentHolder(
                            boardPostSummaryRowView);
                } else {
                    boardPostInitialHolder
                            = (BoardPostInitialCommentHolder) boardPostSummaryRowView
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
            if (content == null) {
                content = "";
            }
            if (!TextUtils.isEmpty(content)) {
                content = Html.fromHtml(content).toString();
                int lastLineFeed = content.lastIndexOf("\n");
                if (lastLineFeed != -1 && (lastLineFeed == content.length() - 1)) {
                    content = content.substring(0, content.length() - 2);
                }
            }

            String dateAndTime = comment.getDateAndTimeOfComment();

            if (!isFirstComment) {
                boardPostCommentHolder.commentAuthorTextView
                        .setText(author);
                boardPostCommentHolder.commentTitleTextView.setText(title);

                boardPostCommentHolder.commentContentTextView.setText(content);

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
                        context.getResources(), 4);
                for (int i = 0; i < level; i++) {
                    View spacer = new View(context);
                    spacer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                            commentLevelIndentPx, LinearLayout.LayoutParams.MATCH_PARENT));
                    boardPostCommentHolder.whitespaceLayout.addView(spacer,
                            i);
                }
                LinearLayout commentLayout = (LinearLayout) boardPostSummaryRowView
                        .findViewById(R.id.board_post_comment_comment_section);

                boardPostSummaryRowView
                        .setOnClickListener(
                                new AllCommentClickListener(
                                        boardPostCommentHolder.actionRelativeLayout,
                                        commentLayout,
                                        comment));
                boardPostCommentHolder.thisTextView.setOnClickListener(
                        new ThisACommentClickListener(comment,thisACommentActionListener));

                boardPostCommentHolder.replyTextView.setOnClickListener(
                        new ReplyToCommentClickListener(comment, replyToCommentActionListener)
                );

                boolean actionSectionVisible = comment
                        .isActionSectionVisible();
                if (actionSectionVisible) {
                    boardPostCommentHolder.actionRelativeLayout
                            .setVisibility(View.VISIBLE);
                } else {
                    boardPostCommentHolder.actionRelativeLayout
                            .setVisibility(View.GONE);
                }

                if (comment.isDoHighlightedAnimation()) {
                    final TransitionDrawable transitionDrawable
                            = (TransitionDrawable) boardPostCommentHolder.commentSection
                            .getBackground();
                    transitionDrawable
                            .startTransition(HIGHLIGHTED_COMMENT_ANIMATION_LENGTH / 2);
                    boardPostCommentHolder.commentSection.postDelayed(
                            new Runnable() {

                                @Override
                                public void run() {
                                    transitionDrawable
                                            .reverseTransition(
                                                    HIGHLIGHTED_COMMENT_ANIMATION_LENGTH / 2);

                                }

                            }, HIGHLIGHTED_COMMENT_ANIMATION_LENGTH / 2);
                    comment.setDoHighlightedAnimation(false);
                }
                boardPostCommentHolder.commentContentTextView
                        .setLinkClickedListener(new ActiveTextView.OnLinkClickedListener() {
                            @Override
                            public void onClick(String url) {
                                // handleLinkClicked(url);
                            }
                        });
            } else {
                dateAndTime = boardPost.getDateOfPost();
                String numberOfReplies = boardPost.getNumberOfReplies()
                        + " replies";
                boardPostInitialHolder.commentAuthorTextView
                        .setText(author);
                boardPostInitialHolder.commentTitleTextView.setText(title);
                boardPostInitialHolder.commentContentTextView.setText(content);
                boardPostInitialHolder.commentDateTimeTextView
                        .setText(dateAndTime);
                boardPostInitialHolder.noOfCommentsTextView
                        .setText(numberOfReplies);
                boardPostInitialHolder.commentContentTextView
                        .setLinkClickedListener(new ActiveTextView.OnLinkClickedListener() {
                            @Override
                            public void onClick(String url) {
                                // handleLinkClicked(url);
                            }
                        });
            }


        }
        return boardPostSummaryRowView;
    }


    private BoardPostCommentHolder inflateBoardPostCommentHolder(View rowView,
            int listPosition) {

        BoardPostCommentHolder boardPostCommentHolder = new BoardPostCommentHolder();
        boardPostCommentHolder.commentTitleTextView = (TextView) rowView
                .findViewById(R.id.board_post_comment_content_title);
        boardPostCommentHolder.commentContentTextView = (ActiveTextView) rowView
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
        boardPostCommentHolder.thisTextView = (TextView) rowView
                .findViewById(R.id.board_post_comment_this);
        boardPostCommentHolder.commentSection = (LinearLayout) rowView
                .findViewById(R.id.board_post_comment_content_section);
        LinearLayout commentLayout = (LinearLayout) rowView
                .findViewById(R.id.board_post_comment_comment_section);
        rowView.setTag(boardPostCommentHolder);
        rowView.setOnClickListener(null);
//        rowView.setOnClickListener(new CommentSectionClickListener(
//                listPosition, new AllCommentClickListener(
//                new WeakReference<>(
//                        boardPostCommentHolder.actionRelativeLayout),
//                new WeakReference<View>(commentLayout),
//                new WeakReference<>(this))));
        return boardPostCommentHolder;
    }

    private BoardPostInitialCommentHolder inflateBoardPostInitialCommentHolder(
            View rowView) {

        BoardPostInitialCommentHolder boardPostInitialHolder = new BoardPostInitialCommentHolder();

        boardPostInitialHolder.commentTitleTextView = (TextView) rowView
                .findViewById(R.id.board_post_initial_comment_title);
        boardPostInitialHolder.commentAuthorTextView = (TextView) rowView
                .findViewById(R.id.board_post_initial_comment_author_subheading);
        boardPostInitialHolder.commentContentTextView = (ActiveTextView) rowView
                .findViewById(R.id.board_post_initial_comment_text);
        boardPostInitialHolder.commentDateTimeTextView = (TextView) rowView
                .findViewById(R.id.board_post_initial_comment_date_time_subheading);
        boardPostInitialHolder.noOfCommentsTextView = (TextView) rowView
                .findViewById(R.id.board_post_initial_comment_replies_subheading);
        rowView.setTag(boardPostInitialHolder);
        return boardPostInitialHolder;
    }
}
