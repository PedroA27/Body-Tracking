package com.example.bodytracking

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bodytracking.ConfigActivity.Companion.HEIGHT
import com.example.bodytracking.ConfigActivity.Companion.PREFS_NAME
import com.example.bodytracking.ConfigActivity.Companion.SWITCH_STATE_KEY
import com.example.bodytracking.ConfigActivity.Companion.USER_NAME
import com.example.bodytracking.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.LineChart
import java.io.File
import java.util.Locale
import kotlin.math.log10

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
//    var sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

    private lateinit var chartConfigurator: ChartConfigurator
    private lateinit var lineChart: LineChart
    private var number: Int = 0

    companion object {
        const val WEEK_GOAL = "weekGoal"

    }

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
        val window: Window = this.getWindow()
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //  add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.actionBarColor));

        val weightView = binding.averageWeight
        var formattedWeight = String.format("%.1f", averageWeight(number)).replace('.', ',')

        //--------
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedSwitchState = sharedPreferences.getBoolean(SWITCH_STATE_KEY, false)
        //-------------
        val bfView = binding.bfView
        val bf = String.format("%.1f", lastBodyFat(savedSwitchState)).replace('.', ',')
        val bfResult = "$bf %"
        bfView.text = bfResult
        //________________________
        //customName
        val customGoal = binding.weekGoal

// Load and format the saved value for display
        val sharedPreferencesWeek: String? = sharedPreferences.getString(WEEK_GOAL, null)
        if (sharedPreferencesWeek != null && sharedPreferencesWeek.trim().length > 0) {
            customGoal.setText(sharedPreferencesWeek)
        }
        customGoal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var text = s?.toString()?.trim() ?: ""
                sharedPreferences.edit().apply {
                    if (text.isBlank()) {
                        remove(WEEK_GOAL) // Clear if empty to show hint
                    } else {
                        putString(WEEK_GOAL, text)
                    }
                }.apply()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })



//        lineChart = findViewById(R.id.lineChart)
        lineChart = binding.lineChart
        chartConfigurator = ChartConfigurator(this)
        chartConfigurator.configureLineChart(lineChart,number)
        loadSavedImage(binding.root.context, binding.circleUser)

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
        binding.config.setOnClickListener {
            val intent = Intent(this,ConfigActivity::class.java)
            startActivity(intent)

        }


    }
    // Is it breaking the app?
    override fun onResume() {
        super.onResume()
        chartConfigurator.configureLineChart(lineChart, number)
    }



    fun lastBodyFat(maleFemale: Boolean): Double{
        val dbHelper = MeasuresDatabaseHelper(this)
//        val db = dbHelper.readableDatabase
        val biggestDate = dbHelper.getBiggestDate()

        val appMeasures = dbHelper.findByDate(biggestDate)
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentHeight = sharedPreferences.getString(HEIGHT, null)?.toFloatOrNull()
        println("Line 139 "+currentHeight)
        if(maleFemale) {
            if (appMeasures != null && currentHeight != null) {

                return 36.76 + 86.01 * log10((((appMeasures.midWaist + appMeasures.lowerWaist + appMeasures.upperWaist).toDouble()) / 3) * 0.393701 - appMeasures.neck.toDouble() * 0.393701) - 70.041 * log10(
                    currentHeight * 0.393701
                )
            }
            return 0.0
        }
        else{
            if (appMeasures != null && currentHeight != null) {

                return 163.205 * log10((((appMeasures.midWaist + appMeasures.lowerWaist + appMeasures.upperWaist).toDouble()) / 3) * 0.393701 - appMeasures.neck.toDouble() * 0.393701 + appMeasures.hips.toDouble() * 0.393701) - 97.684 * log10(currentHeight * 0.393701  ) - 78.387
            }
            return 0.0

        }
//        return 0.0
    }

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
    private fun loadSavedImage(context: Context, imageView: ImageView) {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val imagePath = sharedPreferences.getString("profile_image_path", null)

        if (imagePath != null) {
            val file = File(imagePath)
            if (file.exists()) {
                imageView.setImageURI(Uri.parse(imagePath))
            }
        }
    }


}