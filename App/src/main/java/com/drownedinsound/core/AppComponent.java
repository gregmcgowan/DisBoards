package com.drownedinsound.core;

import com.drownedinsound.data.NetworkModule;
import com.drownedinsound.data.PersistenceModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by gregmcgowan on 24/04/2016.
 */
@Singleton
@Component(modules = {DisBoardsAppModule.class, NetworkModule.class, PersistenceModule.class})
public interface AppComponent extends CoreAppGraph {


    /*
    * * An initializer that creates the graph from an application.
   */
    final class Initialiser {

        public static AppComponent init(DisBoardsApp app) {
            return DaggerAppComponent.builder()
                    .disBoardsAppModule(new DisBoardsAppModule(app))
                    .networkModule(new NetworkModule())
                    .persistenceModule(new PersistenceModule())
                    .build();
        }

        private Initialiser() {
        } // No instances.
    }
}
