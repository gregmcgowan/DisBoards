package com.drownedinsound.ui.post;

import com.drownedinsound.R;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostComment;
import com.drownedinsound.ui.base.DisBoardsLoadingLayout;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class BoardPostView implements BoardPostContract.View {

    private BoardPostAdapter adapter;

    @InjectView(R.id.loading_layout)
    DisBoardsLoadingLayout loadingLayout;

    @InjectView(R.id.board_post_connection_error_text_view)
    TextView connectionErrorTextView;

    @InjectView(R.id.board_post_comment_list)
    RecyclerView commentsList;

    private BoardPost boardPost;

    private String boardPostId;

    public BoardPostView(View view) {
        ButterKnife.inject(this, view);
        adapter = new BoardPostAdapter(view.getContext());
        commentsList.setAdapter(adapter);
        loadingLayout.setContentView(commentsList);
    }

    @Override
    public void showLoadingProgress(boolean show) {
        if(show) {
            loadingLayout.showAnimatedViewAndHideContent();
        } else {
            //loadingLayout.setContentShownListener(onContentShownListener);
            loadingLayout.hideAnimatedViewAndShowContent();
        }
    }

    @Override
    public void showBoardPost(BoardPost boardPost) {
        //TODO this is crap. rewrite
        this.boardPost = boardPost;
        connectionErrorTextView.setVisibility(View.GONE);
        adapter.setBoardPost(boardPost);

        BoardPostComment initialPost = new BoardPostComment();
        initialPost.setBoardPostID(boardPostId);
        initialPost
                .setAuthorUsername(boardPost
                        .getAuthorUsername());
        initialPost
                .setDateAndTime(boardPost
                        .getDateOfPost());
        initialPost.setContent(boardPost.getContent());
        initialPost.setTitle(boardPost
                .getTitle());
        initialPost.setBoardPost(boardPost);

        ArrayList<BoardPostComment> commentList = new ArrayList<>();
        commentList.add(initialPost);
        commentList.addAll(boardPost.getComments());

        adapter.setComments(commentList);
    }

    @OnClick(R.id.back_button)
    public void backAction() {


    }

    @OnClick(R.id.refresh_board_posts_button)
    public void refreshButtonAction() {
    }


    @Override
    public void showErrorView() {

    }

    @Override
    public void showThisACommentFailed() {

    }

    @Override
    public void showGoToLatestCommentOption() {

    }

    @Override
    public void setOnContentShownListener(
            DisBoardsLoadingLayout.ContentShownListener contentShownListener) {

    }

    @Override
    public boolean lastCommentIsVisible() {
        return false;
    }

    @Override
    public boolean userHasInteractedWithUI() {
        return false;
    }
}
