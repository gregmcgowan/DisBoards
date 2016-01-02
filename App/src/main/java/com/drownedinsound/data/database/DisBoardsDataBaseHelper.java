package com.drownedinsound.data.database;

import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostListDao;
import com.drownedinsound.data.generatered.DaoMaster;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.data.model.BoardTypeConstants;
import com.drownedinsound.data.network.UrlConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import de.greenrobot.dao.internal.SqlUtils;
import timber.log.Timber;

/**
 * Created by gregmcgowan on 01/01/16.
 */
public class DisBoardsDataBaseHelper extends DaoMaster.OpenHelper {

    public DisBoardsDataBaseHelper(Context context, String name,
            SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        //Prepopulate the board list information, maybe could come from a json file instead
        ArrayList<BoardPostList> boardPostListInfos = new ArrayList<>();
        boardPostListInfos.add(new BoardPostList(BoardListTypes.MUSIC,
                BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL, 0, 19, 0));
        boardPostListInfos.add(new BoardPostList(BoardListTypes.SOCIAL,
                BoardTypeConstants.SOCIAL_DISPLAY_NAME, UrlConstants.SOCIAL_URL, 0, 20, 1));
        boardPostListInfos.add(new BoardPostList(BoardListTypes.ANNOUNCEMENTS_CLASSIFIEDS,
                BoardTypeConstants.ANNOUNCEMENTS_CLASSIFIEDS_DISPLAY_NAME,
                UrlConstants.ANNOUNCEMENTS_CLASSIFIEDS_URL, 0, 21, 2));
        boardPostListInfos.add(new BoardPostList(BoardListTypes.MUSICIANS,
                BoardTypeConstants.MUSICIANS_DISPLAY_NAME,
                UrlConstants.MUSICIANS_URL, 0, 22, 3));
        boardPostListInfos.add(new BoardPostList(BoardListTypes.FESTIVALS,
                BoardTypeConstants.FESTIVALS_DISPLAY_NAME,
                UrlConstants.FESTIVALS_URL, 0, 23, 4));
        boardPostListInfos.add(new BoardPostList(BoardListTypes.YOUR_MUSIC,
                BoardTypeConstants.YOUR_MUSIC_DISPLAY_NAME,
                UrlConstants.YOUR_MUSIC_URL, 0, 24, 5));
        boardPostListInfos.add(new BoardPostList(BoardListTypes.ERRORS_SUGGESTIONS,
                BoardTypeConstants.ERROR_SUGGESTIONS_DISPLAY_NAME,
                UrlConstants.ERRORS_SUGGESTIONS_URL, 0, 25, 6));

        if (boardPostListInfos.size() > 0) {
            String sql = SqlUtils.createSqlInsert("INSERT INTO ", BoardPostListDao.TABLENAME,
                    new String[]{
                            BoardPostListDao.Properties.BoardListTypeID.columnName,
                            BoardPostListDao.Properties.DisplayName.columnName,
                            BoardPostListDao.Properties.Url.columnName,
                            BoardPostListDao.Properties.LastFetchedMs.columnName,
                            BoardPostListDao.Properties.SectionId.columnName,
                            BoardPostListDao.Properties.PageIndex.columnName});
            Timber.d("Compile statement = " + sql);
            SQLiteStatement insertStatement = db.compileStatement(sql);

            db.beginTransaction();
            try {
                for (BoardPostList boardPostList : boardPostListInfos) {
                    insertStatement.clearBindings();
                    insertStatement.bindString(1, boardPostList.getBoardListTypeID());
                    insertStatement.bindString(2, boardPostList.getDisplayName());
                    insertStatement.bindString(3, boardPostList.getUrl());
                    insertStatement.bindLong(4, boardPostList.getLastFetchedMs());
                    insertStatement.bindLong(5, boardPostList.getSectionId());
                    insertStatement.bindLong(6, boardPostList.getPageIndex());

                    long insertRow = insertStatement.executeInsert();
                    Timber.d("Pre-populate insertRow =" + insertRow);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
