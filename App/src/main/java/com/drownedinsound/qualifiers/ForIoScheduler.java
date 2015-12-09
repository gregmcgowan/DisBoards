package com.drownedinsound.qualifiers;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by gregmcgowan on 02/12/14.
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ForIoScheduler {

}
