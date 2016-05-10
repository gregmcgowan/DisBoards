package com.drownedinsound.ui.postList;

import com.drownedinsound.core.SingleIn;
import com.drownedinsound.ui.addPost.AddPostActivity;
import com.drownedinsound.ui.addPost.AddPostFragment;

import dagger.Subcomponent;

/**
 * Created by gregmcgowan on 27/04/2016.
 */
@SingleIn(BoardPostListFragmentComponent.class)
@Subcomponent()
public interface BoardPostListFragmentComponent {

    void inject(BoardPostListFragment boardPostListFragment);



}
