package com.gregmcgowan.drownedinsound.data.parser.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
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
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;
import com.gregmcgowan.drownedinsound.data.model.BoardType;
import com.gregmcgowan.drownedinsound.utils.DateUtils;

public class BoardPostParser extends StreamingParser {
    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardPostParser";

    private static final String MAIN_CONTENT_CLASS = "content no-border";
    private static final String EDITORIAL_CLASS = "editorial";
    private static final boolean DEBUG_PARSER = false;

    private static final String COMMENT_CLASS = "comment";

    private static final String COMMENT_CONTENT_CLASS = "comment_content";
    private static final String COMMENT_FOOTER_CLASS = "comment_footer";
    private static final String COMMENT_LIST_CLASS = "comments_list";
    private static final String THREAD_CLASS = "thread";
    private static final String THIS_CLASS = "this";

    private String boardPostId;
    private BoardType boardType;
    private InputStream inputStream;
    private StringBuilder buffer;
    private boolean consumingHtmlTags;
    private int initialContentDivLevel;
    private int initialContentAnchorNumber;
    private int commentFooterDivLevel;
    private BoardPost currentBoardPost;
    private PageState pageState;
    private PageState baseDivState;
    private SpanClass spanClass;
    private ArrayList<BoardPostComment> comments;
    private BoardPostComment currentBoardPostComment;
    private int boardPostCommentLevel;
    private long latestCommentTime;
    private String latestCommentId;

    public BoardPostParser(InputStream inputStream, String boardPostId,
	    BoardType boardType) {
	this.boardPostId = boardPostId;
	this.boardType = boardType;
	this.inputStream = inputStream;
	this.buffer = new StringBuilder(1024);
	comments = new ArrayList<BoardPostComment>();
	boardPostCommentLevel = -1;
    }

    private static enum PageState {
	INITIAL_CONTENT_DIV, EDITIORIAL_DIV, COMMENT_DIV, COMMENT_CONTENT_DIV, COMMENT_FOOTER_DIV, COMMENT_THIS_DIV, COMMENT_LIST_DIV
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
	currentBoardPost.setBoardType(boardType);
	currentBoardPost.setId(boardPostId);
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
			    } else if (COMMENT_CLASS.equals(className)) {
				setPageState(PageState.COMMENT_DIV);
				if (currentBoardPostComment != null) {
				    currentBoardPostComment
					    .setBoardPost(currentBoardPost);
				    comments.add(currentBoardPostComment);
				}
				currentBoardPostComment = new BoardPostComment();
				currentBoardPostComment
					.setCommentLevel(boardPostCommentLevel);
				String id = ((StartTag) tag)
					.getAttributeValue(HtmlConstants.ID);
				if (id != null && id.length() > 1) {
				    id = id.substring(1);
				    currentBoardPostComment.setId(id);
				}
			    } else if (COMMENT_CONTENT_CLASS.equals(className)) {
				consumingHtmlTags = true;
				setPageState(PageState.COMMENT_CONTENT_DIV);
			    } else if (COMMENT_FOOTER_CLASS.equals(className)) {
				setPageState(PageState.COMMENT_FOOTER_DIV);
			    } else if (COMMENT_LIST_CLASS.equals(className)) {
				setPageState(PageState.COMMENT_LIST_DIV);
				baseDivState = PageState.COMMENT_LIST_DIV;
			    } else if ("reply_form".equals(className)) {
				setPageState(null);
			    } else if ("this".equals(className)) {
				setPageState(PageState.COMMENT_THIS_DIV);
			    }
			    if (isInPageState(PageState.COMMENT_FOOTER_DIV)) {
				commentFooterDivLevel++;
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
				// Add the initial post as the first content
				BoardPostComment boardPostComment = new BoardPostComment();
				boardPostComment.setId(boardPostId);
				boardPostComment
					.setAuthorUsername(currentBoardPost
						.getAuthorUsername());
				boardPostComment
					.setDateAndTimeOfComment(currentBoardPost
						.getDateOfPost());
				boardPostComment.setContent(content);
				boardPostComment.setTitle(currentBoardPost
					.getTitle());
				boardPostComment.setBoardPost(currentBoardPost);

				comments.add(boardPostComment);
			    } else if (isInPageState(PageState.COMMENT_CONTENT_DIV)) {
				setPageState(null);
				consumingHtmlTags = false;
				String content = readFromBuffer(false);
				currentBoardPostComment.setContent(content);
			    } else if (isInPageState(PageState.COMMENT_THIS_DIV)) {
				setPageState(null);
				String usersWhoThisd = readFromBuffer();
				currentBoardPostComment
					.setUsersWhoHaveThissed(usersWhoThisd);
				clearBuffer();
			    }
			    if (isInPageState(PageState.COMMENT_FOOTER_DIV)) {
				commentFooterDivLevel--;
				if (commentFooterDivLevel == 0) {
				    setPageState(null);
				    String footerText = readFromBuffer();
				    // Log.d(TAG,
				    // "Comment footer text  ="+footerText);
				    if (!TextUtils.isEmpty(footerText)) {
					String[] combinedDateAndTimeBits = footerText
						.split("\\Q|\\E");
					if (combinedDateAndTimeBits != null
						&& combinedDateAndTimeBits.length >= 2) {
					    String author = combinedDateAndTimeBits[0]
						    .trim();
					    String replyToAuthor = null;
					    if (!TextUtils.isEmpty(author)) {
						String[] split = author
							.split("@");
						if (split != null
							&& split.length > 1) {
						    author = split[0].trim();
						    replyToAuthor = split[1]
							    .trim();
						}
					    }
					    String dateAndTime = combinedDateAndTimeBits[1]
						    .trim();
		
					    currentBoardPostComment
						    .setAuthorUsername(author);
					    currentBoardPostComment
						    .setReplyToUsername(replyToAuthor);
					    currentBoardPostComment
						    .setDateAndTimeOfComment(dateAndTime);
					    dateAndTime = dateAndTime.replace(
						    "\'", "");
					    dateAndTime = dateAndTime.replace(
						    ",", "");
					    Date dateOfPost = DateUtils
						    .parseDate(
							    dateAndTime,
							    DateUtils.DIS_BOARD_COMMENT_DATE_FORMAT);
					    if (dateOfPost != null) {
						long dateOfPostLongValue = dateOfPost
							.getTime();
						if (dateOfPostLongValue > latestCommentTime) {
						    latestCommentTime = dateOfPostLongValue;
						    latestCommentId = currentBoardPostComment
							    .getId();
						}
					    }

					}
				    }
				    clearBuffer();
				}
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

			    if (isInPageState(PageState.COMMENT_DIV)) {
				String title = readFromBuffer();
				currentBoardPostComment.setTitle(title);
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
				if (dateAndTime != null) {
				    dateAndTime = dateAndTime.replace("th", "");
				    dateAndTime = dateAndTime.replace("st", "");
				    dateAndTime = dateAndTime.replace("rd", "");
				    dateAndTime = dateAndTime.replace("nd", "");
				    dateAndTime = dateAndTime.replace(",", "");

				    Date parsedDate = DateUtils
					    .parseDate(
						    dateAndTime,
						    DateUtils.DIS_BOARD_POST_DATE_FORMAT);
				    if (parsedDate != null) {
					long parsedDateLongValue = parsedDate
						.getTime();
					Log.d(TAG, "Date of post"
						+ parsedDateLongValue);
					latestCommentTime = parsedDateLongValue;
				    }
				}
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
		    } else if (HtmlConstants.LIST.equals(tagName)) {
			if (tag instanceof StartTag) {
			    String className = ((StartTag) tag)
				    .getAttributeValue(HtmlConstants.CLASS);
			    if (THREAD_CLASS.equals(className)) {
				boardPostCommentLevel++;
			    }
			} else if (tag instanceof EndTag
				&& PageState.COMMENT_LIST_DIV
					.equals(baseDivState)) {
			    boardPostCommentLevel--;
			}
		    }

		    if (tag instanceof EndTag) {
			if (!consumingHtmlTags
				&& !isInPageState(PageState.COMMENT_FOOTER_DIV)
				&& !isInPageState(PageState.COMMENT_THIS_DIV)) {
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

	    if (currentBoardPostComment != null) {
		currentBoardPostComment.setBoardPost(currentBoardPost);
		comments.add(currentBoardPostComment);
	    }
	    if (latestCommentId != null) {
		currentBoardPost.setLatestCommentId(latestCommentId);
	    } else {
		currentBoardPost.setLatestCommentId(currentBoardPost.getId());
	    }
	    currentBoardPost.setLastUpdatedTime(latestCommentTime);
	    currentBoardPost.setLastViewedTime(System.currentTimeMillis());
	    currentBoardPost.setComments(comments);

	    streamedSource.close();
	} catch (IOException e) {
	    if (DisBoardsConstants.DEBUG) {
		e.printStackTrace();
	    }
	}
	if (DisBoardsConstants.DEBUG) {
	    Log.d(TAG, "Parsed board post in "
		    + (System.currentTimeMillis() - start) + " ms");
	    if (DEBUG_PARSER) {
		Log.d(TAG, currentBoardPost.toString());
		for (BoardPostComment boardPostComment : comments) {
		    Log.d(TAG, boardPostComment.toString());
		}
	    }

	}

	return currentBoardPost;
    }

    private boolean consumeText() {
	return isInPageState(PageState.INITIAL_CONTENT_DIV)
		|| isInPageState(PageState.EDITIORIAL_DIV)
		|| isInPageState(PageState.COMMENT_DIV)
		|| isInPageState(PageState.COMMENT_CONTENT_DIV)
		|| isInPageState(PageState.COMMENT_FOOTER_DIV)
		|| isInPageState(PageState.COMMENT_THIS_DIV)
		|| isInPageState(PageState.COMMENT_LIST_DIV);
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
