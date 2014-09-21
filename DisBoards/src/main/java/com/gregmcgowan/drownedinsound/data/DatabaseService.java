package com.gregmcgowan.drownedinsound.data;

import com.gregmcgowan.drownedinsound.core.DisBoardsApp;
import com.gregmcgowan.drownedinsound.core.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.events.RetrievedFavouritesEvent;
import com.gregmcgowan.drownedinsound.events.SetBoardPostFavouriteStatusResultEvent;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


/**
 * Created by gregmcgowan on 27/10/2013.
 */
public class DatabaseService extends IntentService {

    public static final String DATABASE_SERVICE_REQUESTED_KEY = "DATABASE_SERVICE_REQUESTED_KEY";

    public static final int SET_BOARD_POST_FAVOURITE_STATUS = 1;

    public static final int GET_FAVOURITE_BOARD_POSTS = 2;

    public static final int REMOVE_OLD_POSTS = 3;

    private static final String SERVICE_NAME = "DatabaseService";


    @Inject
    DatabaseHelper databaseHelper;

    @Inject
    EventBus eventBus;

    public DatabaseService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DisBoardsApp disBoardsApp = DisBoardsApp.getApplication(this);
        disBoardsApp.inject(this);

        int requestedService = intent.getIntExtra(
                DATABASE_SERVICE_REQUESTED_KEY, 0);
        switch (requestedService) {
            case SET_BOARD_POST_FAVOURITE_STATUS:
                setBoardPostFavouriteStatus(intent);
                break;
            case GET_FAVOURITE_BOARD_POSTS:
                doGetFavouriteBoardPostsAction(intent);
                break;
            default:
                break;
        }
    }

    private void setBoardPostFavouriteStatus(Intent intent) {
        BoardPost boardPost = intent.getParcelableExtra(DisBoardsConstants.BOARD_POST_KEY);
        boolean isFavourite = intent.getBooleanExtra(DisBoardsConstants.IS_FAVOURITE, false);
        boolean updated = databaseHelper.setBoardPostFavouriteStatus(boardPost, isFavourite);
        eventBus.post(new SetBoardPostFavouriteStatusResultEvent(updated, isFavourite));
    }


    private void doGetFavouriteBoardPostsAction(Intent intent) {
        List<BoardPost> favouritedBoardPosts = databaseHelper.getFavouritedBoardPosts();
        eventBus.post(new RetrievedFavouritesEvent(new ArrayList<>(favouritedBoardPosts)));
    }
}
