package com.gregmcgowan.drownedinsound.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;
import com.gregmcgowan.drownedinsound.data.model.BoardPostSummary;

public class FakeDataFactory {

    private final static String USER_NAME = "A drowned in sound user";
    private final static String SHORT_BOARD_POST = "Here is a short board post";
    private final static String MEDIUM_BOARD_POST = "<p>problem is, I no longer require his services. dunno why I agreed in the first place, tbh. real sucker for this type of doorstep manipulation, me</p>\n<p>don't get me wrong, he does an OK job, but I just don't really need my windows cleaned once a month.  every year is perfectly fine. every other year, in fact. </p>\n<p>problem is:</p>\n<p>a) I don't like letting people down. kid has to earn a living and shiiiit\n<br />b) he looks a right hard fuck and may pummel me to dust</p>\n<p>I've tried avoiding him, but as long as I have windows, he'll always be cleaning them</p>\n<p>guess I'm gonna have to move or something</p>\n<p>what you say?</p>";
    private final static String SHORT_COMMENT = "";
    private final static String LONG_COMMENT = "<p>Here is a much larger comment</p> <p>gerwgggggggggggggggggggggggggggggggggggggggggggggg</p> <p>fffffffffffffffff</>";

    // private final static String REALLY_LONG_BOARD_POST = ""

    public static BoardPost generateRandomBoardPost() {
	BoardPost boardPost = new BoardPost();
	boardPost.setAuthorUsername(USER_NAME);
	boardPost.setTitle("RAnDOm Dis Post " + System.currentTimeMillis());
	long currentTime = System.currentTimeMillis();
	if (currentTime % 2 == 0) {
	    boardPost.setContent(SHORT_BOARD_POST);
	} else {
	    boardPost.setContent(MEDIUM_BOARD_POST);
	}
	boardPost.setComments(getFakeComments());
	boardPost.setDateOfPost(new Date());
	// boardPost.setContent("Here is a board)
	return boardPost;
    }

    private static List<BoardPostComment> getFakeComments() {
	ArrayList<BoardPostComment> comments = new ArrayList<BoardPostComment>();
	long currentTime = System.currentTimeMillis();
	if(currentTime % 2 == 0){
	    //simple comments
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, false,true));
	    comments.add(createRandomCommentAtLevel(0, true, true, false));
	    comments.add(createRandomCommentAtLevel(0, true, false,true));
	    comments.add(createRandomCommentAtLevel(0, true, false, false));
	    comments.add(createRandomCommentAtLevel(0, false, true, true));
	} else if(currentTime % 5 == 0){
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(8, true, true, true));
	    comments.add(createRandomCommentAtLevel(9, true, true, true));
	    comments.add(createRandomCommentAtLevel(10, true, true, true));
	    comments.add(createRandomCommentAtLevel(11, true, true, true));
	    comments.add(createRandomCommentAtLevel(12, true, true, true));
	    comments.add(createRandomCommentAtLevel(13, true, true, true));
	    comments.add(createRandomCommentAtLevel(14, true, true, true));
	    comments.add(createRandomCommentAtLevel(15, true, true, true));
	    comments.add(createRandomCommentAtLevel(16, true, true, true));
	    comments.add(createRandomCommentAtLevel(17, true, true, true));
	    comments.add(createRandomCommentAtLevel(18, true, true, true));
	    comments.add(createRandomCommentAtLevel(19, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	} else {
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	    comments.add(createRandomCommentAtLevel(0, true, true, true));
	    comments.add(createRandomCommentAtLevel(1, true, true, true));
	    comments.add(createRandomCommentAtLevel(2, true, true, true));
	    comments.add(createRandomCommentAtLevel(3, true, true, true));
	    comments.add(createRandomCommentAtLevel(4, true, true, true));
	    comments.add(createRandomCommentAtLevel(5, true, true, true));
	    comments.add(createRandomCommentAtLevel(6, true, true, true));
	    comments.add(createRandomCommentAtLevel(7, true, true, true));
	}
	return comments;
    }

    private static BoardPostComment createRandomCommentAtLevel(int level,
	    boolean includeTitle, boolean includeContent,
	    boolean includeUsersThatHaveThised) {
	long currentTime = System.currentTimeMillis();

	BoardPostComment boardPostComment = new BoardPostComment();
	boardPostComment.setAuthorUsername("Random user name"
		+ System.currentTimeMillis());
	if (includeTitle) {
	    boardPostComment.setTitle("Here is a title");
	}
	if (includeContent) {
	    if (currentTime % 2 == 0) {
		boardPostComment.setContent(SHORT_COMMENT);
	    } else {
		boardPostComment.setContent(LONG_COMMENT);
	    }
	}
	boardPostComment.setCommentLevel(level);

	if (includeUsersThatHaveThised) {
	    String[] users = null;
	    if (currentTime % 2 == 0) {
		users = new String[] { "GREG", "SOMEONEELSE" };
	    } else {
		users = new String[] { "SOMEONEELSE", "SOMEONEELSE",
			"SOMEONEELSE", "SOMEONEELSE", "SOMEONEELSE",
			"SOMEONEELSE", "SOMEONEELSE" };
	    }
	    boardPostComment.setUsersWhoHaveThissed(users);
	}
	return boardPostComment;
    }

    public static ArrayList<BoardPostSummary> generateRandomBoardPostSummaryList() {
	ArrayList<BoardPostSummary> list = new ArrayList<BoardPostSummary>();

	for (int i = 0; i < 20; i++) {
	    BoardPostSummary boardPostSummary = new BoardPostSummary();
	    boardPostSummary.setAuthorUsername(USER_NAME);
	    boardPostSummary
		    .setTitle("Here is a drowned in sound post no " + i);
	    boardPostSummary.setNumberOfReplies(10);
	    boardPostSummary.setLastPostDate(new Date());
	    list.add(boardPostSummary);
	}

	return list;
    }

}

