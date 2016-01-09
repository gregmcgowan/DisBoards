package com.drownedinsound.data.parser.streaming;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.UserSessionRepo;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.utils.DateUtils;
import com.drownedinsound.utils.StringUtils;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StreamedSource;
import net.htmlparser.jericho.Tag;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class BoardPostSummaryListParser  {

    private static final int POST_URL_ANCHOR_INDEX = 1;

    private static final int DESCRIPTION_TABLE_ROW_INDEX = 2;

    private static final int REPLIES_TABLE_ROW_INDEX = 3;

    private static final int LAST_POST_TABLE_ROW_INDEX = 4;

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "BoardPostParser";

    private static final Object STICKY_CLASS = "content_type_label";

    private static final boolean DEBUG_PARSER = false;

    private UserSessionRepo userSessionRepo;

    public BoardPostSummaryListParser(UserSessionRepo userSessionRepo) {
        this.userSessionRepo = userSessionRepo;
    }

    public  List<BoardPostSummary> parse(@BoardPostList.BoardPostListType String boardListType,InputStream inputStream) {
        boolean inBoardPostTable = false;
        int tableRowCell= 0;
        int spanNumber = 0;
        int anchorNumber = 0;

        String spanClass= null;

        List<BoardPostSummary> boardPostSummaries = new ArrayList<>();
        BoardPostSummary currentBoardPostSummary = null;

        StringBuilder buffer = new StringBuilder(1024);
        long start = System.currentTimeMillis();

        try {
            StreamedSource streamedSource = new StreamedSource(inputStream);
            for (Segment segment : streamedSource) {
                if (segment instanceof Tag) {
                    Tag tag = (Tag) segment;
                    String tagName = tag.getName();
                    if (tagName.equals(HtmlConstants.TABLE)) {
                        inBoardPostTable = tag instanceof StartTag;
                    } else if (HtmlConstants.META.equals(tagName)) {
                        String metaString = tag.toString();
                        if (metaString.contains(HtmlConstants.AUTHENTICITY_TOKEN_NAME)) {
                            Attributes attributes = tag.parseAttributes();
                            if (attributes != null) {
                                String authToken = attributes.getValue("content");
                                userSessionRepo.setAuthenticityToken(authToken);
                            }
                        }
                    } else if (tagName.equals(HtmlConstants.TABLE_ROW)) {
                        if (tag instanceof StartTag) {
                            String trString = tag.toString();
                            if (isStartOfNewPostTr(trString)) {
                                currentBoardPostSummary = new BoardPostSummary();
                                currentBoardPostSummary.setBoardListTypeID(boardListType);
                            }
                            tableRowCell = 0;
                        } else {
                            if (currentBoardPostSummary != null) {
                                boardPostSummaries.add(currentBoardPostSummary);
                                currentBoardPostSummary = null;
                            }
                        }
                    } else if (tagName.equals(HtmlConstants.TABLE_CELL)) {
                        if (tag instanceof StartTag) {
                            tableRowCell++;
                            anchorNumber = 0;
                            spanNumber = 0;
                        } else {
                            if (inBoardPostTable
                                    && tableRowCell == REPLIES_TABLE_ROW_INDEX) {
                                int numberOfReplies = 0;
                                String repliesText = buffer.toString().trim();
                                if (!StringUtils.isEmpty(repliesText)) {
                                    repliesText = repliesText.replace("&nbsp;"," ");
                                    String[] repliesTokens = repliesText.split("\\s");
                                    if (repliesTokens.length > 0) {
                                        try {
                                            numberOfReplies = Integer.parseInt(repliesTokens[0]);
                                        } catch (NumberFormatException nfe) {

                                        }
                                    }
                                }
                                currentBoardPostSummary.setNumberOfReplies(numberOfReplies);
                            }
                        }
                    } else if (tagName.endsWith(HtmlConstants.ANCHOR)) {
                        if (tag instanceof StartTag) {
                            anchorNumber++;
                            if (inBoardPostTable) {
                                String tagString = tag.toString();
                                if (anchorNumber == POST_URL_ANCHOR_INDEX
                                        && tableRowCell == DESCRIPTION_TABLE_ROW_INDEX) {
                                    extractPostId(tagString,currentBoardPostSummary);
                                } else if (tableRowCell == LAST_POST_TABLE_ROW_INDEX) {
                                    int titleIndex = tagString.indexOf(HtmlConstants.TITLE);
                                    if (titleIndex != -1) {
                                        String title = tagString.substring(titleIndex,
                                                tagString.length() - 1);
                                        String[] keyValue = title.split("=");
                                        if (keyValue != null && keyValue.length == 2) {
                                            String value = keyValue[1];
                                            long timestamp = parseDate(value,
                                                    DateUtils.DIS_BOARD_LAST_COMMENT_DATE_FORMAT);
                                            if (timestamp != -1) {
                                                currentBoardPostSummary.setLastUpdatedTime(timestamp);
                                            }
                                        }
                                    }
                                    extractPostId(tagString,currentBoardPostSummary);
                                }
                            }
                        } else {
                            if (inBoardPostTable
                                    && tableRowCell == DESCRIPTION_TABLE_ROW_INDEX) {
                                String bufferOutput = buffer.toString().trim();
                                if (anchorNumber == 1) {
                                    String title = bufferOutput;
                                    // Log.d(TAG, "Title [" + title + "]");
                                    currentBoardPostSummary.setTitle(title);
                                } else if (anchorNumber == 2) {
                                    String[] authorTokens = bufferOutput.split("\\s");
                                    if (authorTokens != null && authorTokens.length > 1) {
                                        String author = authorTokens[1];
                                        // Log.d(TAG, "Author [" + author + "]");
                                        currentBoardPostSummary.setAuthorUsername(author);
                                    }
                                }
                            }
                        }
                    } else if (tagName.endsWith(HtmlConstants.SPAN)) {
                        if (inBoardPostTable) {
                            if (tag instanceof StartTag) {
                                spanNumber++;
                                String spanString = segment.toString();
                                if (spanNumber == 1) {
                                    HashMap<String, String> parameters = ParsingUtils.createAttributeMapFromStartTag(
                                            spanString);
                                    spanClass = parameters.get(HtmlConstants.CLASS);

                                }
                            }
                            if (tag instanceof EndTag) {
                                if (DEBUG_PARSER) {
                                    Timber.d("SpanNumber [" + spanNumber + "] content [" + buffer.toString().trim()
                                            + "] class " + spanClass);
                                }

                                if (spanNumber == 1 && STICKY_CLASS.equals(spanClass)) {
                                    currentBoardPostSummary
                                            .setIsSticky(HtmlConstants.STICKY.equalsIgnoreCase(buffer.toString().trim()));
                                }
                            }
                        }
                    } else if (HtmlConstants.META.equals(tagName)) {
                        String metaString = tag.toString();
                        if (metaString.contains(HtmlConstants.AUTHENTICITY_TOKEN_NAME)) {
                            Attributes attributes = tag.parseAttributes();
                            if (attributes != null) {
                                String authToken = attributes.getValue("content");
                                userSessionRepo.setAuthenticityToken(authToken);
                            }
                        }
                    }

                    if (tag instanceof EndTag) {
                        buffer.setLength(0);
                    }
                } else {
                    if (inBoardPostTable) {
                        buffer.append(segment.toString());
                    }
                }
            }
            streamedSource.close();

        } catch (IOException ioe) {
            if (DisBoardsConstants.DEBUG) {
                ioe.printStackTrace();
            }
        }
        if (DisBoardsConstants.DEBUG && DEBUG_PARSER) {
            Timber.d( "Parsed " + boardPostSummaries.size() + " board posts in "
                    + (System.currentTimeMillis() - start) + " ms");
            for (BoardPostSummary boardPost : boardPostSummaries) {
                Timber.d(boardPost.toString());
            }
        }
        return boardPostSummaries;
    }


    private long parseDate(String dateString, String format) {
        long timeStamp = -1;
        if (!StringUtils.isEmpty(dateString)) {
            int indexOfComma = dateString.indexOf(",");
            if (indexOfComma != -1) {
                dateString = dateString.substring(1, indexOfComma - 2) +
                        dateString.substring(indexOfComma + 1);
                dateString = dateString.replace("'", "");
                dateString = dateString.replace("&nbsp;", " ");
                dateString = dateString.replace("\n", "");
            }
            Date parsedDate = DateUtils.parseDate(dateString, format);
            if (parsedDate != null) {
                timeStamp = parsedDate.getTime();
            }
            if (DEBUG_PARSER) {
                Timber.d( "parse date =" + parsedDate.toString());
            }
        }
        return timeStamp;
    }

    private boolean isStartOfNewPostTr(String trString) {
        // TODO better way to do this
        return trString != null
                && trString.startsWith("<tr style='background-color");
    }

    private void extractPostId(String tagString, BoardPostSummary currentBoardPostSummary) {
        String postId;
        HashMap<String, String> parameters = ParsingUtils.createAttributeMapFromStartTag(tagString);
        if (parameters != null) {
            String href = parameters.get(HtmlConstants.HREF);
            if (!StringUtils.isEmpty(href)) {
                int indexOfLastForwardSlash = href.lastIndexOf("/");
                if (indexOfLastForwardSlash != -1) {
                    postId = href.substring(indexOfLastForwardSlash + 1);
                    if (postId.contains("#last")) {
                        postId = postId.replace("#last", "");
                    } else if (postId.contains("#")) {
                        postId = postId.replace("#", "");
                    }
                    currentBoardPostSummary.setBoardPostID(postId);
                }
            }
        }
    }

}
