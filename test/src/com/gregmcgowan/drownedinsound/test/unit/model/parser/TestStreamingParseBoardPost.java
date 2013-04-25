package com.gregmcgowan.drownedinsound.test.unit.model.parser;

import java.util.ArrayList;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostParser;
import com.gregmcgowan.drownedinsound.test.unit.model.BoardPostTestData;
import com.gregmcgowan.drownedinsound.test.utils.AssertUtils;

public class TestStreamingParseBoardPost extends InputStreamTest {

    public void testParseBoardPostOne(){
	
	BoardPost expectedBoardPost = new BoardPost();
	expectedBoardPost.setId("123456");
	expectedBoardPost.setAuthorUsername(BoardPostTestData.BOARD_POST_AUTHOR);
	expectedBoardPost.setContent(BoardPostTestData.BOARD_POST_CONTENT);
	expectedBoardPost.setDateOfPost(BoardPostTestData.BOARD_POST_DATE_TIME);
	expectedBoardPost.setTitle(BoardPostTestData.BOARD_POST_TITLE);
	expectedBoardPost.setNumberOfReplies(BoardPostTestData.BOARD_POST_NUMBER_OF_COMMENTS);
	expectedBoardPost.setBoardType(BoardType.SOCIAL);
	
	BoardPostComment comment0 = new BoardPostComment();
	comment0.setId("123456");
	comment0.setTitle(BoardPostTestData.BOARD_POST_TITLE);
	comment0.setContent(BoardPostTestData.BOARD_POST_CONTENT);
	comment0.setAuthorUsername(BoardPostTestData.BOARD_POST_AUTHOR);
	comment0.setDateAndTimeOfComment(BoardPostTestData.BOARD_POST_DATE_TIME);
	comment0.setBoardPost(expectedBoardPost);
	
	BoardPostComment comment1 = new BoardPostComment();
	comment1.setId("7453256");
	comment1.setTitle("put them in a bowl of");
	comment1.setContent("<p>fuck you buddy</p>");
	comment1.setAuthorUsername("Silkyskillz11");
	comment1.setDateAndTimeOfComment("15 Apr '13, 17:15");
	comment1.setUsersWhoHaveThissed("ohgood, waffle, wonton, chickenbones , and Antelope this'd this");
	comment1.setBoardPost(expectedBoardPost);
	
	BoardPostComment comment2 = new BoardPostComment();
	comment2.setId("7453261");
	comment2.setTitle("Thanks");
	comment2.setAuthorUsername("Balonz   @Silkyskillz11");
	comment2.setCommentLevel(1);
	comment2.setDateAndTimeOfComment("15 Apr '13, 17:16");
	comment2.setBoardPost(expectedBoardPost);
	
	BoardPostComment comment3 = new BoardPostComment();
	comment3.setId("7453257");
	comment3.setTitle("This is one of the most boring posts of all time");
	comment3.setAuthorUsername("ma0sm");
	comment3.setDateAndTimeOfComment("15 Apr '13, 17:15");
	comment3.setBoardPost(expectedBoardPost);
	
	
	BoardPostComment comment4 = new BoardPostComment();
	comment4.setId("7453260");
	comment4.setTitle("Clear it");
	comment4.setContent("<p>Let's talk backups!</p>");
	comment4.setAuthorUsername("ma0sm   @ma0sm");
	comment4.setCommentLevel(1);
	comment4.setDateAndTimeOfComment("15 Apr '13, 17:15");
	comment4.setBoardPost(expectedBoardPost);
	
	BoardPostComment comment5 = new BoardPostComment();
	comment5.setId("7453264");
	comment5.setTitle("Thanks");
	comment5.setAuthorUsername("Balonz   @ma0sm");
	comment5.setCommentLevel(1);
	comment5.setDateAndTimeOfComment("15 Apr '13, 17:16");
	comment5.setBoardPost(expectedBoardPost);
	
	
	BoardPostComment comment6 = new BoardPostComment();
	comment6.setId("7453266");
	comment6.setTitle("Sorry Balonx");
	comment6.setContent("<p>I replied proper straight after though, in the interest of balance like</p>");
	comment6.setAuthorUsername("ma0sm   @Balonz");
	comment6.setCommentLevel(2);
	comment6.setDateAndTimeOfComment("15 Apr '13, 17:17");
	comment6.setBoardPost(expectedBoardPost);
	
	BoardPostComment comment7 = new BoardPostComment();
	comment7.setId("7453271");
	comment7.setTitle("Are you suggesting that some people");
	comment7.setContent("<p>take photographs for the sake of taking them and once they&#39;ve filled their SD card they delete them all to just take some more?</p>");
	comment7.setAuthorUsername("ma0sm");
	comment7.setDateAndTimeOfComment("15 Apr '13, 17:19");
	comment7.setBoardPost(expectedBoardPost);
	
	BoardPostComment comment8 = new BoardPostComment();
	comment8.setId("7453276");
	comment8.setTitle("Yeah");
	comment8.setContent("<p>mainly the French. </p>");
	comment8.setAuthorUsername("Balonz   @ma0sm");
	comment8.setCommentLevel(1);
	comment8.setDateAndTimeOfComment("15 Apr '13, 17:20");
	comment8.setBoardPost(expectedBoardPost);
	
	BoardPostComment comment9 = new BoardPostComment();
	comment9.setId("7453281");
	comment9.setTitle("I don-");
	comment9.setContent("<p>What th-</p>");
	comment9.setCommentLevel(1);
	comment9.setAuthorUsername("ma0sm   @Balonz");
	comment9.setDateAndTimeOfComment("15 Apr '13, 17:21");
	comment9.setBoardPost(expectedBoardPost);
	
	BoardPostComment comment10 = new BoardPostComment();
	comment10.setId("7453277");
	comment10.setTitle("buy a new card");
	comment10.setContent("<p>keep the old one</p>");
	comment10.setAuthorUsername("hibster");	
	comment10.setDateAndTimeOfComment("15 Apr '13, 17:20");
	comment10.setBoardPost(expectedBoardPost);
	
	ArrayList<BoardPostComment> comments = new ArrayList<BoardPostComment>();
	comments.add(comment0);
	comments.add(comment1);
	comments.add(comment2);
	comments.add(comment3);
	comments.add(comment4);
	comments.add(comment5);
	comments.add(comment6);
	comments.add(comment7);
	comments.add(comment8);
	comments.add(comment9);
	comments.add(comment10);

	expectedBoardPost.setComments(comments);
	
	BoardPostParser streamingBoardPostParser = new BoardPostParser(getTestInputStream(),"123456",BoardType.SOCIAL);
	BoardPost actualBoardPost = streamingBoardPostParser.parse();
	AssertUtils.assertBoardPost(expectedBoardPost, actualBoardPost);
    }
    
    
    
    @Override
    protected String getTestInputStreamFilename() {
	return BoardPostTestData.BOARD_POST_FILENAME;
    }

}
