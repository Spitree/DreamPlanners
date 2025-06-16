package com.example.dreamplanner.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlanDao {
    @Query("SELECT COUNT(*) FROM `Plan`")
    suspend fun count(): Int
    @Query("SELECT * FROM `Plan`")
    fun getAll(): LiveData<List<Plan>>
    @Query("DELETE FROM `Plan`")
    suspend fun deleteAll()
    @Insert
    suspend fun insert(plan: Plan)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plans: List<Plan>)
    @Update
    suspend fun update(plan: Plan)
    @Delete
    suspend fun delete(plan: Plan)
}

@Dao
interface DailyTaskDao {
    @Query("SELECT COUNT(*) FROM DailyTask")
    suspend fun count(): Int
    @Query("SELECT * FROM DailyTask")
    fun getAll(): LiveData<List<DailyTask>>
    @Insert
    suspend fun insert(dailyTask: DailyTask)
    @Insert
    suspend fun insertAll(dailyTask: List<DailyTask>)
    @Update
    suspend fun update(dailyTask: DailyTask)
    @Delete
    suspend fun delete(dailyTask: DailyTask)
}

@Dao
interface SleepEntryDao {
    @Query("SELECT COUNT(*) FROM SleepEntry")
    suspend fun count(): Int
    @Query("SELECT * FROM SleepEntry")
    fun getAll(): LiveData<List<SleepEntry>>
    @Insert
    suspend fun insert(sleepEntry: SleepEntry)
    @Insert
    suspend fun insertAll(sleepEntry: List<SleepEntry>)
    @Update
    suspend fun update(sleepEntry: SleepEntry)
    @Delete
    suspend fun delete(sleepEntry: SleepEntry)
}
@Dao
interface GoalDao {
    @Query("SELECT COUNT(*) FROM Goal")
    suspend fun count(): Int
    @Query("SELECT * FROM Goal")
    fun getAll(): LiveData<List<Goal>>
    @Insert
    suspend fun insert(goal: Goal)
    @Insert
    suspend fun insertAll(goal: List<Goal>)
    @Update
    suspend fun update(goal: Goal)
    @Delete
    suspend fun delete(goal: Goal)
}