package com.drownedinsound.data.database;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.BoardPostListInfo;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardPostComment;
import com.drownedinsound.data.model.BoardListType;
import com.drownedinsound.data.model.BoardTypeConstants;
import com.drownedinsound.data.model.DraftBoardPost;
import com.drownedinsound.data.network.UrlConstants;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

/**
 * Database helper class used to manage the creation and upgrading of the the
 * DisBoards database. This provides the DAOs used by the other classes. The
 * database will be used to provide offline content and keep track of what data
 * has been requested already.
 *
 * @author gregmcgowan
 */
@Singleton
public class DatabaseHelper extends OrmLiteSqliteOpenHelper implements DisBoardsLocalRepo {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "DatabaseHelper";

    private static final Class<?>[] DATA_CLASSES = {BoardPost.class,
            BoardPostComment.class, BoardPostListInfo.class, DraftBoardPost.class};

    private static final String DATABASE_NAME = "disBoards.db";

    private static final int DATABASE_VERSION = 2;

    private static DatabaseHelper instance;

    private Dao<BoardPost, String> boardPostDao;

    private Dao<BoardPostComment, String> boardPostCommentDao;

    private Dao<BoardPostListInfo, BoardListType> boardDao;

    private Dao<DraftBoardPost, String> draftBoardPostDao;

    private ArrayList<BoardPostListInfo> boardPostListInfos;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
        for (Class<?> dataClass : DATA_CLASSES) {
            try {
                TableUtils.createTable(connectionSource, dataClass);
                initliase();
            } catch (SQLException e) {
                if (DisBoardsConstants.DEBUG) {
                    Log.e(TAG, "Cannot create database");
                }
                throw new RuntimeException(e);
            }
        }

    }

    public void clearAllTables() {
        try {
            for (Class<?> dataClass : DATA_CLASSES) {
                TableUtils.clearTable(connectionSource, dataClass);
            }
        } catch (SQLException e) {
            if (DisBoardsConstants.DEBUG) {
                Log.e(TAG, "Can't clear databases", e);
            }
        }
    }

    /**
     * Initliases any static data that is required
     */
    public void initliase() {
        initliaseBoardType();
    }

    private void initliaseBoardType() {
        boardPostListInfos = new ArrayList<>();
        boardPostListInfos.add(new BoardPostListInfo(BoardListType.MUSIC,
                BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL, 19, 0));
        boardPostListInfos.add(new BoardPostListInfo(BoardListType.SOCIAL,
                BoardTypeConstants.SOCIAL_DISPLAY_NAME, UrlConstants.SOCIAL_URL, 20, 1));
        boardPostListInfos.add(new BoardPostListInfo(BoardListType.ANNOUNCEMENTS_CLASSIFIEDS,
                BoardTypeConstants.ANNOUNCEMENTS_CLASSIFIEDS_DISPLAY_NAME,
                UrlConstants.ANNOUNCEMENTS_CLASSIFIEDS_URL, 21, 2));
        boardPostListInfos.add(new BoardPostListInfo(BoardListType.MUSICIANS,
                BoardTypeConstants.MUSICIANS_DISPLAY_NAME,
                UrlConstants.MUSICIANS_URL, 22, 3));
        boardPostListInfos.add(new BoardPostListInfo(BoardListType.FESTIVALS,
                BoardTypeConstants.FESTIVALS_DISPLAY_NAME,
                UrlConstants.FESTIVALS_URL, 23, 4));
        boardPostListInfos.add(new BoardPostListInfo(BoardListType.YOUR_MUSIC,
                BoardTypeConstants.YOUR_MUSIC_DISPLAY_NAME,
                UrlConstants.YOUR_MUSIC_URL, 24, 5));
        boardPostListInfos.add(new BoardPostListInfo(BoardListType.ERRORS_SUGGESTIONS,
                BoardTypeConstants.ERROR_SUGGESTIONS_DISPLAY_NAME,
                UrlConstants.ERRORS_SUGGESTIONS_URL, 25, 6));
        for (BoardPostListInfo boardPostListInfo : boardPostListInfos) {
            setBoard(boardPostListInfo).first().subscribe(new Subscriber<Void>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Void aVoid) {

                }
            });
        }

    }

    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
            int oldVersion, int newVersion) {
        try {
            if (DisBoardsConstants.DEBUG) {
                Log.i(TAG, "onUpgrade");
            }
            for (Class<?> dataClass : DATA_CLASSES) {
                TableUtils.dropTable(connectionSource, dataClass, true);
            }

            onCreate(db, connectionSource);
        } catch (SQLException e) {

            if (DisBoardsConstants.DEBUG) {
                Log.e(TAG, "Can't drop databases", e);
            }
            throw new RuntimeException(e);
        }
    }

    private Dao<BoardPost, String> getBoardPostDao() throws SQLException {
        if (boardPostDao == null) {
            boardPostDao = getDao(BoardPost.class);
        }
        return boardPostDao;
    }

    private Dao<BoardPostComment, String> getBoardPostCommentDao()
            throws SQLException {
        if (boardPostCommentDao == null) {
            boardPostCommentDao = getDao(BoardPostComment.class);
        }
        return boardPostCommentDao;
    }

    private Dao<BoardPostListInfo, BoardListType> getBoardDao() throws SQLException {
        if (boardDao == null) {
            boardDao = getDao(BoardPostListInfo.class);
        }
        return boardDao;
    }

    private Dao<DraftBoardPost, String> getDraftBoardPostDao() throws SQLException {
        if (draftBoardPostDao == null) {
            draftBoardPostDao = getDao(DraftBoardPost.class);
        }
        return draftBoardPostDao;
    }



    /**
     * Gets the board post with the provided ID from the database. null is
     * returned if the post does not exist
     */
    public Observable<BoardPost> getBoardPost(final String postId) {
        return null;
//        BoardPost boardPost = null;
//        try {
//            final Dao<BoardPost, String> boardPostDao = getBoardPostDao();
//            boardPost = boardPostDao.queryForId(postId);
//
//        } catch (SQLException exception) {
//            if (DisBoardsConstants.DEBUG) {
//                exception.printStackTrace();
//            }
//        }
//        return boardPost;
    }

    /**
     * Saves the board post provided to the database
     */
    public Observable<Void> setBoardPost(final BoardPost boardPost) {

//        try {
//            final Dao<BoardPost, String> boardPostDao = getBoardPostDao();
//            final Dao<BoardPostComment, String> boardPostCommentDao = getBoardPostCommentDao();
//
//            boardPostDao.callBatchTasks(new Callable<Void>() {
//
//                @Override
//                public Void call() throws Exception {
//                    CreateOrUpdateStatus status = boardPostDao
//                            .createOrUpdate(boardPost);
//                    if (status.getNumLinesChanged() == 0) {
//                        if (DisBoardsConstants.DEBUG) {
//                            Log.d(TAG, "Could not create or update board post "
//                                    + boardPost.getId());
//                        }
//                    }
//                    Collection<BoardPostComment> boardPostComments = boardPost
//                            .getComments();
//                    if (boardPostComments != null
//                            && boardPostComments.size() > 0) {
//                        for (BoardPostComment boardPostComment : boardPostComments) {
//                            status = boardPostCommentDao
//                                    .createOrUpdate(boardPostComment);
//                            if (status.getNumLinesChanged() == 0) {
//                                if (DisBoardsConstants.DEBUG) {
//                                    Log.d(TAG,
//                                            "Could not create or update comments for board post "
//                                                    + boardPost.getId());
//                                }
//                            }
//                        }
//                    }
//                    return null;
//                }
//
//            });
//        } catch (Exception exception) {
//            if (DisBoardsConstants.DEBUG) {
//                exception.printStackTrace();
//            }
//        }

        return null;
    }

    public Observable<DraftBoardPost> getDraftBoardPost(BoardListType boardListType) {
//        DraftBoardPost draftBoardPost = null;
//        try {
//            final Dao<DraftBoardPost, String> draftBoardPostDao = getDraftBoardPostDao();
//            List<DraftBoardPost> draftBoardPosts = draftBoardPostDao
//                    .queryForEq(DraftBoardPost.BOARD_TYPE_FIELD, boardType);
//            if (draftBoardPosts != null) {
//                Log.d(DisBoardsConstants.LOG_TAG_PREFIX,
//                        "Number of draft board posts =" + draftBoardPosts.size());
//            }
//            if (draftBoardPosts != null && draftBoardPosts.size() > 0) {
//                draftBoardPost = draftBoardPosts.get(0);
//            }
//        } catch (SQLException sqlException) {
//            sqlException.printStackTrace();
//        }
//
//        return draftBoardPost;
        return null;
    }


    public Observable<Void> setDraftBoardPost(DraftBoardPost draftBoardPost) {
        try {
            final Dao<DraftBoardPost, String> draftBoardPostDao = getDraftBoardPostDao();
            removeDraftBoardPost(draftBoardPost.getBoardListType());
            int i = draftBoardPostDao.create(draftBoardPost);
            Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Added " + i + " Draft board posts");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }


    public Observable<Void> removeDraftBoardPost(BoardListType boardListType) {
        try {
            final Dao<DraftBoardPost, String> draftBoardPostDao = getDraftBoardPostDao();
            DeleteBuilder<DraftBoardPost, String> deleteBuilder = draftBoardPostDao.deleteBuilder();
            deleteBuilder.where().eq(DraftBoardPost.BOARD_TYPE_FIELD, boardListType);
            int i = deleteBuilder.delete();
            Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Deleted " + i + " Draft board posts");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    /**
     * Fetches all the boardPosts from the database with the suppied boardTypeId
     */
    public Observable<List<BoardPost>> getBoardPosts(BoardListType boardListType) {
        List<BoardPost> posts = new ArrayList<BoardPost>();
        try {
            final Dao<BoardPost, String> boardPostDao = getBoardPostDao();
            posts = boardPostDao.queryForEq(BoardPost.BOARD_TYPE_FIELD,
                    boardListType);
        } catch (SQLException e) {
            if (DisBoardsConstants.DEBUG) {
                e.printStackTrace();
            }
        }
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG,
                    "Found "
                            + (posts.size() + " posts for " + boardListType.name()));
        }
        Collections.sort(posts, BoardPost.COMPARATOR);
        return null;
    }

    /**
     * Saves the board posts provided to the database
     */
    public Observable<Void> setBoardPosts(List<BoardPost> boardPosts) {
        if (boardPosts != null) {
            for (BoardPost boardPost : boardPosts) {
                setBoardPost(boardPost);
            }
        }
        return null;
    }

    /**
     * Gets the requested board type from the database
     */
    public Observable<BoardPostListInfo> getBoard(final BoardListType boardListType) {
        return  Observable.create(new Observable.OnSubscribe<BoardPostListInfo>() {
            @Override
            public void call(Subscriber<? super BoardPostListInfo> subscriber) {
                try {
                    Dao<BoardPostListInfo, BoardListType> boardDao = getBoardDao();
                    BoardPostListInfo boardPostListInfo = boardDao.queryForId(boardListType);
                    subscriber.onNext(boardPostListInfo);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<List<BoardPostListInfo>> getAllBoardPostInfos() {
        return Observable.create(new Observable.OnSubscribe<List<BoardPostListInfo>>() {
            @Override
            public void call(Subscriber<? super List<BoardPostListInfo>> subscriber) {
                try {
                    Dao<BoardPostListInfo, BoardListType> boardDao = getBoardDao();
                    List<BoardPostListInfo> boardPostListInfos = boardDao.queryForAll();
                    subscriber.onNext(boardPostListInfos);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }

            }
        });
    }

    /**
     * Saves the provided board to the database.
     */
    public Observable<Void> setBoard(final BoardPostListInfo boardPostListInfo) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    Dao<BoardPostListInfo, BoardListType> boardDao = getBoardDao();
                    CreateOrUpdateStatus createOrUpdateStatus
                            = boardDao.createOrUpdate(boardPostListInfo);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * Returns the board are stored in memory
     */
    public ArrayList<BoardPostListInfo> getCachedBoards() {
        return boardPostListInfos;
    }

    public boolean setBoardPostFavouriteStatus(final BoardPost boardPost, boolean isFavourite) {
        boolean updated = true;
        boardPost.setFavourited(isFavourite);
        try {
            final Dao<BoardPost, String> boardPostDao = getBoardPostDao();

            boardPostDao.callBatchTasks(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    CreateOrUpdateStatus status = boardPostDao
                            .createOrUpdate(boardPost);
                    if (status.getNumLinesChanged() == 0) {
                        if (DisBoardsConstants.DEBUG) {
                            Log.d(TAG, "Could not create or update board post "
                                    + boardPost.getId());
                        }
                    } else {
                        Log.d(TAG, "Updated favourite status for board post " + boardPost.getId());
                    }

                    return null;
                }

            });
        } catch (Exception exception) {
            updated = false;
            if (DisBoardsConstants.DEBUG) {
                exception.printStackTrace();
            }
        }
        return updated;
    }

    /**
     * Fetches all the boardPosts from the database with the suppied boardTypeId
     */
    public List<BoardPost> getFavouritedBoardPosts() {
        List<BoardPost> posts = new ArrayList<BoardPost>();
        try {
            final Dao<BoardPost, String> boardPostDao = getBoardPostDao();
            posts = boardPostDao.queryForEq(BoardPost.IS_FAVOURITED_FIELD_NAME,
                    true);
        } catch (SQLException e) {
            if (DisBoardsConstants.DEBUG) {
                e.printStackTrace();
            }
        }
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG,
                    "Found "
                            + (posts.size() + " favourited board posts"));
        }
        Collections.sort(posts, BoardPost.COMPARATOR);
        return posts;
    }



}
