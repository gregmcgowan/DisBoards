package com.drownedinsound.ui.start;

import com.drownedinsound.core.SingleIn;

import dagger.Subcomponent;

/**
 * Created by gregmcgowan on 24/04/2016.
 */
@SingleIn(LoginComponent.class)
@Subcomponent()
public interface LoginComponent {

    void inject(LoginActivity loginActivity);
}
