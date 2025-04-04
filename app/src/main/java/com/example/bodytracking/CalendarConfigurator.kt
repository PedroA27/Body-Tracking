package com.example.bodytracking

import android.app.DatePickerDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.example.bodytracking.databinding.ActivityConfigBinding
import java.util.Calendar

class CalendarConfigurator {
//    fun showDatePickerDialog(context: Context, binding : ViewDataBinding) {
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//
//        val datePickerDialog = DatePickerDialog(
//            context,R.style.CustomDatePickerTheme,
//            { _, selectedYear, selectedMonth, selectedDay ->
//                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
//                binding.insertDate.setText(selectedDate)
//            },
//            year, month, day
//        )
//
//        datePickerDialog.show()
//    }
//    private fun hideKeyboard(context: Context, binding: ActivityConfigBinding) {
//        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        // Hide Keyboard when putting Date
//        val view = currentFocus ?: binding.root
//        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//    }
}