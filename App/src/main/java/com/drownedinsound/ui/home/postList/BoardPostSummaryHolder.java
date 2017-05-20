package com.drownedinsound.ui.home.postList;

import com.drownedinsound.BoardPostSummaryModel;
import com.drownedinsound.R;
import com.drownedinsound.utils.UiUtils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

class BoardPostSummaryHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.board_post_list_row_read_marker)
    View postReadMarkerView;

    @BindView(R.id.board_post_list_row_title)
    TextView titleTextView;

    @BindView(R.id.board_post_list_row_author)
    TextView authorTextView;

    @BindView(R.id.board_post_list_row_sticky)
    TextView stickyTextView;

    @BindView(R.id.board_post_list_row_number_of_replies)
    TextView numberOfRepliesTextView;

    @BindView(R.id.board_post_list_row_last_updated)
    TextView lastUpdatedTextView;

    BoardPostSummaryHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bind(final int position, final BoardPostSummaryModel boardPostSummary,
            final BoardPostListAdapter.BoardPostListListener boardPostListListner) {
        titleTextView.setText(boardPostSummary.getTitle());
        authorTextView.setText(boardPostSummary.getAuthorUsername());
        numberOfRepliesTextView.setText(boardPostSummary.getAuthorUsername());
        lastUpdatedTextView.setText(boardPostSummary.getLastUpdatedText());
        stickyTextView
                .setVisibility(boardPostSummary.isSticky() ? View.VISIBLE : View.GONE);

        final Context context = itemView.getContext();
        if (boardPostSummary.getMarkAsRead()) {
            UiUtils.setBackgroundDrawable(postReadMarkerView,
                    ContextCompat.getDrawable(context, R.drawable.white_circle_blue_outline));
        } else {
            UiUtils.setBackgroundDrawable(postReadMarkerView,
                    ContextCompat.getDrawable(context, R.drawable.filled_blue_circle));
        }

        if (position % 2 == 0) {
            UiUtils.setBackgroundDrawable(itemView,
                    ContextCompat.getDrawable(context, R.drawable.board_list_row_selector));
        } else {
            UiUtils.setBackgroundDrawable(itemView,
                    ContextCompat
                            .getDrawable(context, R.drawable.alternate_board_list_row_selector));
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.setBackgroundDrawable(postReadMarkerView,
                        ContextCompat.getDrawable(context, R.drawable.white_circle_blue_outline));
                if (boardPostListListner != null) {
                    boardPostListListner.boardPostSelected(position, boardPostSummary);
                }
            }
        });
    }

}
