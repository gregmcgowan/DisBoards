package com.drownedinsound.ui.postList;

import com.drownedinsound.core.SingleIn;

import dagger.Subcomponent;

/**
 * Created by gregmcgowan on 02/05/2016.
 */
@SingleIn(BoardPostListParentComponent.class)
@Subcomponent
public interface BoardPostListParentComponent {

    void inject(BoardPostListParentActivity boardPostListParentActivity);
}
