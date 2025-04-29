package com.example.bodytracking

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
        lineChart.setDrawGridBackground(false)
        lineChart.setBackgroundColor(Color.TRANSPARENT)
        lineChart.invalidate()
    }

    companion object {
        fun getWeekDays(): List<String> {
            val calendar = Calendar.getInstance()

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

            val weekDays = mutableListOf<String>()

            for (i in 0..6) {
                weekDays.add(dateFormat.format(calendar.time))
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            return weekDays
        }

        fun getMonthDays(): List<String> {
            val calendar = Calendar.getInstance()

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            calendar.set(Calendar.DAY_OF_MONTH, 1)

            val monthDays = mutableListOf<String>()

            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            for (i in 1..daysInMonth) {
                monthDays.add(dateFormat.format(calendar.time))
                if (i == today) break
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            return monthDays
        }

        fun getYearDays(): List<String> {
            val calendar = Calendar.getInstance()

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            calendar.set(Calendar.DAY_OF_YEAR, 1)

            val yearDays = mutableListOf<String>()

            val daysInYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)

            val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

            for (i in 1..daysInYear) {
                yearDays.add(dateFormat.format(calendar.time))
                if (i == today) break
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            return yearDays
        }
    }
}



class YValueFormatter : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return "${value.toInt()} kg"
    }
}

class XValueFormatter(private val days: List<String>, private val lineChart: LineChart) : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val index = value.toInt()
        return if (index >= 0 && index < days.size) {
            days[index]
        } else {
            ""
        }
    }
}