package com.example.dreamplanner

class Models {
    data class Plan(
        val name: String,
        val date: Int,
        val prio: Int,
        val place: String
    )
    data class Goal(
        val name: String,
        var completed: Boolean = false
    )

    data class DailyTask(
        val name: String,
        var completed: Boolean = false
    )

    data class SleepEntry(
        val start: String,
        val end: String
    )

    val samplePlans = listOf(
        Plan(name = "Nauka Kotlin", date = 20250430, prio = 1, place = "Dom"),
        Plan(name = "Zakupy spożywcze", date = 20250428, prio = 2, place = "Supermarket"),
        Plan(name = "Spotkanie z przyjacielem", date = 20250502, prio = 1, place = "Kawiarnia")
    )

    val sampleGoals = listOf(
        Goal("Cel 1"),
        Goal("Cel 2")
    )

    val sampleDailyTasks = listOf(
        DailyTask("Punkt 1"),
        DailyTask("Punkt 2", completed = true),
        DailyTask("Punkt 3")
    )

    val sampleSleepEntries = listOf(
        SleepEntry("22:00", "06:00"),
        SleepEntry("22:30", "04:00"),
        SleepEntry("23:00", "07:00")
    )

}