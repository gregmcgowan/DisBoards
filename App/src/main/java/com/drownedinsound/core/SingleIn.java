package com.drownedinsound.core;

import javax.inject.Scope;

/**
 * Created by gregmcgowan on 28/04/2016.
 */
@Scope
public @interface SingleIn { Class<?> value();}
