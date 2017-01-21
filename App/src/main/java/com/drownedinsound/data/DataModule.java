package com.drownedinsound.data;

import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.core.SingleIn;
import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.database.DisBoardsLocalRepoImpl;
import com.drownedinsound.data.generatered.DaoMaster;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.network.NetworkUtil;
import com.drownedinsound.data.parser.streaming.BoardPostParser;
import com.drownedinsound.data.parser.streaming.BoardPostSummaryListParser;
import com.drownedinsound.data.parser.streaming.DisWebPageParser;


import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module(includes = {})
public class DataModule {

    @Provides
    @SingleIn(SessionComponent.class)
    BoardPostSummaryListParser postSummaryListParser() {
        return new BoardPostSummaryListParser();
    }

    @Provides
    @SingleIn(SessionComponent.class)
    BoardPostParser provideBoardPostParser() {
        return new BoardPostParser();
    }
    @Provides
    @SingleIn(SessionComponent.class)
    DisBoardsLocalRepo disBoardsLocalRepo(DaoMaster daoMaster) {
        return new DisBoardsLocalRepoImpl(daoMaster.newSession());
    }

    @Provides
    @SingleIn(SessionComponent.class)
    DisApiClient disApiClient(OkHttpClient okHttpClient, NetworkUtil networkUtil,
            DisWebPageParser disWebPageParser) {
        return new DisApiClient(okHttpClient, networkUtil, disWebPageParser);
    }
}
