package com.example.dreamplanner.database

import com.example.dreamplanner.Articles

val samplePlans = listOf(
    Plan(
        name = "Nauka Kotlin",
        date = 1735689600000L, // 2025-04-30
        priority = 1,
        place = "Dom",
        description = "Przerobić kurs JetBrains",
        completed = false
    ),
    Plan(
        name = "Zakupy spożywcze",
        date = 1735516800000L, // 2025-04-28
        priority = 2,
        place = "Supermarket",
        description = "Kupić warzywa, owoce i chleb",
        completed = true
    ),
    Plan(
        name = "Spotkanie z przyjacielem",
        date = 1735852800000L, // 2025-05-02
        priority = 1,
        place = "Kawiarnia",
        description = "Omówić wspólny projekt",
        completed = false
    ),
    Plan(
        name = "Wizyta u lekarza",
        date = 1736208400000L, // 2025-05-06
        priority = 3,
        place = "Przychodnia",
        description = "Kontrola okresowa",
        completed = false
    ),
    Plan(
        name = "Weekendowy wypad",
        date = 1736467600000L, // 2025-05-09
        priority = 2,
        place = "Mazury",
        description = "Odpoczynek i relaks",
        completed = true
    )
)

val sampleDailyTasks = listOf(
    DailyTask(
        name = "Zrobić aplikację",
        completed = false,
        priority = 1,
        date = 1736467600000L, // 2025-05-09,
        description = "Ukończyć MVP aplikacji DreamPlanner"
    ),
    DailyTask(
        name = "Przeczytać książkę",
        completed = true,
        priority = 2,
        date = 1736467600000L, // 2025-05-09,
        description = "Skończyć 'Atomic Habits'"
    ),
    DailyTask(
        name = "Uczyć się regularnie",
        completed = false,
        priority = 1,
        date = 1736467600000L, // 2025-05-09,
        description = "30 minut dziennie nauki programowania"
    ),
    DailyTask(
        name = "Poprawić kondycję",
        completed = false,
        priority = 3,
        date = 1736467600000L, // 2025-05-09,
        description = "Codzienne spacery i bieganie 3 razy w tygodniu"
    ),
    DailyTask(
        name = "Zacząć oszczędzać",
        completed = true,
        priority = 2,
        date = 1736467600000L, // 2025-05-09,
        description = "Stworzyć budżet miesięczny"
    )
)

val sampleGoals = listOf(
    Goal(name = "Medytacja", completed = true),
    Goal(name = "Ćwiczenia", completed = false),
    Goal(name = "Planowanie dnia", completed = true),
    Goal(name = "Czytanie", completed = false),
    Goal(name = "Poranna rutyna", completed = true)
)

val sampleArticles = listOf(
    Article(
        name = "Exploring Lucid Dreams",
        description = "A beginner's guide to achieving awareness in your dreams.",
        url = "https://example.com/lucid-dreams-guide",
        section = "Lucid Dreams"
    ),
    Article(
        name = "The Psychology of Nightmares",
        description = "Why we have nightmares and how to cope with them.",
        url = "https://example.com/nightmares-psychology",
        section = "Nightmares"
    ),
    Article(
        name = "Patterns in Recurring Dreams",
        description = "What recurring dreams say about your subconscious.",
        url = "https://example.com/recurring-dreams-patterns",
        section = "Recurring Dreams"
    ),
    Article(
        name = "Flying in Dreams: What Does It Mean?",
        description = "Symbolism and interpretation of flying in dreams.",
        url = "https://example.com/flying-in-dreams",
        section = "Other Dreams"
    )
)
val sampleSleepEntries = listOf(
    SleepEntry(start = 2200, end = 600),    // 22:00 i 06:00 jako ms od północy
    SleepEntry(start = 2300, end = 700), // 23:30 i 07:00
    SleepEntry(start = 15, end = 800) // 00:15 i 08:00
)

