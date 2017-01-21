package com.drownedinsound.test;

import com.drownedinsound.data.database.DisBoardsLocalRepo;
import com.drownedinsound.data.network.DisApiClient;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;


/**
 * Created by gregmcgowan on 19/05/2016.
 */
public class DisBoardRepo2Test {

    @Mock
    DisApiClient disApiClient;

    @Mock
    DisBoardsLocalRepo disBoardsLocalRepo;

    private DisRepo2 disRepo2;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        disRepo2 = new DisBoardRepoImpl2(disApiClient,disBoardsLocalRepo);
    }

}
