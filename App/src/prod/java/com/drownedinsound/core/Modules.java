package com.drownedinsound.core;

/**
 * Created by gregmcgowan on 20/07/2014.
 */
public class Modules {

    public static Object[] list(DisBoardsApp disBoardsApp) {
        return new Object[]{new DisBoardsAppModule(disBoardsApp)};
    }

}
