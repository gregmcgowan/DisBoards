package com.drownedinsound.core;

import com.drownedinsound.ui.post.BoardPostComponent;
import com.drownedinsound.ui.postList.BoardPostListFragmentComponent;
import com.drownedinsound.ui.postList.BoardPostListParentComponent;
import com.drownedinsound.ui.start.LoginComponent;
import com.drownedinsound.ui.start.StartActivity;

/**
 * Created by gregmcgowan on 04/05/2016.
 */
public interface BaseSessionComponet {

    LoginComponent loginComponent();

    BoardPostListParentComponent boardPostListParentComponent();

    BoardPostListFragmentComponent boardPostListComponent();

    BoardPostComponent boardPostComponent();

    void inject(StartActivity startActivity);
}
