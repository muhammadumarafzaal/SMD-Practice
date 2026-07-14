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

    // Camera Result Launcher
    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            Toast.makeText(this, "Photo captured!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        dbHelper = DBHelper(this)
        createNotificationChannel()

        findViewById<MaterialCardView>(R.id.navbar).setOnClickListener {
            Toast.makeText(this, "Search clicked!", Toast.LENGTH_SHORT).show()
        }

        val btnFetch = findViewById<Button>(R.id.btnFetchRecipe)
        val btnSave = findViewById<Button>(R.id.btnSaveDb)
        val btnHistory = findViewById<Button>(R.id.btnGoToHistory)
        val btnCamera = findViewById<Button>(R.id.btnTakePhoto)
        val etNotes = findViewById<EditText>(R.id.etUserNotes)
        val tvApiResponse = findViewById<TextView>(R.id.tvApiResponse)

        btnFetch.setOnClickListener {
            // Placeholder logic to show it's working
            tvApiResponse.text = "Fetching advice..."
            Toast.makeText(this, "Fetching advice...", Toast.LENGTH_SHORT).show()
        }
        
        btnSave.setOnClickListener {
            val note = etNotes.text.toString()
            if (note.isNotEmpty()) {
                dbHelper.insertWorkout(note, "Motivational Quote")
                Snackbar.make(it, "Workout saved!", Snackbar.LENGTH_LONG).show()
                sendWorkoutNotification()
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
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle("QuickFit")
            .setContentText("Workout saved successfully!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(1, builder.build())
        }
    }
}