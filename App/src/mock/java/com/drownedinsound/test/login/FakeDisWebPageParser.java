package com.drownedinsound.test.login;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
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
    public List<BoardPostSummary> parseBoardPostSummaryList(
            @BoardPostList.BoardPostListType String boardListType, InputStream inputStream)
            throws IOException {
        return null;
    }

    @Override
    public BoardPost parseBoardPost(@BoardPostList.BoardPostListType String boardListType,
            InputStream inputStream) throws IOException {
        return null;
    }
}
