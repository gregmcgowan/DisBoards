package com.drownedinsound.ui.home.postList;

import com.drownedinsound.R;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class BoardPostListView implements BoardPostListContract.View,
        BoardPostListAdapter.BoardPostListListener {

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

    private BoardPostListContract.Presenter presenter;

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

        readDrawable = ContextCompat.getDrawable(context,
                R.drawable.white_circle_blue_outline);
        unreadDrawable = ContextCompat.getDrawable(context,
                R.drawable.filled_blue_circle);

        adapter = new BoardPostListAdapter(context);
        adapter.setBoardPostListListner(this);

        listView.setAdapter(adapter);
    }

    @Override
    public void setPresenter(BoardPostListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    private void doRefreshAction() {
        presenter.handleRefresh();
    }

    @Override
    public String getBoardListType() {
        return boardListType;
    }

    @Override
    public void showBoardPostSummaries(List<BoardPostSummary> boardPostsSummaries) {
        adapter.setBoardPosts(boardPostsSummaries);
    }

    @Override
    public void showLoadingProgress(boolean show) {
        if(show) {
            loadingLayout.showAnimatedViewAndHideContent();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            loadingLayout.hideAnimatedViewAndShowContent();
        }
    }

    @Override
    public void showErrorView() {
        connectionErrorTextView.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
    }


    @Override
    public void scrollToPostAt(int position) {

    }

    @Override
    public void boardPostSelected(int position, BoardPostSummary boardPostSummary) {
        presenter.handleBoardPostSelected(boardPostSummary);
    }
}
