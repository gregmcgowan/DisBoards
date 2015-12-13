package com.drownedinsound.data.model;

import java.util.ArrayList;

/**
 * Represents a board post comment which may be a reply to an
 * existing comment and/or have children comments
 *
 * @author Greg
 */
public class BoardPostCommentTreeNode {

    private BoardPostComment boardPostComment;

    private ArrayList<BoardPostCommentTreeNode> children;

    private BoardPostCommentTreeNode parent;

    public BoardPostCommentTreeNode(BoardPostComment boardPostComment) {
        this.boardPostComment = boardPostComment;
        children = new ArrayList<>();
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


}
