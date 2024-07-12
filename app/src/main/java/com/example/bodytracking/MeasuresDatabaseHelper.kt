package com.example.bodytracking

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import android.health.connect.datatypes.WeightRecord
import android.icu.util.Measure

class MeasuresDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "bodytracking.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "Measures"
        private const val COLUMN_ID = "id"
        private const val COLUMN_WEIGHT= "weight"
        private const val COLUMN_UPPER_WAIST = "upperWaist"
        private const val COLUMN_MID_WAIST = "midWaist"
        private const val COLUMN_LOWER_WAIST = "lowerWaist"
        private const val COLUMN_NECK = "neck"
        private const val COLUMN_DATE = "date"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_UPPER_WAIST NUMERIC, $COLUMN_MID_WAIST NUMERIC, $COLUMN_LOWER_WAIST NUMERIC, $COLUMN_NECK NUMERIC, $COLUMN_DATE DATE NOT NULL UNIQUE)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }
    fun insertMeasures(measures: AppMeasures) {
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_DATE, measures.date.time) //Convert date to long
            put(COLUMN_NECK, measures.neck)
            put(COLUMN_LOWER_WAIST, measures.lowerWaist)
            put(COLUMN_MID_WAIST, measures.midWaist)
            put(COLUMN_UPPER_WAIST, measures.upperWaist)
            put(COLUMN_WEIGHT, measures.weightRecord)
        }

        db.insert(TABLE_NAME,null,values)
        db.close()
    }


}