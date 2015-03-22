package com.drownedinsound.ui.summarylist;

import com.drownedinsound.R;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.utils.UiUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by gregmcgowan on 23/10/2013.
 */
public class BoardPostSummaryListAdapter extends ArrayAdapter<BoardPost> {

    private List<BoardPost> summaries;

    private WeakReference<Context> contextWeakReference;

    private Drawable whiteBackgroundSelector;

    private Drawable alternateColorSelector;

    private Drawable readDrawable;

    private Drawable unreadDrawable;

    public BoardPostSummaryListAdapter(Context context,
            int textViewResourceId, List<BoardPost> boardPostSummaries) {
        super(context, textViewResourceId, boardPostSummaries);
        this.contextWeakReference = new WeakReference<Context>(context);
        this.summaries = boardPostSummaries;
        this.readDrawable = context.getResources().getDrawable(
                R.drawable.white_circle_blue_outline);
        this.unreadDrawable = context.getResources().getDrawable(
                R.drawable.filled_blue_circle);
        this.whiteBackgroundSelector = getContext().getResources().getDrawable(
                R.drawable.board_list_row_selector);
        this.alternateColorSelector = context.getResources()
                .getDrawable(R.drawable.alternate_board_list_row_selector);
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
        Context context = contextWeakReference.get();
        if (context == null) {
            Log.w(DisBoardsConstants.LOG_TAG_PREFIX,
                    "Null context in board post summary list adapter");
            return null;
        }
        if (boardPostSummaryRowView == null) {
            if (context != null) {
                LayoutInflater vi = (LayoutInflater) context
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
            }
        } else {
            holder = (BoardPostSummaryHolder) boardPostSummaryRowView
                    .getTag();
        }

        if (summary != null) {
            String title = summary.getTitle();
            String authorusername = "by " + summary.getAuthorUsername();
            int numberOfReplies = summary.getNumberOfReplies();
            String numberOfRepliesText;
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
            boolean markAsRead = lastViewedTime > 0
                    && lastViewedTime >= lastUpdatedTime;

            holder.titleTextView.setText(title);
            holder.authorTextView.setText(authorusername);
            holder.numberOfRepliesTextView.setText(numberOfRepliesText);
            holder.lastUpdatedTextView.setText(lastUpdatedText);
            holder.stickyTextView.setVisibility(stickyVisible);
            if (markAsRead) {
                UiUtils.setBackgroundDrawable(holder.postReadMarkerView,readDrawable);
            } else {
                UiUtils.setBackgroundDrawable(holder.postReadMarkerView,unreadDrawable);
            }

            if (position % 2 == 0) {
                whiteBackgroundSelector = context
                            .getResources().getDrawable(
                                    R.drawable.board_list_row_selector);
                UiUtils.setBackgroundDrawable(boardPostSummaryRowView,whiteBackgroundSelector);
            } else {
                alternateColorSelector = context
                            .getResources()
                            .getDrawable(
                                    R.drawable.alternate_board_list_row_selector);
                UiUtils.setBackgroundDrawable(boardPostSummaryRowView,alternateColorSelector);
            }

        }
        return boardPostSummaryRowView;
    }
}
