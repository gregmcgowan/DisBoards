package com.drownedinsound.ui.home.postList;

import com.drownedinsound.BoardPostSummaryModel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class BoardPostSummaryHolder extends RecyclerView.ViewHolder {

    BoardPostSummaryHolder(View itemView) {
        super(itemView);
    }

     View backgroundView;

     View postReadMarkerView;

     TextView titleTextView;

     TextView authorTextView;

     TextView stickyTextView;

     TextView numberOfRepliesTextView;

     TextView lastUpdatedTextView;

     BoardPostSummaryModel boardPost;

}
