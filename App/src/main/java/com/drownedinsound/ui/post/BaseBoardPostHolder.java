package com.drownedinsound.ui.post;

import com.drownedinsound.BoardPostItem;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public abstract class BaseBoardPostHolder <T extends BoardPostItem> extends RecyclerView.ViewHolder {

    BaseBoardPostHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    abstract void bind(T item);
}
