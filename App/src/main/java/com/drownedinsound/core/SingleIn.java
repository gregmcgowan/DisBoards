package com.drownedinsound.core;

import javax.inject.Scope;

@Scope
public @interface SingleIn { Class<?> value();}
