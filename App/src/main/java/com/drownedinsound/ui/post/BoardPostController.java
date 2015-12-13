package com.drownedinsound.ui.post;

import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.events.FailedToGetBoardPostEvent;
import com.drownedinsound.events.PostCommentEvent;
import com.drownedinsound.events.RetrievedBoardPostEvent;
import com.drownedinsound.qualifiers.ForDatabase;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.Ui;

import android.content.Intent;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.greenrobot.event.EventBus;

/**
 * Created by gregmcgowan on 02/10/15.
 */
@Singleton
public class BoardPostController extends BaseUIController {

    private DisApiClient disApiClient;

    private EventBus eventBus;

    private ExecutorService databaseExecutorService;

    @Inject
    public BoardPostController(EventBus eventBus,
            DisApiClient disApiClient, @ForDatabase ExecutorService dbExecutorService) {
        this.eventBus = eventBus;
        this.disApiClient = disApiClient;
        this.databaseExecutorService = dbExecutorService;
    }


    @Override
    public void onUiAttached(Ui ui) {
        if (ui instanceof BoardPostParentUi) {
            if (!eventBus.isRegistered(this)) {
                eventBus.registerSticky(this);
            }
        }
    }

    @Override
    public void onUiDetached(Ui ui) {
        if (ui instanceof BoardPostParentUi) {
            if (eventBus.isRegistered(this)) {
                eventBus.unregister(this);
            }
        }
    }

    public void thisAComment(BoardPostUI boardPostUI, BoardType boardType,
            String postID, String commentID) {
        boardPostUI.showLoadingProgress(true);
        int id = getId(boardPostUI);
        //disApiClient.thisAComment(postUrl, postID, commentID, boardType,id  );
    }

    public void loadBoardPost(BoardPostUI boardPostUI, String boardPostId,
            BoardType boardType) {
        boardPostUI.showLoadingProgress(true);
        int uiID = getId(boardPostUI);
        //disApiClient.getBoardPost(boardPostUrl, boardPostId, boardType, uiID);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(RetrievedBoardPostEvent event) {
        int callingID = event.getUiId();

        BoardPostUI boardPostUI = (BoardPostUI) findUi(callingID);
        if (boardPostUI != null) {
            BoardPost boardPost = event.getBoardPost();

            boolean showGoToLastCommentOption = event
                    .isDisplayGotToLatestCommentOption()
                    && boardPost.showGoToLastCommentOption();

            boardPostUI.showBoardPost(boardPost, -1);

            if (event.isCached()) {
                boardPostUI.showCachedPopup();
            }
            boardPostUI.showLoadingProgress(false);
//            if (showGoToLastCommentOption) {
//                hideAnimatedLogoAndShowList(new OnListShownHandler() {
//                    @Override
//                    public void doOnListShownAction() {
//                        displayScrollToHiddenCommentOption(true);
//                    }
//                });
//
//            } else {
//                hideAnimatedLogoAndShowList(new OnListShownHandler() {
//                    @Override
//                    public void doOnListShownAction() {
//                        //floatingReplyButton.show(true);
//                    }
//                });
//            }

        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(FailedToGetBoardPostEvent failedToGetBoardPostEvent) {
        BoardPostUI boardPostUI = (BoardPostUI) findUi(failedToGetBoardPostEvent.getCallingId());
        if (boardPostUI != null) {
            boardPostUI.showErrorView();
        }
    }

    public void replyToComment(ReplyToCommentUi replyToCommentUi,String boardPostId, String replyToCommentID, String commentTitle,
            String commentContent, BoardType boardType) {
        replyToCommentUi.showLoadingProgress(true);

        int uiID = getId(replyToCommentUi);

        //disApiClient.postComment(boardPostId,replyToCommentID,commentTitle,commentContent,boardType);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(PostCommentEvent postCommentEvent) {
        eventBus.removeStickyEvent(postCommentEvent);
        ReplyToCommentUi replyToCommentUi = (ReplyToCommentUi) findUi(postCommentEvent.getUiID());
        if(replyToCommentUi != null) {
            if(postCommentEvent.isSuccess()) {
                replyToCommentUi.hidePostCommentUi();
            } else {
                replyToCommentUi.handlePostCommentFailure();
            }

        }

    }
}
