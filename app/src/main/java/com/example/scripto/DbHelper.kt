package com.example.scripto

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "scripto", factory, 3) {

    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
    CREATE TABLE users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        email TEXT UNIQUE,
        password TEXT
    )""".trimIndent()
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun addUser(user: User): Boolean {
        val values = ContentValues()
        values.put("email", user.email)
        values.put("password", user.password)

        val db = this.writableDatabase
        val result = db.insert("users", null, values)
        db.close()

        return result != -1L
    }

    fun getUser(email: String, password: String): Boolean {
        val db = this.readableDatabase

        val result = db.rawQuery(
            "SELECT * FROM users WHERE email = ? AND password = ?",
            arrayOf(email, password)
        )

        val exists = result.moveToFirst()
        result.close()
        return exists
    }

    fun isUserExists(email: String): Boolean {
        val db = this.readableDatabase

        val result = db.rawQuery(
            "SELECT * FROM users WHERE email = ?",
            arrayOf(email)
        )

        val exists = result.moveToFirst()
        result.close()
        return exists
    }
}