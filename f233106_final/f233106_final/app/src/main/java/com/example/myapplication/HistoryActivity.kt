package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: LogAdapter
    private var workouts = ArrayList<Pair<Int, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        dbHelper = DBHelper(this)
        workouts = dbHelper.getAllWorkouts()
        
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = LogAdapter(workouts) { id ->
            dbHelper.deleteWorkout(id)
            workouts.clear()
            workouts.addAll(dbHelper.getAllWorkouts())
            adapter.notifyDataSetChanged()
        }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }
}