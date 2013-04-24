package com.gregmcgowan.drownedinsound.test.unit.model.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostSummaryListParser;
import com.gregmcgowan.drownedinsound.test.unit.model.BoardPostSummaryOneTestData;
import com.gregmcgowan.drownedinsound.test.utils.AssertUtils;

public class TestBoardSummaryListStreamingParser extends InstrumentationTestCase {
        
    private AssetManager assetManager;
    private InputStream testInputStream;
    
    @Override
    protected void setUp() throws Exception {
	super.setUp();
	assetManager = getInstrumentation().getContext().getAssets();
	testInputStream = assetManager.open(BoardPostSummaryOneTestData.BOARD_SUMMMARY_ONE_FILENAME); 
    }
    
    @Override
    protected void tearDown() throws Exception {
	super.tearDown();
	testInputStream.close();
    }

    public void testBoardSummaryListParseAmount() throws Exception { 
	BoardPostSummaryListParser boardPostParser = new BoardPostSummaryListParser(
		testInputStream, BoardType.SOCIAL);
	ArrayList<BoardPost> boardPosts = boardPostParser.parse();
	Assert.assertEquals(BoardPostSummaryOneTestData.BOARD_SUMMARY_ONE_POSTS, boardPosts.size());	
    }
    
    public void testParseBoardPostSummaryContentOne() throws Exception {
	BoardPost expectedBoardPostOne = new BoardPost();
	expectedBoardPostOne.setId(BoardPostSummaryOneTestData.POST_ONE_SUMMARY_ID);
	expectedBoardPostOne.setTitle(BoardPostSummaryOneTestData.POST_ONE_SUMMARY_ONE_TITLE);
	expectedBoardPostOne.setAuthorUsername(BoardPostSummaryOneTestData.POST_ONE_SUMMARY_ONE_AUTHOR);
	expectedBoardPostOne.setSticky(BoardPostSummaryOneTestData.POST_ONE_SUMMARY_ONE_IS_STICKY);
	expectedBoardPostOne.setCreatedTime(BoardPostSummaryOneTestData.POST_ONE_SUMMARY_ONE_DATE_OF_POST_LONG);
	expectedBoardPostOne.setLastUpdatedTime(BoardPostSummaryOneTestData.POST_ONE_SUMMARY_LAST_UPDATED_LONG);
	expectedBoardPostOne.setNumberOfReplies(BoardPostSummaryOneTestData.POST_ONE_SUMMARY_NUMBER_OF_REPLIES);
	
	BoardPost expectedBoardPostFour = new BoardPost();
	expectedBoardPostFour.setId(BoardPostSummaryOneTestData.POST_FOUR_SUMMARY_ID);
	expectedBoardPostFour.setTitle(BoardPostSummaryOneTestData.POST_FOUR_SUMMARY_ONE_TITLE);
	expectedBoardPostFour.setAuthorUsername(BoardPostSummaryOneTestData.POST_FOUR_SUMMARY_ONE_AUTHOR);
	expectedBoardPostFour.setSticky(BoardPostSummaryOneTestData.POST_FOUR_SUMMARY_ONE_IS_STICKY);
	expectedBoardPostFour.setLastUpdatedTime(BoardPostSummaryOneTestData.POST_FOUR_SUMMARY_LAST_UPDATED_LONG);
	expectedBoardPostFour.setCreatedTime(BoardPostSummaryOneTestData.POST_FOUR_SUMMARY_ONE_DATE_OF_POST_LONG);
	expectedBoardPostFour.setNumberOfReplies(BoardPostSummaryOneTestData.POST_FOUR_SUMMARY_NUMBER_OF_REPLIES);
	
	List<BoardPost> expectedBoardPosts = new ArrayList<BoardPost>();
	expectedBoardPosts.add(expectedBoardPostOne);
	expectedBoardPosts.add(expectedBoardPostFour);
	
	BoardPostSummaryListParser boardPostParser = new BoardPostSummaryListParser(
		testInputStream, BoardType.SOCIAL);
	
	ArrayList<BoardPost> actualBoardPosts = boardPostParser.parse();
	Assert.assertEquals(BoardPostSummaryOneTestData.BOARD_SUMMARY_ONE_POSTS, actualBoardPosts.size());
	
	AssertUtils.assertBoardPost(expectedBoardPostOne, actualBoardPosts.get(0));
	AssertUtils.assertBoardPost(expectedBoardPostFour, actualBoardPosts.get(3));
    }
    
}
