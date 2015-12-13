package com.drownedinsound.test.login;

import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.data.parser.streaming.DisWebPageParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by gregmcgowan on 12/12/15.
 */
public class FakeDisWebPageParser implements DisWebPageParser {

    public static String authToken;

    public static void setAuthToken(String thisAuthToken) {
        authToken = thisAuthToken;
    }

    @Override
    public String getAuthenticationToken(InputStream inputStream) throws IOException {
        return authToken;
    }

    @Override
    public BoardPost parseBoardPost(InputStream inputStream) throws IOException {
        return null;
    }

    @Override
    public List<BoardPost> parseBoardPostSummaryList(BoardType boardType, InputStream inputStream)
            throws IOException {
        return null;
    }
}
