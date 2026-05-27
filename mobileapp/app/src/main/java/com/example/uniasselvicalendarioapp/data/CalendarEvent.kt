package com.example.uniasselvicalendarioapp.data

import java.time.LocalDate

data class CalendarEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val color: Int = DEFAULT_EVENT_COLOR
) {
    fun occursOn(date: LocalDate): Boolean {
        val end = endDate ?: startDate
        return !date.isBefore(startDate) && !date.isAfter(end)
    }

    companion object {
        val DEFAULT_EVENT_COLOR: Int = 0xFF1565C0.toInt()
        val CLASS_EVENT_COLOR: Int = 0xFF2E7D32.toInt()
        val EXAM_EVENT_COLOR: Int = 0xFFC62828.toInt()
        val HOLIDAY_EVENT_COLOR: Int = 0xFFF9A825.toInt()
    }
}
