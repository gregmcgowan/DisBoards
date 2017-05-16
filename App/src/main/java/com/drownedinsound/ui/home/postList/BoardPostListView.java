package com.drownedinsound.ui.home.postList;

import com.drownedinsound.BoardPostSummaryModel;
import com.drownedinsound.R;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BoardPostListView implements BoardPostListContract.View,
        BoardPostListAdapter.BoardPostListListener {

    @BindView(R.id.loading_layout)
    DisBoardsLoadingLayout loadingLayout;

    @BindView(R.id.board_list_connection_error_text_view)
    TextView connectionErrorTextView;

    @BindView(R.id.board_post_summary_list)
    RecyclerView listView;

    @BindView(R.id.swipeToRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private BoardPostListAdapter adapter;

    private final @BoardPostList.BoardPostListType String boardListType;

    private BoardPostListContract.Presenter presenter;

    public BoardPostListView(View rootView, String boardListType) {
        this.boardListType = boardListType;
        ButterKnife.bind(this, rootView);

        loadingLayout.setContentView(listView);

        swipeRefreshLayout.setColorSchemeResources(R.color.highlighted_blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefreshAction();
            }
        });
        Context context = rootView.getContext();

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
    public void showBoardPostSummaries(@NonNull List<BoardPostSummaryModel> boardPostsSummaries) {
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
    public void boardPostSelected(int position, BoardPostSummaryModel boardPostSummary) {
       presenter.handleBoardPostSelected(boardPostSummary);
    }
}
