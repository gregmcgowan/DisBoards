package com.drownedinsound.ui.post;

import com.drownedinsound.BordPostCommentListTypeFactory;
import com.drownedinsound.core.SingleIn;
import com.drownedinsound.ui.base.Navigator;

import android.view.View;

import javax.inject.Named;

import dagger.Binds;
import dagger.BindsInstance;
import dagger.Module;
import dagger.Subcomponent;


@SingleIn(BoardPostComponent.class)
@Subcomponent(modules = BoardPostComponent.BoardPostModule.class)
public interface BoardPostComponent {

    void inject(BoardPostActivity boardPostActivity);


    @Module
    interface BoardPostModule {
        @Binds
        BoardPostContract.Presenter providePresenter(BoardPostPresenter boardPostPresenter);

        @Binds
        BoardPostContract.View provideView(BoardPostView boardPostView);

        @Binds
        BordPostCommentListTypeFactory provideTypeFactory(BoardPostTypeFactory boardPostTypeFactory);
    }

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        BoardPostComponent.Builder view(View view);

        @BindsInstance
        BoardPostComponent.Builder postId(@Named("postId") String postId);

        @BindsInstance
        BoardPostComponent.Builder boardPostType(@Named("boardPostType") String type);

        @BindsInstance
        BoardPostComponent.Builder navigator(Navigator navigator);

        BoardPostComponent build();
    }

}
