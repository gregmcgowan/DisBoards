package com.gregmcgowan.drownedinsound.data.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a summary of a board post. These will be displayed in the board
 * main page in a list. These allow the user to get an overview of the post.
 * 
 * @author Greg
 * 
 */
public class BoardPostSummary implements Parcelable {

    private String title;
    private String originalPostSummary;
    private String authorUsername;
    private Date postCreatedDate;
    private Date lastPostDate;
    private Date lastViewedDate;
    private String lastPosterUsername;
    private boolean alreadyRead;
    private int numberOfReplies;
    private String boardPostId;
    private boolean isSticky;

    public BoardPostSummary(){
	
    }
    
    protected BoardPostSummary(Parcel in) {
        title = in.readString();
        originalPostSummary = in.readString();
        authorUsername = in.readString();
        lastPosterUsername = in.readString();
        alreadyRead = in.readByte() != 0x00;
        numberOfReplies = in.readInt();
        boardPostId = in.readString();
        isSticky = in.readByte() != 0x00;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(originalPostSummary);
        dest.writeString(authorUsername);
        dest.writeString(lastPosterUsername);
        dest.writeByte((byte) (alreadyRead ? 0x01 : 0x00));
        dest.writeInt(numberOfReplies);
        dest.writeString(boardPostId);
        dest.writeByte((byte) (isSticky ? 0x01 : 0x00));
    }

    public static final Parcelable.Creator<BoardPostSummary> CREATOR = new Parcelable.Creator<BoardPostSummary>() {
        public BoardPostSummary createFromParcel(Parcel in) {
            return new BoardPostSummary(in);
        }

        public BoardPostSummary[] newArray(int size) {
            return new BoardPostSummary[size];
        }
    };
    
    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getOriginalPostSummary() {
	return originalPostSummary;
    }

    public void setOriginalPostSummary(String originalPostSummary) {
	this.originalPostSummary = originalPostSummary;
    }

    public String getAuthorUsername() {
	return authorUsername;
    }

    public void setAuthorUsername(String author) {
	this.authorUsername = author;
    }

    public Date getPostCreatedDate() {
	return postCreatedDate;
    }

    public void setPostCreatedDate(Date postCreatedDate) {
	this.postCreatedDate = postCreatedDate;
    }

    public Date getLastPostDate() {
	return lastPostDate;
    }

    public void setLastPostDate(Date lastPostDate) {
	this.lastPostDate = lastPostDate;
    }

    public String getLastPoster() {
	return lastPosterUsername;
    }

    public void setLastPoster(String lastPoster) {
	this.lastPosterUsername = lastPoster;
    }

    public boolean isAlreadyRead() {
	return alreadyRead;
    }

    public void setAlreadyRead(boolean alreadyRead) {
	this.alreadyRead = alreadyRead;
    }

    public int getNumberOfReplies() {
	return numberOfReplies;
    }

    public void setNumberOfReplies(int numberOfReplies) {
	this.numberOfReplies = numberOfReplies;
    }

    public String getBoardPostId() {
	return boardPostId;
    }

    public void setBoardPostId(String postUrlPostfix) {
	this.boardPostId = postUrlPostfix;
    }

    public boolean isSticky() {
	return isSticky;
    }

    public void setSticky(boolean isSticky) {
	this.isSticky = isSticky;
    }

}
