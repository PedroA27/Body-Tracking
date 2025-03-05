package com.example.bodytracking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bodytracking.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import com.github.mikephil.charting.charts.LineChart
import kotlin.math.log10

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    var sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

    private lateinit var chartConfigurator: ChartConfigurator
    private lateinit var lineChart: LineChart
    private var number: Int = 0


    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonDB.setOnClickListener {
            val dbHelper = MeasuresDatabaseHelper(this)
            val measures = dbHelper.measuresList()
            dbHelper.measuresList()

            if (!measures.isNullOrEmpty()) {
                Toast.makeText(this, measures.size.toString(), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No measures found", Toast.LENGTH_SHORT).show()
            }

        }
        val weightView = binding.averageWeight
        var formattedWeight = String.format("%.1f", averageWeight(number)).replace('.', ',')


        val bfView = binding.bfView
        val bf = String.format("%.1f", lastBodyFat()).replace('.', ',')
        val bfResult = "$bf %"
        bfView.text = bfResult

//        lineChart = findViewById(R.id.lineChart)
        lineChart = binding.lineChart
        chartConfigurator = ChartConfigurator(this)
        chartConfigurator.configureLineChart(lineChart,number)

        binding.weekButton.setOnClickListener {
            Log.d("69", "Line passed")
            number = 0
            chartConfigurator.configureLineChart(lineChart,number)
            formattedWeight = String.format("%.1f", averageWeight(number)).replace('.', ',')
            val weightResult = "$formattedWeight kg"
            weightView.text = weightResult
            println("Line 75")

        }
        binding.monthButton.setOnClickListener {
            println("Line 77")
            number = 1
            chartConfigurator.configureLineChart(lineChart,number)
            formattedWeight = String.format("%.1f", averageWeight(number)).replace('.', ',')
            val weightResult = "$formattedWeight kg"
            weightView.text = weightResult
            println("Line 84")
        }
        binding.yearButton.setOnClickListener {
            println("Line 86")
            number = 2
            chartConfigurator.configureLineChart(lineChart,number)
            formattedWeight = String.format("%.1f", averageWeight(number)).replace('.', ',')
            val weightResult = "$formattedWeight kg"
            weightView.text = weightResult

        }

        val weightResult = "$formattedWeight kg"
        weightView.text = weightResult
        binding.buttonEnterData.setOnClickListener {
            val intent = Intent(this,DataInsertActivity::class.java)
            startActivity(intent)

        }


    }
    // Is it breaking the app?
    override fun onResume() {
        super.onResume()
        chartConfigurator.configureLineChart(lineChart, number)
    }


    fun lastBodyFat(): Double{
        val dbHelper = MeasuresDatabaseHelper(this)
//        val db = dbHelper.readableDatabase
        val biggestDate = dbHelper.getBiggestDate()

        val appMeasures = dbHelper.findByDate(biggestDate)

        if (appMeasures != null) {

            return 36.76 + 86.01*log10((((appMeasures.midWaist + appMeasures.lowerWaist + appMeasures.upperWaist).toDouble())/3)/2.54 - appMeasures.neck.toDouble()/2.54) - 70.041*log10(178/2.54)
        }
        return 0.0
    }

//    fun getWeekDays(): List<String> {
//        val calendar = Calendar.getInstance() // Get today date
//
//        // Date format definition (exemplo: "dd/MM/yyyy")
//        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//
//        // Get first day of the week
//        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
//
//        // List with all week days
//        val weekDays = mutableListOf<String>()
//
//        // Put all the days into the list
//        for (i in 0..6) {
//            weekDays.add(dateFormat.format(calendar.time))
//            calendar.add(Calendar.DAY_OF_MONTH, 1) // Pass for the next day
//        }
//        return weekDays
//    }
//    fun getMonthDays(): List<String> {
//        val calendar = Calendar.getInstance() // Get today date
//
//        // Date format definition (exemplo: "dd/MM/yyyy")
//        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//
//        // Get first day of the month
//        calendar.set(Calendar.DAY_OF_MONTH, 1)
//
//        // List with all month days
//        val monthDays = mutableListOf<String>()
//
//        // Get last mday of the month
//        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
//
//        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//
//        // Put the days into a list
//        for (i in 1..daysInMonth) {
//            monthDays.add(dateFormat.format(calendar.time))
//            if (i == today) break
//            calendar.add(Calendar.DAY_OF_MONTH, 1) // Pass to the next day
////            return monthDays
//        }
//        return monthDays
//        //    Return all the entries
//    }
//    fun getYearDays(): List<String> {
//        val calendar = Calendar.getInstance() // Get today date
//
//        // Date format definition (exemplo: "dd/MM/yyyy")
//        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//
//        // Get first day of the year
//        calendar.set(Calendar.DAY_OF_YEAR, 1)
//
//        // List with all year days
//        val yearDays = mutableListOf<String>()
//
//        // Get last year day
//        val daysInYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
//
//        val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
//
//        // Put the days into a list
//        for (i in 1..daysInYear) {
//            yearDays.add(dateFormat.format(calendar.time))
//            if (i == today) break
//            calendar.add(Calendar.DAY_OF_YEAR, 1) // Pass to the next day
//        }
//        return yearDays
//        //    Return all the entries
//    }
    fun averageWeight(number: Int): Float {
        var days = ChartConfigurator.getWeekDays()
        when(number){
            0 -> days = ChartConfigurator.getWeekDays()
            1 -> days = ChartConfigurator.getMonthDays()
            2 -> days = ChartConfigurator.getYearDays()
        }

        val dbHelper = MeasuresDatabaseHelper(this)
        val db = dbHelper.readableDatabase
        var total : Float = 0.00f
        var divisor = 0
        for (i in days) {
            if(MeasuresDatabaseHelper.doesDateExist(db,removeDateLeadingZeros(i))) {
                divisor += 1
                total += (dbHelper.findByDate(removeDateLeadingZeros(i))?.weight ?: 0.00f)
            }
        }
        if(divisor != 0){
            return total/divisor
        }
        return 0.00f
    }
    fun removeDateLeadingZeros(date: String): String{
        val parts = date.split("/")
        val removeZeros = parts.map { it.toInt().toString()}
        return removeZeros.joinToString("/")
    }



}