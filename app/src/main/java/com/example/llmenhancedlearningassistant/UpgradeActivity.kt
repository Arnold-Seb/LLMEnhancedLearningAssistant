package com.example.llmenhancedlearningassistant

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class UpgradeActivity : ComponentActivity() {

    private lateinit var basicPlanButton: Button
    private lateinit var premiumPlanButton: Button
    private lateinit var proPlanButton: Button
    private lateinit var confirmPaymentButton: Button
    private lateinit var backButton: Button

    private lateinit var paymentSection: View
    private lateinit var selectedPlanText: TextView
    private lateinit var paymentMethodGroup: RadioGroup

    private var selectedPlan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)

        basicPlanButton = findViewById(R.id.basicPlanButton)
        premiumPlanButton = findViewById(R.id.premiumPlanButton)
        proPlanButton = findViewById(R.id.proPlanButton)
        confirmPaymentButton = findViewById(R.id.confirmPaymentButton)
        backButton = findViewById(R.id.backButton)

        paymentSection = findViewById(R.id.paymentSection)
        selectedPlanText = findViewById(R.id.selectedPlanText)
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup)

        paymentSection.visibility = View.GONE

        basicPlanButton.setOnClickListener {
            showPaymentSection("Starter Plan")
        }

        premiumPlanButton.setOnClickListener {
            showPaymentSection("Intermediate Plan")
        }

        proPlanButton.setOnClickListener {
            showPaymentSection("Advanced Plan")
        }

        confirmPaymentButton.setOnClickListener {
            val selectedPaymentId = paymentMethodGroup.checkedRadioButtonId

            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedPaymentMethod =
                findViewById<RadioButton>(selectedPaymentId).text.toString()

            Toast.makeText(
                this,
                "$selectedPlan purchased using $selectedPaymentMethod",
                Toast.LENGTH_LONG
            ).show()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun showPaymentSection(planName: String) {
        selectedPlan = planName
        selectedPlanText.text = "Selected Plan: $planName"
        paymentSection.visibility = View.VISIBLE
    }
}