package com.gregmcgowan.drownedinsound.test.unit.model.parser;

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
import com.gregmcgowan.drownedinsound.test.unit.model.BoardPostSummaryOneTestData;
import com.gregmcgowan.drownedinsound.test.utils.AssertUtils;

public class TestJsoupBoardSummaryParser extends InstrumentationTestCase {

    public void testParseBoardPostSummaryOne() throws Exception {
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
	AssetManager assetManager = getInstrumentation().getContext()
		.getAssets();
	InputStream testInputStream = assetManager
		.open(BoardPostSummaryOneTestData.BOARD_SUMMMARY_ONE_FILENAME);
	long start = System.currentTimeMillis();
	Document document = null;
	try {
	    document = Jsoup.parse(testInputStream,
		    HttpClient.CONTENT_ENCODING, UrlConstants.BASE_URL);
	} catch (IOException e) {
	    if (DisBoardsConstants.DEBUG) {
		e.printStackTrace();
	    }
	}
	List<BoardPost> actualBoardPosts = new ArrayList<BoardPost>();
	if (document != null) {

	    BoardPostSummaryListParser parser = new BoardPostSummaryListParser(
		    document, BoardType.SOCIAL, null);
	    actualBoardPosts = parser.parseDocument();
	    Log.d(DisBoardsConstants.LOG_TAG_PREFIX, "Parsed posts in "
		    + (System.currentTimeMillis() - start) + " ms");
	}
	AssertUtils.assertBoardPost(expectedBoardPostOne, actualBoardPosts.get(0));
	AssertUtils.assertBoardPost(expectedBoardPostFour, actualBoardPosts.get(3));
	testInputStream.close();

    }

}
