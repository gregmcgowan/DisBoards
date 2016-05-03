package com.drownedinsound.core;

import com.drownedinsound.data.DataModule;

/**
 * Created by gregmcgowan on 23/04/2016.
 */
public interface CoreAppGraph {

    void inject(DisBoardsApp disBoardsApp);

    SessionComponent provideSessionComponent(DataModule dataModule);
}
