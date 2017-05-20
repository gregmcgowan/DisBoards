package com.drownedinsound.core;

import com.drownedinsound.data.DataModule;
import com.drownedinsound.data.ProdDataModule;

import dagger.Subcomponent;

/**
 * Created by gregmcgowan on 04/05/2016.
 */
@SingleIn(SessionComponent.class)
@Subcomponent(modules = {DataModule.class, ProdDataModule.class})
public interface SessionComponent extends BaseSessionComponent {

}
