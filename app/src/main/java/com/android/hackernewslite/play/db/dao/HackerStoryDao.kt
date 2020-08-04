package com.android.hackernewslite.play.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.hackernewslite.play.model.HackerStory

@Dao
interface HackerStoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(hackerStory: HackerStory): Long

    @Query("SELECT * FROM hacker_stories order by score asc")
    fun getAllSavedStories(): LiveData<List<HackerStory>>

    @Delete
    suspend fun delete(hackerStory: HackerStory)
}