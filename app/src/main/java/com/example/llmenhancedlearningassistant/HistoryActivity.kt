package com.example.llmenhancedlearningassistant

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity

class HistoryActivity : ComponentActivity() {

    private lateinit var historyText: TextView
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyText = findViewById(R.id.historyText)
        backButton = findViewById(R.id.backButton)

        val preferences: SharedPreferences =
            getSharedPreferences("QuizHistory", MODE_PRIVATE)

        val savedHistory = preferences.getString(
            "history",
            "No quiz history available yet."
        )

        historyText.text = savedHistory

        backButton.setOnClickListener {
            finish()
        }
    }
}