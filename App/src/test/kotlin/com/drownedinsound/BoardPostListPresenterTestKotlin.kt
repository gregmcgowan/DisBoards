package com.drownedinsound

import com.drownedinsound.data.DisBoardRepo
import com.drownedinsound.data.generatered.BoardPost
import com.drownedinsound.data.generatered.BoardPostSummary
import com.drownedinsound.ui.base.Navigator
import com.drownedinsound.ui.post.BoardPostContract
import com.drownedinsound.ui.post.BoardPostPresenter
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.annotations.Fixture
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import rx.Observable.just
import rx.Scheduler
import rx.schedulers.Schedulers


class BoardPostListPresenterTestKotlin {

    private val disBoardRepo: DisBoardRepo = mock()
    private val navigator: Navigator = mock()
    private val expectedBoardPost: BoardPost = mock()
    private val mockView: BoardPostContract.View = mock()

    private val sheduler: Scheduler = Schedulers.immediate()

    @Fixture lateinit var boardPostSummary: BoardPostSummary
    @Fixture lateinit var boardPostId: String
    @Fixture lateinit var boardType: String

    private lateinit var sut: BoardPostPresenter

    @Before
    fun setup() {
        FixtureAnnotations.initFixtures(this)
        sut = BoardPostPresenter(boardPostId, boardType, mockView, navigator, sheduler, sheduler, disBoardRepo)
    }


    @Test
    fun getPostSuccessfully() {
        whenever(disBoardRepo.getBoardPost(boardType, boardPostId, false)).thenReturn(
                just<BoardPost>(expectedBoardPost))

        whenever(disBoardRepo.getBoardPostSummary(boardType, boardPostId))
                .thenReturn(just(boardPostSummary))

        sut.onViewCreated()

        verify(mockView).showLoadingProgress(true)
        verify(mockView).showBoardPost(expectedBoardPost)
        verify(mockView).showLoadingProgress(false)
    }
}
