package com.example.dreamplanner.database
import androidx.room.*

@Entity
data class Plan(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val name: String,
    val priority: Int,
    val date: Long,
    val place: String,
    val description: String = "",
    val completed: Boolean = false
)

@Entity
data class DailyTask(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val name: String,
    val priority: Int,
    val description: String,
    val date: Long,
    val completed: Boolean
)

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val name: String,
    val completed: Boolean = false
)


@Entity
data class SleepEntry(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val startTime: Long,  // np. System.currentTimeMillis()
    val stopTime: Long,
    val date: String      // np. "2025-06-16"
)


@Entity
data class Article(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // unikalne ID (auto-generowane)
    val name: String,
    val section: String,
    val description: String?, // opcjonalnie
    val url: String?
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String
)
