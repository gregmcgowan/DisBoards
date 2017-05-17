package com.drownedinsound

import android.view.View
import com.drownedinsound.ui.post.BaseBoardPostHolder

data class BoardPostSummaryModel(
        val index: Int,
        val title: String? = "No title",
        val authorUsername: String,
        val numberOfRepliesText: String,
        val lastUpdatedText: String,
        val markAsRead: Boolean,
        val isSticky: Boolean)

interface BoardPostItem {
    fun getType(typeFactory: TypeFactory) : Int
}

data class InitialComment(val comment: Comment,
                          val numberOfRepliesText: String) : BoardPostItem {
    override fun getType(typeFactory: TypeFactory): Int = typeFactory.getType(this)
}

data class ReplyComment(val comment: Comment,
                        val usersWhoHaveThisdThis : String?,
                        val commentLevel: Int) : BoardPostItem {
    override fun getType(typeFactory: TypeFactory) : Int  = typeFactory.getType(this)
}

data class Comment(
        val title: String?,
        val author: String,
        val content: String,
        val dateAndTime: String? = "unknown")

interface TypeFactory {
    fun getType(initialComment: InitialComment) : Int
    fun getType(replyComment: ReplyComment) : Int
    fun createViewHolder(view: View, viewType: Int): BaseBoardPostHolder<*>
}


