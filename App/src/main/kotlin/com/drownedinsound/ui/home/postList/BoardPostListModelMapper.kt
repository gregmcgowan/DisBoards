package com.drownedinsound.ui.home.postList

import com.drownedinsound.BoardPostSummaryModel
import com.drownedinsound.data.generatered.BoardPostSummary
import com.drownedinsound.fromHtml
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class BoardPostListModelMapper @Inject constructor() {

    fun map(boardPostSummaries: List<BoardPostSummary>): List<BoardPostSummaryModel> {
        return boardPostSummaries
                .mapIndexedTo(mutableListOf()) { index, summary -> boardPostListModel(summary, index) }
                .toImmutableList()
    }

    private fun boardPostListModel(summary: BoardPostSummary, index: Int): BoardPostSummaryModel {
        val numberOfReplies = summary.numberOfReplies
        //TODO plurals
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

        val markAsRead = summary.lastViewedTime > 0
                && summary.lastViewedTime >= summary.lastUpdatedTime
        val userName = summary.authorUsername

        return BoardPostSummaryModel(
                index = index,
                title = summary.title?.fromHtml(),
                authorUsername = "by $userName",
                numberOfRepliesText = numberOfRepliesText,
                lastUpdatedText = summary.lastUpdatedInReadableString,
                markAsRead = markAsRead,
                isSticky = summary.isSticky)

    }

}