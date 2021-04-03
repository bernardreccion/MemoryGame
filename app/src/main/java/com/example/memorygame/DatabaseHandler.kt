package com.example.memorygame

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

val DATABASE_NAME = "MyDB"
val TABLE_NAME = "Leaderboards"
val COL_TIME = "Time"
val COL_ID = "ID"

class DatabaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    //will be executed when the device doesn't contain a database
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME +" (" +
                COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TIME +" INTEGER)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //will be executed when we have an older version of database
    }

    fun insertData(record: Record) {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_TIME, record.time)
        var result = db.insert(TABLE_NAME, null, cv)
        if (result == -1.toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    fun viewData() :MutableList<Record> {
        var list :MutableList<Record> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM " + TABLE_NAME +
                " ORDER BY " + COL_TIME + " ASC " +
                " LIMIT 10"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()) {
            do {
                var record = Record()
                record.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                record.time = result.getString(result.getColumnIndex(COL_TIME)).toLong()
                list.add(record)
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

}
