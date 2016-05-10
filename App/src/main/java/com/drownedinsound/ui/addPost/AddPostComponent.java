package com.drownedinsound.ui.addPost;

import com.drownedinsound.core.SingleIn;

import dagger.Subcomponent;

/**
 * Created by gregmcgowan on 10/05/2016.
 */
@SingleIn(AddPostComponent.class)
@Subcomponent()
public interface AddPostComponent {

    void inject(AddPostFragment addPostFragment);
}
