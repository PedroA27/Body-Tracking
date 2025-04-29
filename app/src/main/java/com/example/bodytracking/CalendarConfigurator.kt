package com.example.bodytracking

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.viewbinding.ViewBinding
import com.example.bodytracking.databinding.ActivityConfigBinding
import com.example.bodytracking.databinding.ActivityDataInsertBinding
import java.util.Calendar

class CalendarConfigurator {
    fun showDatePickerDialog(context: Context, binding : ViewBinding) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            context,R.style.CustomDatePickerTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                when (binding) {
                    is ActivityConfigBinding -> binding.insertDate.setText(selectedDate)
                    is ActivityDataInsertBinding -> binding.insertDate.setText(selectedDate)
                    else -> throw IllegalArgumentException("Unsupported binding")
                }
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    fun hideKeyboard(activity: Activity, binding: ViewBinding) {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus ?: binding.root
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}