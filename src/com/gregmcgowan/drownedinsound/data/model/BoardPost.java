package com.gregmcgowan.drownedinsound.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This represents a board post. Board post contains the title, content, author
 * username the number of comments made, the actual comments and the date of the
 * post
 * 
 * @author Greg
 * 
 */
public class BoardPost implements Parcelable {

    private String title;
    private String content;
    private String authorUsername;
    private String noOfReplies;
    private Date dateOfPost;
    private List<BoardPostCommentTreeNode> boardPostTreeNodes;
    private transient List<BoardPostComment> commentsCache;

    public BoardPost() {
	boardPostTreeNodes = new ArrayList<BoardPostCommentTreeNode>();
    }

    public BoardPost(List<BoardPostComment> comments) {
	setComments(comments);
    }
 
    public BoardPost(Parcel in) {
	boardPostTreeNodes = new ArrayList<BoardPostCommentTreeNode>();
	createFromParcel(in);
    }

    public void setComments(List<BoardPostComment> comments){
	boardPostTreeNodes = new ArrayList<BoardPostCommentTreeNode>();
	for (BoardPostComment comment : comments) {
	    if (comment.getCommentLevel() == 0){
		boardPostTreeNodes.add(makeTreeNode(comment, comments));
	    }
	}
    }
    
    public List<BoardPostCommentTreeNode> getTreeNodes() {
	return boardPostTreeNodes;
    }

    public List<BoardPostComment> getComments() {
	if (commentsCache == null) {
	    commentsCache = new ArrayList<BoardPostComment>();

	    if (boardPostTreeNodes == null) {
		boardPostTreeNodes = new ArrayList<BoardPostCommentTreeNode>();
	    }
	    for (BoardPostCommentTreeNode node : boardPostTreeNodes) {
		commentsCache.addAll(node.getCommentAndAllChildren());
	    }
	}
	return commentsCache;
    }

    private BoardPostCommentTreeNode makeTreeNode(BoardPostComment comment,
	    List<BoardPostComment> allComments) {
	BoardPostCommentTreeNode node = new BoardPostCommentTreeNode(comment);
	int nodeLevel = comment.getCommentLevel();
	int nodeIndex = allComments.indexOf(comment);
	comment.setTreeNode(node);
	for (int i = nodeIndex + 1; i < allComments.size(); i++) {
	    BoardPostComment childComment = allComments.get(i);
	    if (childComment.getCommentLevel() > nodeLevel + 1)
		continue;
	    if (childComment.getCommentLevel() <= nodeLevel)
		break;
	    node.addChild(makeTreeNode(childComment, allComments));
	}
	return node;
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

    public String getNoOfReplies() {
	return getComments().size() + " replies";
    }


    public Date getDateOfPost() {
	return dateOfPost;
    }

    public void setDateOfPost(Date dateOfPost) {
	this.dateOfPost = dateOfPost;
    }

    public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
    }

    public void writeToParcel(Parcel parcel, int flag) {
	parcel.writeString(title);
	parcel.writeString(content);
	parcel.writeString(authorUsername);
	parcel.writeString(noOfReplies);
	long time = -1;
	if (dateOfPost != null) {
	    time = dateOfPost.getTime();
	}
	parcel.writeLong(time);
	// TODO comments
    }

    private void createFromParcel(Parcel parcel) {
	title = parcel.readString();
	content = parcel.readString();
	authorUsername = parcel.readString();
	noOfReplies = parcel.readString();
	long time = parcel.readLong();
	if (time != -1) {
	    dateOfPost = new Date();
	}
	// TODO comments
    }

    public static final Parcelable.Creator<BoardPost> CREATOR = new Parcelable.Creator<BoardPost>() {
	public BoardPost createFromParcel(Parcel in) {
	    return new BoardPost(in);
	}

	public BoardPost[] newArray(int size) {
	    return new BoardPost[size];
	}
    };

}
