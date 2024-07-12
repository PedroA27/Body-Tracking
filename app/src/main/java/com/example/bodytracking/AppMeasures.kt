package com.example.bodytracking

import android.health.connect.datatypes.WeightRecord
import android.icu.util.Measure
import java.util.Date


data class AppMeasures(val id: Int, val weightRecord: Float, val upperWaist: Float, val midWaist: Float, val lowerWaist: Float, val neck: Float, val date: Date)