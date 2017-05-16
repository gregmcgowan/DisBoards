package com.drownedinsound.ui.home

import com.drownedinsound.data.DisBoardRepo
import com.drownedinsound.qualifiers.ForIoScheduler
import com.drownedinsound.qualifiers.ForMainThreadScheduler
import com.drownedinsound.ui.home.postList.BoardPostListContract
import rx.Scheduler
import java.util.*

class HomeScreenPresenter(
        private val homeScreenView: HomeScreenContract.View,
        private val disBoardRepo: DisBoardRepo,
        @param:ForMainThreadScheduler private val mainThreadScheduler: Scheduler,
        @param:ForIoScheduler private val backgroundThreadScheduler: Scheduler) : HomeScreenContract.Presenter {

    private val listPresenterMap = HashMap<String, BoardPostListContract.Presenter>()

    override fun onViewCreated() {
        getAllBoardPostLists()
    }

    private fun getAllBoardPostLists() {
        disBoardRepo.allBoardPostLists
                .subscribeOn(backgroundThreadScheduler)
                .observeOn(mainThreadScheduler)
                .subscribe(homeScreenView::showBoardPostLists)
    }

    override fun addBoardListPresenter(type: String, presenter: BoardPostListContract.Presenter) {
        listPresenterMap.put(type, presenter)
        presenter.onViewCreated()
    }

    override fun removeBoardPostListView(type: String) {
        listPresenterMap.remove(type)?.onViewDestroyed()
    }

    override fun handleListDisplayed(type: String) {
        listPresenterMap[type]?.onViewDisplayed()
    }

    override fun handlePageTabReselected(position: Int) {

    }
}