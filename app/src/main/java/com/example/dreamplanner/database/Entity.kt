package com.example.dreamplanner.database
import androidx.room.*
import java.sql.Time
import java.util.Date

@Entity
data class Plan(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // unikalne ID (auto-generowane)
    val name: String,
    val date: Long,
    val priority: Int,
    val place: String,
)

@Entity
data class DailyTask(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // unikalne ID (auto-generowane)
    val name: String,
    val completed: Boolean,
)

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // unikalne ID (auto-generowane)
    val name: String,
    val completed: Boolean,
)


@Entity
data class SleepEntry(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // unikalne ID (auto-generowane)
    val start: Long,
    val end: Long,
)
