package com.example.bodytracking
import android.content.Context
import android.graphics.Color
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IValueFormatter

import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.properties.Delegates

class ChartConfigurator(private val context: Context) {
    fun removeDateLeadingZeros(date: String): String{
        val parts = date.split("/")
        val removeZeros = parts.map { it.toInt().toString()}
        return removeZeros.joinToString("/")
    }
    fun getEntrys(days: List<String>): List<Entry> {
        val dbHelper = MeasuresDatabaseHelper(context)
        val db = dbHelper.readableDatabase
        val entries = mutableListOf<Entry>()
        var index = 0
        for (i in days) {
            if(MeasuresDatabaseHelper.doesDateExist(db,removeDateLeadingZeros(i))) {
                val weight = (dbHelper.findByDate(removeDateLeadingZeros(i))?.weight ?: 0.00f)
                entries.add(Entry(index.toFloat(), weight))
            }
            else {
                entries.add(Entry(index.toFloat(), Float.NaN))
            }
            index++
        }
        return entries
    }
    fun configureLineChart(lineChart: LineChart, dateIndex: Int) {
        val customY = YValueFormatter()
        var customX = XValueFormatter(getWeekDays(),lineChart)
        when(dateIndex){
            0->customX = XValueFormatter(getWeekDays(),lineChart)
            1->customX = XValueFormatter(getMonthDays(),lineChart)
            2->customX = XValueFormatter(getYearDays(),lineChart)
        }
        val customFont = ResourcesCompat.getFont(context, R.font.palanquin_bold)



        // General Chart Configurations
        lineChart.setExtraOffsets(0f,8f, 0f,0f)
        lineChart.xAxis.textSize = 16f
        lineChart.xAxis.textColor = Color.WHITE
        lineChart.xAxis.typeface = customFont
        lineChart.xAxis.valueFormatter = customX
        lineChart.xAxis.granularity = 1f
        lineChart.xAxis.spaceMin = .3f
        lineChart.xAxis.spaceMax = .3f

        lineChart.xAxis.isGranularityEnabled = true



        lineChart.xAxis.setDrawGridLines(false)
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisRight.setDrawGridLines(false)



        //Left Subtitle
        lineChart.axisLeft.textSize = 16f
        lineChart.axisLeft.textColor = Color.WHITE
        lineChart.axisLeft.typeface = customFont
        lineChart.axisLeft.valueFormatter = customY
        lineChart.axisLeft.granularity = 1f

        //Right Subtitle
        lineChart.axisRight.textSize = 16f
        lineChart.axisRight.textColor = Color.WHITE
        lineChart.axisRight.typeface = customFont
        lineChart.axisRight.valueFormatter = customY
        lineChart.axisRight.granularity = 1f


        // Subtitle Configurations
        val legend = lineChart.legend
        legend.typeface = customFont
        legend.textColor = Color.WHITE
        legend.textSize = 16f
        legend.formSize = 16f
        legend.form = Legend.LegendForm.LINE


        //Linha
        var lastValidY = 0f
        val entries = mutableListOf<Entry>()


        when(dateIndex){
            0 ->getEntrys(getWeekDays()).forEachIndexed{ index, value ->
                val y = if (value.y.isNaN()) lastValidY else value.y
                if(!value.y.isNaN()){
                    lastValidY = value.y
                }
                entries.add(Entry(index.toFloat(),y))
            }
            1 ->getEntrys(getMonthDays()).forEachIndexed{ index, value ->
                val y = if (value.y.isNaN()) lastValidY else value.y
                if(!value.y.isNaN()){
                    lastValidY = value.y
                }
                entries.add(Entry(index.toFloat(),y))
            }
            2 ->getEntrys(getYearDays()).forEachIndexed{ index, value ->
                val y = if (value.y.isNaN()) lastValidY else value.y
                if(!value.y.isNaN()){
                    lastValidY = value.y
                }
                entries.add(Entry(index.toFloat(),y))
            }

        }
        val lineDataSet = LineDataSet(entries, "Peso")
        lineDataSet.setDrawValues(false)
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawCircleHole(false)

        lineDataSet.setDrawCircles(false)
        lineDataSet.color = Color.RED
        lineDataSet.lineWidth = 2f


        lineChart.data = LineData(lineDataSet)
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.xAxis.setLabelCount(2, false)
//        lineChart.xAxis.setLabelCount(entries.size, true)


        // Personalization
        lineChart.setDrawGridBackground(false)
        lineChart.setBackgroundColor(Color.TRANSPARENT)

        // Graphic update
        lineChart.invalidate()
    }
    companion object {
        fun getWeekDays(): List<String> {
            val calendar = Calendar.getInstance() // Get today date

            // Date format definition (exemplo: "dd/MM/yyyy")
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Get first day of the week
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

            // List with all week days
            val weekDays = mutableListOf<String>()

            // Put all the days into the list
            for (i in 0..6) {
                weekDays.add(dateFormat.format(calendar.time))
                calendar.add(Calendar.DAY_OF_MONTH, 1) // Pass for the next day
            }
            return weekDays
        }

        fun getMonthDays(): List<String> {
            Log.d("158", "Line passed")
            val calendar = Calendar.getInstance() // Get today date

            // Date format definition (exemplo: "dd/MM/yyyy")
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Get first day of the month
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            // List with all month days
            val monthDays = mutableListOf<String>()

            // Get last mday of the month
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            println("Line 173")
            // Put the days into a list
            for (i in 1..daysInMonth) {
                monthDays.add(dateFormat.format(calendar.time))
                if (i == today) break
                calendar.add(Calendar.DAY_OF_MONTH, 1) // Pass to the next day
//            return monthDays
            }
            println(monthDays)
            return monthDays
            //    Return all the entries
        }

        fun getYearDays(): List<String> {
            val calendar = Calendar.getInstance() // Get today date

            // Date format definition (exemplo: "dd/MM/yyyy")
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Get first day of the year
            calendar.set(Calendar.DAY_OF_YEAR, 1)

            // List with all year days
            val yearDays = mutableListOf<String>()

            // Get last year day
            val daysInYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)

            val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

            // Put the days into a list
            for (i in 1..daysInYear) {
                yearDays.add(dateFormat.format(calendar.time))
                if (i == today) break
                calendar.add(Calendar.DAY_OF_YEAR, 1) // Pass to the next day
            }
            return yearDays
            //    Return all the entries
        }
    }

}



class YValueFormatter : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return "${value.toInt()} kg" // Exemplo: formata como "75 kg"
    }
}



class XValueFormatter(private val days: List<String>, private val lineChart: LineChart) : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val index = value.toInt()

        // Verify the index is within bounds
        return if (index >= 0 && index < days.size) {
            days[index]
        } else {
            "" // Return empty string for invalid indices
        }
    }
}