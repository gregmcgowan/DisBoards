package com.drownedinsound.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by gregmcgowan on 17/10/2013.
 */
@DatabaseTable(tableName = "draft_board_post")
public class DraftBoardPost {

    public static final String BOARD_TYPE_FIELD = "board_type";

    private static final String ID_FIELD = "_id";

    @DatabaseField(id = true, columnName = ID_FIELD, generatedId = false)
    private String id;

    @DatabaseField
    private String title;

    @DatabaseField
    private String content;

    @DatabaseField(columnName = BOARD_TYPE_FIELD)
    private BoardListType boardListType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BoardListType getBoardListType() {
        return boardListType;
    }

    public void setBoardListType(BoardListType boardListType) {
        this.boardListType = boardListType;
    }

    public DraftBoardPost() {

    }


}
