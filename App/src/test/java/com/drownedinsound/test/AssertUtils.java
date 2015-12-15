package com.drownedinsound.test;

import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardPostComment;

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
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getAuthorUsername(),
        	actual.getAuthorUsername());
        Assert.assertEquals(expected.getContent(), actual.getContent());
        Assert.assertEquals(expected.getDateOfPost(), actual.getDateOfPost());
        Assert.assertEquals(expected.getLastViewedTime(),
                actual.getLastViewedTime());
        Assert.assertEquals(expected.getNumberOfReplies(),
                actual.getNumberOfReplies());
        Assert.assertEquals(expected.getSummary(), actual.getSummary());
        Assert.assertEquals(expected.getContent(), actual.getContent());
        Assert.assertEquals(expected.getTitle(), actual.getTitle());
        Assert.assertEquals(expected.getBoardListType(), actual.getBoardListType());

        Collection<BoardPostComment> expectedComments = expected.getComments();
        if (expectedComments != null && expectedComments.size() > 0) {
            assertComments(expectedComments, actual.getComments());
        }
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
        Assert.assertEquals(expected.getDateAndTimeOfComment(),
                actual.getDateAndTimeOfComment());
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getTitle(), actual.getTitle());
        Assert.assertEquals(expected.getBoardPost().getId(), actual
                .getBoardPost().getId());
        Assert.assertEquals("Expected ["+expected.getUsersWhoHaveThissed() + "] actual ["+actual.getUsersWhoHaveThissed()+"]",expected.getUsersWhoHaveThissed(), actual.getUsersWhoHaveThissed());
    }
}
