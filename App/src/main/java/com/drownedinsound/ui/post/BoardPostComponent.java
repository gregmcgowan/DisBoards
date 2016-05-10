package com.drownedinsound.ui.post;

import com.drownedinsound.core.SingleIn;
import com.drownedinsound.ui.addComment.AddCommentActivity;
import com.drownedinsound.ui.addComment.AddCommentFragment;

import dagger.Subcomponent;

/**
 * Created by gregmcgowan on 28/04/2016.
 */
@SingleIn(BoardPostComponent.class)
@Subcomponent()
public interface BoardPostComponent {

    void inject(BoardPostFragment boardPostFragment);

}
