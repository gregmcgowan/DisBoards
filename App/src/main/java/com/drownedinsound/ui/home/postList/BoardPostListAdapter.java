package com.drownedinsound.ui.home.postList;

import com.drownedinsound.BoardPostListModel;
import com.drownedinsound.R;
import com.drownedinsound.utils.CollectionUtils;
import com.drownedinsound.utils.UiUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class BoardPostListAdapter extends RecyclerView.Adapter<BoardPostSummaryHolder> {

    private List<BoardPostListModel> boardPosts;

    private Context context;

    private Drawable whiteBackgroundSelector;

    private Drawable alternateColorSelector;

    private Drawable readDrawable;

    private Drawable unreadDrawable;

    private BoardPostListListener boardPostListListner;

    BoardPostListAdapter(Context context) {
        this.context = context;
        this.boardPosts = new ArrayList<>();
        this.readDrawable = context.getResources().getDrawable(
                R.drawable.white_circle_blue_outline);
        this.unreadDrawable = context.getResources().getDrawable(
                R.drawable.filled_blue_circle);
        this.whiteBackgroundSelector = context.getResources().getDrawable(
                R.drawable.board_list_row_selector);
        this.alternateColorSelector = context.getResources()
                .getDrawable(R.drawable.alternate_board_list_row_selector);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setBoardPosts(List<BoardPostListModel> newPosts) {
        if (!CollectionUtils.equals(newPosts, boardPosts)) {
            boardPosts = newPosts;
            notifyDataSetChanged();
        }
    }

    public void setBoardPostListListner(
            BoardPostListListener boardPostListListner) {
        this.boardPostListListner = boardPostListListner;
    }

    @Override
    public BoardPostSummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View boardPostSummaryRowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_list_row, parent, false);

        BoardPostSummaryHolder holder = new BoardPostSummaryHolder(boardPostSummaryRowView);
        holder.backgroundView = boardPostSummaryRowView;

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

        return holder;
    }

    @Override
    public void onBindViewHolder(final BoardPostSummaryHolder holder, final int position) {
        final BoardPostListModel boardPostModel = (BoardPostListModel) getItem(position);
        if (boardPostModel != null) {
            holder.boardPost = boardPostModel;
            holder.titleTextView.setText(Html.fromHtml(boardPostModel.getTitle()));
            holder.authorTextView.setText(boardPostModel.getAuthorUsername());
            holder.numberOfRepliesTextView.setText(boardPostModel.getAuthorUsername());
            holder.lastUpdatedTextView.setText(boardPostModel.getLastUpdatedText());
            holder.stickyTextView
                    .setVisibility(boardPostModel.isSticky() ? View.VISIBLE : View.GONE);

            if (boardPostModel.getMarkAsRead()) {
                UiUtils.setBackgroundDrawable(holder.postReadMarkerView, readDrawable);
            } else {
                UiUtils.setBackgroundDrawable(holder.postReadMarkerView, unreadDrawable);
            }

            if (position % 2 == 0) {
                whiteBackgroundSelector = context
                        .getResources().getDrawable(
                                R.drawable.board_list_row_selector);
                UiUtils.setBackgroundDrawable(holder.backgroundView, whiteBackgroundSelector);
            } else {
                alternateColorSelector = context
                        .getResources()
                        .getDrawable(
                                R.drawable.alternate_board_list_row_selector);
                UiUtils.setBackgroundDrawable(holder.backgroundView, alternateColorSelector);
            }
            holder.backgroundView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UiUtils.setBackgroundDrawable(holder.postReadMarkerView, readDrawable);
                    if (boardPostListListner != null) {
                        boardPostListListner.boardPostSelected(position, boardPostModel);
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return boardPosts.size();
    }

    Object getItem(int position) {
        return boardPosts.get(position);
    }

    interface BoardPostListListener {

        void boardPostSelected(int position, BoardPostListModel boardPostSummary);
    }

}
