package com.example.uniasselvicalendarioapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uniasselvicalendarioapp.demo.CalendarSetup
import com.example.uniasselvicalendarioapp.view.CalendarComponent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendar = findViewById<CalendarComponent>(R.id.calendar)
        CalendarSetup.configure(calendar)
    }
}
