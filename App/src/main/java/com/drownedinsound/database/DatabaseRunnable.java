package com.drownedinsound.database;

/**
 * Created by gregmcgowan on 02/12/14.
 */
public abstract class DatabaseRunnable implements Runnable {

    protected DatabaseHelper dbHelper;


    public DatabaseRunnable(DatabaseHelper databaseHelper) {
        this.dbHelper = databaseHelper;
    }


}
