package com.drownedinsound.data.generatered;

import java.util.List;
import com.drownedinsound.data.generatered.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.drownedinsound.data.model.BoardPostCommentTreeNode;

import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
// KEEP INCLUDES END
/**
 * Entity mapped to table "BOARD_POST".
 */
public class BoardPost {

    private String boardPostID;
    private String title;
    private String summary;
    private String content;
    private String authorUsername;
    private String dateOfPost;
    private Integer numberOfReplies;
    private long lastViewedTime;
    private long createdTime;
    private long lastUpdatedTime;
    private String latestCommentID;
    private Integer numberOfTimesRead;
    private boolean isFavourite;
    private boolean isSticky;
    private String boardListTypeID;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient BoardPostDao myDao;

    private BoardPostList boardPostList;
    private String boardPostList__resolvedKey;

    private List<BoardPostComment> unorderedComments;

    // KEEP FIELDS - put your custom fields here
    public static final BoardPostComparator COMPARATOR = new BoardPostComparator();

    private List<BoardPostCommentTreeNode> boardPostTreeNodes;
    // KEEP FIELDS END

    public BoardPost() {
    }

    public BoardPost(String boardPostID) {
        this.boardPostID = boardPostID;
    }

    public BoardPost(String boardPostID, String title, String summary, String content, String authorUsername, String dateOfPost, Integer numberOfReplies, long lastViewedTime, long createdTime, long lastUpdatedTime, String latestCommentID, Integer numberOfTimesRead, boolean isFavourite, boolean isSticky, String boardListTypeID) {
        this.boardPostID = boardPostID;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.authorUsername = authorUsername;
        this.dateOfPost = dateOfPost;
        this.numberOfReplies = numberOfReplies;
        this.lastViewedTime = lastViewedTime;
        this.createdTime = createdTime;
        this.lastUpdatedTime = lastUpdatedTime;
        this.latestCommentID = latestCommentID;
        this.numberOfTimesRead = numberOfTimesRead;
        this.isFavourite = isFavourite;
        this.isSticky = isSticky;
        this.boardListTypeID = boardListTypeID;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBoardPostDao() : null;
    }

    public String getBoardPostID() {
        return boardPostID;
    }

    public void setBoardPostID(String boardPostID) {
        this.boardPostID = boardPostID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public Integer getNumberOfReplies() {
        return numberOfReplies;
    }

    public void setNumberOfReplies(Integer numberOfReplies) {
        this.numberOfReplies = numberOfReplies;
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

    public String getLatestCommentID() {
        return latestCommentID;
    }

    public void setLatestCommentID(String latestCommentID) {
        this.latestCommentID = latestCommentID;
    }

    public Integer getNumberOfTimesRead() {
        return numberOfTimesRead;
    }

    public void setNumberOfTimesRead(Integer numberOfTimesRead) {
        this.numberOfTimesRead = numberOfTimesRead;
    }

    public boolean getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public boolean getIsSticky() {
        return isSticky;
    }

    public void setIsSticky(boolean isSticky) {
        this.isSticky = isSticky;
    }

    public String getBoardListTypeID() {
        return boardListTypeID;
    }

    public void setBoardListTypeID(String boardListTypeID) {
        this.boardListTypeID = boardListTypeID;
    }

    /** To-one relationship, resolved on first access. */
    public BoardPostList getBoardPostList() {
        String __key = this.boardListTypeID;
        if (boardPostList__resolvedKey == null || boardPostList__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BoardPostListDao targetDao = daoSession.getBoardPostListDao();
            BoardPostList boardPostListNew = targetDao.load(__key);
            synchronized (this) {
                boardPostList = boardPostListNew;
            	boardPostList__resolvedKey = __key;
            }
        }
        return boardPostList;
    }

    public void setBoardPostList(BoardPostList boardPostList) {
        synchronized (this) {
            this.boardPostList = boardPostList;
            boardListTypeID = boardPostList == null ? null : boardPostList.getBoardListTypeID();
            boardPostList__resolvedKey = boardListTypeID;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<BoardPostComment> getUnorderedComments() {
        if (unorderedComments == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BoardPostCommentDao targetDao = daoSession.getBoardPostCommentDao();
            List<BoardPostComment> unorderedCommentsNew = targetDao._queryBoardPost_UnorderedComments(boardPostID);
            synchronized (this) {
                if(unorderedComments == null) {
                    unorderedComments = unorderedCommentsNew;
                }
            }
        }
        return unorderedComments;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetUnorderedComments() {
        unorderedComments = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    public void setComments(Collection<BoardPostComment> newComents) {
        boardPostTreeNodes = new ArrayList<>();
        for (BoardPostComment comment : newComents) {
            if (comment.getCommentLevel() == 0) {
                boardPostTreeNodes.add(makeTreeNode(comment, newComents));
            }
        }
    }

    public List<BoardPostComment> getComments() {
        List<BoardPostComment> comments = new ArrayList<>();
        if (boardPostTreeNodes == null) {
            boardPostTreeNodes = new ArrayList<>();
        }
        for (BoardPostCommentTreeNode node : boardPostTreeNodes) {
            comments.addAll(node.getCommentAndAllChildren());
        }
        return comments;
    }

    private BoardPostCommentTreeNode makeTreeNode(
            BoardPostComment comment,
            Collection<BoardPostComment> allComments) {
        BoardPostCommentTreeNode node = new BoardPostCommentTreeNode(comment);
        int nodeLevel = comment.getCommentLevel();
        List<BoardPostComment> allCommentsList = new ArrayList<>(
                allComments);
        int nodeIndex = allCommentsList.indexOf(comment);
        comment.setTreeNode(node);
        for (int i = nodeIndex + 1; i < allCommentsList.size(); i++) {
            BoardPostComment childComment = allCommentsList.get(i);
            if (childComment.getCommentLevel() > nodeLevel + 1) {
                continue;
            }
            if (childComment.getCommentLevel() <= nodeLevel) {
                break;
            }
            node.addChild(makeTreeNode(childComment, allCommentsList));
        }
        return node;
    }

    public static class BoardPostComparator implements Comparator<BoardPost> {

        @Override
        public int compare(BoardPost leftHandSideBoardPost,
                BoardPost rightHandSidePost) {
            if (leftHandSideBoardPost == null) {
                return 1;
            }
            if (rightHandSidePost == null) {
                return -1;
            }

            boolean lhsIsSticky = leftHandSideBoardPost.getIsSticky();
            boolean rhsIsSticky = rightHandSidePost.getIsSticky();

            if (!lhsIsSticky && rhsIsSticky) {
                return 1;
            }
            if (lhsIsSticky && !rhsIsSticky) {
                return -1;
            }

            long leftHandsideLastUpdatedTime = leftHandSideBoardPost
                    .getLastUpdatedTime();
            long rightHandsideLastUpdatedTime = rightHandSidePost
                    .getLastUpdatedTime();

            if (rightHandsideLastUpdatedTime > leftHandsideLastUpdatedTime) {
                return 1;
            }
            if (leftHandsideLastUpdatedTime > rightHandsideLastUpdatedTime) {
                return -1;
            }

            return 0;
        }
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

    @Override
    public String toString() {
        return "BoardPost [id=" + boardPostID + ", title=" + title + ", isSticky="
                + isSticky + ", summary=" + summary + ", content=" + content
                + ", authorUsername=" + authorUsername + ", dateOfPost="
                + dateOfPost + ", numberOfReplies=" + numberOfReplies
                + ", " + ", lastViewedTime="
                + lastViewedTime + ", createdTime=" + createdTime
                + ", lastUpdatedTime=" + lastUpdatedTime + ", boardType="
                + boardListTypeID + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((boardPostID == null) ? 0 : boardPostID.hashCode());
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
        BoardPost other = (BoardPost) obj;
        if (boardPostID == null) {
            if (other.boardPostID != null) {
                return false;
            }
        } else if (!boardPostID.equals(other.boardPostID)) {
            return false;
        }
        return true;
    }
    // KEEP METHODS END

}