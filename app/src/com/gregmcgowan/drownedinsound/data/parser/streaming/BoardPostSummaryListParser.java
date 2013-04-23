package com.gregmcgowan.drownedinsound.data.parser.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.StreamedSource;
import net.htmlparser.jericho.Tag;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;

public class BoardPostSummaryListParser {

    private static final int POST_URL_ANCHOR_INDEX = 1;

    private static final int DESCRIPTION_TABLE_ROW_INDEX = 2;
    private static final int REPLIES_TABLE_ROW_INDEX = 3;
    private static final int LAST_POST_TABLE_ROW_INDEX = 4;

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardPostParser";

    private static final Object STICKY_CLASS = "content_type_label";

    private InputStream inputStream;

    private BoardType boardType;

    private boolean inBoardPostTable;

    private boolean inTableRow;

    private boolean inTableRowCell;
    private int tableRowCell;

    private int spanNumber;

    private boolean inAnchor;
    private int anchorNumber;

    private ArrayList<BoardPost> boardPosts;
    private BoardPost currentBoardPost;
    private StringBuilder buffer;

    public BoardPostSummaryListParser(InputStream inputStream,
	    BoardType boardType) {
	this.inputStream = inputStream;
	this.boardType = boardType;
	this.boardPosts = new ArrayList<BoardPost>();
	this.buffer = new StringBuilder(1024);
    }

    private boolean isStartOfNewPostTr(String trString) {
	return trString != null
		&& trString.startsWith("<tr style=\"background-color");
    }

    public ArrayList<BoardPost> parse() {
	long start = System.currentTimeMillis();
	try {
	    StreamedSource streamedSource = new StreamedSource(inputStream);
	    // writer=new FileWriter("StreamedSourceCopyOutput.html");
	    Log.d(TAG, "Processing segments:");
	    for (Segment segment : streamedSource) {
		if (segment instanceof Tag) {
		    Tag tag = (Tag) segment;
		    String tagName = tag.getName();

		    if (tagName.equals(HtmlConstants.TABLE)) {
			inBoardPostTable = tag instanceof StartTag;

		    } else if (tagName.equals(HtmlConstants.TABLE_ROW)) {
			// Log.d(TAG, "tr = " + tag.toString());
			if (tag instanceof StartTag) {
			    String trString = tag.toString();
			    if (isStartOfNewPostTr(trString)) {
				currentBoardPost = new BoardPost();
				currentBoardPost.setBoardType(boardType);
			    }
			    tableRowCell = 0;
			    inTableRow = true;
			} else {
			    if (currentBoardPost != null) {
				boardPosts.add(currentBoardPost);
			    }
			    inTableRow = false;
			}
		    } else if (tagName.equals(HtmlConstants.TABLE_CELL)) {
			if (tag instanceof StartTag) {
			    if (inBoardPostTable) {
				createAttributeMapFromStartTag(segment
					.toString());
			    }
			    tableRowCell++;
			    anchorNumber = 0;
			    spanNumber = 0;

			    inTableRowCell = true;
			    // Log.d(TAG, "td start = " + tag.toString());
			} else {
			    // Log.d(TAG, "td end = " + tag.toString());
			    if (inBoardPostTable
				    && tableRowCell == REPLIES_TABLE_ROW_INDEX) {
				int numberOfReplies = 0;
				String repliesText = Html.fromHtml(
					buffer.toString().trim()).toString();
				if (!TextUtils.isEmpty(repliesText)) {
				    String[] repliesTokens = repliesText
					    .split("\\s");
				    if (repliesTokens != null
					    && repliesTokens.length > 0) {
					try {
					    numberOfReplies = Integer
						    .parseInt(repliesTokens[0]);
					} catch (NumberFormatException nfe) {

					}
				    }
				}
				currentBoardPost
					.setNumberOfReplies(numberOfReplies);
			    }
			    inTableRowCell = false;
			}
		    } else if (tagName.endsWith(HtmlConstants.ANCHOR)) {
			if (tag instanceof StartTag) {
			    anchorNumber++;
			    if (inBoardPostTable) {
				if (anchorNumber == POST_URL_ANCHOR_INDEX
					&& tableRowCell == DESCRIPTION_TABLE_ROW_INDEX) {
				    extractPostId(segment.toString());
				}
			    }
			    inAnchor = true;

			} else {
			    inAnchor = false;
			    if (inBoardPostTable
				    && tableRowCell == DESCRIPTION_TABLE_ROW_INDEX) {
				String bufferOutput = Html.fromHtml(
					buffer.toString().trim()).toString();
				parseDescriptionRowAnchorText(bufferOutput);
			    }
			}
		    } else if (tagName.endsWith(HtmlConstants.SPAN)) {
			if (inBoardPostTable) {
			    if (tag instanceof StartTag) {
				spanNumber++;
				HashMap<String, String> parameters = createAttributeMapFromStartTag(segment
					.toString());
				if (spanNumber == 1) {
				    String spanClass = parameters
					    .get(HtmlConstants.CLASS);
				    if (STICKY_CLASS.equals(spanClass)) {
					currentBoardPost.setSticky(true);
				    }
				    if (parameters != null) {
					long timeStamp = getTimestampFromParameters(parameters);
					if (timeStamp != -1) {
					    if (tableRowCell == DESCRIPTION_TABLE_ROW_INDEX
						    && !currentBoardPost
							    .isSticky()) {
						currentBoardPost
							.setCreatedTime(timeStamp);
					    }
					    if (tableRowCell == LAST_POST_TABLE_ROW_INDEX) {
						currentBoardPost
							.setLastUpdatedTime(timeStamp);
					    }
					}
				    }
				} else if (tableRowCell == DESCRIPTION_TABLE_ROW_INDEX
					&& spanNumber == 2
					&& currentBoardPost.isSticky()) {
				    long timeStamp = getTimestampFromParameters(parameters);
				    currentBoardPost.setCreatedTime(timeStamp);
				}

			    } else {

			    }
			}
		    }
		    if (tag instanceof EndTag) {
			clearBuffer();
		    }
		} else {
		    if (inBoardPostTable) {
			// Log.d(TAG, "Plain text " + segment.toString());
			buffer.append(segment.toString());
		    }
		}
	    }

	} catch (IOException ioe) {

	} finally {
	    if (inputStream != null) {
		try {
		    inputStream.close();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}
	Log.d(TAG,
		"Parsed " + boardPosts.size() + " board posts in "
			+ (System.currentTimeMillis() - start) + " ms");
	for (BoardPost boardPost : boardPosts) {
	    Log.d(TAG, boardPost.toString());
	}

	return boardPosts;
    }

    private long getTimestampFromParameters(HashMap<String, String> parameters) {
	String timeStampString = parameters.get(HtmlConstants.TITLE);
	long timeStamp = -1;
	try {
	    timeStamp = Long.parseLong(timeStampString) * 1000;
	} catch (NumberFormatException nfe) {

	}
	return timeStamp;
    }

    private void parseDescriptionRowAnchorText(String bufferOutput) {
	if (anchorNumber == 1) {
	    String title = bufferOutput;
	   // Log.d(TAG, "Title [" + title + "]");
	    currentBoardPost.setTitle(bufferOutput);
	} else if (anchorNumber == 2) {
	    String[] authorTokens = bufferOutput.split("\\s");
	    if (authorTokens != null && authorTokens.length > 1) {
		String author = authorTokens[1];
		//Log.d(TAG, "Author [" + author + "]");
		currentBoardPost.setAuthorUsername(author);
	    }
	}
    }

    private void extractPostId(String tagString) {
	String postId = null;
	HashMap<String, String> parameters = createAttributeMapFromStartTag(tagString);
	if (parameters != null) {
	    String href = parameters.get(HtmlConstants.HREF);
	    if (!TextUtils.isEmpty(href)) {
		int indexOfLastForwardSlash = href.lastIndexOf("/");
		if (indexOfLastForwardSlash != -1) {
		    postId = href.substring(indexOfLastForwardSlash + 1);
		    currentBoardPost.setId(postId);
		}
	    }
	}
    }

    private void clearBuffer() {
	buffer = new StringBuilder(1024);
    }

    private HashMap<String, String> createAttributeMapFromStartTag(String tag) {
	HashMap<String, String> hashMap = new HashMap<String, String>();
	String removeStartAndEnd = tag.substring(1, tag.length() - 1);
	// Log.d(TAG, removeStartAndEnd);
	if (!TextUtils.isEmpty(removeStartAndEnd)) {
	    String[] splitAttributes = removeStartAndEnd.split("\\s");
	    if (splitAttributes != null && splitAttributes.length > 0) {
		for (int i = 0; i < splitAttributes.length; i++) {
		    String splitAttribute = splitAttributes[i];
		    String[] keyValue = splitAttribute.split("=");
		    if (keyValue != null && keyValue.length > 1) {
			String key = keyValue[0];
			String value = keyValue[1];
			if (!TextUtils.isEmpty(key)
				&& !TextUtils.isEmpty(value)) {
			    // Remove quotes
			    value = value.substring(1, value.length() - 1);
			    /*
			     * Log.d(TAG, "Key [" + key + "] Value [" + value +
			     * "]");
			     */
			    hashMap.put(key, value);
			}
		    }
		}
	    }
	}
	return hashMap;
    }

}
