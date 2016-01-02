package com.drownedinsound.data.generatered;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.drownedinsound.data.generatered.BoardPost;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BOARD_POST".
*/
public class BoardPostDao extends AbstractDao<BoardPost, String> {

    public static final String TABLENAME = "BOARD_POST";

    /**
     * Properties of entity BoardPost.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property BoardPostID = new Property(0, String.class, "boardPostID", true, "BOARD_POST_ID");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Summary = new Property(2, String.class, "summary", false, "SUMMARY");
        public final static Property Content = new Property(3, String.class, "content", false, "CONTENT");
        public final static Property AuthorUsername = new Property(4, String.class, "authorUsername", false, "AUTHOR_USERNAME");
        public final static Property DateOfPost = new Property(5, String.class, "dateOfPost", false, "DATE_OF_POST");
        public final static Property NumberOfReplies = new Property(6, Integer.class, "numberOfReplies", false, "NUMBER_OF_REPLIES");
        public final static Property LastViewedTime = new Property(7, long.class, "lastViewedTime", false, "LAST_VIEWED_TIME");
        public final static Property CreatedTime = new Property(8, long.class, "createdTime", false, "CREATED_TIME");
        public final static Property LastUpdatedTime = new Property(9, long.class, "lastUpdatedTime", false, "LAST_UPDATED_TIME");
        public final static Property LatestCommentID = new Property(10, String.class, "latestCommentID", false, "LATEST_COMMENT_ID");
        public final static Property NumberOfTimesRead = new Property(11, Integer.class, "numberOfTimesRead", false, "NUMBER_OF_TIMES_READ");
        public final static Property IsFavourite = new Property(12, boolean.class, "isFavourite", false, "IS_FAVOURITE");
        public final static Property IsSticky = new Property(13, boolean.class, "isSticky", false, "IS_STICKY");
        public final static Property BoardListTypeID = new Property(14, String.class, "boardListTypeID", false, "BOARD_LIST_TYPE_ID");
    };

    private DaoSession daoSession;

    private Query<BoardPost> boardPostList_PostsQuery;

    public BoardPostDao(DaoConfig config) {
        super(config);
    }
    
    public BoardPostDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BOARD_POST\" (" + //
                "\"BOARD_POST_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: boardPostID
                "\"TITLE\" TEXT," + // 1: title
                "\"SUMMARY\" TEXT," + // 2: summary
                "\"CONTENT\" TEXT," + // 3: content
                "\"AUTHOR_USERNAME\" TEXT," + // 4: authorUsername
                "\"DATE_OF_POST\" TEXT," + // 5: dateOfPost
                "\"NUMBER_OF_REPLIES\" INTEGER," + // 6: numberOfReplies
                "\"LAST_VIEWED_TIME\" INTEGER NOT NULL ," + // 7: lastViewedTime
                "\"CREATED_TIME\" INTEGER NOT NULL ," + // 8: createdTime
                "\"LAST_UPDATED_TIME\" INTEGER NOT NULL ," + // 9: lastUpdatedTime
                "\"LATEST_COMMENT_ID\" TEXT," + // 10: latestCommentID
                "\"NUMBER_OF_TIMES_READ\" INTEGER," + // 11: numberOfTimesRead
                "\"IS_FAVOURITE\" INTEGER NOT NULL ," + // 12: isFavourite
                "\"IS_STICKY\" INTEGER NOT NULL ," + // 13: isSticky
                "\"BOARD_LIST_TYPE_ID\" TEXT);"); // 14: boardListTypeID
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BOARD_POST\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, BoardPost entity) {
        stmt.clearBindings();
 
        String boardPostID = entity.getBoardPostID();
        if (boardPostID != null) {
            stmt.bindString(1, boardPostID);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
 
        String summary = entity.getSummary();
        if (summary != null) {
            stmt.bindString(3, summary);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
 
        String authorUsername = entity.getAuthorUsername();
        if (authorUsername != null) {
            stmt.bindString(5, authorUsername);
        }
 
        String dateOfPost = entity.getDateOfPost();
        if (dateOfPost != null) {
            stmt.bindString(6, dateOfPost);
        }
 
        Integer numberOfReplies = entity.getNumberOfReplies();
        if (numberOfReplies != null) {
            stmt.bindLong(7, numberOfReplies);
        }
        stmt.bindLong(8, entity.getLastViewedTime());
        stmt.bindLong(9, entity.getCreatedTime());
        stmt.bindLong(10, entity.getLastUpdatedTime());
 
        String latestCommentID = entity.getLatestCommentID();
        if (latestCommentID != null) {
            stmt.bindString(11, latestCommentID);
        }
 
        Integer numberOfTimesRead = entity.getNumberOfTimesRead();
        if (numberOfTimesRead != null) {
            stmt.bindLong(12, numberOfTimesRead);
        }
        stmt.bindLong(13, entity.getIsFavourite() ? 1L: 0L);
        stmt.bindLong(14, entity.getIsSticky() ? 1L: 0L);
 
        String boardListTypeID = entity.getBoardListTypeID();
        if (boardListTypeID != null) {
            stmt.bindString(15, boardListTypeID);
        }
    }

    @Override
    protected void attachEntity(BoardPost entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public BoardPost readEntity(Cursor cursor, int offset) {
        BoardPost entity = new BoardPost( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // boardPostID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // summary
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // content
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // authorUsername
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // dateOfPost
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // numberOfReplies
            cursor.getLong(offset + 7), // lastViewedTime
            cursor.getLong(offset + 8), // createdTime
            cursor.getLong(offset + 9), // lastUpdatedTime
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // latestCommentID
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11), // numberOfTimesRead
            cursor.getShort(offset + 12) != 0, // isFavourite
            cursor.getShort(offset + 13) != 0, // isSticky
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14) // boardListTypeID
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, BoardPost entity, int offset) {
        entity.setBoardPostID(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSummary(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAuthorUsername(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDateOfPost(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setNumberOfReplies(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setLastViewedTime(cursor.getLong(offset + 7));
        entity.setCreatedTime(cursor.getLong(offset + 8));
        entity.setLastUpdatedTime(cursor.getLong(offset + 9));
        entity.setLatestCommentID(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setNumberOfTimesRead(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setIsFavourite(cursor.getShort(offset + 12) != 0);
        entity.setIsSticky(cursor.getShort(offset + 13) != 0);
        entity.setBoardListTypeID(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(BoardPost entity, long rowId) {
        return entity.getBoardPostID();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(BoardPost entity) {
        if(entity != null) {
            return entity.getBoardPostID();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "posts" to-many relationship of BoardPostList. */
    public List<BoardPost> _queryBoardPostList_Posts(String boardListTypeID) {
        synchronized (this) {
            if (boardPostList_PostsQuery == null) {
                QueryBuilder<BoardPost> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.BoardListTypeID.eq(null));
                boardPostList_PostsQuery = queryBuilder.build();
            }
        }
        Query<BoardPost> query = boardPostList_PostsQuery.forCurrentThread();
        query.setParameter(0, boardListTypeID);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getBoardPostListDao().getAllColumns());
            builder.append(" FROM BOARD_POST T");
            builder.append(" LEFT JOIN BOARD_POST_LIST T0 ON T.\"BOARD_LIST_TYPE_ID\"=T0.\"BOARD_LIST_TYPE_ID\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected BoardPost loadCurrentDeep(Cursor cursor, boolean lock) {
        BoardPost entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        BoardPostList boardPostList = loadCurrentOther(daoSession.getBoardPostListDao(), cursor, offset);
        entity.setBoardPostList(boardPostList);

        return entity;    
    }

    public BoardPost loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<BoardPost> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<BoardPost> list = new ArrayList<BoardPost>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<BoardPost> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<BoardPost> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}