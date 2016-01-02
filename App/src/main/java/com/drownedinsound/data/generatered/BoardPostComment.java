package com.drownedinsound.data.generatered;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import com.drownedinsound.data.model.BoardPostCommentTreeNode;
// KEEP INCLUDES END
/**
 * Entity mapped to table "BOARD_POST_COMMENT".
 */
public class BoardPostComment {

    private String commentID;
    private String title;
    private String content;
    private String authorUsername;
    private String replyToUsername;
    private String usersWhoHaveThissed;
    private String dateAndTime;
    private int commentLevel;
    private String boardPostID;

    // KEEP FIELDS - put your custom fields here
    private boolean actionSectionVisible;

    private BoardPostCommentTreeNode treeNode;

    private boolean doHighlightedAnimation;

    private BoardPost boardPost;

    // KEEP FIELDS END

    public BoardPostComment() {
    }

    public BoardPostComment(String commentID) {
        this.commentID = commentID;
    }

    public BoardPostComment(String commentID, String title, String content, String authorUsername, String replyToUsername, String usersWhoHaveThissed, String dateAndTime, int commentLevel, String boardPostID) {
        this.commentID = commentID;
        this.title = title;
        this.content = content;
        this.authorUsername = authorUsername;
        this.replyToUsername = replyToUsername;
        this.usersWhoHaveThissed = usersWhoHaveThissed;
        this.dateAndTime = dateAndTime;
        this.commentLevel = commentLevel;
        this.boardPostID = boardPostID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
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

    public String getReplyToUsername() {
        return replyToUsername;
    }

    public void setReplyToUsername(String replyToUsername) {
        this.replyToUsername = replyToUsername;
    }

    public String getUsersWhoHaveThissed() {
        return usersWhoHaveThissed;
    }

    public void setUsersWhoHaveThissed(String usersWhoHaveThissed) {
        this.usersWhoHaveThissed = usersWhoHaveThissed;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public int getCommentLevel() {
        return commentLevel;
    }

    public void setCommentLevel(int commentLevel) {
        this.commentLevel = commentLevel;
    }

    public String getBoardPostID() {
        return boardPostID;
    }

    public void setBoardPostID(String boardPostID) {
        this.boardPostID = boardPostID;
    }

    // KEEP METHODS - put your custom methods here

    public boolean isActionSectionVisible() {
        return actionSectionVisible;
    }

    public void setActionSectionVisible(boolean actionSectionVisible) {
        this.actionSectionVisible = actionSectionVisible;
    }

    public void setDoHighlightedAnimation(boolean doHighlightedAnimation) {
        this.doHighlightedAnimation = doHighlightedAnimation;
    }

    public boolean isDoHighlightedAnimation() {
        return doHighlightedAnimation;
    }

    public BoardPostCommentTreeNode getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(BoardPostCommentTreeNode treeNode) {
        this.treeNode = treeNode;
    }

    public BoardPost getBoardPost() {
        return boardPost;
    }

    public void setBoardPost(BoardPost boardPost) {
        this.boardPost = boardPost;
    }

    // KEEP METHODS END

}
