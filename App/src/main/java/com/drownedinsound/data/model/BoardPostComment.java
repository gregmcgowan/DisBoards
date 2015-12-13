package com.drownedinsound.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents a comment that has been made against a drowned in sound board post
 *
 * @author Greg
 */
@DatabaseTable(tableName = "board_post_comment")
public class BoardPostComment {

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
    private String replyToUsername;

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

    private boolean doHighlightedAnimation;


    public BoardPostComment() {

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

    public String getReplyToUsername() {
        return replyToUsername;
    }

    public void setReplyToUsername(String replyToUsername) {
        this.replyToUsername = replyToUsername;
    }


    @Override
    public String toString() {
        return "BoardPostComment [id=" + id + ", title=" + title + ", content="
                + content + ", authorUsername=" + authorUsername
                + ", usersWhoHaveThissed=" + usersWhoHaveThissed
                + ", dateAndTimeOfComment=" + dateAndTimeOfComment
                + ", boardPost=" + (boardPost != null ? boardPost.getId() : boardPost)
                + ", commentLevel=" + commentLevel
                + ", actionSectionVisible=" + actionSectionVisible + "]";
    }

    public void setDoHighlightedAnimation(boolean doHighlightedAnimation) {
        this.doHighlightedAnimation = doHighlightedAnimation;
    }

    public boolean isDoHighlightedAnimation() {
        return doHighlightedAnimation;
    }

}
