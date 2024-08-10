package com.example.bodytracking

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Toast
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bodytracking.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.buttonEnterData.setOnClickListener {
            val intent = Intent(this,DataInsertActivity::class.java)
            startActivity(intent)
        }

        binding.buttonDB.setOnClickListener {
            val dbHelper = MeasuresDatabaseHelper(this)
            dbHelper.printAllMeasures()
        }
    }
}