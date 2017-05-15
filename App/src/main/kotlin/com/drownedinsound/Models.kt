package com.drownedinsound

import android.view.View
import com.drownedinsound.ui.post.BaseBoardPostHolder

data class BoardPostListModel(
        val index: Int,
        val title: String,
        val authorUsername: String,
        val numberOfRepliesText: String,
        val lastUpdatedText: String,
        val markAsRead: Boolean,
        val isSticky: Boolean)

interface BoardPostItem {
    fun getType(typeFactory: TypeFactory) : Int
}

interface TypeFactory {
    fun getType(initialComment: InitialComment) : Int
    fun getType(replyComment: ReplyComment) : Int
    fun createViewHolder(view: View, viewType: Int): BaseBoardPostHolder<*>
}

data class CommentInfo(
        val title: String?,
        val author: String,
        val content: String,
        val dateAndTime: String? = "unknown")

data class InitialComment(val commentInfo: CommentInfo,
                          val numberOfRepliesText: String) : BoardPostItem {
    override fun getType(typeFactory: TypeFactory): Int = typeFactory.getType(this)
}

data class ReplyComment(val commentInfo: CommentInfo,
                        val usersWhoHaveThisdThis : String?,
                        val commentLevel: Int) : BoardPostItem {
    override fun getType(typeFactory: TypeFactory) : Int  = typeFactory.getType(this)
}

