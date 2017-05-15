package com.drownedinsound.ui.post;

import com.drownedinsound.BoardPostItem;
import com.drownedinsound.R;
import com.drownedinsound.TypeFactory;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class BoardPostView implements BoardPostContract.View {

    private BoardPostAdapter adapter;

    @BindView(R.id.loading_layout)
    DisBoardsLoadingLayout loadingLayout;

    @BindView(R.id.board_post_connection_error_text_view)
    TextView connectionErrorTextView;

    @BindView(R.id.board_post_comment_list)
    RecyclerView commentsList;

    private BoardPostContract.Presenter presenter;

    BoardPostView(View view, TypeFactory typeFactory) {
        ButterKnife.bind(this, view);
        adapter = new BoardPostAdapter(typeFactory);
        commentsList.setAdapter(adapter);
        loadingLayout.setContentView(commentsList);
    }

    @Override
    public void setPresenter(BoardPostContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showLoadingProgress(boolean show) {
        if(show) {
            loadingLayout.showAnimatedViewAndHideContent();
        } else {
            loadingLayout.hideAnimatedViewAndShowContent();
        }
    }

    @Override
    public void showBoardPostItems(List<BoardPostItem> items) {
        adapter.setComments(items);
    }

    @OnClick(R.id.back_button)
    void backAction() {
        presenter.handleBackAction();
    }

    @OnClick(R.id.refresh_board_posts_button)
    void refreshButtonAction() {
        presenter.handleRefreshAction();
    }


    @Override
    public void showErrorView() {
        connectionErrorTextView.setVisibility(View.VISIBLE);
    }

}
