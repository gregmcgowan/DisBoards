package com.drownedinsound.events;

public class SentNewPostEvent {

    private SentNewPostState state;

    public SentNewPostEvent(SentNewPostState state) {
        this.state = state;
    }

    public SentNewPostState getState() {
        return state;
    }

    public static enum SentNewPostState {
        SENT, CONFIRMED, FAILED
    }

}
