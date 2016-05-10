package com.drownedinsound.ui.addComment;

import com.drownedinsound.core.SingleIn;

import dagger.Subcomponent;

/**
 * Created by gregmcgowan on 10/05/2016.
 */
@SingleIn(AddCommentComponent.class)
@Subcomponent()
public interface AddCommentComponent {

    void inject(AddCommentFragment addCommentFragment);
}
