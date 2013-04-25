package com.gregmcgowan.drownedinsound.test.unit.model.parser;

import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostParser;
import com.gregmcgowan.drownedinsound.test.unit.model.BoardPostTestData;
import com.gregmcgowan.drownedinsound.test.utils.AssertUtils;

public class TestStreamingParseBoardPost extends InputStreamTest {

    public void testParseBoardPostOne(){
	
	BoardPost expectedBoardPost = new BoardPost();
	expectedBoardPost.setAuthorUsername(BoardPostTestData.BOARD_POST_AUTHOR);
	expectedBoardPost.setContent(BoardPostTestData.BOARD_POST_CONTENT);
	expectedBoardPost.setDateOfPost(BoardPostTestData.BOARD_POST_DATE_TIME);
	expectedBoardPost.setTitle(BoardPostTestData.BOARD_POST_TITLE);
	expectedBoardPost.setNumberOfReplies(BoardPostTestData.BOARD_POST_NUMBER_OF_COMMENTS);
	
	BoardPostParser streamingBoardPostParser = new BoardPostParser(getTestInputStream(),null,BoardType.SOCIAL);
	BoardPost actualBoardPost = streamingBoardPostParser.parse();
	AssertUtils.assertBoardPost(expectedBoardPost, actualBoardPost);
    }
    
    
    
    @Override
    protected String getTestInputStreamFilename() {
	return BoardPostTestData.BOARD_POST_FILENAME;
    }

}
