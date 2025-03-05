package com.example.bodytracking

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE


class MeasuresDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    companion object{
        private const val DATABASE_NAME = "bodytracking.db"
        private const val DATABASE_VERSION = 3
        private const val TABLE_NAME = "Measures"
        private const val COLUMN_ID = "id"
        private const val COLUMN_WEIGHT= "weight"
        private const val COLUMN_UPPER_WAIST = "upperWaist"
        private const val COLUMN_MID_WAIST = "midWaist"
        private const val COLUMN_LOWER_WAIST = "lowerWaist"
        private const val COLUMN_NECK = "neck"
        private const val COLUMN_DATE = "date"




        fun updateMeasure(db: SQLiteDatabase, upperWaist: Float, midWaist: Float, weight: Float, date: String, neck: Float, lowerWaist: Float) {
            val values = ContentValues().apply {
                put("upperWaist", upperWaist)
                put("midWaist", midWaist)
                put("weight", weight)
                put("date", date)
                put("neck", neck)
                put("lowerWaist", lowerWaist)
            }

            // Definindo a cláusula WHERE para identificar a linha que deseja atualizar
            val selection = "date = ?"
            val selectionArgs = arrayOf(date.toString())

            // Atualizando a linha no banco de dados
            val count = db.update(
                "Measures",   // Nome da tabela
                values,       // Valores a serem atualizados
                selection,    // Cláusula WHERE
                selectionArgs // Argumentos para a cláusula WHERE
            )

            // Você pode verificar se a atualização foi bem-sucedida com a variável "count"
            if (count > 0) {
                println("Linha atualizada com sucesso.")
            } else {
                println("Falha ao atualizar a linha.")
            }
        }

        fun doesDateExist(db: SQLiteDatabase, date: String): Boolean {
            // Define a cláusula WHERE para procurar a data
            val selection = "date = ?"

            // Define o argumento que será usado na cláusula WHERE
            val selectionArgs = arrayOf(date)

            // Realiza a consulta na tabela
            val cursor = db.query(
                "Measures",        // Nome da tabela
                arrayOf("date"),   // Colunas a serem retornadas
                selection,         // Cláusula WHERE
                selectionArgs,     // Argumentos da cláusula WHERE
                null,              // Group By
                null,              // Having
                null               // Order By
            )

            // Verifica se há algum resultado (se o cursor moveu para a primeira linha)
            val exists = cursor.moveToFirst()

            // Fecha o cursor para liberar recursos
            cursor.close()

            // Retorna true se a data foi encontrada, false caso contrário
            return exists
        }

    }
    fun biggestDate(date1: String, date2: String): String{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val laterDate = if (dateFormat.parse(date1).after(dateFormat.parse(date2))) date1 else date2
        return laterDate
    }
    fun saveData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun getBiggestDate() : String{
        return sharedPreferences.getString("biggestDate", "00/00/0000") ?: "00/00/0000" // '?' used to make sure null returns dont exist
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_WEIGHT NUMERIC,$COLUMN_UPPER_WAIST NUMERIC, $COLUMN_MID_WAIST NUMERIC, $COLUMN_LOWER_WAIST NUMERIC, $COLUMN_NECK NUMERIC, $COLUMN_DATE TEXT NOT NULL UNIQUE)"
        saveData("biggestDate","00/00/0000")
        db?.execSQL(createTableQuery)
    }



    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }
    fun insertAppMeasures(measures: AppMeasures) {
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_DATE, measures.date) //Convert date to long
            put(COLUMN_NECK, measures.neck)
            put(COLUMN_LOWER_WAIST, measures.lowerWaist)
            put(COLUMN_MID_WAIST, measures.midWaist)
            put(COLUMN_UPPER_WAIST, measures.upperWaist)
            put(COLUMN_WEIGHT, measures.weight)
        }
        db.insert(TABLE_NAME,null,values)
        saveData("biggestDate",biggestDate(getBiggestDate(),measures.date))

        db.close()
    }

    fun findByDate(date : String) : AppMeasures?{
        val db = readableDatabase
        val cursor = db.query(
            "Measures",            // Nome da tabela
            null,                 // Colunas a retornar (null para todas)
            "date = ?",           // Cláusula WHERE
            arrayOf(date),        // Argumentos para o WHERE
            null,                 // groupBy
            null,                 // having
            null                  // orderBy
        )

        // Verifica se encontrou algum resultado
        if (cursor != null && cursor.moveToFirst()) {
            // Extrai os valores e cria o data class
            val measure = AppMeasures(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                weight = cursor.getFloat(cursor.getColumnIndexOrThrow("weight")),
                upperWaist = cursor.getFloat(cursor.getColumnIndexOrThrow("upperWaist")),
                midWaist = cursor.getFloat(cursor.getColumnIndexOrThrow("midWaist")),
                lowerWaist = cursor.getFloat(cursor.getColumnIndexOrThrow("lowerWaist")),
                neck = cursor.getFloat(cursor.getColumnIndexOrThrow("neck")),
                date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            )
//            db.close()
            cursor.close()
            return measure
        }
//        db.close()
        cursor?.close()
        return null
    }
    fun measuresList(): ArrayList<AppMeasures>? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null, // Columns - null to select all columns
            null, // Selection - null to select all rows
            null, // Selection args
            null, // Group by
            null, // Having
            null  // Order by
        )

        val appMeasuresArrayList: ArrayList<AppMeasures> = ArrayList()

        if (cursor.moveToFirst()) {
            do {
                appMeasuresArrayList.add(
                    AppMeasures(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_UPPER_WAIST)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_MID_WAIST)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_LOWER_WAIST)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_NECK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return appMeasuresArrayList
    }

}