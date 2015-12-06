package com.drownedinsound.core;

import com.drownedinsound.test.login.FakeDataModule;

/**
 * Created by gregmcgowan on 20/07/2014.
 */
public class Modules {

    public static Object[] list(DisBoardsApp disBoardsApp) {
        return new Object[]{new DisBoardsModule(disBoardsApp),new FakeDataModule()};
    }

}
