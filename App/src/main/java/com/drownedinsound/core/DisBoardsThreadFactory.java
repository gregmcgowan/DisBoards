package com.drownedinsound.core;

import android.text.TextUtils;

import java.util.concurrent.ThreadFactory;

public class DisBoardsThreadFactory implements ThreadFactory {

    private String name;

    public DisBoardsThreadFactory(String threadName) {
        this.name = threadName;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread newThread = null;
        if (!TextUtils.isEmpty(name)) {
            newThread = new Thread(r, name);
        } else {
            newThread = new Thread(r);
        }
        return newThread;
    }

}
