package com.gregmcgowan.drownedinsound.data.parser.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

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

public class BoardPostParser extends StreamingParser {
    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardPostParser";

    private static final String MAIN_CONTENT_CLASS = "content no-border";
    private static final String EDITORIAL_CLASS = "editorial";
    private static final boolean DEBUG_PARSER = true;

    private String boardPostId;
    private BoardType boardType;
    private InputStream inputStream;
    private StringBuilder buffer;
    private boolean consumingHtmlTags;
    private int initialContentDivLevel = 0;
    private int initialContentAnchorNumber = 0;
    private BoardPost currentBoardPost;
    private PageState pageState;
    private SpanClass spanClass;

    public BoardPostParser(InputStream inputStream, String boardPostId,
	    BoardType boardType) {
	this.boardPostId = boardPostId;
	this.boardType = boardType;
	this.inputStream = inputStream;
	this.buffer = new StringBuilder(1024);
	pageState = null;
    }

    private static enum PageState {
	INITIAL_CONTENT_DIV, EDITIORIAL_DIV, COMMENT_DIV, COMMENT_CONTENT_DIV, COMMENT_FOOTER_DIV
    }

    private static enum SpanClass {
	DATE
    }

    private void setPageState(PageState pageState) {
	this.pageState = pageState;
    }

    private boolean isInPageState(PageState requiredPageState) {
	return pageState != null && pageState.equals(requiredPageState);
    }

    public BoardPost parse() {
	long start = System.currentTimeMillis();
	currentBoardPost = new BoardPost();
	try {
	    StreamedSource streamedSource = new StreamedSource(inputStream);
	    for (Segment segment : streamedSource) {
		if (segment instanceof Tag) {
		    Tag tag = (Tag) segment;
		    String tagName = tag.getName();
		    if (HtmlConstants.DIV.equals(tagName)) {
			if (tag instanceof StartTag) {
			    String className = ((StartTag) tag)
				    .getAttributeValue(HtmlConstants.CLASS);
			    if (MAIN_CONTENT_CLASS.equals(className)) {
				setPageState(PageState.INITIAL_CONTENT_DIV);
			    } else if (EDITORIAL_CLASS.equals(className)) {
				setPageState(PageState.EDITIORIAL_DIV);
				consumingHtmlTags = true;
			    }
			    if (isInPageState(PageState.INITIAL_CONTENT_DIV)) {
				initialContentDivLevel++;
			    }
			} else {
			    if (isInPageState(PageState.INITIAL_CONTENT_DIV)) {
				initialContentDivLevel--;
				if (initialContentDivLevel == 0) {
				    setPageState(null);
				    initialContentAnchorNumber = 0;
				}
			    } else if (isInPageState(PageState.EDITIORIAL_DIV)) {
				consumingHtmlTags = false;
				setPageState(null);
				String content = readFromBuffer(false);
				currentBoardPost.setContent(content);
			    }
			}
		    } else if (HtmlConstants.ANCHOR.equals(tagName)) {
			if (tag instanceof StartTag) {
			    if (isInPageState(PageState.INITIAL_CONTENT_DIV)) {
				initialContentAnchorNumber++;
			    }
			} else {
			    // Log.d(TAG, "in content div"+inInitialContentDiv);
			    if (isInPageState(PageState.INITIAL_CONTENT_DIV)) {
				if (initialContentAnchorNumber == 1) {
				    String title = readFromBuffer();
				    currentBoardPost.setTitle(title);
				} else if (initialContentAnchorNumber == 2) {
				    String author = readFromBuffer();
				    currentBoardPost.setAuthorUsername(author);
				} else if (initialContentAnchorNumber == 5) {
				    String numberOfReplies = readFromBuffer();
				    if (!TextUtils.isEmpty(numberOfReplies)) {
					StringTokenizer tokeniser = new StringTokenizer(
						numberOfReplies, " ");
					String replyAmountString = tokeniser
						.nextToken();
					try {
					    int replies = Integer
						    .parseInt(replyAmountString);
					    currentBoardPost
						    .setNumberOfReplies(replies);
					} catch (NumberFormatException nfe) {

					}
				    }
				}
			    }
			}
		    } else if (HtmlConstants.SPAN.equals(tagName)) {
			if (tag instanceof StartTag) {
			    if (isInPageState(PageState.INITIAL_CONTENT_DIV)) {
				String className = ((StartTag) tag)
					.getAttributeValue(HtmlConstants.CLASS);
				if (HtmlConstants.DATE_CLASS.equals(className)) {
				    spanClass = SpanClass.DATE;
				}
			    }
			}
			if (tag instanceof EndTag) {
			    if (SpanClass.DATE.equals(spanClass)) {
				String dateAndTime = readFromBuffer();
				currentBoardPost.setDateOfPost(dateAndTime);
				spanClass = null;
			    }
			}

		    } else if (HtmlConstants.PARAGRAPH.equals(tagName)) {
			if (consumingHtmlTags) {
			    buffer.append(segment.toString());
			}
		    } else if (HtmlConstants.BREAK.equals(tagName)) {
			if (consumingHtmlTags) {
			    buffer.append(segment.toString());
			}
		    }

		    if (tag instanceof EndTag) {
			if (!consumingHtmlTags) {
			    clearBuffer();
			}
		    }
		} else {
		    if (consumeText()) {
			// Log.d(TAG, "adding to buffer = " +
			// segment.toString());
			buffer.append(segment.toString());
		    }
		}
	    }
	} catch (IOException e) {
	    if (DisBoardsConstants.DEBUG) {
		e.printStackTrace();
	    }
	}
	if (DisBoardsConstants.DEBUG) {
	    if (DEBUG_PARSER) {
		Log.d(TAG, currentBoardPost.toString());
	    }
	    Log.d(TAG, "Parsed board post in "
		    + (System.currentTimeMillis() - start) + " ms");
	}
	return currentBoardPost;
    }

    private boolean consumeText() {
	return isInPageState(PageState.INITIAL_CONTENT_DIV)
		|| isInPageState(PageState.EDITIORIAL_DIV)
		|| isInPageState(PageState.COMMENT_DIV);
    }

    private void clearBuffer() {
	buffer.setLength(0);
    }

    private String readFromBuffer() {
	return readFromBuffer(true);
    }

    private String readFromBuffer(boolean convertFromHtml) {
	String content = buffer.toString().trim();
	if (convertFromHtml) {
	    content = Html.fromHtml(content).toString();
	}
	return content;
    }
}
