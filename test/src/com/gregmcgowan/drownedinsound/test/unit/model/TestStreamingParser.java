package com.gregmcgowan.drownedinsound.test.unit.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostSummaryListParser;
import com.gregmcgowan.drownedinsound.test.utils.AssertUtils;

public class TestStreamingParser extends InstrumentationTestCase {
    
    private static final String BOARD_SUMMMARY_ONE_FILENAME = "board_summary_one.html";
    private static final int BOARD_SUMMARY_ONE_POSTS = 40;
    
    //Expected board post summary 0
    private static final String POST_ONE_SUMMARY_ONE_TITLE = "Recommended Albums Thread (2013)";
    private static final String POST_ONE_SUMMARY_ONE_AUTHOR = "forzaborza";
    private static final boolean POST_ONE_SUMMARY_ONE_IS_STICKY = true;
    private static final long POST_ONE_SUMMARY_ONE_DATE_OF_POST_LONG  = 1358685066;
    private static final int POST_ONE_SUMMARY_NUMBER_OF_REPLIES = 215;
    private static final long POST_ONE_SUMMARY_LAST_UPDATED_LONG  = 1365605448;
    
    //Expected board post summary 3
    private static final String POST_FOUR_SUMMARY_ONE_TITLE = "Rolling Dance Mongrel 2013";
    private static final String POST_FOUR_SUMMARY_ONE_AUTHOR = "jimitheexploder";
    private static final boolean POST_FOUR_SUMMARY_ONE_IS_STICKY =false;
    private static final long  POST_FOUR_SUMMARY_ONE_DATE_OF_POST_LONG = 1357580479;
    private static final int POST_FOUR_SUMMARY_NUMBER_OF_REPLIES = 490; 
    private static final long POST_FOUR_SUMMARY_LAST_UPDATED_LONG  = 1365689246;
    
    private AssetManager assetManager;
    private InputStream testInputStream;
    
    @Override
    protected void setUp() throws Exception {
	super.setUp();

    }
    
    @Override
    protected void tearDown() throws Exception {
	super.tearDown();
    }

    public void testBoardSummaryListParseAmount() throws Exception { 
	assetManager = getInstrumentation().getContext().getAssets();
	testInputStream = assetManager.open(BOARD_SUMMMARY_ONE_FILENAME); 
	BoardPostSummaryListParser boardPostParser = new BoardPostSummaryListParser(
		testInputStream, BoardType.SOCIAL);
	ArrayList<BoardPost> boardPosts = boardPostParser.parse();
	Assert.assertEquals(BOARD_SUMMARY_ONE_POSTS, boardPosts.size());
	testInputStream.close();
    }
    
/*    public void testParseBoardPostSummaryOne() throws Exception {
	BoardPost expectedBoardPostOne = new BoardPost();
	expectedBoardPostOne.setTitle(POST_ONE_SUMMARY_ONE_TITLE);
	expectedBoardPostOne.setAuthorUsername(POST_ONE_SUMMARY_ONE_AUTHOR);
	expectedBoardPostOne.setSticky(POST_ONE_SUMMARY_ONE_IS_STICKY);
	expectedBoardPostOne.setCreatedTime(POST_ONE_SUMMARY_ONE_DATE_OF_POST_LONG);
	expectedBoardPostOne.setLastUpdatedTime(POST_ONE_SUMMARY_LAST_UPDATED_LONG);
	expectedBoardPostOne.setNumberOfReplies(POST_ONE_SUMMARY_NUMBER_OF_REPLIES);
	
	BoardPost expectedBoardPostFour = new BoardPost();
	expectedBoardPostFour.setTitle(POST_FOUR_SUMMARY_ONE_TITLE);
	expectedBoardPostFour.setAuthorUsername(POST_FOUR_SUMMARY_ONE_AUTHOR);
	expectedBoardPostFour.setSticky(POST_FOUR_SUMMARY_ONE_IS_STICKY);
	expectedBoardPostFour.setLastUpdatedTime(POST_FOUR_SUMMARY_LAST_UPDATED_LONG);
	expectedBoardPostFour.setCreatedTime(POST_FOUR_SUMMARY_ONE_DATE_OF_POST_LONG);
	expectedBoardPostFour.setNumberOfReplies(POST_FOUR_SUMMARY_NUMBER_OF_REPLIES);
	
	List<BoardPost> expectedBoardPosts = new ArrayList<BoardPost>();
	expectedBoardPosts.add(expectedBoardPostOne);
	expectedBoardPosts.add(expectedBoardPostFour);
	
	assetManager = getInstrumentation().getContext().getAssets();
	testInputStream = assetManager.open(BOARD_SUMMMARY_ONE_FILENAME); 
	
	BoardPostParser boardPostParser = new BoardPostParser(
		testInputStream, null, null);
	
	ArrayList<BoardPost> actualBoardPosts = boardPostParser.parse();
	
	ArrayList<BoardPost> boardPosts = boardPostParser.parse();
	Assert.assertEquals(BOARD_SUMMARY_ONE_POSTS, boardPosts.size());
	
	AssertUtils.assertBoardPost(expectedBoardPostOne, actualBoardPosts.get(0));
	AssertUtils.assertBoardPost(expectedBoardPostFour, actualBoardPosts.get(3));
	testInputStream.close();
    }*/
    
}
