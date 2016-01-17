package com.drownedinsound.test;

import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostComment;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.data.parser.streaming.BoardPostParser;
import com.drownedinsound.data.parser.streaming.BoardPostSummaryListParser;
import com.drownedinsound.data.parser.streaming.DisWebPageParser;
import com.drownedinsound.data.parser.streaming.DisWebPagerParserImpl;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gregmcgowan on 12/12/15.
 */
public class DisBoardWebParserTest {

    private DisWebPageParser disWebPageParser;

    @Mock
    UserSessionRepo userSessionRepo;


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        BoardPostSummaryListParser boardPostSummaryListParser = new BoardPostSummaryListParser(userSessionRepo);
        BoardPostParser boardPostParser = new BoardPostParser(userSessionRepo);
        disWebPageParser = new DisWebPagerParserImpl(boardPostParser, boardPostSummaryListParser);
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

        verify(userSessionRepo, atLeastOnce()).setAuthenticityToken(anyString());;

        Assert.assertEquals(42, actualBoardPosts.size());

        AssertUtils.assertBoardPostSummary(expectedBoardPostOne, actualBoardPosts.get(0));
        AssertUtils.assertBoardPostSummary(expectedBoardPostFour, actualBoardPosts.get(3));
    }

    @Test
    public void testParsePost() throws Exception {
        BoardPost expectedBoardPost = new BoardPost();
        expectedBoardPost.setBoardPostID("4471118");
        expectedBoardPost.setAuthorUsername("shitty_zombies");
        expectedBoardPost.setTitle("Charlie Brooker&#x27;s 2015 Wipe");
        expectedBoardPost.setContent("<p>Christ, that was bleak, wasn&#x27;t it?</p>");
        expectedBoardPost.setDateOfPost("22:55, 30 December '15");
        expectedBoardPost.setBoardListTypeID(BoardListTypes.SOCIAL);
        expectedBoardPost.setNumberOfReplies(5);

        BoardPostComment firstComment = new BoardPostComment();
        firstComment.setBoardPostID("4471118");
        firstComment.setDateAndTime("30 Dec '15, 23:01");
        firstComment.setAuthorUsername("-dan-");
        firstComment.setTitle("Cheers, hadn&#x27;t even realised it was on.");
        firstComment.setCommentLevel(0);
        firstComment.setCommentID("8823245");

        BoardPostComment secondComment = new BoardPostComment();
        secondComment.setBoardPostID("4471118");
        secondComment.setDateAndTime("30 Dec '15, 23:01");
        secondComment.setAuthorUsername("BMS1");
        secondComment.setTitle("I&#x27;ve got it taped. I&#x27;ll watch it tomorrow.");
        secondComment.setContent("<p>Usually it's good fun. Hopefully, Barry Shitpeas has some good lines.</p>");
        secondComment.setCommentID("8823246");

        BoardPostComment thirdComment = new BoardPostComment();
        thirdComment.setBoardPostID("4471118");
        thirdComment.setTitle("Had a feeling it would be");
        thirdComment.setContent("<p>Hence why I didn't watch it this time!</p>");
        thirdComment.setAuthorUsername("kiyonemakibi");
        thirdComment.setDateAndTime("30 Dec '15, 23:19");
        thirdComment.setCommentID("8823251");

        BoardPostComment fourthComment = new BoardPostComment();
        fourthComment.setBoardPostID("4471118");
        fourthComment.setTitle("i think he&#x27;s phoning it in now");
        fourthComment.setAuthorUsername("bluto");
        fourthComment.setDateAndTime("31 Dec '15, 03:13");
        fourthComment.setCommentID("8823285");

        BoardPostComment fifthComment = new BoardPostComment();
        fifthComment.setBoardPostID("4471118");
        fifthComment.setTitle("Autofill?");
        fifthComment.setAuthorUsername("Pentago");
        fifthComment.setDateAndTime("31 Dec '15, 09:56");
        fifthComment.setUsersWhoHaveThissed("bluto and ericthethird this'd this");
        fifthComment.setCommentID("8823351");
        fifthComment.setCommentLevel(1);

        List<BoardPostComment> boardPostComments = new ArrayList<>();
        boardPostComments.add(firstComment);
        boardPostComments.add(secondComment);
        boardPostComments.add(thirdComment);
        boardPostComments.add(fourthComment);
        boardPostComments.add(fifthComment);

        expectedBoardPost.setComments(boardPostComments);

        InputStream inputStream = getInputStream("board_post.html");

        BoardPost actual = disWebPageParser.parseBoardPost(BoardListTypes.SOCIAL, "4471118",
                inputStream);
        verify(userSessionRepo, atLeastOnce()).setAuthenticityToken(anyString());;

        AssertUtils.assertBoardPost(expectedBoardPost,actual);
    }


    private InputStream getInputStream(String filename) {
        return getClass().getClassLoader().getResourceAsStream(filename);
    }


}
