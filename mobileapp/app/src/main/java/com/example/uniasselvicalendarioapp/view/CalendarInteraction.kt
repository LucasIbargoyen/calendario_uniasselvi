package com.example.uniasselvicalendarioapp.view

import com.example.uniasselvicalendarioapp.data.CalendarEvent
import java.time.LocalDate

interface CalendarInteraction {
    fun setSemesterRange(start: LocalDate, end: LocalDate): CalendarInteraction
    fun setEvents(eventList: List<CalendarEvent>): CalendarInteraction
    fun addEvent(event: CalendarEvent): CalendarInteraction
    fun removeEvent(eventId: String): CalendarInteraction
    fun updateEvent(event: CalendarEvent): CalendarInteraction
    fun getEventsOnDate(date: LocalDate): List<CalendarEvent>
    fun getEventsInRange(start: LocalDate, end: LocalDate): List<CalendarEvent>
    fun goToMonth(year: Int, month: Int): CalendarInteraction
    fun goToNextMonth(): CalendarInteraction
    fun goToPreviousMonth(): CalendarInteraction
    fun goToToday(): CalendarInteraction
    fun getCurrentYear(): Int
    fun getCurrentMonth(): Int
    fun getSelectionStart(): LocalDate?
    fun getSelectionEnd(): LocalDate?
    fun clearSelection(): CalendarInteraction
    fun refresh(): CalendarInteraction
    fun setOnDayClickListener(listener: OnDayClickListener): CalendarInteraction
    fun setOnSelectionListener(listener: OnSelectionListener): CalendarInteraction
    fun setOnSemesterEndListener(listener: OnSemesterEndListener): CalendarInteraction
}

interface OnDayClickListener {
    fun onDayClicked(date: LocalDate, events: List<CalendarEvent>)
}

interface OnSelectionListener {
    fun onSelectionChanged(start: LocalDate?, end: LocalDate?)
}

interface OnSemesterEndListener {
    fun onSemesterEndReached()
}
