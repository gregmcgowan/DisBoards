package com.drownedinsound.ui.postList;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostSummary;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by gregmcgowan on 23/10/2013.
 */
public class BoardPostSummaryHolder extends RecyclerView.ViewHolder {

    public BoardPostSummaryHolder(View itemView) {
        super(itemView);
    }

    public View backgroundView;

    public View postReadMarkerView;

    public TextView titleTextView;

    public TextView authorTextView;

    public TextView stickyTextView;

    public TextView numberOfRepliesTextView;

    public TextView lastUpdatedTextView;

    public BoardPostSummary boardPost;

}
