package com.example.memorygame

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


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
        val cv = ContentValues()
        cv.put(COL_TIME, record.time)
        db.insert(TABLE_NAME, null, cv)
        db.close()
    }

    fun viewData() :MutableList<Record> {
        val list :MutableList<Record> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM " + TABLE_NAME +
                " ORDER BY " + COL_TIME + " ASC " +
                " LIMIT 10"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()) {
            do {
                val record = Record()
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
