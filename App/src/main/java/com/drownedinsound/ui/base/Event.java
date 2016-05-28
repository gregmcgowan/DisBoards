package com.drownedinsound.ui.base;

/**
 * Created by gregmcgowan on 19/05/2016.
 */
public class Event<T> {

    private T data;
    private Status status;
    private Throwable error;


    public Event(T data, Status status, Throwable error) {
        this.data = data;
        this.status = status;
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public Status getStatus() {
        return status;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Event<?> event = (Event<?>) o;

        if (data != null ? !data.equals(event.data) : event.data != null) {
            return false;
        }
        if (status != event.status) {
            return false;
        }
        return error != null ? error.equals(event.error) : event.error == null;

    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }
}
