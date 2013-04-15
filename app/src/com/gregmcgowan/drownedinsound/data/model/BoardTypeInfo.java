package com.gregmcgowan.drownedinsound.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This represents information for a specific type of board. 
 * 

 * 
 * @author gregmcgowan
 *
 */
public class BoardTypeInfo  implements Parcelable{
    
    private BoardType boardType;
    private String displayName;
    private String url;
    
    public BoardTypeInfo(BoardType boardType, String displayName, String url) {
	this.boardType = boardType;
	this.displayName = displayName;
	this.url = url;
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
    
    protected BoardTypeInfo(Parcel in) {
        displayName = in.readString();
        url = in.readString();
        boardType = (BoardType) in.readSerializable();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(displayName);
        dest.writeString(url);
        dest.writeSerializable(boardType);
    }

    public static final Parcelable.Creator<BoardTypeInfo> CREATOR = new Parcelable.Creator<BoardTypeInfo>() {
        public BoardTypeInfo createFromParcel(Parcel in) {
            return new BoardTypeInfo(in);
        }

        public BoardTypeInfo[] newArray(int size) {
            return new BoardTypeInfo[size];
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
	BoardTypeInfo other = (BoardTypeInfo) obj;
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
    
    
    
}
