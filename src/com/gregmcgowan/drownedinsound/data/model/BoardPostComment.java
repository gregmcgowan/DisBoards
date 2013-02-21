package com.gregmcgowan.drownedinsound.data.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a comment that has been made against a drowned in sound
 * board post
 * 
 * @author Greg
 *
 */
public class BoardPostComment implements Parcelable {
    
    private String title;
    private String content;
    private String authorUsername;
    private String[] usersWhoHaveThissed;
    private Date dateAndTimeOfComment;
    private BoardPostCommentTreeNode treeNode;
    private String commentId;
    
    public BoardPostComment(){
	
    }
    
    protected BoardPostComment(Parcel in) {
	readFromParcel(in);
    }
    
    /**
     * Indicates if the comment is a top level one (level would be 0) or if
     * it is a reply to another comment. So level 1 would be reply to a level 0
     * comment, level 2 a reply to a level 1 comment etc etc
     */
    private int commentLevel;
    
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
    public String[] getUsersWhoHaveThissed() {
        return usersWhoHaveThissed;
    }
    public void setUsersWhoHaveThissed(String[] usersWhoHaveThissed) {
        this.usersWhoHaveThissed = usersWhoHaveThissed;
    }
    public Date getDateAndTimeOfComment() {
        return dateAndTimeOfComment;
    }
    public void setDateAndTimeOfComment(Date dateAndTimeOfComment) {
        this.dateAndTimeOfComment = dateAndTimeOfComment;
    }
    public BoardPostCommentTreeNode getTreeNode() {
        return treeNode;
    }
    public void setTreeNode(BoardPostCommentTreeNode treeNode) {
        this.treeNode = treeNode;
    }
    public String getCommentId() {
        return commentId;
    }
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
    public int getCommentLevel() {
	return commentLevel;
    }
    public void setCommentLevel(int commentLevel) {
	this.commentLevel = commentLevel;
    }
    
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        title = in.readString();
        content = in.readString();
        authorUsername = in.readString();
        commentId = in.readString();
        commentLevel = in.readInt();
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(authorUsername);
        dest.writeString(commentId);
        dest.writeInt(commentLevel);
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
