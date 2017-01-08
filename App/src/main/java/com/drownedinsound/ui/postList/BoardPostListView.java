package com.drownedinsound.ui.postList;

import com.drownedinsound.R;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gregmcgowan on 07/01/2017.
 */

public class BoardPostListView implements BoardPostListContract.View {

    @InjectView(R.id.loading_layout)
    DisBoardsLoadingLayout loadingLayout;

    @InjectView(R.id.board_list_connection_error_text_view)
    TextView connectionErrorTextView;

    @InjectView(R.id.board_post_summary_list)
    RecyclerView listView;

    @InjectView(R.id.swipeToRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private Drawable readDrawable;

    private Drawable unreadDrawable;

    private BoardPostListAdapter adapter;

    private boolean dualPaneMode;

    private boolean wasInDualPaneMode;

    private int currentlySelectedPost;

    private String postId;

    private int lastPageFetched;

    private int pageIndex;

    private int firstVisiblePosition;

    private final @BoardPostList.BoardPostListType String boardListType;

    public BoardPostListView(View rootView, String boardListType) {
        this.boardListType = boardListType;
        ButterKnife.inject(this, rootView);

        loadingLayout.setContentView(listView);

        swipeRefreshLayout.setColorSchemeResources(R.color.highlighted_blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefreshAction();
            }
        });
        Context context = rootView.getContext();
        readDrawable = context.getResources().getDrawable(
                R.drawable.white_circle_blue_outline);
        unreadDrawable = context.getResources().getDrawable(
                R.drawable.filled_blue_circle);

        adapter = new BoardPostListAdapter(context);
        //adapter.setBoardPostListListner(this);

        listView.setLayoutManager(new LinearLayoutManager(listView.getContext()));
        listView.setAdapter(adapter);

    }

    private void doRefreshAction() {
    }

    @Override
    public String getBoardListType() {
        return null;
    }

    @Override
    public void showBoardPostSummaries(List<BoardPostSummary> boardPostsSummaries) {

    }

    @Override
    public void showLoadingProgress(boolean show) {

    }

    @Override
    public void showErrorView() {

    }


    @Override
    public void scrollToPostAt(int position) {

    }
}
