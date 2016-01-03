package com.drownedinsound.data.generatered;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.drownedinsound.data.generatered.BoardPostSummary;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BOARD_POST_SUMMARY".
*/
public class BoardPostSummaryDao extends AbstractDao<BoardPostSummary, String> {

    public static final String TABLENAME = "BOARD_POST_SUMMARY";

    /**
     * Properties of entity BoardPostSummary.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property BoardPostID = new Property(0, String.class, "boardPostID", true, "BOARD_POST_ID");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property AuthorUsername = new Property(2, String.class, "authorUsername", false, "AUTHOR_USERNAME");
        public final static Property NumberOfReplies = new Property(3, Integer.class, "numberOfReplies", false, "NUMBER_OF_REPLIES");
        public final static Property IsSticky = new Property(4, boolean.class, "isSticky", false, "IS_STICKY");
        public final static Property LastUpdatedTime = new Property(5, long.class, "lastUpdatedTime", false, "LAST_UPDATED_TIME");
        public final static Property LastViewedTime = new Property(6, long.class, "lastViewedTime", false, "LAST_VIEWED_TIME");
        public final static Property BoardListTypeID = new Property(7, String.class, "boardListTypeID", false, "BOARD_LIST_TYPE_ID");
    };


    public BoardPostSummaryDao(DaoConfig config) {
        super(config);
    }
    
    public BoardPostSummaryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BOARD_POST_SUMMARY\" (" + //
                "\"BOARD_POST_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: boardPostID
                "\"TITLE\" TEXT," + // 1: title
                "\"AUTHOR_USERNAME\" TEXT," + // 2: authorUsername
                "\"NUMBER_OF_REPLIES\" INTEGER," + // 3: numberOfReplies
                "\"IS_STICKY\" INTEGER NOT NULL ," + // 4: isSticky
                "\"LAST_UPDATED_TIME\" INTEGER NOT NULL ," + // 5: lastUpdatedTime
                "\"LAST_VIEWED_TIME\" INTEGER NOT NULL ," + // 6: lastViewedTime
                "\"BOARD_LIST_TYPE_ID\" TEXT NOT NULL );"); // 7: boardListTypeID
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BOARD_POST_SUMMARY\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, BoardPostSummary entity) {
        stmt.clearBindings();
 
        String boardPostID = entity.getBoardPostID();
        if (boardPostID != null) {
            stmt.bindString(1, boardPostID);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
 
        String authorUsername = entity.getAuthorUsername();
        if (authorUsername != null) {
            stmt.bindString(3, authorUsername);
        }
 
        Integer numberOfReplies = entity.getNumberOfReplies();
        if (numberOfReplies != null) {
            stmt.bindLong(4, numberOfReplies);
        }
        stmt.bindLong(5, entity.getIsSticky() ? 1L: 0L);
        stmt.bindLong(6, entity.getLastUpdatedTime());
        stmt.bindLong(7, entity.getLastViewedTime());
        stmt.bindString(8, entity.getBoardListTypeID());
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public BoardPostSummary readEntity(Cursor cursor, int offset) {
        BoardPostSummary entity = new BoardPostSummary( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // boardPostID
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // authorUsername
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // numberOfReplies
            cursor.getShort(offset + 4) != 0, // isSticky
            cursor.getLong(offset + 5), // lastUpdatedTime
            cursor.getLong(offset + 6), // lastViewedTime
            cursor.getString(offset + 7) // boardListTypeID
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, BoardPostSummary entity, int offset) {
        entity.setBoardPostID(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAuthorUsername(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setNumberOfReplies(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setIsSticky(cursor.getShort(offset + 4) != 0);
        entity.setLastUpdatedTime(cursor.getLong(offset + 5));
        entity.setLastViewedTime(cursor.getLong(offset + 6));
        entity.setBoardListTypeID(cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(BoardPostSummary entity, long rowId) {
        return entity.getBoardPostID();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(BoardPostSummary entity) {
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
    
}
