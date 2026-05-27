package com.example.uniasselvicalendarioapp.demo

import android.widget.Toast
import com.example.uniasselvicalendarioapp.data.CalendarEvent
import com.example.uniasselvicalendarioapp.view.CalendarComponent
import com.example.uniasselvicalendarioapp.view.OnDayClickListener
import java.time.LocalDate

object CalendarSetup {

    fun configure(calendar: CalendarComponent) {
        val semesterStart = LocalDate.of(2025, 2, 10)
        val semesterEnd = LocalDate.of(2025, 7, 5)

        calendar
            .setSemesterRange(semesterStart, semesterEnd)
            .setEvents(sampleEvents())
            .setOnDayClickListener(object : OnDayClickListener {
                override fun onDayClicked(date: LocalDate, events: List<CalendarEvent>) {
                    handleDayClick(date, events, calendar)
                }
            })
    }

    private fun sampleEvents(): List<CalendarEvent> = listOf(
        CalendarEvent(
            name = "Prova de POO",
            description = "Avaliação bimestral",
            startDate = LocalDate.of(2025, 4, 15),
            startTime = "14:00",
            endTime = "16:00",
            color = CalendarEvent.EXAM_EVENT_COLOR
        ),
        CalendarEvent(
            name = "Apresentação TCC",
            description = "Banca final",
            startDate = LocalDate.of(2025, 6, 20),
            endDate = LocalDate.of(2025, 6, 21),
            color = CalendarEvent.DEFAULT_EVENT_COLOR
        ),
        CalendarEvent(
            name = "Feriado",
            description = "Paixão de Cristo",
            startDate = LocalDate.of(2025, 4, 18),
            color = CalendarEvent.HOLIDAY_EVENT_COLOR
        )
    )

    private fun handleDayClick(
        date: LocalDate,
        events: List<CalendarEvent>,
        calendar: CalendarComponent
    ) {
        val context = calendar.context
        val msg = if (events.isEmpty()) {
            "${date.dayOfMonth}/${date.monthValue}/${date.year} - Sem eventos"
        } else {
            events.joinToString("\n") { "${it.name} (${it.color.toColorName()})" }
        }
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    private fun Int.toColorName(): String = when (this) {
        CalendarEvent.EXAM_EVENT_COLOR -> "Vermelho"
        CalendarEvent.HOLIDAY_EVENT_COLOR -> "Amarelo"
        CalendarEvent.CLASS_EVENT_COLOR -> "Verde"
        else -> "Azul"
    }
}
