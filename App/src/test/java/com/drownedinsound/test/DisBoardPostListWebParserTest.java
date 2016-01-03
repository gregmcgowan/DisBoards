package com.drownedinsound.test;

import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.data.parser.streaming.BoardPostSummaryListParser;
import com.drownedinsound.data.parser.streaming.DisWebPageParser;
import com.drownedinsound.data.parser.streaming.DisWebPagerParserImpl;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.util.List;

/**
 * Created by gregmcgowan on 12/12/15.
 */
public class DisBoardPostListWebParserTest {

    private DisWebPageParser disWebPageParser;

    @Mock
    UserSessionRepo userSessionRepo;


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        BoardPostSummaryListParser boardPostSummaryListParser = new BoardPostSummaryListParser(userSessionRepo);
        disWebPageParser = new DisWebPagerParserImpl(null, boardPostSummaryListParser);
    }

    @Test
    public void testGetAuthToken() throws Exception{
        String expected = "uVzOJW0EFRZnB/atMD+vo9Ead2N15LSw+LBQ0tztJDA=";

        InputStream inputStream = getInputStream("auth_token.html");
        String auctal = disWebPageParser.getAuthenticationToken(inputStream);
        Assert.assertEquals(expected,auctal);
    }

    @Test
    public void testParseListSummary() throws Exception {
        BoardPostSummary expectedBoardPostOne = new BoardPostSummary();
        expectedBoardPostOne.setBoardPostID("4445891");
        expectedBoardPostOne.setTitle("Contacting the Moderators for issues (Redux)");
        expectedBoardPostOne.setAuthorUsername("TheoGB");
        expectedBoardPostOne.setIsSticky(true);
        expectedBoardPostOne.setLastUpdatedTime(1447159080000l);
        expectedBoardPostOne.setNumberOfReplies(298);
        expectedBoardPostOne.setBoardListTypeID(BoardListTypes.SOCIAL);

        BoardPostSummary expectedBoardPostFour = new BoardPostSummary();
        expectedBoardPostFour.setBoardPostID("4470683");
        expectedBoardPostFour.setTitle("Sunday Thread");
        expectedBoardPostFour.setAuthorUsername("marilyninthesky");
        expectedBoardPostFour.setLastUpdatedTime(14500050000000l);
        expectedBoardPostFour.setNumberOfReplies(8);
        expectedBoardPostFour.setBoardListTypeID(BoardListTypes.SOCIAL);

        InputStream inputStream = getInputStream("board_post_summary_list.html");

        List<BoardPostSummary> actualBoardPosts = disWebPageParser.parseBoardPostSummaryList(BoardListTypes.SOCIAL,
                inputStream);
        Assert.assertEquals(42, actualBoardPosts.size());

        AssertUtils.assertBoardPostSummary(expectedBoardPostOne, actualBoardPosts.get(0));
        AssertUtils.assertBoardPostSummary(expectedBoardPostFour, actualBoardPosts.get(3));
    }


    private InputStream getInputStream(String filename) {
        return getClass().getClassLoader().getResourceAsStream(filename);
    }


}
