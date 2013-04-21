package com.gregmcgowan.drownedinsound.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.Board;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.model.BoardTypeConstants;
import com.gregmcgowan.drownedinsound.network.UrlConstants;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of the the
 * DisBoards database. This provides the DAOs used by the other classes. The
 * database will be used to provide offline content and keep track of what data
 * has been requested already.
 * 
 * @author gregmcgowan
 * 
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "DatabaseHelper";
    private static final Class<?>[] DATA_CLASSES = { BoardPost.class,
	    BoardPostComment.class, Board.class };
    private static final String DATABASE_NAME = "disBoards.db";
    private static final int DATABASE_VERSION = 2;

    private static DatabaseHelper instance;

    private Dao<BoardPost, String> boardPostDao;
    private Dao<BoardPostComment, String> boardPostCommentDao;
    private Dao<Board, BoardType> boardDao;
    private ArrayList<Board> boards;

    public synchronized static DatabaseHelper getInstance(Context context) {
	if (instance == null) {
	    instance = new DatabaseHelper(context);
	}
	return instance;
    }

    private DatabaseHelper(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
	boards = new ArrayList<Board>();
	boards.add(new Board(BoardType.MUSIC,
		BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL));
	boards.add(new Board(BoardType.SOCIAL,
		BoardTypeConstants.SOCIAL_DISPLAY_NAME, UrlConstants.SOCIAL_URL));
	boards.add(new Board(BoardType.ANNOUNCEMENTS_CLASSIFIEDS,
		BoardTypeConstants.ANNOUNCEMENTS_CLASSIFIEDS_DISPLAY_NAME,
		UrlConstants.ANNOUNCEMENTS_CLASSIFIEDS_URL));
	boards.add(new Board(BoardType.MUSICIANS,
		BoardTypeConstants.MUSICIANS_DISPLAY_NAME,
		UrlConstants.MUSICIANS_URL));
	boards.add(new Board(BoardType.FESTIVALS,
		BoardTypeConstants.FESTIVALS_DISPLAY_NAME,
		UrlConstants.FESTIVALS_URL));
	boards.add(new Board(BoardType.YOUR_MUSIC,
		BoardTypeConstants.YOUR_MUSIC_DISPLAY_NAME,
		UrlConstants.YOUR_MUSIC_URL));
	boards.add(new Board(BoardType.ERRORS_SUGGESTIONS,
		BoardTypeConstants.ERROR_SUGGESTIONS_DISPLAY_NAME,
		UrlConstants.ERRORS_SUGGESTIONS_URL));
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

    /**
     * Gets the board post with the provided ID from the database. null is
     * returned if the post does not exist
     * 
     * @param postId
     * @return
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
     * 
     * @param boardPost
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

    /**
     * Fetches all the boardPosts from the database with the suppied boardTypeId
     * 
     * @return
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
	return posts;
    }

    /**
     * Saves the board posts provided to the database
     * 
     * @param boardPosts
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
     * 
     * @param boardType
     * @return
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
     * 
     * @param board
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
     * 
     * @return
     */
    public ArrayList<Board> getCachedBoards() {
	return boards;
    }

}
