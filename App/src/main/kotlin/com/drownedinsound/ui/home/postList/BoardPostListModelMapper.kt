package com.drownedinsound.ui.home.postList

import com.drownedinsound.BoardPostListModel
import com.drownedinsound.data.generatered.BoardPostSummary
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class BoardPostListModelMapper @Inject constructor() {

    fun map(boardPostSummaries: List<BoardPostSummary>): List<BoardPostListModel> {
        return boardPostSummaries
                .mapIndexedTo(mutableListOf()) { index, summary -> boardPostListModel(summary, index) }
                .toImmutableList()
    }

    private fun boardPostListModel(summary: BoardPostSummary, index: Int): BoardPostListModel {
        val title = summary.title
        val username = summary.authorUsername
        val authorise = "by $username"
        val numberOfReplies = summary.numberOfReplies
        val numberOfRepliesText: String
        if (numberOfReplies > 0) {
            if (numberOfReplies > 1) {
                numberOfRepliesText = "$numberOfReplies replies "
            } else {
                numberOfRepliesText = "1 reply"
            }

        } else {
            numberOfRepliesText = "No replies"
        }
        val lastUpdatedText = summary.lastUpdatedInReadableString
        val lastViewedTime = summary.lastViewedTime
        val lastUpdatedTime = summary.lastUpdatedTime
        val isSticky = summary.isSticky
        val markAsRead = lastViewedTime > 0 && lastViewedTime >= lastUpdatedTime

        return BoardPostListModel(index, title, authorise,
                numberOfRepliesText, lastUpdatedText, markAsRead, isSticky)

    }

}