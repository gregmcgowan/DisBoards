package com.drownedinsound.ui.post;

import com.drownedinsound.core.SingleIn;

import dagger.Subcomponent;

/**
 * Created by gregmcgowan on 28/04/2016.
 */
@SingleIn(BoardPostComponent.class)
@Subcomponent()
public interface BoardPostComponent {

    void inject(BoardPostFragment boardPostFragment);

    //TODO move to own controller
    void inject(AddCommentActivity addCommentActivity);
    void inject(AddCommentFragment addCommentFragment);
}
