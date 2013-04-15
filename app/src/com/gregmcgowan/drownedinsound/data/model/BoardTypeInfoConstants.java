package com.gregmcgowan.drownedinsound.data.model;

import java.util.ArrayList;

import com.gregmcgowan.drownedinsound.network.UrlConstants;

public class BoardTypeInfoConstants {

    public static final ArrayList<BoardTypeInfo> BOARDS = new ArrayList<BoardTypeInfo>();
    static {

	BOARDS.add(new BoardTypeInfo(BoardType.MUSIC,
		BoardTypeConstants.MUSIC_DISPLAY_NAME, UrlConstants.MUSIC_URL));
	BOARDS.add(new BoardTypeInfo(BoardType.SOCIAL,
		BoardTypeConstants.SOCIAL_DISPLAY_NAME, UrlConstants.SOCIAL_URL));
	BOARDS.add(new BoardTypeInfo(BoardType.ANNOUNCEMENTS_CLASSIFIEDS,
		BoardTypeConstants.ANNOUNCEMENTS_CLASSIFIEDS_DISPLAY_NAME,
		UrlConstants.ANNOUNCEMENTS_CLASSIFIEDS_URL));
	BOARDS.add(new BoardTypeInfo(BoardType.MUSICIANS,
		BoardTypeConstants.MUSICIANS_DISPLAY_NAME,
		UrlConstants.MUSICIANS_URL));
	BOARDS.add(new BoardTypeInfo(BoardType.FESTIVALS,
		BoardTypeConstants.FESTIVALS_DISPLAY_NAME,
		UrlConstants.FESTIVALS_URL));
	BOARDS.add(new BoardTypeInfo(BoardType.YOUR_MUSIC,
		BoardTypeConstants.YOUR_MUSIC_DISPLAY_NAME,
		UrlConstants.YOUR_MUSIC_URL));
	BOARDS.add(new BoardTypeInfo(BoardType.ERRORS_SUGGESTIONS,
		BoardTypeConstants.ERROR_SUGGESTIONS_DISPLAY_NAME,
		UrlConstants.ERRORS_SUGGESTIONS_URL));

    };

    public static ArrayList<BoardTypeInfo> geBoardsToFetch(
	    BoardTypeInfo boardTypeInfo) {
	ArrayList<BoardTypeInfo> next2Tabs = new ArrayList<BoardTypeInfo>();
	int indexOfBoardTypeInfo = BOARDS.indexOf(boardTypeInfo);
	if (indexOfBoardTypeInfo != -1) {
	    int lastIndex = BOARDS.size() - 1;
	    next2Tabs.add(BOARDS.get(indexOfBoardTypeInfo));
	    if (indexOfBoardTypeInfo == 0) {
		next2Tabs.add(BOARDS.get(indexOfBoardTypeInfo + 1));
		next2Tabs.add(BOARDS.get(indexOfBoardTypeInfo + 2));
	    } else if (indexOfBoardTypeInfo == lastIndex) {
		next2Tabs.add(BOARDS.get(indexOfBoardTypeInfo + -1));
		next2Tabs.add(BOARDS.get(indexOfBoardTypeInfo - 2));
	    } else {
		next2Tabs.add(BOARDS.get(indexOfBoardTypeInfo - 1));
		next2Tabs.add(BOARDS.get(indexOfBoardTypeInfo + 1));
	    }
	}

	return next2Tabs;
    }

}
