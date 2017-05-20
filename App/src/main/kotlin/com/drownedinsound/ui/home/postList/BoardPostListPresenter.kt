package com.drownedinsound.ui.home.postList

import com.drownedinsound.BoardPostSummaryModel
import com.drownedinsound.core.DisBoardsAppModule
import com.drownedinsound.data.DisBoardRepo
import com.drownedinsound.data.generatered.BoardPostList
import com.drownedinsound.data.generatered.BoardPostSummary
import com.drownedinsound.ui.base.Navigator
import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import rx.Scheduler
import rx.Subscription
import timber.log.Timber
import java.util.*
import javax.inject.Named

@AutoFactory class BoardPostListPresenter(
        private val boardListView: BoardPostListContract.View,
        private val navigator: Navigator,
        @param:Provided private val boardPostMapper: BoardPostListModelMapper,
        @param:Provided private val disBoardRepo: DisBoardRepo,
        @param:Provided @param:Named(DisBoardsAppModule.MAIN_THREAD_SCHEDULER) private val mainThreadScheduler: Scheduler,
        @param:Provided @param:Named(DisBoardsAppModule.BACKGROUND_THREAD_SCHEDULER) private val backgroundThreadScheduler: Scheduler)
    : BoardPostListContract.Presenter {

    private var loadingSubscription: Subscription? = null
    private var boardPostSummaries: List<BoardPostSummary> = listOf()

    override fun onViewCreated() {
        loadList(false)
        boardListView.setPresenter(this)
    }

    override fun onViewDisplayed() = Unit

    private fun loadList(forceUpdate: Boolean) {
        if (loadingSubscription == null) {
            boardListView.showLoadingProgress(true)

            loadingSubscription = disBoardRepo
                    .getBoardPostSummaryList(boardListView.boardListType, 0, forceUpdate)
                    .map { boardPostSummaries -> storeAndMap(boardPostSummaries) }
                    .subscribeOn(backgroundThreadScheduler)
                    .observeOn(mainThreadScheduler)
                    .subscribe({ items -> handleItems(items) },
                            { error -> handleError(error) })
        }
    }

    private fun handleItems(boardPostSummaries: List<BoardPostSummaryModel>) {
        boardListView.showBoardPostSummaries(boardPostSummaries)
        boardListView.showLoadingProgress(false)
    }

    private fun handleError(e: Throwable) {
        Timber.d(e, "Error")
        boardListView.showLoadingProgress(false)
        boardListView.showErrorView()
    }

    private fun storeAndMap(boardPostSummaries: MutableList<BoardPostSummary>): List<BoardPostSummaryModel> {
        this.boardPostSummaries = boardPostSummaries
        return boardPostMapper.map(boardPostSummaries)
    }

    private fun stopLoading() {
        loadingSubscription?.unsubscribe()
        loadingSubscription = null
        boardListView.showLoadingProgress(false)
    }

    override fun onViewDestroyed() = stopLoading()

    override fun handleRefresh() = loadList(true)

    override fun handleBoardPostSelected(boardPostSummaryModel: BoardPostSummaryModel) {
        val summary = boardPostSummaries[boardPostSummaryModel.index]
        @BoardPostList.BoardPostListType val boardListType = summary.boardListTypeID
        summary.lastViewedTime = Date().time
        summary.numberOfTimesOpened = summary.numberOfTimesOpened + 1

        disBoardRepo.setBoardPostSummary(summary)
                .subscribeOn(backgroundThreadScheduler)
                .subscribe()

        navigator.showBoardPostScreen(boardListType, summary.boardPostID)
    }
}
