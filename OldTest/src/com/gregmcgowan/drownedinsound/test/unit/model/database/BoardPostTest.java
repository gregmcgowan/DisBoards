package com.gregmcgowan.drownedinsound.test.unit.model.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gregmcgowan.drownedinsound.data.DatabaseHelper;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;
import com.gregmcgowan.drownedinsound.data.model.BoardPostStatus;
import com.gregmcgowan.drownedinsound.data.model.BoardType;

public class BoardPostTest extends DatabaseTest {

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

    private static final String POST_TWO_AUTHOR = "Author Two";
    private static final String POST_TWO_CONTENT = "Some content two";
    private static final String POST_TWO_DATE = "1971";
    private static final String POST_TWO_ID = "2";
    private static final String POST_TWO_SUMMARY = "Some.. 2";
    private static final String POST_TWO_TITLE = "Title 2";

    private static final String POST_THREE_AUTHOR = "Author three";
    private static final String POST_THREE_CONTENT = "Some content three";
    private static final String POST_THREE_DATE = "1972";
    private static final String POST_THREE_ID = "3";
    private static final String POST_THREE_SUMMARY = "Some..3";
    private static final String POST_THREE_TITLE = "Title 3";

    private static final String POST_FOUR_AUTHOR = "Author Four";
    private static final String POST_FOUR_CONTENT = "Some content four";
    private static final String POST_FOUR_DATE = "1973";
    private static final String POST_FOUR_ID = "4";
    private static final String POST_FOUR_SUMMARY = "Some..4";
    private static final String POST_FOUR_TITLE = "Title 4";

    private BoardPost createBoardPost(String author, BoardPostStatus status,
	    BoardType boardType, String content, String dateOfPost, String id,
	    int numberOfReplies, String summary, String title) {
	BoardPost expectedBoardPost = new BoardPost();
	expectedBoardPost.setAuthorUsername(author);
	expectedBoardPost.setBoardPostStatus(status);
	expectedBoardPost.setBoardType(boardType);
	expectedBoardPost.setContent(content);
	expectedBoardPost.setDateOfPost(dateOfPost);
	expectedBoardPost.setId(id);
	expectedBoardPost.setNumberOfReplies(numberOfReplies);
	expectedBoardPost.setSummary(summary);
	expectedBoardPost.setTitle(title);
	return expectedBoardPost;
    }

    public void testAddBoardPostWithNoComments() {
	BoardPost expectedBoardPost = createBoardPost(POST_ONE_AUTHOR,
		BoardPostStatus.REQUESTING_SUMMARY, BoardType.SOCIAL,
		POST_ONE_CONTENT, POST_ONE_DATE, POST_ONE_ID, 0,
		POST_ONE_SUMMARY, POST_ONE_TITLE);

	DatabaseHelper.getInstance(getContext())
		.setBoardPost(expectedBoardPost);

	BoardPost actualBoardPost = DatabaseHelper.getInstance(getContext())
		.getBoardPost(POST_ONE_ID);
	assertBoardPost(expectedBoardPost, actualBoardPost);

    }

    public void testAddBoardPostAndAddAgain() {
	//Add a board post
	BoardPost expectedBoardPost = createBoardPost(POST_ONE_AUTHOR,
		BoardPostStatus.REQUESTING_SUMMARY, BoardType.SOCIAL,
		POST_ONE_CONTENT, POST_ONE_DATE, POST_ONE_ID, 0,
		POST_ONE_SUMMARY, POST_ONE_TITLE);

	DatabaseHelper.getInstance(getContext())
		.setBoardPost(expectedBoardPost);

	BoardPost actualBoardPost = DatabaseHelper.getInstance(getContext())
		.getBoardPost(POST_ONE_ID);
	assertBoardPost(expectedBoardPost, actualBoardPost);
	
	//Add in again and make sure it does not get added twice
	DatabaseHelper.getInstance(getContext())
	.setBoardPost(expectedBoardPost);
	
	List<BoardPost> actualSocialBoardPosts = DatabaseHelper.getInstance(
		getContext()).getBoardPosts(BoardType.SOCIAL);
	assertEquals(actualSocialBoardPosts.size(), 1);
    }

    public void testAddBoardPostWithComments() {

	BoardPost expectedBoardPost = createBoardPost(POST_ONE_AUTHOR,
		BoardPostStatus.REQUESTING_SUMMARY, BoardType.SOCIAL,
		POST_ONE_CONTENT, POST_ONE_DATE, POST_ONE_ID, 2,
		POST_ONE_SUMMARY, POST_ONE_TITLE);

	Collection<BoardPostComment> expectedBoardPosts = new ArrayList<BoardPostComment>();
	expectedBoardPosts.add(createBoardPostCommentOne(expectedBoardPost));
	expectedBoardPosts.add(createBoardPostCommentTwo(expectedBoardPost));

	expectedBoardPost.setComments(expectedBoardPosts);

	DatabaseHelper.getInstance(getContext())
		.setBoardPost(expectedBoardPost);

	BoardPost actualBoardPost = DatabaseHelper.getInstance(getContext())
		.getBoardPost(POST_ONE_ID);
	assertBoardPost(expectedBoardPost, actualBoardPost);	

    }
    
    public void testAddBoardPostWithCommentsInTwoSteps() {

	BoardPost expectedBoardPost = createBoardPost(POST_ONE_AUTHOR,
		BoardPostStatus.REQUESTING_SUMMARY, BoardType.SOCIAL,
		POST_ONE_CONTENT, POST_ONE_DATE, POST_ONE_ID, 2,
		POST_ONE_SUMMARY, POST_ONE_TITLE);

	Collection<BoardPostComment> expectedBoardPosts = new ArrayList<BoardPostComment>();
	expectedBoardPosts.add(createBoardPostCommentOne(expectedBoardPost));
	expectedBoardPost.setComments(expectedBoardPosts);

	DatabaseHelper.getInstance(getContext())
		.setBoardPost(expectedBoardPost);

	BoardPost actualBoardPost = DatabaseHelper.getInstance(getContext())
		.getBoardPost(POST_ONE_ID);
	assertBoardPost(expectedBoardPost, actualBoardPost);	
	
	expectedBoardPosts.add(createBoardPostCommentTwo(expectedBoardPost));
	expectedBoardPost.setComments(expectedBoardPosts);

	DatabaseHelper.getInstance(getContext())
		.setBoardPost(expectedBoardPost);

	actualBoardPost = DatabaseHelper.getInstance(getContext())
		.getBoardPost(POST_ONE_ID);
	assertBoardPost(expectedBoardPost, actualBoardPost);	
    }

    public void testAddSeveralPostsWithDifferentBoardIds() {
	BoardPost expectedBoardPost1 = createBoardPost(POST_ONE_AUTHOR,
		BoardPostStatus.REQUESTING_SUMMARY, BoardType.SOCIAL,
		POST_ONE_CONTENT, POST_ONE_DATE, POST_ONE_ID, 0,
		POST_ONE_SUMMARY, POST_ONE_TITLE);
	BoardPost expectedBoardPost2 = createBoardPost(POST_TWO_AUTHOR,
		BoardPostStatus.REQUESTING_SUMMARY, BoardType.SOCIAL,
		POST_TWO_CONTENT, POST_TWO_DATE, POST_TWO_ID, 0,
		POST_TWO_SUMMARY, POST_TWO_TITLE);
	BoardPost expectedBoardPost3 = createBoardPost(POST_THREE_AUTHOR,
		BoardPostStatus.REQUESTING_SUMMARY, BoardType.MUSIC,
		POST_THREE_CONTENT, POST_THREE_DATE, POST_THREE_ID, 0,
		POST_THREE_SUMMARY, POST_THREE_TITLE);
	BoardPost expectedBoardPost4 = createBoardPost(POST_FOUR_AUTHOR,
		BoardPostStatus.REQUESTING_SUMMARY, BoardType.MUSIC,
		POST_FOUR_CONTENT, POST_FOUR_DATE, POST_FOUR_ID, 0,
		POST_FOUR_SUMMARY, POST_FOUR_TITLE);

	List<BoardPost> expectedSocialBoardPosts = new ArrayList<BoardPost>();
	expectedSocialBoardPosts.add(expectedBoardPost1);
	expectedSocialBoardPosts.add(expectedBoardPost2);

	List<BoardPost> expectedMusicBoardPosts = new ArrayList<BoardPost>();
	expectedMusicBoardPosts.add(expectedBoardPost3);
	expectedMusicBoardPosts.add(expectedBoardPost4);

	DatabaseHelper.getInstance(getContext()).setBoardPosts(
		expectedSocialBoardPosts);

	List<BoardPost> actualSocialBoardPosts = DatabaseHelper.getInstance(
		getContext()).getBoardPosts(BoardType.SOCIAL);

	assertBoardPosts(expectedSocialBoardPosts, actualSocialBoardPosts);

	DatabaseHelper.getInstance(getContext()).setBoardPosts(
		expectedMusicBoardPosts);

	List<BoardPost> actualMusicBoardPosts = DatabaseHelper.getInstance(
		getContext()).getBoardPosts(BoardType.MUSIC);

	assertBoardPosts(expectedMusicBoardPosts, actualMusicBoardPosts);

    }

    private BoardPostComment createBoardPostCommentOne(BoardPost parent) {
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

    private BoardPostComment createBoardPostCommentTwo(BoardPost parent) {
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

    private static void assertComments(Collection<BoardPostComment> expected,
	    Collection<BoardPostComment> actual) {
	List<BoardPostComment> expectedList = new ArrayList<BoardPostComment>(
		expected);
	List<BoardPostComment> actualList = new ArrayList<BoardPostComment>(
		actual);
	int currentCommentIndex = 0;
	for (BoardPostComment expectedComment : expectedList) {
	    assertComment(expectedComment,
		    actualList.get(currentCommentIndex++));
	}

    }

    private static void assertComment(BoardPostComment expected,
	    BoardPostComment actual) {
	assertEquals(expected.getAuthorUsername(), actual.getAuthorUsername());
	assertEquals(expected.getCommentLevel(), actual.getCommentLevel());
	assertEquals(expected.getContent(), actual.getContent());
	assertEquals(expected.getDateAndTimeOfComment(),
		actual.getDateAndTimeOfComment());
	assertEquals(expected.getId(), actual.getId());
	assertEquals(expected.getTitle(), actual.getTitle());
	assertEquals(expected.getBoardPost().getId(), actual.getBoardPost()
		.getId());
    }

}
