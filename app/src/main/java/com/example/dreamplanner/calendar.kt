package com.example.dreamplanner

import android.content.Context
import android.net.Uri
import com.example.dreamplanner.database.Plan
import java.util.Calendar

fun getTodayCalendarEvents(context: Context): List<Plan> {
    val contentResolver = context.contentResolver
    val events = mutableListOf<Plan>()

    val startOfDay = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val endOfDay = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    val projection = arrayOf("title", "dtstart", "eventLocation")
    val selection = "(dtstart >= ?) AND (dtstart <= ?)"
    val selectionArgs = arrayOf(startOfDay.toString(), endOfDay.toString())

    val cursor = contentResolver.query(
        Uri.parse("content://com.android.calendar/events"),
        projection,
        selection,
        selectionArgs,
        null
    )

    cursor?.use {
        val titleIndex = it.getColumnIndex("title")
        val dateIndex = it.getColumnIndex("dtstart")
        val placeIndex = it.getColumnIndex("eventLocation")

        while (it.moveToNext()) {
            val title = it.getString(titleIndex) ?: "Bez tytułu"
            val date = it.getLong(dateIndex)
            val place = it.getString(placeIndex) ?: "Brak lokalizacji"
            events.add(Plan(name = title, date = date, priority = 1, place = place))
        }
    }
    return events
}

