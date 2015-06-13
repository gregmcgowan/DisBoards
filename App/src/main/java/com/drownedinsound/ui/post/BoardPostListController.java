package com.drownedinsound.ui.post;

import android.content.Intent;

import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.events.RetrievedBoardPostSummaryListEvent;
import com.drownedinsound.qualifiers.ForDatabase;
import com.drownedinsound.ui.base.BaseUIController;
import com.drownedinsound.ui.base.Ui;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Created by gregmcgowan on 22/03/15.
 */
@Singleton
public class BoardPostListController extends BaseUIController {

    private DisApiClient disApiClient;
    private EventBus eventBus;
    private ExecutorService databaseExecutorService;

    @Inject
    public BoardPostListController(EventBus eventBus,
            DisApiClient disApiClient, @ForDatabase ExecutorService dbExecutorService) {
        this.eventBus = eventBus;
        this.disApiClient = disApiClient;
        this.databaseExecutorService = dbExecutorService;
    }

    @Override
    public void init(Intent intent) {

    }

    @Override
    public void onPause() {
        eventBus.unregister(this);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onUiAttached(Ui ui) {
        if(!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }


        if(ui instanceof BoardPostListUi) {
            BoardPostListUi boardPostListUi = (BoardPostListUi) ui;
            requestBoardSummaryPage(boardPostListUi,
                    boardPostListUi.getBoardList(),1,true,false,true);
        }
    }

    public void requestBoardSummaryPage(BoardPostListUi boardPostListUi,Board board, int page,boolean showLoadingProgress, boolean forceUpdate, boolean updateUI) {
        if(showLoadingProgress && page == 1) {
          boardPostListUi.showLoadingProgress(true);
        }
        int id = getId(boardPostListUi);
        Timber.d("Going to update id "+id);
        disApiClient.getBoardPostSummaryList(id, page, board, forceUpdate, updateUI);
    }

    @Override
    public void onUiDetached(Ui ui) {

    }

    public void onEventMainThread(RetrievedBoardPostSummaryListEvent event) {
       int callingID = event.getUiID();

       BoardPostListUi boardPostListUi = (BoardPostListUi) findUi(callingID);
       if(boardPostListUi != null) {
           Timber.d("Updated UI for " + event.getBoardType() + " if "+ callingID);
           List<BoardPost> boardPosts = event.getBoardPostSummaryList();
           if(boardPosts.size() > 0 ) {
               if(!event.isAppend()) {
                   boardPostListUi.setBoardPosts(boardPosts);
               } else {
                   boardPostListUi.appendBoardPosts(boardPosts);
               }
           } else {
              boardPostListUi.showErrorView();
           }
       }
    }
}
