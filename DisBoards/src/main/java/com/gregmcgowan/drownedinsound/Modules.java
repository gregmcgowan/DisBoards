package com.gregmcgowan.drownedinsound;

/**
 * Created by gregmcgowan on 20/07/2014.
 */
public class Modules {

    public static Object[] list(DisBoardsApp disBoardsApp){
        return new Object[] {new DisBoardsModule(disBoardsApp)};
    }

}
