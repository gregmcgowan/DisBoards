package com.drownedinsound.qualifiers;

/**
 * Created by gregmcgowan on 09/12/15.
 *
 */

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;


/**
 * Created by gregmcgowan on 09/12/15.
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ForMainThreadScheduler {

}
