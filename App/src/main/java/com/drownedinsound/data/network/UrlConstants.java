package com.drownedinsound.data.network;

import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.model.BoardListTypes;
import com.drownedinsound.utils.StringUtils;

import android.text.TextUtils;

public class UrlConstants {

    public static final String MUSIC_BOARD_NAME = "music";

    public static final String SOCIAL_BOARD_NAME = "social";

    public static final String ANNOUNCEMENTS_BOARD_NAME = "announcements";

    public static final String MUSICIANS_BOARD_NAME = "musicians";

    public static final String FESTIVALS_BOARD_NAME = "festivals";

    public static final String YOUR_MUSIC_BOARD_NAME = "your_music";

    public static final String ERRORS_SUGGESTIONS_BOARD_NAME = "errors";

    public final static String BASE_URL = "http://drownedinsound.com/";

    public final static String LOGIN_URL = BASE_URL + "session/create";

    public final static String LOGIN_PATH = "session/create";

    public static final String BOARD_BASE_PATH = "community/boards/";

    public final static String BOARD_BASE_URL = BASE_URL + BOARD_BASE_PATH;

    public final static String MUSIC_URL = BOARD_BASE_URL + MUSIC_BOARD_NAME;

    public final static String SOCIAL_URL = BOARD_BASE_URL + SOCIAL_BOARD_NAME;


    public final static String ANNOUNCEMENTS_CLASSIFIEDS_URL = BOARD_BASE_URL
            + ANNOUNCEMENTS_BOARD_NAME;

    public final static String MUSICIANS_URL = BOARD_BASE_URL + MUSICIANS_BOARD_NAME;

    public final static String FESTIVALS_URL = BOARD_BASE_URL + FESTIVALS_BOARD_NAME;

    public final static String YOUR_MUSIC_URL = BOARD_BASE_URL + YOUR_MUSIC_BOARD_NAME;

    public final static String ERRORS_SUGGESTIONS_URL = BOARD_BASE_URL
            + ERRORS_SUGGESTIONS_BOARD_NAME;

    public final static String COMMENTS_URL = BASE_URL + "/comments";

    public final static String NEW_POST_URL = "http://drownedinsound.com/topics";


    public static @BoardPostList.BoardPostListType String getBoardType(String url) {
        @BoardPostList.BoardPostListType String boardListType = null;
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("www.")) {
                url = url.replace("www.", "");
            }
            if (url.startsWith(UrlConstants.MUSIC_URL)) {
                boardListType = BoardListTypes.MUSIC;
            } else if (url.startsWith(UrlConstants.SOCIAL_URL)) {
                boardListType = BoardListTypes.SOCIAL;
            } else if (url.startsWith(UrlConstants.ANNOUNCEMENTS_CLASSIFIEDS_URL)) {
                boardListType = BoardListTypes.ANNOUNCEMENTS_CLASSIFIEDS;
            } else if (url.startsWith(UrlConstants.MUSICIANS_URL)) {
                boardListType = BoardListTypes.MUSICIANS;
            } else if (url.startsWith(UrlConstants.FESTIVALS_URL)) {
                boardListType = BoardListTypes.FESTIVALS;
            } else if (url.startsWith(UrlConstants.YOUR_MUSIC_URL)) {
                boardListType = BoardListTypes.YOUR_MUSIC;
            } else if (url.startsWith(UrlConstants.ERRORS_SUGGESTIONS_URL)) {
                boardListType = BoardListTypes.ERRORS_SUGGESTIONS;
            }
        }
        return boardListType;
    }

    public static String getBoardPostListUrl(String baseUrl, @BoardPostList.BoardPostListType String boardListType) {
        StringBuilder urlBuilder =  new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append(BOARD_BASE_PATH);

        if(BoardListTypes.MUSIC.equals(boardListType)) {
            urlBuilder.append(MUSIC_BOARD_NAME);
            urlBuilder.append("/");
        } else if (BoardListTypes.SOCIAL.equals(boardListType)) {
            urlBuilder.append(SOCIAL_BOARD_NAME);
            urlBuilder.append("/");
        } else if (BoardListTypes.ANNOUNCEMENTS_CLASSIFIEDS.equals(boardListType)) {
            urlBuilder.append(ANNOUNCEMENTS_BOARD_NAME);
            urlBuilder.append("/");
        } else if (BoardListTypes.MUSICIANS.equals(boardListType)) {
            urlBuilder.append(MUSICIANS_BOARD_NAME);
            urlBuilder.append("/");
        } else if (BoardListTypes.FESTIVALS.equals(boardListType)) {
            urlBuilder.append(FESTIVALS_BOARD_NAME);
            urlBuilder.append("/");
        } else if (BoardListTypes.ERRORS_SUGGESTIONS.equals(boardListType)) {
            urlBuilder.append(ERRORS_SUGGESTIONS_BOARD_NAME);
            urlBuilder.append("/");
        }

        return urlBuilder.toString();
    }

    public static String getBoardPostUrl(String baseUrl, @BoardPostList.BoardPostListType String boardListType,
            String boardPostId) {
        String boardPostUrl = getBoardPostListUrl(baseUrl,boardListType);
        if(!StringUtils.isEmpty(boardPostUrl)) {
            boardPostUrl += boardPostId;
        }
        return boardPostUrl;
    }
}
