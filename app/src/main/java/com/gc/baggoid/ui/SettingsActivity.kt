package com.gc.baggoid.ui

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gc.baggoid.R
import com.gc.baggoid.models.RulesMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences("app_preferences", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val rulesModeRadioGroup: RadioGroup = findViewById(R.id.rulesModeRadioGroup)
        val saveButton: Button = findViewById(R.id.saveButton)

        // Load the current rule mode from SharedPreferences
        val savedRuleMode = sharedPreferences.getString("rules_mode", RulesMode.SIMPLE.name)

        when (savedRuleMode) {
            RulesMode.SIMPLE.name -> rulesModeRadioGroup.check(R.id.radioSimpleMode)
            RulesMode.EXACT.name -> rulesModeRadioGroup.check(R.id.radioExactMode)
            else -> rulesModeRadioGroup.check(R.id.radioSimpleMode) // default to SIMPLE if null or invalid
        }

        // Save button click listener
        saveButton.setOnClickListener {
            val selectedId = rulesModeRadioGroup.checkedRadioButtonId
            val newMode = when (selectedId) {
                R.id.radioSimpleMode -> RulesMode.SIMPLE
                R.id.radioExactMode -> RulesMode.EXACT
                else -> return@setOnClickListener
            }

            // Save the selected rules mode to SharedPreferences
            sharedPreferences.edit().putString("rules_mode", newMode.name).apply()

            // Show a confirmation message
            Toast.makeText(this, "Game rule updated!", Toast.LENGTH_SHORT).show()

            // Close the settings screen
            finish()
        }
    }
}