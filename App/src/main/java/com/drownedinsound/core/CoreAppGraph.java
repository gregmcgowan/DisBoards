package com.drownedinsound.core;

import com.drownedinsound.data.DataModule;

interface CoreAppGraph {

    void inject(DisBoardsApp disBoardsApp);

    SessionComponent provideSessionComponent(DataModule dataModule);
}
