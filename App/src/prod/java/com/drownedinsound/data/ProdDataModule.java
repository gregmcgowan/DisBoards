package com.drownedinsound.data;

import com.drownedinsound.core.SessionComponent;
import com.drownedinsound.core.SingleIn;
import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.network.DisApiClient;
import com.drownedinsound.data.parser.streaming.BoardPostParser;
import com.drownedinsound.data.parser.streaming.BoardPostSummaryListParser;
import com.drownedinsound.data.parser.streaming.DisWebPageParser;
import com.drownedinsound.data.parser.streaming.DisWebPagerParserImpl;

import dagger.Module;
import dagger.Provides;

@Module(includes = {})
public class ProdDataModule {


    @Provides
    @SingleIn(SessionComponent.class)
    DisWebPageParser disWebPageParser(BoardPostParser boardPostParser,
            BoardPostSummaryListParser boardPostSummaryListParser) {
        return new DisWebPagerParserImpl(boardPostParser, boardPostSummaryListParser);
    }

    @Provides
    @SingleIn(SessionComponent.class)
    DisBoardRepo provideDisBoardRepo(DisApiClient disApiClient, DisBoardsLocalRepo disBoardsLocalRepo) {
        return new DisBoardRepoImpl(disApiClient, disBoardsLocalRepo);
    }

    @Provides
    @SingleIn(SessionComponent.class)
    DisRepo2 provideDisBoardRepo2(DisApiClient disApiClient, DisBoardsLocalRepo disBoardsLocalRepo) {
        return new DisBoardRepoImpl2(disApiClient, disBoardsLocalRepo);

    }
}