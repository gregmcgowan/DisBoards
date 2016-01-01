package com.drownedinsound.data.parser.streaming;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by gregmcgowan on 12/12/15.
 */
public interface DisWebPageParser {

     String getAuthenticationToken(InputStream inputStream) throws IOException;

     BoardPost parseBoardPost(InputStream inputStream) throws IOException;

     List<BoardPost> parseBoardPostSummaryList(@BoardPostList.BoardPostListType String boardListType,
             InputStream inputStream) throws IOException;



}
