package com.drownedinsound.data;

import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.core.SingleIn;
import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.parser.streaming.BoardPostParser;
import com.drownedinsound.data.parser.streaming.BoardPostSummaryListParser;
import com.drownedinsound.data.parser.streaming.DisWebPageParser;

import dagger.Module;
import dagger.Provides;

/**
 * Created by gregmcgowan on 06/12/15.
 */
@Module(includes = {})
public class MockDataModule {

    @Provides
    @SingleIn(SessionComponent.class)
    DisWebPageParser disWebPageParser(BoardPostParser boardPostParser,
            BoardPostSummaryListParser boardPostSummaryListParser) {
        return new MockDisWebPageParser();
    }

    @Provides
    @SingleIn(SessionComponent.class)
    DisBoardRepo provideDisBoardRepo(DisApiClient disApiClient,
            DisBoardsLocalRepo disBoardsLocalRepo) {
        return new MockDisRepo();
    }

}
