package com.example.bodytracking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bodytracking.databinding.ActivityDataInsertBinding

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
        val db = MeasuresDatabaseHelper(this)
        binding.buttonPanel.setOnClickListener{
            val weight = binding.insertWeight.toString().toFloat()
            val upperWaist = binding.insertUpperWaist.toString().toFloat()
            val midWaist = binding.insertMidWaist.toString().toFloat()
            val lowerWaist = binding.insertLowerWaist.toString().toFloat()
            val neck = binding.insertNeck.toString().toFloat()
            val date = binding.insertDate.toString()
            val appMeasures = AppMeasures(0,weight,upperWaist,midWaist,lowerWaist,neck,date)
            db.insertAppMeasures(appMeasures)
            finish()
        }


    }
}