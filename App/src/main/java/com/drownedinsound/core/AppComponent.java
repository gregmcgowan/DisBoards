package com.drownedinsound.core;

import com.drownedinsound.data.NetworkModule;
import com.drownedinsound.data.PersistenceModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DisBoardsAppModule.class, NetworkModule.class, PersistenceModule.class})
interface AppComponent extends CoreAppGraph {


    final class Initialiser {

        static AppComponent init(DisBoardsApp app) {
            return DaggerAppComponent.builder()
                    .disBoardsAppModule(new DisBoardsAppModule(app))
                    .networkModule(new NetworkModule())
                    .persistenceModule(new PersistenceModule())
                    .build();
        }

        private Initialiser() {
        }
    }
}
