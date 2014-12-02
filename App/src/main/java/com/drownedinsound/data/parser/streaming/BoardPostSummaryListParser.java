package com.drownedinsound.data.parser.streaming;

import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.UserSessionManager;
import com.drownedinsound.data.model.BoardPost;
import com.drownedinsound.database.DatabaseHelper;
import com.drownedinsound.data.model.BoardType;
import com.drownedinsound.utils.DateUtils;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StreamedSource;
import net.htmlparser.jericho.Tag;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BoardPostSummaryListParser extends StreamingParser {

    private static final int POST_URL_ANCHOR_INDEX = 1;

    private static final int DESCRIPTION_TABLE_ROW_INDEX = 2;

    private static final int REPLIES_TABLE_ROW_INDEX = 3;

    private static final int LAST_POST_TABLE_ROW_INDEX = 4;

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
            + "BoardPostParser";

    private static final Object STICKY_CLASS = "content_type_label";

    private static final boolean DEBUG_PARSER = false;

    private InputStream inputStream;

    private BoardType boardType;

    private boolean inBoardPostTable;

    private int tableRowCell;

    private int spanNumber;

    private int anchorNumber;

    private ArrayList<BoardPost> boardPosts;

    private BoardPost currentBoardPost;

    private StringBuilder buffer;

    private DatabaseHelper databaseHelper;

    private UserSessionManager userSessionManager;

    public BoardPostSummaryListParser(UserSessionManager userSessionManager,
            InputStream inputStream,
            BoardType boardType, DatabaseHelper databaseHelper) {
        this.inputStream = inputStream;
        this.boardType = boardType;
        this.boardPosts = new ArrayList<>();
        this.buffer = new StringBuilder(1024);
        this.databaseHelper = databaseHelper;
        this.userSessionManager = userSessionManager;
    }

    public ArrayList<BoardPost> parse() {
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
                                userSessionManager.setAuthenticityToken(authToken);
                            }
                        }
                    } else if (tagName.equals(HtmlConstants.TABLE_ROW)) {
                        if (tag instanceof StartTag) {
                            String trString = tag.toString();
                            if (isStartOfNewPostTr(trString)) {
                                currentBoardPost = new BoardPost();
                                currentBoardPost.setBoardType(boardType);
                            }
                            tableRowCell = 0;
                        } else {
                            if (currentBoardPost != null) {
                                // TODO we need to get the last viewed time and
                                // set it here
                                if (databaseHelper != null) {
                                    BoardPost existingPost = databaseHelper
                                            .getBoardPost(currentBoardPost
                                                    .getId());
                                    // We don't want to overwrite certain values
                                    if (existingPost != null) {
                                        currentBoardPost
                                                .setLastViewedTime(existingPost
                                                        .getLastViewedTime());
                                        currentBoardPost
                                                .setNumberOfTimesRead(existingPost
                                                        .getNumberOfTimesRead());
                                        currentBoardPost.setFavourited(existingPost.isFavourited());
                                    }
                                }
                                boardPosts.add(currentBoardPost);
                                currentBoardPost = null;
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
                                setNumberOfReplies();
                            }
                        }
                    } else if (tagName.endsWith(HtmlConstants.ANCHOR)) {
                        if (tag instanceof StartTag) {
                            anchorNumber++;
                            if (inBoardPostTable) {
                                String tagString = tag.toString();
                                if (anchorNumber == POST_URL_ANCHOR_INDEX
                                        && tableRowCell == DESCRIPTION_TABLE_ROW_INDEX) {
                                    extractPostId(tagString);
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
                                                currentBoardPost.setLastUpdatedTime(timestamp);
                                            }
                                        }
                                    }
                                    extractPostId(tagString);
                                }
                            }
                        } else {
                            if (inBoardPostTable
                                    && tableRowCell == DESCRIPTION_TABLE_ROW_INDEX) {
                                String bufferOutput = Html.fromHtml(
                                        buffer.toString().trim()).toString();
                                parseDescriptionRowAnchorText(bufferOutput);
                            }
                        }
                    } else if (tagName.endsWith(HtmlConstants.SPAN)) {
                        if (inBoardPostTable) {
                            if (tag instanceof EndTag) {
                                parseSpanSegment(segment);
                            }
                        }
                    } else if (HtmlConstants.META.equals(tagName)) {
                            String metaString = tag.toString();
                            if (metaString.contains(HtmlConstants.AUTHENTICITY_TOKEN_NAME)) {
                                Attributes attributes = tag.parseAttributes();
                                if (attributes != null) {
                                    String authToken = attributes.getValue("content");
                                    userSessionManager.setAuthenticityToken(authToken);
                                }
                            }
                    }


                    if (tag instanceof EndTag) {
                        clearBuffer();
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
            Log.d(TAG, "Parsed " + boardPosts.size() + " board posts in "
                    + (System.currentTimeMillis() - start) + " ms");
            for (BoardPost boardPost : boardPosts) {
                Log.d(TAG, boardPost.toString());
            }
        }
        return boardPosts;
    }

    private void parseSpanSegment(Segment segment) {
        spanNumber++;
        String spanString = segment.toString();
        HashMap<String, String> parameters = createAttributeMapFromStartTag(spanString);
        if (spanNumber == 1) {
            String spanClass = parameters.get(HtmlConstants.CLASS);
            if (STICKY_CLASS.equals(spanClass)) {
                currentBoardPost.setSticky(true);
            }
            if (parameters != null) {
//                long timeStamp = parseDate();
//                if (timeStamp != -1) {
//                    if (tableRowCell == DESCRIPTION_TABLE_ROW_INDEX
//                        && !currentBoardPost.isSticky()) {
//                        //TODO not sure if we actually need this
//                       // currentBoardPost.setCreatedTime(timeStamp);
//                    }
////                    if (tableRowCell == LAST_POST_TABLE_ROW_INDEX) {
////                        currentBoardPost.setLastUpdatedTime(timeStamp);
////                    }
//                }
            }
        } else if (tableRowCell == DESCRIPTION_TABLE_ROW_INDEX
                && spanNumber == 2 && currentBoardPost.isSticky()) {
            //TODO not sure if we actually need this
            //long timeStamp = parseDate();
            //currentBoardPost.setCreatedTime(timeStamp);
        }
    }

    private long parseDate(String dateString, String format) {
        long timeStamp = -1;
        if (!TextUtils.isEmpty(dateString)) {
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
                Log.d(TAG, "parse date =" + parsedDate.toString());
            }
        }
        return timeStamp;
    }

    private void setNumberOfReplies() {
        int numberOfReplies = 0;
        String repliesText = Html.fromHtml(buffer.toString().trim()).toString();
        if (!TextUtils.isEmpty(repliesText)) {
            String[] repliesTokens = repliesText.split("\\s");
            if (repliesTokens != null && repliesTokens.length > 0) {
                try {
                    numberOfReplies = Integer.parseInt(repliesTokens[0]);
                } catch (NumberFormatException nfe) {

                }
            }
        }
        currentBoardPost.setNumberOfReplies(numberOfReplies);
    }

    private boolean isStartOfNewPostTr(String trString) {
        // TODO better way to do this
        return trString != null
                && trString.startsWith("<tr style='background-color");
    }

    private void parseDescriptionRowAnchorText(String bufferOutput) {
        if (anchorNumber == 1) {
            String title = bufferOutput;
            // Log.d(TAG, "Title [" + title + "]");
            currentBoardPost.setTitle(title);
        } else if (anchorNumber == 2) {
            String[] authorTokens = bufferOutput.split("\\s");
            if (authorTokens != null && authorTokens.length > 1) {
                String author = authorTokens[1];
                // Log.d(TAG, "Author [" + author + "]");
                currentBoardPost.setAuthorUsername(author);
            }
        }
    }

    private void extractPostId(String tagString) {
        String postId;
        HashMap<String, String> parameters = createAttributeMapFromStartTag(tagString);
        if (parameters != null) {
            String href = parameters.get(HtmlConstants.HREF);
            if (!TextUtils.isEmpty(href)) {
                int indexOfLastForwardSlash = href.lastIndexOf("/");
                if (indexOfLastForwardSlash != -1) {
                    postId = href.substring(indexOfLastForwardSlash + 1);
                    if( postId.contains("#last")){
                        postId = postId.replace("#last","");
                    } else  if(postId.contains("#")) {
                        postId = postId.replace("#","");
                    }
                    currentBoardPost.setId(postId);
                }
            }
        }
    }

    private void clearBuffer() {
        buffer.setLength(0);
    }

}
