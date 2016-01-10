package com.gregmcgowan.drownedinsound.test.unit.model.parser;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostSummaryListParser;
import com.gregmcgowan.drownedinsound.test.unit.model.BoardPostTestData;
import com.gregmcgowan.drownedinsound.test.utils.AssertUtils;

public class TestBoardSummaryListStreamingParser extends InputStreamTest {
       

    public void testBoardSummaryListParseAmount() throws Exception { 
	BoardPostSummaryListParser boardPostParser = new BoardPostSummaryListParser(
		getTestInputStream(), BoardType.SOCIAL,null);
	ArrayList<BoardPost> boardPosts = boardPostParser.parse();
	Assert.assertEquals(BoardPostTestData.BOARD_SUMMARY_ONE_POSTS, boardPosts.size());	
    }
    
    public void testParseBoardPostSummaryContentOne() throws Exception {
	BoardPost expectedBoardPostOne = new BoardPost();
	expectedBoardPostOne.setId(BoardPostTestData.POST_ONE_SUMMARY_ID);
	expectedBoardPostOne.setTitle(BoardPostTestData.POST_ONE_SUMMARY_ONE_TITLE);
	expectedBoardPostOne.setAuthorUsername(BoardPostTestData.POST_ONE_SUMMARY_ONE_AUTHOR);
	expectedBoardPostOne.setSticky(BoardPostTestData.POST_ONE_SUMMARY_ONE_IS_STICKY);
	expectedBoardPostOne.setCreatedTime(BoardPostTestData.POST_ONE_SUMMARY_ONE_DATE_OF_POST_LONG);
	expectedBoardPostOne.setLastUpdatedTime(BoardPostTestData.POST_ONE_SUMMARY_LAST_UPDATED_LONG);
	expectedBoardPostOne.setNumberOfReplies(BoardPostTestData.POST_ONE_SUMMARY_NUMBER_OF_REPLIES);
	
	BoardPost expectedBoardPostFour = new BoardPost();
	expectedBoardPostFour.setId(BoardPostTestData.POST_FOUR_SUMMARY_ID);
	expectedBoardPostFour.setTitle(BoardPostTestData.POST_FOUR_SUMMARY_ONE_TITLE);
	expectedBoardPostFour.setAuthorUsername(BoardPostTestData.POST_FOUR_SUMMARY_ONE_AUTHOR);
	expectedBoardPostFour.setSticky(BoardPostTestData.POST_FOUR_SUMMARY_ONE_IS_STICKY);
	expectedBoardPostFour.setLastUpdatedTime(BoardPostTestData.POST_FOUR_SUMMARY_LAST_UPDATED_LONG);
	expectedBoardPostFour.setCreatedTime(BoardPostTestData.POST_FOUR_SUMMARY_ONE_DATE_OF_POST_LONG);
	expectedBoardPostFour.setNumberOfReplies(BoardPostTestData.POST_FOUR_SUMMARY_NUMBER_OF_REPLIES);
	
	List<BoardPost> expectedBoardPosts = new ArrayList<BoardPost>();
	expectedBoardPosts.add(expectedBoardPostOne);
	expectedBoardPosts.add(expectedBoardPostFour);
	
	BoardPostSummaryListParser boardPostParser = new BoardPostSummaryListParser(
		getTestInputStream(), BoardType.SOCIAL,null);
	
	ArrayList<BoardPost> actualBoardPosts = boardPostParser.parse();
	Assert.assertEquals(BoardPostTestData.BOARD_SUMMARY_ONE_POSTS, actualBoardPosts.size());
	
	AssertUtils.assertBoardPost(expectedBoardPostOne, actualBoardPosts.get(0));
	AssertUtils.assertBoardPost(expectedBoardPostFour, actualBoardPosts.get(3));
    }

    @Override
    protected String getTestInputStreamFilename() {
	return BoardPostTestData.BOARD_SUMMMARY_ONE_FILENAME;
    }
    
}
