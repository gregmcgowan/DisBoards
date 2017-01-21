package com.drownedinsound.ui.post;

import com.drownedinsound.core.SingleIn;

import dagger.Subcomponent;


@SingleIn(BoardPostComponent.class)
@Subcomponent(modules = BoardPostModule.class)
public interface BoardPostComponent {

    void inject(BoardPostActivity boardPostActivity);

}
