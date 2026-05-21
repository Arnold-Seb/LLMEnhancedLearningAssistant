package com.example.llmenhancedlearningassistant

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class ProfileActivity : ComponentActivity() {

    private lateinit var studentName: TextView
    private lateinit var studentEmail: TextView

    private lateinit var shareButton: Button
    private lateinit var historyButton: Button
    private lateinit var upgradeButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        studentName = findViewById(R.id.studentName)
        studentEmail = findViewById(R.id.studentEmail)

        shareButton = findViewById(R.id.shareButton)
        historyButton = findViewById(R.id.historyButton)
        upgradeButton = findViewById(R.id.upgradeButton)
        backButton = findViewById(R.id.backButton)

        studentName.text = "Student"
        studentEmail.text = "student@email.com"

        shareButton.setOnClickListener {

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND

                putExtra(
                    Intent.EXTRA_TEXT,
                    """
                    Check out my progress in the LLM Learning Assistant App!
                    
                    I am learning:
                    - AI
                    - Data Structures
                    - Cloud Computing
                    
                    Shared from my Android Learning Assistant App.
                    """.trimIndent()
                )

                type = "text/plain"
            }

            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        historyButton.setOnClickListener {
            startActivity(
                Intent(this, HistoryActivity::class.java)
            )
        }

        upgradeButton.setOnClickListener {
            startActivity(
                Intent(this, UpgradeActivity::class.java)
            )
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}