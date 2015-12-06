package com.drownedinsound.test.login;

import com.drownedinsound.data.DataModule;
import com.drownedinsound.data.DisBoardRepo;
import com.drownedinsound.data.database.DatabaseService;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.network.handlers.LoginResponseHandler;
import com.drownedinsound.data.network.handlers.NewPostHandler;
import com.drownedinsound.data.network.handlers.PostACommentHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardPostHandler;
import com.drownedinsound.data.network.handlers.RetrieveBoardSummaryListHandler;
import com.drownedinsound.data.network.handlers.ThisACommentHandler;
import com.drownedinsound.ui.post.BoardPostActivity;
import com.drownedinsound.ui.post.BoardPostFragment;
import com.drownedinsound.ui.post.PostReplyActivity;
import com.drownedinsound.ui.postList.BoardPostListFragment;
import com.drownedinsound.ui.postList.BoardPostListParentActivity;
import com.drownedinsound.ui.postList.NewPostFragment;
import com.drownedinsound.ui.start.LoginActivity;
import com.drownedinsound.ui.start.StartActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by gregmcgowan on 06/12/15.
 */
@Module(
        overrides = true,
        complete = false,
        includes = {DataModule.class}
)
public class FakeDataModule {

    @Provides
    @Singleton
    DisBoardRepo provideDisBoardRepo(){
        return new FakeDisRepo();
    }

}
