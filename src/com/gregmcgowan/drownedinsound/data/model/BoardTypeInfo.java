package com.gregmcgowan.drownedinsound.data.model;

/**
 * This represents information for a specific type of board. 
 * 

 * 
 * @author gregmcgowan
 *
 */
public class BoardTypeInfo {
    
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
    
}
