package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "WorkoutDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE Workouts (id INTEGER PRIMARY KEY AUTOINCREMENT, note TEXT, quote TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun insertWorkout(note: String, quote: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("note", note)
            put("quote", quote)
        }
        return db.insert("Workouts", null, values)
    }

    fun getAllWorkouts(): ArrayList<Pair<Int, String>> {
        val list = ArrayList<Pair<Int, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Workouts", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val note = cursor.getString(1)
            list.add(Pair(id, note))
        }
        cursor.close()
        return list
    }

    fun deleteWorkout(id: Int) {
        val db = writableDatabase
        db.delete("Workouts", "id = ?", arrayOf(id.toString()))
    }
}