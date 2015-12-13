package com.drownedinsound.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This represents information for a specific type of board.
 *
 * @author gregmcgowan
 */
@DatabaseTable(tableName = "board")
public class Board {

    @DatabaseField(id = true, generatedId = false)
    private BoardType boardType;

    @DatabaseField
    private String displayName;

    @DatabaseField
    private String url;

    @DatabaseField
    private long lastFetchedTime;

    @DatabaseField
    private int sectionId;

    @DatabaseField
    private int pageIndex;

    Board() {

    }

    public Board(BoardType boardType, String displayName, String url, int sectionId,
            int pageIndex) {
        this.boardType = boardType;
        this.displayName = displayName;
        this.url = url;
        this.sectionId = sectionId;
        this.pageIndex = pageIndex;
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

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Board other = (Board) obj;
        if (boardType != other.boardType) {
            return false;
        }
        if (displayName == null) {
            if (other.displayName != null) {
                return false;
            }
        } else if (!displayName.equals(other.displayName)) {
            return false;
        }
        if (url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!url.equals(other.url)) {
            return false;
        }
        return true;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

}
