package com.bayazidht.newsflow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bayazidht.newsflow.data.model.NewsItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(article: NewsItem)

    @Delete
    suspend fun deleteBookmark(article: NewsItem)

    @Query("SELECT * FROM bookmarks")
    fun getAllBookmarks(): Flow<List<NewsItem>>

    @Query("SELECT EXISTS(SELECT * FROM bookmarks WHERE title = :title)")
    suspend fun isBookmarked(title: String): Boolean
}