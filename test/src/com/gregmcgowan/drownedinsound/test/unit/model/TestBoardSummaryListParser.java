package com.gregmcgowan.drownedinsound.test.unit.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.BoardPostSummaryListParser;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UrlConstants;

public class TestBoardSummaryListParser extends InstrumentationTestCase{
    private static final String BOARD_SUMMMARY_ONE_FILENAME = "board_summary_one.html";
    
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
    
    public void testParseBoardPostSummaryOne() throws Exception {
	BoardPost expectedBoardPostOne = new BoardPost();
	expectedBoardPostOne.setTitle(POST_ONE_SUMMARY_ONE_TITLE);
	expectedBoardPostOne.setAuthorUsername(POST_ONE_SUMMARY_ONE_AUTHOR);
	expectedBoardPostOne.setSticky(POST_ONE_SUMMARY_ONE_IS_STICKY);
	expectedBoardPostOne.setNumberOfReplies(POST_ONE_SUMMARY_NUMBER_OF_REPLIES);
	
	BoardPost expectedBoardPostFour = new BoardPost();
	expectedBoardPostFour.setTitle(POST_FOUR_SUMMARY_ONE_TITLE);
	expectedBoardPostFour.setAuthorUsername(POST_FOUR_SUMMARY_ONE_AUTHOR);
	expectedBoardPostFour.setSticky(POST_FOUR_SUMMARY_ONE_IS_STICKY);
	expectedBoardPostFour.setNumberOfReplies(POST_FOUR_SUMMARY_NUMBER_OF_REPLIES);
	
	List<BoardPost> expectedBoardPosts = new ArrayList<BoardPost>();
	expectedBoardPosts.add(expectedBoardPostOne);
	expectedBoardPosts.add(expectedBoardPostFour);
	AssetManager assetManager = getInstrumentation().getContext().getAssets();
	InputStream testInputStream = assetManager.open(BOARD_SUMMMARY_ONE_FILENAME); 
	    Document document = null;
	    try {
		document = Jsoup.parse(testInputStream, HttpClient.CONTENT_ENCODING,
			UrlConstants.BASE_URL);
	    } catch (IOException e) {
		if (DisBoardsConstants.DEBUG) {
		    e.printStackTrace();
		}
	    }
	    List<BoardPost> actualBoardPosts = new ArrayList<BoardPost>();
	    if (document != null) {
		long start = System.currentTimeMillis(); 
		BoardPostSummaryListParser parser = new BoardPostSummaryListParser(
			document, BoardType.SOCIAL,null);
		actualBoardPosts = parser.parseDocument();
		Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Parsed posts in "+(System.currentTimeMillis() - start)+ " ms");
	    }
	
	
	
    }
    
}
