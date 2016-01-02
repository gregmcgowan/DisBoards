package com.drownedinsound.test.login;

import com.drownedinsound.data.DataModule;
import com.drownedinsound.data.DisBoardRepo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by gregmcgowan on 06/12/15.
 */
@Module(
        overrides = true,
        complete = false,
        includes = {DataModule.class}
)
public class FakeDataModule {

    @Provides
    @Singleton
    DisBoardRepo provideDisBoardRepo(){
        return new FakeDisRepo();
    }

}
