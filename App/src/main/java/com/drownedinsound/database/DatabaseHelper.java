package com.drownedinsound.database;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.model.Board;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardPostComment;
import com.drownedinsound.data.model.BoardType;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Database helper class used to manage the creation and upgrading of the the
 * DisBoards database. This provides the DAOs used by the other classes. The
 * database will be used to provide offline content and keep track of what data
 * has been requested already.
 *
 * @author gregmcgowan
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "DatabaseHelper";

    private static final Class<?>[] DATA_CLASSES = {BoardPost.class,
            BoardPostComment.class, Board.class, DraftBoardPost.class};

    private static final String DATABASE_NAME = "disBoards.db";

    private static final int DATABASE_VERSION = 2;

    private static DatabaseHelper instance;

    private Dao<BoardPost, String> boardPostDao;

    private Dao<BoardPostComment, String> boardPostCommentDao;

    private Dao<Board, BoardType> boardDao;

    private Dao<DraftBoardPost, String> draftBoardPostDao;

    private ArrayList<Board> boards;

    public synchronized static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        initliase();
    }

    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
        for (Class<?> dataClass : DATA_CLASSES) {
            try {
                TableUtils.createTable(connectionSource, dataClass);
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
        boards = new ArrayList<>();
        boards.add(new Board(BoardType.MUSIC,
                BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL, 19,0));
        boards.add(new Board(BoardType.SOCIAL,
                BoardTypeConstants.SOCIAL_DISPLAY_NAME, UrlConstants.SOCIAL_URL, 20,1));
        boards.add(new Board(BoardType.ANNOUNCEMENTS_CLASSIFIEDS,
                BoardTypeConstants.ANNOUNCEMENTS_CLASSIFIEDS_DISPLAY_NAME,
                UrlConstants.ANNOUNCEMENTS_CLASSIFIEDS_URL, 21,2));
        boards.add(new Board(BoardType.MUSICIANS,
                BoardTypeConstants.MUSICIANS_DISPLAY_NAME,
                UrlConstants.MUSICIANS_URL, 22,3));
        boards.add(new Board(BoardType.FESTIVALS,
                BoardTypeConstants.FESTIVALS_DISPLAY_NAME,
                UrlConstants.FESTIVALS_URL, 23,4));
        boards.add(new Board(BoardType.YOUR_MUSIC,
                BoardTypeConstants.YOUR_MUSIC_DISPLAY_NAME,
                UrlConstants.YOUR_MUSIC_URL, 24,5));
        boards.add(new Board(BoardType.ERRORS_SUGGESTIONS,
                BoardTypeConstants.ERROR_SUGGESTIONS_DISPLAY_NAME,
                UrlConstants.ERRORS_SUGGESTIONS_URL, 25,6));
        for (Board board : boards) {
            Board storedBoard = getBoard(board.getBoardType());
            if (storedBoard == null) {
                setBoard(board);
            }
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

    private Dao<Board, BoardType> getBoardDao() throws SQLException {
        if (boardDao == null) {
            boardDao = getDao(Board.class);
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
    public BoardPost getBoardPost(final String postId) {
        BoardPost boardPost = null;
        try {
            final Dao<BoardPost, String> boardPostDao = getBoardPostDao();
            boardPost = boardPostDao.queryForId(postId);

        } catch (SQLException exception) {
            if (DisBoardsConstants.DEBUG) {
                exception.printStackTrace();
            }
        }
        if (DisBoardsConstants.DEBUG) {
/*	    Log.d(TAG, (boardPost != null ? "Found" : "Could not find")
            + " board post " + postId);*/
        }
        return boardPost;
    }

    /**
     * Saves the board post provided to the database
     */
    public void setBoardPost(final BoardPost boardPost) {

        try {
            final Dao<BoardPost, String> boardPostDao = getBoardPostDao();
            final Dao<BoardPostComment, String> boardPostCommentDao = getBoardPostCommentDao();

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
                    }
                    Collection<BoardPostComment> boardPostComments = boardPost
                            .getComments();
                    if (boardPostComments != null
                            && boardPostComments.size() > 0) {
                        for (BoardPostComment boardPostComment : boardPostComments) {
                            status = boardPostCommentDao
                                    .createOrUpdate(boardPostComment);
                            if (status.getNumLinesChanged() == 0) {
                                if (DisBoardsConstants.DEBUG) {
                                    Log.d(TAG,
                                            "Could not create or update comments for board post "
                                                    + boardPost.getId());
                                }
                            }
                        }
                    }
                    return null;
                }

            });
        } catch (Exception exception) {
            if (DisBoardsConstants.DEBUG) {
                exception.printStackTrace();
            }
        }

    }

    public DraftBoardPost getDraftBoardPost(BoardType boardType) {
        DraftBoardPost draftBoardPost = null;
        try {
            final Dao<DraftBoardPost, String> draftBoardPostDao = getDraftBoardPostDao();
            List<DraftBoardPost> draftBoardPosts = draftBoardPostDao
                    .queryForEq(DraftBoardPost.BOARD_TYPE_FIELD, boardType);
            if (draftBoardPosts != null) {
                Log.d(DisBoardsConstants.LOG_TAG_PREFIX,
                        "Number of draft board posts =" + draftBoardPosts.size());
            }
            if (draftBoardPosts != null && draftBoardPosts.size() > 0) {
                draftBoardPost = draftBoardPosts.get(0);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return draftBoardPost;
    }


    public void setDraftBoardPost(DraftBoardPost draftBoardPost) {
        try {
            final Dao<DraftBoardPost, String> draftBoardPostDao = getDraftBoardPostDao();
            removeDraftBoardPost(draftBoardPost.getBoardType());
            int i = draftBoardPostDao.create(draftBoardPost);
            Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Added " + i + " Draft board posts");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }


    public void removeDraftBoardPost(BoardType boardType) {
        try {
            final Dao<DraftBoardPost, String> draftBoardPostDao = getDraftBoardPostDao();
            DeleteBuilder<DraftBoardPost, String> deleteBuilder = draftBoardPostDao.deleteBuilder();
            deleteBuilder.where().eq(DraftBoardPost.BOARD_TYPE_FIELD, boardType);
            int i = deleteBuilder.delete();
            Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Deleted " + i + " Draft board posts");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    /**
     * Fetches all the boardPosts from the database with the suppied boardTypeId
     */
    public List<BoardPost> getBoardPosts(BoardType boardType) {
        List<BoardPost> posts = new ArrayList<BoardPost>();
        try {
            final Dao<BoardPost, String> boardPostDao = getBoardPostDao();
            posts = boardPostDao.queryForEq(BoardPost.BOARD_TYPE_FIELD,
                    boardType);
        } catch (SQLException e) {
            if (DisBoardsConstants.DEBUG) {
                e.printStackTrace();
            }
        }
        if (DisBoardsConstants.DEBUG) {
            Log.d(TAG,
                    "Found "
                            + (posts.size() + " posts for " + boardType.name()));
        }
        Collections.sort(posts, BoardPost.COMPARATOR);
        return posts;
    }

    /**
     * Saves the board posts provided to the database
     */
    public void setBoardPosts(List<BoardPost> boardPosts) {
        if (boardPosts != null) {
            for (BoardPost boardPost : boardPosts) {
                setBoardPost(boardPost);
            }
        }
    }

    /**
     * Gets the requested board type from the database
     */
    public Board getBoard(BoardType boardType) {
        Board board = null;
        try {
            Dao<Board, BoardType> boardDao = getBoardDao();
            board = boardDao.queryForId(boardType);
        } catch (SQLException e) {
            if (DisBoardsConstants.DEBUG) {
                e.printStackTrace();
            }
        }
        return board;
    }

    /**
     * Saves the provided board to the database.
     */
    public void setBoard(Board board) {
        try {
            Dao<Board, BoardType> boardDao = getBoardDao();
            boardDao.createOrUpdate(board);
        } catch (SQLException e) {
            if (DisBoardsConstants.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the board are stored in memory
     */
    public ArrayList<Board> getCachedBoards() {
        return boards;
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
