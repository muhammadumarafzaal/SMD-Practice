package com.example.myapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var dbHelper: DBHelper
    private val CHANNEL_ID = "workout_channel"

    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Photo captured!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        dbHelper = DBHelper(this)
        createNotificationChannel()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MotivationFragment())
                .commit()
        }

        val btnFetch = findViewById<Button>(R.id.btnFetchRecipe)
        val btnSave = findViewById<Button>(R.id.btnSaveDb)
        val btnHistory = findViewById<Button>(R.id.btnGoToHistory)
        val btnCamera = findViewById<Button>(R.id.btnTakePhoto)
        val etNotes = findViewById<EditText>(R.id.etUserNotes)
        val tvApiResponse = findViewById<TextView>(R.id.tvApiResponse)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        btnFetch.setOnClickListener {
            fetchAdvice(tvApiResponse, progressBar, btnFetch)
        }
        
        btnSave.setOnClickListener {
            val note = etNotes.text.toString()
            val quote = tvApiResponse.text.toString()
            if (note.isNotEmpty()) {
                dbHelper.insertWorkout(note, quote)
                Snackbar.make(it, "Workout saved!", Snackbar.LENGTH_LONG).show()
                sendWorkoutNotification()
                etNotes.text.clear()
            } else {
                Toast.makeText(this, "Please enter workout notes first", Toast.LENGTH_SHORT).show()
            }
        }

        btnCamera.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePicture.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            }
        }

        btnHistory.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }

        findViewById<MaterialCardView>(R.id.navbar).setOnClickListener {
            Toast.makeText(this, "Search clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAdvice(textView: TextView, progressBar: ProgressBar, button: Button) {
        button.isEnabled = false
        progressBar.visibility = View.VISIBLE
        textView.text = "Fetching Motivation..."

        val request = Request.Builder()
            .url("https://api.adviceslip.com/advice")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    button.isEnabled = true
                    progressBar.visibility = View.GONE
                    textView.text = "Connection Error. Check internet."
                    Log.e("FETCH_ERROR", "Failed to fetch", e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                runOnUiThread {
                    button.isEnabled = true
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful && body != null) {
                        try {
                            val json = JSONObject(body)
                            val advice = json.getJSONObject("slip").getString("advice")
                            textView.text = advice
                        } catch (e: Exception) {
                            textView.text = "Failed to parse data"
                        }
                    } else {
                        textView.text = "Server Error: ${response.code}"
                    }
                }
            }
        })
    }

    fun onFragmentMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Workout Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun sendWorkoutNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("QuickFit")
            .setContentText("Workout saved successfully!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(1, builder.build())
        }
    }
}