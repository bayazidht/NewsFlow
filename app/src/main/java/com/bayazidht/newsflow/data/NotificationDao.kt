package com.bayazidht.newsflow.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(item: NotificationItem)

    @Query("SELECT * FROM notifications_history ORDER BY time DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Query("DELETE FROM notifications_history")
    suspend fun clearAll()
}