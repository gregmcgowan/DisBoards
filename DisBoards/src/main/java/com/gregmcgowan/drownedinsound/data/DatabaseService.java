package com.gregmcgowan.drownedinsound.data;

import android.app.IntentService;
import android.content.Intent;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.events.RetrievedFavouritesEvent;
import com.gregmcgowan.drownedinsound.events.SetBoardPostFavouriteStatusResultEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by gregmcgowan on 27/10/2013.
 */
public class DatabaseService extends IntentService {

    public static final String DATABASE_SERVICE_REQUESTED_KEY = "DATABASE_SERVICE_REQUESTED_KEY";

    public static final int SET_BOARD_POST_FAVOURITE_STATUS = 1;
    public static final int GET_FAVOURITE_BOARD_POSTS = 2;
    public static final int REMOVE_OLD_POSTS = 3;

    private  static final String SERVICE_NAME = "DatabaseService";

    private DatabaseHelper databaseHelper;

    public DatabaseService() {
        super(SERVICE_NAME);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        int requestedService = intent.getIntExtra(
            DATABASE_SERVICE_REQUESTED_KEY, 0);
        switch(requestedService) {
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
       boolean isFavourite = intent.getBooleanExtra(DisBoardsConstants.IS_FAVOURITE,false);
       boolean updated =  databaseHelper.setBoardPostFavouriteStatus(boardPost,isFavourite);
       EventBus.getDefault().post(new SetBoardPostFavouriteStatusResultEvent(updated,isFavourite));
    }


    private void doGetFavouriteBoardPostsAction(Intent intent) {
        List<BoardPost> favouritedBoardPosts = databaseHelper.getFavouritedBoardPosts();
        EventBus.getDefault().post(new RetrievedFavouritesEvent(new ArrayList<BoardPost>(favouritedBoardPosts)));
    }
}
