package com.gregmcgowan.drownedinsound.data.model;

import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by gregmcgowan on 17/10/2013.
 */
@DatabaseTable(tableName="draft_board_post")
public class DraftBoardPost implements Parcelable {

    public static final String BOARD_TYPE_FIELD = "board_type";

    private static final String ID_FIELD = "_id";

    @DatabaseField(id = true, columnName = ID_FIELD, generatedId = false)
    private String id;

    @DatabaseField
    private String title;

    @DatabaseField
    private String content;

    @DatabaseField(columnName = BOARD_TYPE_FIELD)
    private BoardType boardType;

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

    public BoardType getBoardType() {
        return boardType;
    }

    public void setBoardType(BoardType boardType) {
        this.boardType = boardType;
    }

    public DraftBoardPost(){

    }

    protected DraftBoardPost(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        boardType = (BoardType)in.readSerializable();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeSerializable(boardType);
    }

    public static final Parcelable.Creator<DraftBoardPost> CREATOR = new Parcelable.Creator<DraftBoardPost>() {
        public DraftBoardPost createFromParcel(Parcel in) {
            return new DraftBoardPost(in);
        }

        public DraftBoardPost[] newArray(int size) {
            return new DraftBoardPost[size];
        }
    };


}
