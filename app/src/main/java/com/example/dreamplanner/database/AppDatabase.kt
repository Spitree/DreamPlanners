package com.example.dreamplanner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Plan::class, DailyTask::class, SleepEntry::class, Goal::class, Article::class],
    version = 13)
abstract class AppDatabase : RoomDatabase() {

    abstract fun planDao(): PlanDao
    abstract fun dailyTaskDao(): DailyTaskDao
    abstract fun sleepEntryDao(): SleepEntryDao
    abstract fun goalDao(): GoalDao
    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plans_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).planDao().insertAll(samplePlans)
                                getInstance(context).dailyTaskDao().insertAll(sampleDailyTasks)
                                getInstance(context).goalDao().insertAll(sampleGoals)
                                getInstance(context).sleepEntryDao().insertAll(sampleSleep)
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
