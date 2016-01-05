package com.drownedinsound.test;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostComment;
import com.drownedinsound.data.generatered.BoardPostSummary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import junit.framework.Assert;

public class AssertUtils {

    public static void assertBoardPosts(List<BoardPost> expectedBoardPosts,
            List<BoardPost> actualBoardPosts) {
        int currentBoardPostIndex = 0;
        for (BoardPost expectedBoardPost : expectedBoardPosts) {
            BoardPost actualBoardPost = actualBoardPosts
                    .get(currentBoardPostIndex++);
            assertBoardPost(expectedBoardPost, actualBoardPost);
        }
    }

    public static void assertBoardPost(BoardPost expected, BoardPost actual) {
        Assert.assertEquals(expected.getBoardPostID(), actual.getBoardPostID());
        Assert.assertEquals(expected.getAuthorUsername(),
        	actual.getAuthorUsername());
        Assert.assertEquals(expected.getContent(), actual.getContent());
        Assert.assertEquals(expected.getDateOfPost(), actual.getDateOfPost());
        Assert.assertEquals(expected.getNumberOfReplies(),
                actual.getNumberOfReplies());
        Assert.assertEquals(expected.getSummary(), actual.getSummary());
        Assert.assertEquals(expected.getContent(), actual.getContent());
        Assert.assertEquals(expected.getTitle(), actual.getTitle());
        Assert.assertEquals(expected.getBoardListTypeID(), actual.getBoardListTypeID());

        Collection<BoardPostComment> expectedComments = expected.getComments();
        if (expectedComments != null && expectedComments.size() > 0) {
            assertComments(expectedComments, actual.getComments());
        }
    }

    public static void assertBoardPostSummary(BoardPostSummary expected, BoardPostSummary actual) {
        Assert.assertEquals(expected.getBoardPostID(), actual.getBoardPostID());
        Assert.assertEquals(expected.getAuthorUsername(),
                actual.getAuthorUsername());;
        Assert.assertEquals(expected.getNumberOfReplies(),
                actual.getNumberOfReplies());
        Assert.assertEquals(expected.getTitle(), actual.getTitle());
        Assert.assertEquals(expected.getBoardListTypeID(), actual.getBoardListTypeID());
    }

    private static void assertComments(Collection<BoardPostComment> expected,
            Collection<BoardPostComment> actual) {
        List<BoardPostComment> expectedList = new ArrayList<>(
                expected);
        List<BoardPostComment> actualList = new ArrayList<>(
                actual);
        int currentCommentIndex = 0;
        for (BoardPostComment expectedComment : expectedList) {
            assertComment(expectedComment,
                    actualList.get(currentCommentIndex++));
        }

    }

    private static void assertComment(BoardPostComment expected,
            BoardPostComment actual) {
        Assert.assertEquals("Expected "+expected.getAuthorUsername() + " actual "+actual.getAuthorUsername(),expected.getAuthorUsername(),
                actual.getAuthorUsername());
        Assert.assertEquals(expected.getCommentLevel(),
                actual.getCommentLevel());
        Assert.assertEquals(expected.getContent(), actual.getContent());
        Assert.assertEquals(expected.getDateAndTime(),
                actual.getDateAndTime());
        Assert.assertEquals(expected.getCommentID(), actual.getCommentID());
        Assert.assertEquals(expected.getTitle(), actual.getTitle());
        Assert.assertEquals(expected.getBoardPostID(), actual.getBoardPostID());
        Assert.assertEquals("Expected ["+expected.getUsersWhoHaveThissed() + "] actual ["+actual.getUsersWhoHaveThissed()+"]",expected.getUsersWhoHaveThissed(), actual.getUsersWhoHaveThissed());
    }
}
