package com.gregmcgowan.drownedinsound.data.parser;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;
import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;
import com.gregmcgowan.drownedinsound.data.model.BoardType;

public class BoardPostParser {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardPostParser";

    private static final boolean DEBUG_PARSER = true;
    
    private Document boardPostDocument;
    private String boardPostId;
    private BoardType boardType;
    
    
    public BoardPostParser(Document document, String boardId, BoardType boardType) {
	this.boardPostDocument = document;
	this.boardPostId = boardId;
	this.boardType = boardType;
    }

    public BoardPost parseDocument() {
	BoardPost boardPost = null;
	long startTime = System.currentTimeMillis();
	if (boardPostDocument != null) {
	    // First get the info about the initial post
	    // Title
	    Elements originalPostElements = boardPostDocument
		    .getElementsByClass("content").get(1).getAllElements();
	    String title = originalPostElements.get(2).text();
	    if(DisBoardsConstants.DEBUG && DEBUG_PARSER){
		Log.d(TAG, "Title = " + title);
	    }
	    
	    // Author username
	    Elements byLineElements = originalPostElements.get(5)
		    .getAllElements();
	    String author = byLineElements.get(1).text();
	    if(DisBoardsConstants.DEBUG && DEBUG_PARSER) {
		Log.d(TAG, "Author = " + author);
	    }

	    // Replies and date
	    String replies = byLineElements.get(5).text();
	    String date = byLineElements.get(6).text();
	    if(DisBoardsConstants.DEBUG && DEBUG_PARSER){
		Log.d(TAG, "Replies = " + replies + " date = " + date);
	    }
	    
	    Element contentElement = originalPostElements.get(12);
	    String content = contentElement.html();
	    if(DisBoardsConstants.DEBUG && DEBUG_PARSER){
		Log.d(TAG, "Content = " + content);
	    }
	
	    // TODO check we have the required fields
	    boardPost = new BoardPost();
	    boardPost.setContent(content);
	    boardPost.setTitle(title);
	    boardPost.setAuthorUsername(author);
	    boardPost.setDateOfPost(date);
	    boardPost.setId(boardPostId);
	    boardPost.setBoardType(boardType);
	    boardPost.setLastViewedTime(System.currentTimeMillis());

	    // And add the post content as first comment
	    ArrayList<BoardPostComment> boardPostComments = new ArrayList<BoardPostComment>();
	    BoardPostComment boardPostComment = new BoardPostComment();
	    boardPostComment.setId(boardPostId);
	    boardPostComment.setAuthorUsername(author);
	    boardPostComment.setCommentLevel(0);
	    boardPostComment.setDateAndTimeOfComment(date);
	    boardPostComment.setContent(content);
	    boardPostComment.setTitle(title);
	    boardPostComment.setBoardPost(boardPost);
	    
	    boardPostComments.add(boardPostComment);
	    
	    //And add the rest of the comments
	    Elements threadElements = boardPostDocument
		    .getElementsByClass("thread");
	    if (threadElements.size() > 0) {
		Element topLevel = threadElements.get(0);
		Elements children = topLevel.children();
		if (children.size() > 0) {
		    boardPostComments = getListOfComments(0, boardPostComments,
			    children,boardPost);
		}
	    }
	    //Don't forgot to add it to the boardPost
	    boardPost.setComments(boardPostComments);
	    boardPost.setNumberOfReplies(boardPostComments.size());
	}
	Log.d(TAG, "Parsed post in "+ (System.currentTimeMillis() - startTime) +" ms");
	return boardPost;
    }

    private ArrayList<BoardPostComment> getListOfComments(int currentLevel,
	    ArrayList<BoardPostComment> boardPosts, Elements threadElements,BoardPost boardPostParent) {
	for (Element threadElement : threadElements) {
	    Elements children = threadElement.children();
	    int noOfChildren = children.size();
	    String id =  threadElement.children().get(0).id().substring(1);
	    if(DisBoardsConstants.DEBUG && DEBUG_PARSER) {
		Log.d(TAG, "Board post id ["+ id +"]");
	    }
	    
	    if (noOfChildren == 1) {
		Elements titleElements = threadElement.select("h3");
		String title = "";
		if (titleElements.size() > 0) {
		    title = titleElements.get(0).text();
		}

		Elements contentElements = threadElement
			.select("div.comment_content");
		String content = "";
		if (contentElements.size() > 0) {
		    content = contentElements.get(0).html();
		}

		Elements authorAndDateElements = children.get(0).select(
			"div.comment_footer");
		String author = "";
		String dateAndTime = "";
		if (authorAndDateElements != null) {
		    String combinedDateAndTime = authorAndDateElements.text();
		    if (!TextUtils.isEmpty(combinedDateAndTime)) {
			String[] combinedDateAndTimeBits = combinedDateAndTime
				.split("\\Q|\\E");
			if (combinedDateAndTimeBits != null
				&& combinedDateAndTimeBits.length >= 2) {
			    author = combinedDateAndTimeBits[0].trim();
			    dateAndTime = combinedDateAndTimeBits[1].trim();
			}
		    }
		}
		
		if (DisBoardsConstants.DEBUG && DEBUG_PARSER) {
		    Log.d(TAG, "title [" + title + "]");
		    Log.d(TAG, "content [" + content + "]");
		    Log.d(TAG, "comment Level [" + currentLevel + "]");
		    Log.d(TAG, "author [" + author+"]");
		    Log.d(TAG, "dateAndTime [" + dateAndTime +"]");
		}

		BoardPostComment boardPostComment = new BoardPostComment();
		boardPostComment.setTitle(title);
		boardPostComment.setContent(content);
		boardPostComment.setCommentLevel(currentLevel);
		boardPostComment.setAuthorUsername(author);
		boardPostComment.setDateAndTimeOfComment(dateAndTime);
		boardPostComment.setId(id);
		boardPostComment.setBoardPost(boardPostParent);
		boardPosts.add(boardPostComment);

	    } else if (noOfChildren > 1) {
		Elements titleElements = children.get(0).select("h3");
		String title = "";
		if (titleElements.size() > 0) {
		    title = titleElements.get(0).text();
		}

		Elements contentElements = children.get(0).select(
			"div.comment_content");
		String content = "";
		if (contentElements.size() > 0) {
		    content = contentElements.get(0).html();
		}

		Elements authorAndDateElements = children.get(0).select(
			"div.comment_footer");
		String author = "";
		String dateAndTime = "";
		if (authorAndDateElements != null) {
		    String combinedDateAndTime = authorAndDateElements.text();
		    if (!TextUtils.isEmpty(combinedDateAndTime)) {
			String[] combinedDateAndTimeBits = combinedDateAndTime
				.split("\\Q|\\E");
			if (combinedDateAndTimeBits != null
				&& combinedDateAndTimeBits.length >= 2) {
			    author = combinedDateAndTimeBits[0].trim();
			    dateAndTime = combinedDateAndTimeBits[1].trim();
			}
		    }
		}

		Element possiblyThisElement = children.get(1);
		
		String thisCombinedText = "";
		boolean thisPresent = "this".equals(possiblyThisElement
			.className());
		if(thisPresent) {
		    thisCombinedText = possiblyThisElement.text();
		}
	
		if (DisBoardsConstants.DEBUG && DEBUG_PARSER) {
		    Log.d(TAG, "title [" + title + "]");
		    Log.d(TAG, "content [" + content + "]");
		    Log.d(TAG, "comment Level [" + currentLevel + "]");
		    Log.d(TAG, "thisPresent [" + thisPresent+"]");
		    Log.d(TAG, "author [" + author+"]");
		    Log.d(TAG, "dateAndTime [" + dateAndTime +"]");
		    Log.d(TAG, "This element  = " + thisCombinedText);
		}

		BoardPostComment boardPostComment = new BoardPostComment();
		boardPostComment.setTitle(title);
		boardPostComment.setContent(content);
		boardPostComment.setCommentLevel(currentLevel);
		boardPostComment.setAuthorUsername(author);
		boardPostComment.setDateAndTimeOfComment(dateAndTime);
		boardPostComment.setUsersWhoHaveThissed(thisCombinedText);
		boardPostComment.setId(id);
		boardPostComment.setBoardPost(boardPostParent);
		boardPosts.add(boardPostComment);

		// Get responses
		Elements comments = null;
		if (thisPresent && noOfChildren == 3) {
		    comments = threadElement.children().get(2).children();
		} else if (!thisPresent && noOfChildren == 2) {
		    comments = threadElement.children().get(1).children();
		}

		if (comments != null && comments.size() > 0) {
		    boardPosts.addAll(getListOfComments(currentLevel + 1,
			    new ArrayList<BoardPostComment>(), comments,boardPostParent));

		}
	    }

	}
	return boardPosts;
    }

}
