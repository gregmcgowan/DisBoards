package com.drownedinsound.data.parser.streaming;

import com.drownedinsound.BuildConfig;
import com.drownedinsound.core.DisBoardsConstants;
import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostComment;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.utils.DateUtils;
import com.drownedinsound.utils.StringUtils;

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
import java.util.StringTokenizer;

import timber.log.Timber;

public class BoardPostParser {


    private static final String MAIN_CONTENT_CLASS = "content no-border";

    private static final String EDITORIAL_CLASS = "editorial";

    private static final boolean DEBUG_PARSER = false;

    private static final String COMMENT_CLASS = "comment";

    private static final String COMMENT_CONTENT_CLASS = "comment_content";

    private static final String COMMENT_FOOTER_CLASS = "comment_footer";

    private static final String COMMENT_LIST_CLASS = "comments_list";

    private static final String THREAD_CLASS = "thread";

    private static final String THIS_CLASS = "this";

    private enum PageState {
        INITIAL_CONTENT_DIV, EDITIORIAL_DIV, COMMENT_DIV, COMMENT_CONTENT_DIV, COMMENT_FOOTER_DIV, COMMENT_THIS_DIV, COMMENT_LIST_DIV
    }

    private enum SpanClass {
        DATE
    }


    private boolean isInPageState(PageState currentPageState,PageState requiredPageState) {
        return currentPageState != null && currentPageState.equals(requiredPageState);
    }

    public BoardPost parse(@BoardPostList.BoardPostListType String boardListType, InputStream inputStream) {
        boolean consumingHtmlTags = false;
        int initialContentDivLevel = 0;
        int initialContentAnchorNumber = 0;
        int commentFooterDivLevel = 0;

        PageState currentPageState = null;
        PageState baseDivState = null;
        SpanClass spanClass = null;

        StringBuilder buffer = new StringBuilder(1024);

        int boardPostCommentLevel = -1;
        String latestCommentId = null;
        long latestCommentTime = 0;
        long start = System.currentTimeMillis();
        String boardPostId = null;

        BoardPost currentBoardPost = new BoardPost();
        currentBoardPost.setBoardListTypeID(boardListType);
        currentBoardPost.setBoardPostID(boardPostId);
        currentBoardPost.setLastFetchedTime(System.currentTimeMillis());

        ArrayList<BoardPostComment> comments = new ArrayList<>();
        BoardPostComment currentBoardPostComment = null;

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
                                currentPageState = PageState.INITIAL_CONTENT_DIV;
                            } else if (EDITORIAL_CLASS.equals(className)) {
                                currentPageState = PageState.EDITIORIAL_DIV;
                                consumingHtmlTags = true;
                            } else if (COMMENT_CLASS.equals(className)) {
                                currentPageState = PageState.COMMENT_DIV;
                                if (currentBoardPostComment != null) {
                                    currentBoardPostComment
                                            .setBoardPost(currentBoardPost);
                                    comments.add(currentBoardPostComment);
                                }
                                currentBoardPostComment = new BoardPostComment();
                                currentBoardPostComment.setBoardPostID(boardPostId);
                                currentBoardPostComment
                                        .setCommentLevel(boardPostCommentLevel);
                                String id = ((StartTag) tag)
                                        .getAttributeValue(HtmlConstants.ID);
                                if (id != null && id.length() > 1) {
                                    id = id.substring(1);
                                    currentBoardPostComment.setCommentID(id);
                                }
                            } else if (COMMENT_CONTENT_CLASS.equals(className)) {
                                consumingHtmlTags = true;
                                currentPageState = PageState.COMMENT_CONTENT_DIV;
                            } else if (COMMENT_FOOTER_CLASS.equals(className)) {
                                currentPageState = PageState.COMMENT_FOOTER_DIV;
                            } else if (COMMENT_LIST_CLASS.equals(className)) {
                                currentPageState = PageState.COMMENT_LIST_DIV;
                                baseDivState = PageState.COMMENT_LIST_DIV;
                            } else if ("reply_form".equals(className)) {
                                currentPageState = null;
                            } else if ("this".equals(className)) {
                                currentPageState = PageState.COMMENT_THIS_DIV;
                            }
                            if (isInPageState(currentPageState,PageState.COMMENT_FOOTER_DIV)) {
                                commentFooterDivLevel++;
                            }

                            if (isInPageState(currentPageState, PageState.INITIAL_CONTENT_DIV)) {
                                initialContentDivLevel++;
                            }
                        }  else {
                            if (isInPageState(currentPageState,PageState.INITIAL_CONTENT_DIV)) {
                                initialContentDivLevel--;
                                if (initialContentDivLevel == 0) {
                                    currentPageState = null;
                                    initialContentAnchorNumber = 0;
                                }
                            } else if (isInPageState(currentPageState, PageState.EDITIORIAL_DIV)) {
                                consumingHtmlTags = false;
                                currentPageState = null;
                                String content = buffer.toString().trim();
                                currentBoardPost.setContent(content);
                            } else if (isInPageState(currentPageState,PageState.COMMENT_CONTENT_DIV)) {
                                currentPageState = null;
                                consumingHtmlTags = false;
                                String content = buffer.toString().trim();
                                currentBoardPostComment.setContent(content);
                            } else if (isInPageState(currentPageState, PageState.COMMENT_THIS_DIV)) {
                                currentPageState = null;
                                String usersWhoThisd = buffer.toString().trim();
                                usersWhoThisd = usersWhoThisd.replace("\n", "");
                                usersWhoThisd = usersWhoThisd.replaceAll("[ ]{2,}", " ");
                                currentBoardPostComment
                                        .setUsersWhoHaveThissed(usersWhoThisd);
                                buffer.setLength(0);
                            }
                            if (isInPageState(currentPageState,PageState.COMMENT_FOOTER_DIV)) {
                                commentFooterDivLevel--;
                                if (commentFooterDivLevel == 0) {
                                    currentPageState = null;
                                    String footerText = buffer.toString().trim();
                                    if (!StringUtils.isEmpty(footerText)) {
                                        String[] combinedDateAndTimeBits = footerText
                                                .split("\\Q|\\E");
                                        if (combinedDateAndTimeBits != null
                                                && combinedDateAndTimeBits.length >= 2) {
                                            String author = combinedDateAndTimeBits[0]
                                                    .trim();
                                            String replyToAuthor = null;
                                            if (!StringUtils.isEmpty(author)) {
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
                                                    .setDateAndTime(dateAndTime);
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
                                                            .getCommentID();
                                                }
                                            }

                                        }
                                    }
                                    buffer.setLength(0);
                                }
                            }

                        }
                    } else if (HtmlConstants.ANCHOR.equals(tagName)) {
                        if (tag instanceof StartTag) {
                            if (isInPageState(currentPageState,PageState.INITIAL_CONTENT_DIV)) {
                                if (initialContentAnchorNumber == 0) {
                                    HashMap<String, String> parameters = ParsingUtils
                                            .createAttributeMapFromStartTag(tag.toString());
                                    if (parameters != null) {
                                        String href = parameters.get(HtmlConstants.HREF);
                                        int indexOfLastForwardSlash = href.lastIndexOf("/");
                                        if (indexOfLastForwardSlash != -1) {
                                            boardPostId = href
                                                    .substring(indexOfLastForwardSlash + 1);
                                            currentBoardPost.setBoardPostID(boardPostId);
                                        }
                                    }
                                }
                                initialContentAnchorNumber++;
                            }
                        } else {
                            if (isInPageState(currentPageState,PageState.INITIAL_CONTENT_DIV)) {
                                if (initialContentAnchorNumber == 1) {
                                    String title = buffer.toString().trim();
                                    currentBoardPost.setTitle(title);
                                } else if (initialContentAnchorNumber == 2) {
                                    String author = buffer.toString().trim();
                                    currentBoardPost.setAuthorUsername(author);
                                } else if (initialContentAnchorNumber == 5) {
                                    String numberOfReplies = buffer.toString().trim();
                                    if (!StringUtils.isEmpty(numberOfReplies)) {
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

                            if (isInPageState(currentPageState,PageState.COMMENT_DIV)) {
                                String title = buffer.toString().trim();
                                currentBoardPostComment.setTitle(title);
                            }

                        }
                    } else if (HtmlConstants.SPAN.equals(tagName)) {
                        if (tag instanceof StartTag) {
                            if (isInPageState(currentPageState,PageState.INITIAL_CONTENT_DIV)) {
                                String className = ((StartTag) tag)
                                        .getAttributeValue(HtmlConstants.CLASS);
                                if (HtmlConstants.DATE_CLASS.equals(className)) {
                                    spanClass = SpanClass.DATE;
                                }
                            }
                        }
                        if (tag instanceof EndTag) {
                            if (SpanClass.DATE.equals(spanClass)) {
                                String dateAndTime = buffer.toString().trim();
                                currentBoardPost.setDateOfPost(dateAndTime);
                                spanClass = null;
                                if (dateAndTime != null) {
                                    dateAndTime = dateAndTime.replace(",", "");
                                    dateAndTime = dateAndTime.replace("'", "");
                                    Timber.d("Date and time " + dateAndTime);
                                    Date parsedDate = DateUtils
                                            .parseDate(
                                                    dateAndTime,
                                                    DateUtils.DIS_BOARD_POST_DATE_FORMAT);
                                    if (parsedDate != null) {
                                        long parsedDateLongValue = parsedDate
                                                .getTime();
                                        Timber.d( "Date of post"
                                                + parsedDateLongValue);
                                        latestCommentTime = parsedDateLongValue;
                                    } else {
                                        Timber.d( "Parsed date is null");
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
                                && !isInPageState(currentPageState,PageState.COMMENT_FOOTER_DIV)
                                && !isInPageState(currentPageState,PageState.COMMENT_THIS_DIV)) {
                            buffer.setLength(0);
                        }
                    }
                } else {
                    if (consumeText(currentPageState)) {
                        buffer.append(segment.toString());
                    }
                }
            }

            if (currentBoardPostComment != null) {
                currentBoardPostComment.setBoardPost(currentBoardPost);
                comments.add(currentBoardPostComment);
            }
            if (latestCommentId != null) {
                currentBoardPost.setLatestCommentID(latestCommentId);
            }
            currentBoardPost.setLastUpdatedTime(latestCommentTime);
            currentBoardPost.setLastViewedTime(System.currentTimeMillis());
            currentBoardPost.setComments(comments);

            streamedSource.close();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        if (BuildConfig.DEBUG) {
            Timber.d( "Parsed board post in "
                    + (System.currentTimeMillis() - start) + " ms");
            if (DEBUG_PARSER) {
                Timber.d( currentBoardPost.toString());
                for (BoardPostComment boardPostComment : comments) {
                    Timber.d(boardPostComment.toString());
                }
            }

        }

        return currentBoardPost;
    }

    private boolean consumeText(PageState currentPageState) {
        return isInPageState(currentPageState,PageState.INITIAL_CONTENT_DIV)
                || isInPageState(currentPageState,PageState.EDITIORIAL_DIV)
                || isInPageState(currentPageState,PageState.COMMENT_DIV)
                || isInPageState(currentPageState,PageState.COMMENT_CONTENT_DIV)
                || isInPageState(currentPageState,PageState.COMMENT_FOOTER_DIV)
                || isInPageState(currentPageState,PageState.COMMENT_THIS_DIV)
                || isInPageState(currentPageState,PageState.COMMENT_LIST_DIV);
    }
}
