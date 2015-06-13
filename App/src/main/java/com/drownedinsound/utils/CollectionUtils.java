package com.drownedinsound.utils;

import java.util.Collection;

/**
 * Created by gregmcgowan on 05/04/15.
 */
public class CollectionUtils {

    public static boolean equals(Collection<?> listA, Collection<?> listB) {
        boolean equals = false;
        if (listA != null && listB != null) {
            equals = listA.containsAll(listB) && listB.containsAll(listA);
        }
        return equals;
    }
}
