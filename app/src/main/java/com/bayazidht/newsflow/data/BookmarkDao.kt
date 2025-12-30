package com.bayazidht.newsflow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(article: NewsArticle)

    @Delete
    suspend fun deleteBookmark(article: NewsArticle)

    @Query("SELECT * FROM bookmarks")
    fun getAllBookmarks(): Flow<List<NewsArticle>>

    @Query("SELECT EXISTS(SELECT * FROM bookmarks WHERE title = :title)")
    suspend fun isBookmarked(title: String): Boolean
}