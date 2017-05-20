package com.drownedinsound.ui.post;

import com.drownedinsound.BoardPostItem;
import com.drownedinsound.BordPostCommentListTypeFactory;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

class BoardPostAdapter extends RecyclerView.Adapter<BaseBoardPostHolder> {

    private List<BoardPostItem> boardPostItems;

    private final BordPostCommentListTypeFactory bordPostCommentListTypeFactory;

    BoardPostAdapter(BordPostCommentListTypeFactory bordPostCommentListTypeFactory) {
        this.bordPostCommentListTypeFactory = bordPostCommentListTypeFactory;
        this.boardPostItems = new ArrayList<>();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setComments(List<BoardPostItem> comments) {
        this.boardPostItems = comments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return boardPostItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return boardPostItems.get(position).getType(bordPostCommentListTypeFactory);
    }

    @Override
    public BaseBoardPostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        return bordPostCommentListTypeFactory.createViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(BaseBoardPostHolder holder, int position) {
        holder.bind(boardPostItems.get(position));
    }



}
