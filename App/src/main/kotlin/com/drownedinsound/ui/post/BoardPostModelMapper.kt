package com.drownedinsound.ui.post

import android.text.TextUtils
import com.drownedinsound.*
import com.drownedinsound.data.generatered.BoardPost
import com.drownedinsound.data.generatered.BoardPostComment
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class BoardPostModelMapper @Inject constructor() {

    fun map(boardPost: BoardPost): List<BoardPostItem> {
        val boardPostItems: MutableList<BoardPostItem> = mutableListOf()

        boardPostItems.add(createInitialPostModel(boardPost))

        return boardPost.comments.mapTo(boardPostItems) { createReplyCommentModel(it) }
                .toImmutableList()
    }

    private fun createInitialPostModel(boardPost: BoardPost): InitialComment {
        val commentInfo = Comment(
                title = boardPost.title,
                author = boardPost.authorUsername.trim(),
                content = boardPost.content?.fromHtml() ?: "",
                dateAndTime = boardPost.dateOfPost)

        val numberOfRepliesText: String
        val numberOfReplies = boardPost.numberOfReplies
        if (numberOfReplies > 0) {
            if (numberOfReplies > 1) {
                numberOfRepliesText = "$numberOfReplies replies "
            } else {
                numberOfRepliesText = "1 reply"
            }
        } else {
            numberOfRepliesText = "No replies"
        }
        return InitialComment(commentInfo, numberOfRepliesText)
    }

    private fun createReplyCommentModel(comment: BoardPostComment): ReplyComment {
        var author = comment.authorUsername
        val replyTo = comment.replyToUsername
        if (!TextUtils.isEmpty(replyTo)) {
            author = author + "\n" + "@ " + replyTo
        }
        val replyComment = ReplyComment(
                Comment(title = comment.title?.fromHtml(),
                        author = author,
                        content = comment.content?.fromHtml() ?: "",
                        dateAndTime = comment.dateAndTime),
                comment.usersWhoHaveThissed,
                comment.commentLevel);
        return replyComment
    }
}
