package com.drownedinsound.events;

/**
 * Created by gregmcgowan on 15/11/15.
 */
public class PostCommentEvent {

    private int uiID;
    private boolean success;

    public PostCommentEvent(int uiID, boolean success) {
        this.uiID = uiID;
        this.success = success;
    }

    public int getUiID() {
        return uiID;
    }

    public boolean isSuccess() {
        return success;
    }
}
