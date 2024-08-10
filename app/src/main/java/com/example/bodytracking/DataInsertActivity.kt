package com.example.bodytracking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bodytracking.databinding.ActivityDataInsertBinding
import java.util.Calendar
import android.app.DatePickerDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
class DataInsertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataInsertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataInsertBinding.inflate(layoutInflater)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.icReturn.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
//            Toast.makeText(this, "Aoba", Toast.LENGTH_SHORT).show()
        }

//        binding.insertDate.setOnClickListener {
//            hideKeyboard()
//            showDatePickerDialog()
//        }

//        binding.insertDate.setOnTouchListener() { v, event ->
//            if (event.action == android.view.MotionEvent.ACTION_UP) {
//                v.performClick() // Garante que o click seja registrado
//            }
//            true // Consume o evento de toque para impedir o teclado
//        }
//        binding.insertDate.inputType = android.text.InputType.TYPE_NULL
//
//        binding.insertDate.apply {
//            isFocusable = false
//            isFocusableInTouchMode = false
//            inputType = android.text.InputType.TYPE_NULL
//
//            setOnClickListener {
//                hideKeyboard()
//                Handler(Looper.getMainLooper()).postDelayed({
//                    showDatePickerDialog()
//                }, 50)
//            }
//
//            setOnTouchListener { v, event ->
//                if (event.action == android.view.MotionEvent.ACTION_UP) {
//                    v.performClick()
//                }
//                true
//            }
//        }
        binding.insertDate.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            inputType = android.text.InputType.TYPE_NULL

            setOnClickListener {
                hideKeyboard()
                Handler(Looper.getMainLooper()).postDelayed({
                    showDatePickerDialog()
                }, 15)
            }

            setOnTouchListener { v, event ->
                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    v.performClick()
                }
                true
            }
        }




        binding.buttonPanel.setOnClickListener{
            val dbHelper = MeasuresDatabaseHelper(this)
            val db = dbHelper.readableDatabase
            val weight = binding.insertWeight.text.toString().toFloatOrNull()
            val upperWaist = binding.insertUpperWaist.text.toString().toFloatOrNull()
            val midWaist = binding.insertMidWaist.text.toString().toFloatOrNull()
            val lowerWaist = binding.insertLowerWaist.text.toString().toFloatOrNull()
            val neck = binding.insertNeck.text.toString().toFloatOrNull()
            val date = binding.insertDate.text.toString()

            val exists = MeasuresDatabaseHelper.doesDateExist(db, date)

            if (weight != null && upperWaist != null && midWaist != null && lowerWaist != null && neck != null && date.isNotEmpty()) {
                if(exists){
                    MeasuresDatabaseHelper.updateMeasure(db,upperWaist, midWaist, weight, date, neck, lowerWaist)
                    Toast.makeText(this, "Operação Concluída", Toast.LENGTH_SHORT).show()

                }
                else {
                    val appMeasures =
                        AppMeasures(0, weight, upperWaist, midWaist, lowerWaist, neck, date)
                    dbHelper.insertAppMeasures(appMeasures)
                    Toast.makeText(this, "Operação Concluída", Toast.LENGTH_SHORT).show()

                }
                finish()
            }
            else{
                Toast.makeText(this, "Por favor, insira todos os valores corretamente.", Toast.LENGTH_SHORT).show()
            }

        }




    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.insertDate.setText(selectedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Substituir currentFocus por binding.root ou null
        val view = currentFocus ?: binding.root
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}