package com.gregmcgowan.drownedinsound.data.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.DateUtils;

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
@DatabaseTable(tableName = "board_post")
public class BoardPost implements Parcelable {

    public static final String BOARD_TYPE_FIELD = "board_type";

    private static final String ID_FIELD = "_id";

    @DatabaseField(id = true, columnName = ID_FIELD, generatedId = false)
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
    private long createdTime;

    @DatabaseField
    private long lastUpdatedTime;
    
    @DatabaseField(columnName = BOARD_TYPE_FIELD)
    private BoardType boardType;

    @ForeignCollectionField
    private transient Collection<BoardPostComment> comments;

    private List<BoardPostCommentTreeNode> boardPostTreeNodes;

    public BoardPost() {
	boardPostTreeNodes = new ArrayList<BoardPostCommentTreeNode>();
    }

    public BoardPost(Collection<BoardPostComment> comments) {
	setComments(comments);
    }

    public BoardPost(Parcel in) {
	boardPostTreeNodes = new ArrayList<BoardPostCommentTreeNode>();
	createFromParcel(in);
    }

    public void setComments(Collection<BoardPostComment> newComents) {
	boardPostTreeNodes = new ArrayList<BoardPostCommentTreeNode>();
	for (BoardPostComment comment : newComents) {
	    if (comment.getCommentLevel() == 0) {
		boardPostTreeNodes.add(makeTreeNode(comment, newComents));
	    }
	}
	if (comments == null) {
	    comments = new ArrayList<BoardPostComment>();
	}
	comments.clear();
	for (BoardPostCommentTreeNode node : boardPostTreeNodes) {
	    comments.addAll(node.getCommentAndAllChildren());
	}
    }

    public List<BoardPostCommentTreeNode> getTreeNodes() {
	return boardPostTreeNodes;
    }

    public Collection<BoardPostComment> getComments() {
	if (comments == null) {
	    if (boardPostTreeNodes == null) {
		boardPostTreeNodes = new ArrayList<BoardPostCommentTreeNode>();
	    }
	    if (comments == null) {
		comments = new ArrayList<BoardPostComment>();
	    }
	    for (BoardPostCommentTreeNode node : boardPostTreeNodes) {
		comments.addAll(node.getCommentAndAllChildren());
	    }
	}
	return comments;
    }

    private boolean isSummaryPost() {
	return TextUtils.isEmpty(content);
    }

    private BoardPostCommentTreeNode makeTreeNode(BoardPostComment comment,
	    Collection<BoardPostComment> allComments) {
	BoardPostCommentTreeNode node = new BoardPostCommentTreeNode(comment);
	int nodeLevel = comment.getCommentLevel();
	List<BoardPostComment> allCommentsList = new ArrayList<BoardPostComment>(
		allComments);
	int nodeIndex = allCommentsList.indexOf(comment);
	comment.setTreeNode(node);
	for (int i = nodeIndex + 1; i < allCommentsList.size(); i++) {
	    BoardPostComment childComment = allCommentsList.get(i);
	    if (childComment.getCommentLevel() > nodeLevel + 1)
		continue;
	    if (childComment.getCommentLevel() <= nodeLevel)
		break;
	    node.addChild(makeTreeNode(childComment, allCommentsList));
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

    public long getCreatedTime() {
	return createdTime;
    }

    public void setCreatedTime(long createdTime) {
	this.createdTime = createdTime;
    }

    public long getLastUpdatedTime() {
	return lastUpdatedTime;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
	this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getLastUpdatedInReadableString() {
	StringBuilder lastUpdatedBuilder = new StringBuilder();
	if (lastUpdatedTime > 0) {
	    CharSequence friendlyTime = DateUtils.getRelativeTimeSpanString(
		    lastUpdatedTime, System.currentTimeMillis(),
		    DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
	    lastUpdatedBuilder.append("Last updated ");
	    lastUpdatedBuilder.append(friendlyTime.toString());

	}
	return lastUpdatedBuilder.toString();
    }

    public BoardType getBoardType() {
	return boardType;
    }

    public void setBoardType(BoardType boardType) {
	this.boardType = boardType;
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
	parcel.writeLong(createdTime);
	parcel.writeLong(lastUpdatedTime);
	parcel.writeSerializable(boardType);
	Collection<BoardPostComment> comments = getComments();
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
	createdTime = parcel.readLong();
	lastUpdatedTime = parcel.readLong();
	boardType = (BoardType) parcel.readSerializable();

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
