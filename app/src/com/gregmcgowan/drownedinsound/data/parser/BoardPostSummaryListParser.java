package com.gregmcgowan.drownedinsound.data.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;
import android.util.Log;

import com.gregmcgowan.drownedinsound.DisBoardsConstants;
import com.gregmcgowan.drownedinsound.data.model.BoardPost;
import com.gregmcgowan.drownedinsound.data.model.BoardType;

/**
 * This will parse the document provider into a list of BoardPostSummary objects
 * 
 * @author Greg
 * 
 */
public class BoardPostSummaryListParser {

    private static final String TAG = DisBoardsConstants.LOG_TAG_PREFIX
	    + "BoardPostSummaryListParser";

    private Document document;
    private BoardType boardType;

    public BoardPostSummaryListParser(Document document, BoardType boardType) {
	this.document = document;
	this.boardType = boardType;
    }

    public List<BoardPost> parseDocument() {
	List<BoardPost> list = new ArrayList<BoardPost>();
	if (DisBoardsConstants.DEBUG) {
	    Log.d(TAG, "Starting parsing");
	}
	if (document != null) {
	    Elements tableElements = document.getElementsByTag("table");
	    if (!tableElements.isEmpty()) {
		Element tableElementBody = tableElements.get(0);
		Elements tableElementBodyChildren = tableElementBody.children();
		Element tableRows = tableElementBodyChildren.get(0);
		Elements tableRowsElements = tableRows.children();
		int count = 0;
		for (Element rowElement : tableRowsElements) {
		    if (count > 0) {
			BoardPost postSummary = getPostInfoFromElement(rowElement);
			if (postSummary != null) {
			    list.add(postSummary);
			}
		    }
		    count++;
		}
	    }
	}
	
	if (DisBoardsConstants.DEBUG) {
	    Log.d(TAG, "Finished parsing");
	}
	return list;
    }

    private BoardPost getPostInfoFromElement(Element element) {
	String postTitle = null;
	String authorUsername = null;
	Date postCreationDate = null;
	int numberOfReplies = 0;
	Date lastViewedDateTime = null;
	Date lastPostDateTime = null;
	String lastPostUsername = null;
	String postId = null;
	boolean isSticky = false;
	Elements childElements = element.children();
	// TODO
	Element readColumnElement = childElements.get(0);
	Element descriptionElement = childElements.get(1);

	Elements allDescriptionElements = descriptionElement.getAllElements();
	if (!allDescriptionElements.isEmpty()) {
	    // Add 1 if the post is a sticky
	    // 1 original post summary
	    // 2 title
	    // 3 break
	    // 5 author and date/time
	    Element titleElement = allDescriptionElements.get(1);
	    postTitle = titleElement.text();
	    if (postTitle.startsWith("Sticky")) {
		postTitle = postTitle.replace("Sticky", "").trim();
		isSticky = true;
	    }
	    int postIndex = isSticky ? 2 : 1;
	    String postHref = titleElement.getAllElements().get(postIndex).attr("href");
	    if(!TextUtils.isEmpty(postHref)){
		int indexOfLastForwardSlash = postHref.lastIndexOf("/");
		if(indexOfLastForwardSlash != -1) {
		    postId = postHref.substring(indexOfLastForwardSlash + 1);
		}
	    }
	    int authorIndex = isSticky ? 6 : 5;
	    Element authorElement = allDescriptionElements.get(authorIndex);
	    authorElement = authorElement.getAllElements().get(0);
	    authorUsername = authorElement.text();

	}

	Element repliesElement = childElements.get(2);
	Element lastPostElement = childElements.get(3);
	// TODO validate we are all the required data;
	BoardPost boardPostSummary = new BoardPost();
	boardPostSummary.setAuthorUsername(authorUsername);
	boardPostSummary.setTitle(postTitle);
	boardPostSummary.setId(postId);
	boardPostSummary.setBoardType(boardType);

	return boardPostSummary;
    }
}
