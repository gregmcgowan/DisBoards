package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostComment;
import com.drownedinsound.ui.controls.ActiveTextView;
import com.drownedinsound.utils.StringUtils;
import com.drownedinsound.utils.UiUtils;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gregmcgowan on 13/08/15.
 */
public class BoardPostAdapter extends RecyclerView.Adapter<BaseBoardPostHolder> {

    private static final int HIGHLIGHTED_COMMENT_ANIMATION_LENGTH = 2000;

    private static final int INITIAL_POST_VIEW_TYPE = 1;

    private static final int COMMENT_VIEW_TYPE = 2;

    private List<BoardPostComment> comments;

    private BoardPost boardPost;

    private Context context;

    private ThisACommentActionListener thisACommentActionListener;

    private ReplyToCommentActionListener replyToCommentActionListener;

    private ActiveTextView.OnLinkClickedListener onLinkClickedListener;

    public BoardPostAdapter(Context context) {
        this.context = context;
        this.comments = new ArrayList<>();
    }

    public BoardPostComment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
            if (commentId.equals(comment.getCommentID())) {
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

    public void setOnLinkClickedListener(
            ActiveTextView.OnLinkClickedListener onLinkClickedListener) {
        this.onLinkClickedListener = onLinkClickedListener;
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? INITIAL_POST_VIEW_TYPE : COMMENT_VIEW_TYPE;
    }

    @Override
    public BaseBoardPostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == INITIAL_POST_VIEW_TYPE) {
            View rowView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.board_post_initial_comment_layout, parent, false);

            BoardPostInitialCommentHolder boardPostInitialHolder = new BoardPostInitialCommentHolder(rowView);
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
        } else {
            View rowView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.board_post_comment_layout, parent, false);
            BoardPostCommentHolder boardPostCommentHolder = new BoardPostCommentHolder(rowView);
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
            rowView.setTag(boardPostCommentHolder);
            rowView.setOnClickListener(null);

            return boardPostCommentHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseBoardPostHolder holder, int position) {
        BoardPostComment comment = comments.get(position);
        String title = comment.getTitle();
        if (!StringUtils.isEmpty(title)) {
            title = Html.fromHtml(title).toString();
        }
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

        String dateAndTime = comment.getDateAndTime();

        if (holder instanceof BoardPostInitialCommentHolder) {
            BoardPostInitialCommentHolder boardPostInitialHolder
                    = (BoardPostInitialCommentHolder) holder;
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
                    .setLinkClickedListener(onLinkClickedListener);

        } else if (holder instanceof BoardPostCommentHolder) {
            BoardPostCommentHolder boardPostCommentHolder
                    = (BoardPostCommentHolder) holder;
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

            boardPostCommentHolder.itemView.setOnClickListener(
                            new AllCommentClickListener(
                                    boardPostCommentHolder.actionRelativeLayout,
                                    comment));
            boardPostCommentHolder.thisTextView.setOnClickListener(
                    new ThisACommentClickListener(comment, thisACommentActionListener));

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
                    .setLinkClickedListener(onLinkClickedListener);
        }
    }


}
