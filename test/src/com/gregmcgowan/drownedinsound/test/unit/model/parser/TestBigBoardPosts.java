package com.gregmcgowan.drownedinsound.test.unit.model.parser;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.data.parser.streaming.BoardPostParser;
import com.gregmcgowan.drownedinsound.network.HttpClient;
import com.gregmcgowan.drownedinsound.network.UrlConstants;

public class TestBigBoardPosts extends InputStreamTest {

    @Override
    protected String getTestInputStreamFilename() {
	return "board_post_big.html";
    }

    public void testParseBigPostStreaming() {
	BoardPostParser streamingBoardPostParser = new BoardPostParser(
		getTestInputStream(), "123456", BoardType.SOCIAL);
	BoardPost actualBoardPost = streamingBoardPostParser.parse();

    }

    public void testParseBigPostJSoup() {
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

	com.gregmcgowan.drownedinsound.data.parser.BoardPostParser streamingBoardPostParser = new com.gregmcgowan.drownedinsound.data.parser.BoardPostParser(
		document, "123456", BoardType.SOCIAL);
	BoardPost actualBoardPost = streamingBoardPostParser.parseDocument();
	Log.d("TAG", "Parsed board post in "
		+ (System.currentTimeMillis() - start) + " ms");
    }

}
