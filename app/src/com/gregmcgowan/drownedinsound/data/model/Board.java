package com.gregmcgowan.drownedinsound.data.model;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This represents information for a specific type of board.
 * 
 * @author gregmcgowan
 * 
 */
@DatabaseTable(tableName = "board")
public class Board implements Parcelable {

    @DatabaseField(id = true,generatedId = false)
    private BoardType boardType;
    
    @DatabaseField
    private String displayName;
    
    @DatabaseField
    private String url;
    
    @DatabaseField
    private long lastFetchedTime;
    
    @DatabaseField
    private int sectionId;
    
    Board (){
	
    }
    
    public Board(BoardType boardType, String displayName, String url,int sectionId) {
	this.boardType = boardType;
	this.displayName = displayName;
	this.url = url;
	this.sectionId = sectionId;
    }
   
    protected Board(Parcel in) {
	createFromParcel(in);
    }
    
    public BoardType getBoardType() {
	return boardType;
    }

    public void setBoardType(BoardType boardType) {
	this.boardType = boardType;
    }

    public String getDisplayName() {
	return displayName;
    }

    public void setDisplayName(String displayName) {
	this.displayName = displayName;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public long getLastFetchedTime() {
	return lastFetchedTime;
    }

    public void setLastFetchedTime(long lastFetchedTime) {
	this.lastFetchedTime = lastFetchedTime;
    }

    private void createFromParcel(Parcel in) {
	displayName = in.readString();
	url = in.readString();
	boardType = (BoardType) in.readSerializable();
	lastFetchedTime = in.readLong();
	sectionId = in.readInt();
    }
    
    public int describeContents() {
	return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
	dest.writeString(displayName);
	dest.writeString(url);
	dest.writeSerializable(boardType);
	dest.writeLong(lastFetchedTime);
	dest.writeInt(sectionId);
    }

    public static final Parcelable.Creator<Board> CREATOR = new Parcelable.Creator<Board>() {
	public Board createFromParcel(Parcel in) {
	    return new Board(in);
	}

	public Board[] newArray(int size) {
	    return new Board[size];
	}
    };

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((boardType == null) ? 0 : boardType.hashCode());
	result = prime * result
		+ ((displayName == null) ? 0 : displayName.hashCode());
	result = prime * result + ((url == null) ? 0 : url.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Board other = (Board) obj;
	if (boardType != other.boardType)
	    return false;
	if (displayName == null) {
	    if (other.displayName != null)
		return false;
	} else if (!displayName.equals(other.displayName))
	    return false;
	if (url == null) {
	    if (other.url != null)
		return false;
	} else if (!url.equals(other.url))
	    return false;
	return true;
    }

    /**
     * Returns the 2 closets boards to the board provided.
     *  
     * 
     * @param board
     * @return
     */
    public static ArrayList<Board> getBoardsToFetch(
	    Board board,Context context) {
	ArrayList<Board> boards = DatabaseHelper.getInstance(context).getCachedBoards();
	ArrayList<Board> next2Tabs = new ArrayList<Board>();
	int indexOfBoardTypeInfo = boards.indexOf(board);
	if (indexOfBoardTypeInfo != -1) {
	    int lastIndex = boards.size() - 1;
	    if (indexOfBoardTypeInfo == 0) {
		next2Tabs.add(boards.get(indexOfBoardTypeInfo + 1));
		next2Tabs.add(boards.get(indexOfBoardTypeInfo + 2));
	    } else if (indexOfBoardTypeInfo == lastIndex) {
		next2Tabs.add(boards.get(indexOfBoardTypeInfo + -1));
		next2Tabs.add(boards.get(indexOfBoardTypeInfo - 2));
	    } else {
		next2Tabs.add(boards.get(indexOfBoardTypeInfo - 1));
		next2Tabs.add(boards.get(indexOfBoardTypeInfo + 1));
	    }
	}

	return next2Tabs;
    }

    public int getSectionId() {
	return sectionId;
    }

    public void setSectionId(int sectionId) {
	this.sectionId = sectionId;
    }
    
}
