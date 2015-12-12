package com.drownedinsound.data.parser.streaming;

import com.drownedinsound.data.model.BoardPost;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StreamedSource;
import net.htmlparser.jericho.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by gregmcgowan on 12/12/15.
 */
public class DisWebPagerParserImpl implements DisWebPageParser {

    private BoardPostParser boardPostParser;
    private BoardPostSummaryListParser boardPostSummaryListParser;

    public DisWebPagerParserImpl(
            BoardPostParser boardPostParser,
            BoardPostSummaryListParser boardPostSummaryListParser) {
        this.boardPostParser = boardPostParser;
        this.boardPostSummaryListParser = boardPostSummaryListParser;
    }

    @Override
    public String getAuthenticationToken(InputStream inputStream) throws IOException{
        StreamedSource streamedSource = new StreamedSource(inputStream);
        for (Segment segment : streamedSource) {
            if (segment instanceof Tag) {
                Tag tag = (Tag) segment;
                String tagName = tag.getName();
                if (HtmlConstants.META.equals(tagName)) {
                    String metaString = tag.toString();
                    if (metaString.contains(HtmlConstants.AUTHENTICITY_TOKEN_NAME)) {
                        Attributes attributes = tag.parseAttributes();
                        if (attributes != null) {
                            return attributes.getValue("content");
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public BoardPost parseBoardPost(InputStream inputStream) throws IOException{
        return boardPostParser.parse(inputStream);
    }

    @Override
    public List<BoardPost> parseBoardPostSummaryList(InputStream inputStream) throws IOException{
        return null;
    }
}
