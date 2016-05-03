package com.drownedinsound.core;

import com.drownedinsound.data.DataModule;
import com.drownedinsound.ui.post.BoardPostComponent;
import com.drownedinsound.ui.postList.BoardPostListFragmentComponent;
import com.drownedinsound.ui.postList.BoardPostListParentComponent;
import com.drownedinsound.ui.start.LoginComponent;
import com.drownedinsound.ui.start.StartActivity;

import dagger.Subcomponent;

/**
 * Created by gregmcgowan on 24/04/2016.
 */
@SingleIn(SessionComponent.class)
@Subcomponent(modules = {DataModule.class})
public interface SessionComponent {

    LoginComponent loginComponent();

    BoardPostListParentComponent boardPostListParentComponent();

    BoardPostListFragmentComponent boardPostListComponent();

    BoardPostComponent boardPostComponent();

    void inject(StartActivity startActivity);
}
