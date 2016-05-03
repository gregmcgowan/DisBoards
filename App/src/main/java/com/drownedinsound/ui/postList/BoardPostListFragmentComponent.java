package com.drownedinsound.ui.postList;

import com.drownedinsound.core.SingleIn;

import dagger.Subcomponent;

/**
 * Created by gregmcgowan on 27/04/2016.
 */
@SingleIn(BoardPostListFragmentComponent.class)
@Subcomponent()
public interface BoardPostListFragmentComponent {

    void inject(BoardPostListFragment boardPostListFragment);


    //TODO move to own controller
    void inject(AddPostActivity addPostActivity);
    void inject(AddPostFragment addPostFragment);
}
