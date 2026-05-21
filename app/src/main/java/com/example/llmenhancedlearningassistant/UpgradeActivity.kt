package com.example.llmenhancedlearningassistant

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class UpgradeActivity : ComponentActivity() {

    private lateinit var basicPlanButton: Button
    private lateinit var premiumPlanButton: Button
    private lateinit var proPlanButton: Button
    private lateinit var backButton: Button

    private lateinit var upgradeTitle: TextView
    private lateinit var upgradeSubtitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)

        upgradeTitle = findViewById(R.id.upgradeTitle)
        upgradeSubtitle = findViewById(R.id.upgradeSubtitle)

        basicPlanButton = findViewById(R.id.basicPlanButton)
        premiumPlanButton = findViewById(R.id.premiumPlanButton)
        proPlanButton = findViewById(R.id.proPlanButton)

        backButton = findViewById(R.id.backButton)

        upgradeTitle.text = "Upgrade Account"

        upgradeSubtitle.text =
            "Unlock premium AI learning tools and advanced features."

        basicPlanButton.setOnClickListener {

            Toast.makeText(
                this,
                "Starter Plan Purchased Successfully",
                Toast.LENGTH_SHORT
            ).show()
        }

        premiumPlanButton.setOnClickListener {

            Toast.makeText(
                this,
                "Intermediate Plan Purchased Successfully",
                Toast.LENGTH_SHORT
            ).show()
        }

        proPlanButton.setOnClickListener {

            Toast.makeText(
                this,
                "Advanced Plan Purchased Successfully",
                Toast.LENGTH_SHORT
            ).show()
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}