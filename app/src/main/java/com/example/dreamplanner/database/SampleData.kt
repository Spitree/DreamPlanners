package com.example.dreamplanner.database

val samplePlans = listOf(
    Plan(name = "Nauka Kotlin", date = 1735689600000L, priority = 1, place = "Dom"),        // 2025-04-30 00:00:00 UTC
    Plan(name = "Zakupy spożywcze", date = 1735516800000L, priority = 2, place = "Supermarket"), // 2025-04-28 00:00:00 UTC
    Plan(name = "Spotkanie z przyjacielem", date = 1735852800000L, priority = 1, place = "Kawiarnia") // 2025-05-02 00:00:00 UTC
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
    SleepEntry(start = 22 * 3600000L, end = 6 * 3600000L),    // 22:00 i 06:00 jako ms od północy
    SleepEntry(start = 23 * 3600000L + 30 * 60000L, end = 7 * 3600000L), // 23:30 i 07:00
    SleepEntry(start = 0 * 3600000L + 15 * 60000L, end = 8 * 3600000L) // 00:15 i 08:00
)

