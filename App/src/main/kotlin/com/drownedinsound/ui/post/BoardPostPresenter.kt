package com.drownedinsound.ui.post

import com.drownedinsound.BoardPostItem
import com.drownedinsound.data.DisBoardRepo
import com.drownedinsound.ui.base.Navigator
import rx.Scheduler
import rx.Subscription
import timber.log.Timber

class BoardPostPresenter(
        private val boardPostId: String,
        private val boardListType: String,
        private val boardPostView: BoardPostContract.View,
        private val navigator: Navigator,
        private val mainThreadScheduler: Scheduler,
        private val backgroundThreadScheduler: Scheduler,
        private val disBoardRepo: DisBoardRepo,
        private val boardPostModelMapper: BoardPostModelMapper) : BoardPostContract.Presenter {

    private var loadBoardPostSubscription: Subscription? = null

    override fun onViewCreated() {
        boardPostView.setPresenter(this)
        loadBoardPost(false)
    }

    private fun loadBoardPost(forceUpdate: Boolean) {
        boardPostView.showLoadingProgress(true)
        loadBoardPostSubscription =
                disBoardRepo.getBoardPost(boardListType, boardPostId, forceUpdate)
                        .map(boardPostModelMapper::map)
                        .subscribeOn(backgroundThreadScheduler)
                        .observeOn(mainThreadScheduler)
                        .subscribe(this::displayItems, this::handleError)
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable,"Error getting board post")
        boardPostView.showErrorView()
        boardPostView.showLoadingProgress(false)
    }

    private fun displayItems(items: List<BoardPostItem>) {
        boardPostView.showBoardPostItems(items)
        boardPostView.showLoadingProgress(false)
    }

    override fun onViewDestroyed() {
        loadBoardPostSubscription?.unsubscribe()
    }

    override fun handleBackAction() = navigator.hideCurrentScreen()

    override fun handleRefreshAction() {
        if(loadBoardPostSubscription ==  null
                || loadBoardPostSubscription!!.isUnsubscribed) {
            loadBoardPost(true)
        }
    }
}