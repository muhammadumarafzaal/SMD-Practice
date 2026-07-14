package com.example.mythirdui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mythirdui.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val PREFS = "mythirdui_prefs"
    private val KEY_DARK = "dark_mode"

    override fun onCreate(savedInstanceState: Bundle?) {
        // apply saved theme first
        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean(KEY_DARK, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window insets padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // initialize toggle state
        binding.themeToggle.isChecked = isDark

        binding.themeToggle.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
            prefs.edit().putBoolean(KEY_DARK, isChecked).apply()
        }

        binding.saveBtn.setOnClickListener {
            val name = binding.nameInput.text?.toString().orEmpty()
            val email = binding.emailInput.text?.toString().orEmpty()
            // very small demo save action
            Toast.makeText(this, "Saved: $name — $email", Toast.LENGTH_SHORT).show()
        }

        binding.logoutBtn.setOnClickListener {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
        }
    }
}