package com.drownedinsound.test;

import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
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
public class DisBoardWebParserTest {

    private DisWebPageParser disWebPageParser;

    @Mock
    UserSessionRepo userSessionRepo;

    @Mock
    DisBoardsLocalRepo disBoardsLocalRepo;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        BoardPostSummaryListParser boardPostSummaryListParser = new BoardPostSummaryListParser(userSessionRepo,disBoardsLocalRepo);
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
        BoardPost expectedBoardPostOne = new BoardPost();
        expectedBoardPostOne.setId("4445891");
        expectedBoardPostOne.setTitle("Contacting the Moderators for issues (Redux)");
        expectedBoardPostOne.setAuthorUsername("TheoGB");
        expectedBoardPostOne.setSticky(true);
        expectedBoardPostOne.setLastUpdatedTime(1447159080000l);
        expectedBoardPostOne.setNumberOfReplies(298);
        expectedBoardPostOne.setBoardType(BoardType.SOCIAL);

        BoardPost expectedBoardPostFour = new BoardPost();
        expectedBoardPostFour.setId("4470683");
        expectedBoardPostFour.setTitle("Sunday Thread");
        expectedBoardPostFour.setAuthorUsername("marilyninthesky");
        expectedBoardPostOne.setLastUpdatedTime(14500050000000l);
        expectedBoardPostFour.setNumberOfReplies(8);
        expectedBoardPostFour.setBoardType(BoardType.SOCIAL);

        InputStream inputStream = getInputStream("board_post_summary_list.html");

        List<BoardPost> actualBoardPosts = disWebPageParser.parseBoardPostSummaryList(BoardType.SOCIAL,
                inputStream);
        Assert.assertEquals(42, actualBoardPosts.size());

        AssertUtils.assertBoardPost(expectedBoardPostOne, actualBoardPosts.get(0));
        AssertUtils.assertBoardPost(expectedBoardPostFour, actualBoardPosts.get(3));
    }


    private InputStream getInputStream(String filename) {
        return getClass().getClassLoader().getResourceAsStream(filename);
    }


}
