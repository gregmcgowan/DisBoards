package com.drownedinsound.ui.home.postList;


import android.support.annotation.NonNull;

public class BoardPostSummaryModelJava {

    public final int index;
    public final String title;
    public final String authorUsername;
    public final String numberOfRepliesText;
    public final String lastUpdatedText;
    public final boolean markedAsRead;
    public final boolean isSticky;

    public BoardPostSummaryModelJava(int index,
            @NonNull String title,
            @NonNull String authorUsername,
            @NonNull String numberOfRepliesText,
            @NonNull String lastUpdatedText,
            boolean markedAsRead,
            boolean isSticky) {
        this.index = index;
        this.title = title;
        this.authorUsername = authorUsername;
        this.numberOfRepliesText = numberOfRepliesText;
        this.lastUpdatedText = lastUpdatedText;
        this.markedAsRead = markedAsRead;
        this.isSticky = isSticky;
    }

    @Override
    public String toString() {
        return "BoardPostSummaryModelJava{" +
                "index=" + index +
                ", title='" + title + '\'' +
                ", authorUsername='" + authorUsername + '\'' +
                ", numberOfRepliesText='" + numberOfRepliesText + '\'' +
                ", lastUpdatedText='" + lastUpdatedText + '\'' +
                ", markedAsRead=" + markedAsRead +
                ", isSticky=" + isSticky +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BoardPostSummaryModelJava that = (BoardPostSummaryModelJava) o;

        if (index != that.index) {
            return false;
        }
        if (markedAsRead != that.markedAsRead) {
            return false;
        }
        if (isSticky != that.isSticky) {
            return false;
        }
        if (!title.equals(that.title)) {
            return false;
        }
        if (!authorUsername.equals(that.authorUsername)) {
            return false;
        }
        if (!numberOfRepliesText.equals(that.numberOfRepliesText)) {
            return false;
        }
        return lastUpdatedText.equals(that.lastUpdatedText);

    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + title.hashCode();
        result = 31 * result + authorUsername.hashCode();
        result = 31 * result + numberOfRepliesText.hashCode();
        result = 31 * result + lastUpdatedText.hashCode();
        result = 31 * result + (markedAsRead ? 1 : 0);
        result = 31 * result + (isSticky ? 1 : 0);
        return result;
    }

    //TODO copy
}
