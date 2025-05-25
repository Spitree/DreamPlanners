package com.example.dreamplanner
import com.example.dreamplanner.Models.*

val samplePlans = listOf(
    Plan(name = "Nauka Kotlin", date = 20250430, prio = 1, place = "Dom"),
    Plan(name = "Zakupy spożywcze", date = 20250428, prio = 2, place = "Supermarket"),
    Plan(name = "Spotkanie z przyjacielem", date = 20250502, prio = 1, place = "Kawiarnia")
)

val sampleGoals = listOf(
    Goal(name = "Zrobić aplikację", completed = false),
    Goal(name = "Przeczytać książkę", completed = true),
    Goal(name = "Uczyć się regularnie", completed = false)
)

val sampleDailyTasks = listOf(
    DailyTask(name = "Medytacja", completed = true),
    DailyTask(name = "Ćwiczenia", completed = false),
    DailyTask(name = "Planowanie dnia", completed = true)
)

val sampleSleepEntries = listOf(
    SleepEntry(start = "22:00", end = "06:00"),
    SleepEntry(start = "23:30", end = "07:00"),
    SleepEntry(start = "00:15", end = "08:00")
)
