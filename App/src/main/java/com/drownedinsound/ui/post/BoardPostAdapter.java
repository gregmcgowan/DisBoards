package com.drownedinsound.ui.post;

import com.drownedinsound.BoardPostItem;
import com.drownedinsound.TypeFactory;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

class BoardPostAdapter extends RecyclerView.Adapter<BaseBoardPostHolder> {

    private List<BoardPostItem> boardPostItems;

    private final TypeFactory typeFactory;

    BoardPostAdapter(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;
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
        return boardPostItems.get(position).getType(typeFactory);
    }

    @Override
    public BaseBoardPostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        return typeFactory.createViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(BaseBoardPostHolder holder, int position) {
        holder.bind(boardPostItems.get(position));
    }



}
