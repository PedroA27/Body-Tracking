package com.example.bodytracking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bodytracking.databinding.ActivityDataInsertBinding
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.bodytracking.ConfigActivity.Companion.PREFS_NAME
import com.example.bodytracking.ConfigActivity.Companion.SWITCH_STATE_KEY

class DataInsertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataInsertBinding
    private lateinit var calendarConfigurator: CalendarConfigurator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataInsertBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        calendarConfigurator = CalendarConfigurator()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.actionBarColor));

        //-----------
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedSwitchState = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false)
        val viewsList = listOf(
            binding.dataInsert6,
            binding.hipsText,
            binding.insert6Box,
            binding.unitBox6,
            binding.unitBoxText6,
            binding.insertHips
        )
        updateViewsVisibility(viewsList, savedSwitchState)
        //-------------
        binding.icReturn.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        binding.insertDate.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            inputType = android.text.InputType.TYPE_NULL

            setOnClickListener {
                calendarConfigurator.hideKeyboard(this@DataInsertActivity,binding)
                Handler(Looper.getMainLooper()).postDelayed({
                    calendarConfigurator.showDatePickerDialog(this@DataInsertActivity,binding)
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
            val hips = binding.insertHips.text.toString().toFloatOrNull()
            val date = binding.insertDate.text.toString()
            val exists = MeasuresDatabaseHelper.doesDateExist(db, date)

            if (weight != null && upperWaist != null && midWaist != null && lowerWaist != null && neck != null && date.isNotEmpty()) {
                if(exists){
                    if(savedSwitchState){
                        MeasuresDatabaseHelper.updateMeasure(db,upperWaist, midWaist, weight, date, neck, lowerWaist, 0f)
                        Toast.makeText(this, "Operation Concluded", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        if (hips != null){
                            MeasuresDatabaseHelper.updateMeasure(db,upperWaist, midWaist, weight, date, neck, lowerWaist,hips)
                            Toast.makeText(this, "Operation Concluded", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this, "Please, insert all values correctly.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    if(savedSwitchState){
                        val appMeasures = AppMeasures(0, weight, upperWaist, midWaist, lowerWaist, neck,0f, date)
                        dbHelper.insertAppMeasures(appMeasures)
                        Toast.makeText(this, "Operation Concluded", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        if (hips != null) {
                            val appMeasures = AppMeasures(0, weight, upperWaist, midWaist, lowerWaist, neck,hips, date)
                            dbHelper.insertAppMeasures(appMeasures)
                            Toast.makeText(this, "Operation Concluded", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this, "Please, insert all values correctly.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                finish()
            }
            else{
                Toast.makeText(this, "Please, insert all values correctly.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateViewsVisibility(imageViews: List<View>, MaleFemale: Boolean) {
        val visibility = if (MaleFemale) View.GONE else View.VISIBLE
        imageViews.forEach { it.visibility = visibility }
    }
}