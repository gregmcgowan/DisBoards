package com.gregmcgowan.drownedinsound.data.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This represents a board post. Board post contains the title, content, author
 * username the number of comments made, the actual comments and the date of the
 * post
 * 
 * @author Greg
 * 
 */
@DatabaseTable(tableName = "BoardPosts")
public class BoardPost implements Parcelable {

    private static final String ID_FIELD = "_id";

    @DatabaseField(columnName = ID_FIELD, generatedId = false)
    private String id;
    @DatabaseField
    private String title;
    @DatabaseField
    private boolean isSticky;
    @DatabaseField
    private String summary;
    @DatabaseField
    private String content;
    @DatabaseField
    private String authorUsername;
    @DatabaseField
    private String dateOfPost;
    @DatabaseField
    private int numberOfReplies;
    @DatabaseField
    private BoardPostStatus boardPostStatus;
    @DatabaseField
    private long lastViewedTime;    
    @DatabaseField
    private String boardTypeId;
    
    @ForeignCollectionField 
    private transient List<BoardPostComment> comments;
    
    private List<BoardPostCommentTreeNode> boardPostTreeNodes;
   
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

    public void setComments(List<BoardPostComment> comments) {
	boardPostTreeNodes = new ArrayList<BoardPostCommentTreeNode>();
	for (BoardPostComment comment : comments) {
	    if (comment.getCommentLevel() == 0) {
		boardPostTreeNodes.add(makeTreeNode(comment, comments));
	    }
	}
	populateCommentsCache() ;
    }

    public List<BoardPostCommentTreeNode> getTreeNodes() {
	return boardPostTreeNodes;
    }

    public List<BoardPostComment> getComments() {
	if (comments == null) {
	    if (boardPostTreeNodes == null) {
		boardPostTreeNodes = new ArrayList<BoardPostCommentTreeNode>();
	    }
	    populateCommentsCache() ;
	}
	return comments;
    }

    private void populateCommentsCache() {
	if (comments == null) {
	    comments = new ArrayList<BoardPostComment>();
	}
	// Add a comment for the initial post details as we are using them in a
	// list
	BoardPostComment boardPostComment = new BoardPostComment();
	boardPostComment.setAuthorUsername(getAuthorUsername());
	boardPostComment.setCommentLevel(0);
	boardPostComment.setDateAndTimeOfComment(getDateOfPost());
	boardPostComment.setContent(getContent());
	boardPostComment.setTitle(getTitle());
	comments.add(boardPostComment);

	for (BoardPostCommentTreeNode node : boardPostTreeNodes) {
	    comments.addAll(node.getCommentAndAllChildren());
	}
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

    public String getDateOfPost() {
	return dateOfPost;
    }

    public void setDateOfPost(String dateOfPost) {
	this.dateOfPost = dateOfPost;
    }

    public int describeContents() {
	return 0;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getSummary() {
	return summary;
    }

    public void setSummary(String summary) {
	this.summary = summary;
    }

    public boolean isSticky() {
	return isSticky;
    }

    public void setSticky(boolean isSticky) {
	this.isSticky = isSticky;
    }

    public int getNumberOfReplies() {
	return numberOfReplies;
    }

    public void setNumberOfReplies(int numberOfReplies) {
	this.numberOfReplies = numberOfReplies;
    }

    public BoardPostStatus getBoardPostStatus() {
	return boardPostStatus;
    }

    public void setBoardPostStatus(BoardPostStatus boardPostStatus) {
	this.boardPostStatus = boardPostStatus;
    }

    public long getLastViewedTime() {
	return lastViewedTime;
    }

    public void setLastViewedTime(long lastViewedTime) {
	this.lastViewedTime = lastViewedTime;
    }

    public String getBoardTypeId() {
        return boardTypeId;
    }

    public void setBoardTypeId(String boardTypeId) {
        this.boardTypeId = boardTypeId;
    }

    public void writeToParcel(Parcel parcel, int flag) {
	parcel.writeString(id);
	parcel.writeString(title);
	parcel.writeString(summary);
	parcel.writeInt(isSticky ? 1 : 0);
	parcel.writeString(content);
	parcel.writeString(authorUsername);
	parcel.writeString(dateOfPost);
	parcel.writeInt(numberOfReplies);
	parcel.writeSerializable(boardPostStatus);
	parcel.writeLong(lastViewedTime);
	parcel.writeString(boardTypeId);
	List<BoardPostComment> comments = getComments();
	parcel.writeParcelableArray(
		comments.toArray(new BoardPostComment[comments.size()]), flag);
    }

    private void createFromParcel(Parcel parcel) {
	id = parcel.readString();
	title = parcel.readString();
	summary = parcel.readString();
	isSticky = parcel.readInt() == 1;
	content = parcel.readString();
	authorUsername = parcel.readString();
	dateOfPost = parcel.readString();
	numberOfReplies = parcel.readInt();
	boardPostStatus = (BoardPostStatus) parcel.readSerializable();
	lastViewedTime = parcel.readLong();
	boardTypeId = parcel.readString();
	
	List<BoardPostComment> comments = parcel
		.readArrayList(BoardPostComment.class.getClassLoader());
	setComments(comments);
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
