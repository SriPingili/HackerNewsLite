package com.androideveloper.hackernewsfeed.play.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androideveloper.hackernewsfeed.play.db.dao.HackerStoryDao
import com.androideveloper.hackernewsfeed.play.model.HackerStory

@Database(
    entities = [HackerStory::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class HackerStoryDatabase : RoomDatabase() {
    abstract fun getHackerStoryDao(): HackerStoryDao

    companion object {
        @Volatile
        private var instance: HackerStoryDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                HackerStoryDatabase::class.java,
                "hacker_story_db.db"
            ).build()
    }
}