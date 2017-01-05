package com.drownedinsound.core;

import com.drownedinsound.ui.post.BoardPostComponent;
import com.drownedinsound.ui.postList.BoardPostListFragmentComponent;
import com.drownedinsound.ui.postList.BoardPostListParentComponent;

/**
 * Created by gregmcgowan on 04/05/2016.
 */
public interface BaseSessionComponet {

    BoardPostListParentComponent boardPostListParentComponent();

    BoardPostListFragmentComponent boardPostListComponent();

    BoardPostComponent boardPostComponent();
}
