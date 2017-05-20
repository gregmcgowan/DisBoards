package com.drownedinsound.ui.home.postList;

import com.drownedinsound.BoardPostSummaryModel;
import com.drownedinsound.R;
import com.drownedinsound.utils.CollectionUtils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

class BoardPostListAdapter extends RecyclerView.Adapter<BoardPostSummaryHolder> {

    private List<BoardPostSummaryModel> boardPosts = new ArrayList<>();

    private BoardPostListListener boardPostListListner;

    @Override
    public long getItemId(int position) {
        return position;
    }

    void setBoardPosts(List<BoardPostSummaryModel> newPosts) {
        if (!CollectionUtils.equals(newPosts, boardPosts)) {
            boardPosts = newPosts;
            notifyDataSetChanged();
        }
    }

    void setBoardPostListListener(BoardPostListListener boardPostListListner) {
        this.boardPostListListner = boardPostListListner;
    }

    @Override
    public BoardPostSummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View boardPostSummaryRowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_list_row, parent, false);
        return new BoardPostSummaryHolder(boardPostSummaryRowView);
    }

    @Override
    public void onBindViewHolder(final BoardPostSummaryHolder holder, final int position) {
        final BoardPostSummaryModel boardPostModel = (BoardPostSummaryModel) getItem(position);
        if (boardPostModel != null) {
            holder.bind(position, boardPostModel, boardPostListListner);
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

        void boardPostSelected(int position, BoardPostSummaryModel boardPostSummary);
    }

}
