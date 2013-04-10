package com.gregmcgowan.drownedinsound.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents a comment that has been made against a drowned in sound board post
 * 
 * @author Greg
 * 
 */
@DatabaseTable(tableName = "board_post_comment")
public class BoardPostComment implements Parcelable {

    private static final String ID_FIELD = "_id";

    @DatabaseField(id = true, columnName = ID_FIELD, generatedId = false)
    private String id;
    @DatabaseField
    private String title;
    @DatabaseField
    private String content;
    @DatabaseField
    private String authorUsername;
    @DatabaseField
    private String usersWhoHaveThissed;
    @DatabaseField
    private String dateAndTimeOfComment;
    @DatabaseField(canBeNull = false, foreign = true)
    private BoardPost boardPost;

    /**
     * Indicates if the comment is a top level one (level would be 0) or if it
     * is a reply to another comment. So level 1 would be reply to a level 0
     * comment, level 2 a reply to a level 1 comment etc etc
     */
    @DatabaseField
    private int commentLevel;

    
    private boolean actionSectionVisible;
    private BoardPostCommentTreeNode treeNode;

    public BoardPostComment() {

    }

    protected BoardPostComment(Parcel in) {
	readFromParcel(in);
    }

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

    public String getAuthorUsername() {
	return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
	this.authorUsername = authorUsername;
    }

    public String getUsersWhoHaveThissed() {
	return usersWhoHaveThissed;
    }

    public void setUsersWhoHaveThissed(String usersWhoHaveThissed) {
	this.usersWhoHaveThissed = usersWhoHaveThissed;
    }

    public String getDateAndTimeOfComment() {
	return dateAndTimeOfComment;
    }

    public void setDateAndTimeOfComment(String dateAndTimeOfComment) {
	this.dateAndTimeOfComment = dateAndTimeOfComment;
    }

    public BoardPostCommentTreeNode getTreeNode() {
	return treeNode;
    }

    public void setTreeNode(BoardPostCommentTreeNode treeNode) {
	this.treeNode = treeNode;
    }

    public String getId() {
	return id;
    }

    public void setId(String commentId) {
	this.id = commentId;
    }

    public int getCommentLevel() {
	return commentLevel;
    }

    public void setCommentLevel(int commentLevel) {
	this.commentLevel = commentLevel;
    }

    public BoardPost getBoardPost() {
	return boardPost;
    }

    public void setBoardPost(BoardPost boardPost) {
	this.boardPost = boardPost;
    }

    public int describeContents() {
	return 0;
    }

    public boolean isActionSectionVisible() {
	return actionSectionVisible;
    }

    public void setActionSectionVisible(boolean actionSectionVisible) {
	this.actionSectionVisible = actionSectionVisible;
    }

    private void readFromParcel(Parcel in) {
	title = in.readString();
	content = in.readString();
	authorUsername = in.readString();
	id = in.readString();
	commentLevel = in.readInt();
	dateAndTimeOfComment = in.readString();
	actionSectionVisible = in.readInt() == 1 ;
    }

    public void writeToParcel(Parcel dest, int flags) {
	dest.writeString(title);
	dest.writeString(content);
	dest.writeString(authorUsername);
	dest.writeString(id);
	dest.writeInt(commentLevel);
	dest.writeString(dateAndTimeOfComment);
	dest.writeInt(actionSectionVisible ? 1 : 0);
    }

    public static final Parcelable.Creator<BoardPostComment> CREATOR = new Parcelable.Creator<BoardPostComment>() {
	public BoardPostComment createFromParcel(Parcel in) {
	    return new BoardPostComment(in);
	}

	public BoardPostComment[] newArray(int size) {
	    return new BoardPostComment[size];
	}
    };

}
