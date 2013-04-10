package com.example.disdb.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.disdb.BoardPost;
import com.example.disdb.BoardPostComment;
import com.example.disdb.BoardPostStatus;
import com.example.disdb.DatabaseHelper;

public class BoardPostTest extends DisDBTest {

    private static final String SOCIAL_BOARD_TYPE = "Social";

    private static final String POST_ONE_AUTHOR = "Author";
    private static final String POST_ONE_CONTENT = "Some content";
    private static final String POST_ONE_DATE = "1970";
    private static final String POST_ONE_ID = "1";
    private static final String POST_ONE_SUMMARY = "Some..";
    private static final String POST_ONE_TITLE = "Title";

    private static final String COMMENT_ONE_AUTHOR = "CommentOneAuthor";
    private static final String COMMENT_ONE_CONTENT = "CommentOneContent";
    private static final String COMMENT_ONE_TITLE = "CommentOneTitle";
    private static final String COMMENT_ONE_ID = "123456";
    private static final String COMMENT_ONE_DATE_AND_TIME = "1970";
    private static final String COMMENT_ONE_USERS_WHO_THISED = "One other users thisd";
    
    private static final String COMMENT_TWO_AUTHOR = "CommentTwoAuthor";
    private static final String COMMENT_TWO_CONTENT = "CommentTwoContent";
    private static final String COMMENT_TWO_TITLE = "CommentTwoTitle";
    private static final String COMMENT_TWO_ID = "78910";
    private static final String COMMENT_TWO_DATE_AND_TIME = "1971";
    private static final String COMMENT_TWO_USERS_WHO_THISED = null;
    
    public void testAddBoardPostWithNoComments() {
	BoardPost expectedBoardPost = createBoardPostOne();

	DatabaseHelper.getInstance(getContext())
		.setBoardPost(expectedBoardPost);

	BoardPost actualBoardPost = DatabaseHelper.getInstance(getContext())
		.getBoardPost(POST_ONE_ID);
	assertBoardPost(expectedBoardPost, actualBoardPost);

    }
    
    private BoardPost createBoardPostOne(){
	BoardPost expectedBoardPost = new BoardPost();
	expectedBoardPost.setAuthorUsername(POST_ONE_AUTHOR);
	expectedBoardPost
		.setBoardPostStatus(BoardPostStatus.REQUESTING_SUMMARY);
	expectedBoardPost.setBoardTypeId(SOCIAL_BOARD_TYPE);
	expectedBoardPost.setContent(POST_ONE_CONTENT);
	expectedBoardPost.setDateOfPost(POST_ONE_DATE);
	expectedBoardPost.setId(POST_ONE_ID);
	expectedBoardPost.setNumberOfReplies(0);
	expectedBoardPost.setSummary(POST_ONE_SUMMARY);
	expectedBoardPost.setTitle(POST_ONE_TITLE);
	return expectedBoardPost;
    }
    
    public void testAddBoardPostWithComments() {
	BoardPost expectedBoardPost = createBoardPostOne();

	Collection<BoardPostComment> expectedBoardPosts = new ArrayList<BoardPostComment> ();
	expectedBoardPosts.add(createBoardPostCommentOne(expectedBoardPost));
	expectedBoardPosts.add(createBoardPostCommentTwo(expectedBoardPost));
	
	expectedBoardPost.setComments(expectedBoardPosts);
	
	DatabaseHelper.getInstance(getContext())
		.setBoardPost(expectedBoardPost);

	BoardPost actualBoardPost = DatabaseHelper.getInstance(getContext())
		.getBoardPost(POST_ONE_ID);
	assertBoardPost(expectedBoardPost, actualBoardPost);

    }
    
    private BoardPostComment createBoardPostCommentOne(BoardPost parent){
	BoardPostComment boardPostComment = new BoardPostComment();
	boardPostComment.setAuthorUsername(COMMENT_ONE_AUTHOR);
	boardPostComment.setCommentLevel(0);
	boardPostComment.setContent(COMMENT_ONE_CONTENT);
	boardPostComment.setDateAndTimeOfComment(COMMENT_ONE_DATE_AND_TIME);
	boardPostComment.setId(COMMENT_ONE_ID);
	boardPostComment.setTitle(COMMENT_ONE_TITLE);
	boardPostComment.setUsersWhoHaveThissed(COMMENT_ONE_USERS_WHO_THISED);
	boardPostComment.setBoardPost(parent);
	return boardPostComment;
    }
    
    private BoardPostComment createBoardPostCommentTwo(BoardPost parent){
	BoardPostComment boardPostComment = new BoardPostComment();
	boardPostComment.setAuthorUsername(COMMENT_TWO_AUTHOR);
	boardPostComment.setCommentLevel(1);
	boardPostComment.setContent(COMMENT_TWO_CONTENT);
	boardPostComment.setDateAndTimeOfComment(COMMENT_TWO_DATE_AND_TIME);
	boardPostComment.setId(COMMENT_TWO_ID);
	boardPostComment.setTitle(COMMENT_TWO_TITLE);
	boardPostComment.setUsersWhoHaveThissed(COMMENT_TWO_USERS_WHO_THISED);
	boardPostComment.setBoardPost(parent);
	return boardPostComment;
    }    

    public static void assertBoardPost(BoardPost expected, BoardPost actual) {
	assertEquals(expected.getId(), actual.getId());
	assertEquals(expected.getAuthorUsername(), actual.getAuthorUsername());
	assertEquals(expected.getContent(), actual.getContent());
	assertEquals(expected.getDateOfPost(), actual.getDateOfPost());
	assertEquals(expected.getLastViewedTime(), actual.getLastViewedTime());
	assertEquals(expected.getNumberOfReplies(), actual.getNumberOfReplies());
	assertEquals(expected.getSummary(), actual.getSummary());
	assertEquals(expected.getContent(), actual.getContent());
	assertEquals(expected.getTitle(), actual.getTitle());
	Collection<BoardPostComment> expectedComments = expected.getComments();
	if (expectedComments != null && expectedComments.size() > 0) {
	    assertComments(expectedComments, actual.getComments());
	}
    }

    private static void assertComments(
	    Collection<BoardPostComment> expected,
	    Collection<BoardPostComment> actual) {
	List<BoardPostComment> expectedList = new ArrayList<BoardPostComment>(expected);
	List<BoardPostComment> actualList = new ArrayList<BoardPostComment>( actual);
	int currentCommentIndex = 0;
	for(BoardPostComment expectedComment : expectedList) {
	    assertComment(expectedComment,actualList.get(currentCommentIndex++));
	}

    }

    private static void assertComment(BoardPostComment expected,
	    BoardPostComment actual) {
	assertEquals(expected.getAuthorUsername(),actual.getAuthorUsername());
	assertEquals(expected.getCommentLevel(),actual.getCommentLevel());
	assertEquals(expected.getContent(),actual.getContent());
	assertEquals(expected.getDateAndTimeOfComment(),actual.getDateAndTimeOfComment());
	assertEquals(expected.getId(),actual.getId());
	assertEquals(expected.getTitle(),actual.getTitle());
	assertEquals(expected.getBoardPost().getId(),actual.getBoardPost().getId());;
    }

}
