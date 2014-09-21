package com.gregmcgowan.drownedinsound.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Represents a board post comment which may be a reply to an
 * existing comment and/or have children comments
 *
 * @author Greg
 */
public class BoardPostCommentTreeNode implements Parcelable {

    private BoardPostComment boardPostComment;

    private ArrayList<BoardPostCommentTreeNode> children;

    private BoardPostCommentTreeNode parent;

    public BoardPostCommentTreeNode(BoardPostComment boardPostComment) {
        this.boardPostComment = boardPostComment;
        children = new ArrayList<BoardPostCommentTreeNode>();
    }

    public BoardPostCommentTreeNode(Parcel in) {
        writeFromParcel(in);
    }

    public void addChild(BoardPostCommentTreeNode childBoardPostComment) {
        this.children.add(childBoardPostComment);
        childBoardPostComment.setParent(this);
    }

    public BoardPostCommentTreeNode getParent() {
        return parent;
    }

    public void setParent(BoardPostCommentTreeNode parent) {
        this.parent = parent;
    }

    public BoardPostComment getBoardPostComment() {
        return boardPostComment;
    }

    public ArrayList<BoardPostCommentTreeNode> getChildren() {
        return children;
    }

    public ArrayList<BoardPostComment> getCommentAndAllChildren() {
        ArrayList<BoardPostComment> commentAndItsChildren = new ArrayList<BoardPostComment>();
        commentAndItsChildren.add(getBoardPostComment());
        for (BoardPostCommentTreeNode boardPostCommmentTreeNode : children) {
            commentAndItsChildren.addAll(boardPostCommmentTreeNode.getCommentAndAllChildren());
        }
        return commentAndItsChildren;
    }

    public boolean hasChildren() {
        return children != null && children.size() > 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((boardPostComment == null) ? 0 : boardPostComment.hashCode());
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
        BoardPostCommentTreeNode other = (BoardPostCommentTreeNode) obj;
        if (boardPostComment == null) {
            if (other.boardPostComment != null) {
                return false;
            }
        } else if (!boardPostComment.equals(other.boardPostComment)) {
            return false;
        }
        return true;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    private void writeFromParcel(Parcel in) {
        boardPostComment = in.readParcelable(BoardPostComment.class.getClassLoader());
        children = in.readArrayList(BoardPostCommentTreeNode.class.getClassLoader());
        if (children == null) {
            children = new ArrayList<BoardPostCommentTreeNode>();
        }
        parent = in.readParcelable(BoardPostCommentTreeNode.class.getClassLoader());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(boardPostComment, flags);
        dest.writeParcelableArray(children.toArray(new BoardPostCommentTreeNode[children.size()]),
                flags);
        dest.writeParcelable(parent, flags);
    }

    public static final Parcelable.Creator<BoardPostCommentTreeNode> CREATOR
            = new Parcelable.Creator<BoardPostCommentTreeNode>() {
        public BoardPostCommentTreeNode createFromParcel(Parcel in) {
            return new BoardPostCommentTreeNode(in);
        }

        public BoardPostCommentTreeNode[] newArray(int size) {
            return new BoardPostCommentTreeNode[size];
        }
    };


}
