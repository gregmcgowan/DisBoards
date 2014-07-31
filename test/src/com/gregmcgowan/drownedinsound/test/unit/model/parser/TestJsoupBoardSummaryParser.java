package com.gregmcgowan.drownedinsound.test.unit.model.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.BoardPostSummaryListParser;
import com.gregmcgowan.drownedinsound.data.network.HttpClient;
import com.gregmcgowan.drownedinsound.data.network.UrlConstants;
import com.gregmcgowan.drownedinsound.test.unit.model.BoardPostTestData;
import com.gregmcgowan.drownedinsound.test.utils.AssertUtils;

public class TestJsoupBoardSummaryParser extends InputStreamTest {

    public void testParseBoardPostSummaryOne() throws Exception {
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
	long start = System.currentTimeMillis();
	Document document = null;
	
	try {
	    document = Jsoup.parse(getTestInputStream(),
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

    }

    @Override
    protected String getTestInputStreamFilename() {
	return BoardPostTestData.BOARD_SUMMMARY_ONE_FILENAME;
    }

}
