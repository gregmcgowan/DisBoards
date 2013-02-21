package com.gregmcgowan.drownedinsound.data.parser;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardPostComment;

public class BoardPostParser {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardPostParser";

    private Document boardPostDocument;

    public BoardPostParser(Document document) {
	this.boardPostDocument = document;
    }

    public BoardPost parseDocument() {
	BoardPost boardPost = null;
	if (boardPostDocument != null) {
	    // First get the info about the initial post
	    // Title
	    Elements originalPostElements = boardPostDocument
		    .getElementsByClass("content").get(1).getAllElements();
	    String title = originalPostElements.get(2).text();
	    Log.d(TAG, "Title = " + title);

	    // Author username
	    Elements byLineElements = originalPostElements.get(5)
		    .getAllElements();
	    String author = byLineElements.get(1).text();
	    Log.d(TAG, "Author = " + author);

	    // Replies and date
	    String replies = byLineElements.get(5).text();
	    String date =  byLineElements.get(6).text();
	    Log.d(TAG, "Replies = " + replies + " date = " + date);

	    Element contentElement = originalPostElements.get(12);
	    String content = contentElement.html();
	    Log.d(TAG, "Content = " + content);

	    // Next get all the comments
	    int commentLevel = 0;
	    Elements threadElements = boardPostDocument
		    .getElementsByClass("thread");

	    ArrayList<BoardPostComment> boardPostComments = getListOfComments(0,new ArrayList<BoardPostComment>(), threadElements);
	    //TODO check we have the required fields
	    boardPost = new BoardPost();
	    boardPost.setContent(content);
	    boardPost.setTitle(title);
	 //   boardPost.setNoOfReplies(replies);
	    
	}

	return boardPost;
    }
    
    
    private ArrayList<BoardPostComment> getListOfComments(int currentLevel,ArrayList<BoardPostComment> boardPosts, Elements threadElements){
	    for (Element threadElement : threadElements) {
		if (threadElement.children().size() == 1) {
		    Log.d(TAG, "This should be a comment with no sub comments at "+currentLevel);
		    BoardPostComment boardPostComment = new BoardPostComment();
		} else {
		    boardPosts.addAll(getListOfComments(currentLevel++, boardPosts, threadElement.children()));
		}
	    }
	    return boardPosts;
    }
    
}
