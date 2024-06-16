package com.ambassador.app.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.DatePicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.DialogFragment
import com.ambassador.app.R
import java.util.Calendar
import java.util.Date


class Utility {

    companion object {
        @Composable
        fun stringToDate(it: String): Date {
            val sdf = SimpleDateFormat(stringResource(R.string.date_pattern))
            return sdf.parse(it)
        }
        @Composable
        fun dateToString(date: Date): String {
            val sdf = SimpleDateFormat(stringResource(R.string.date_pattern))
            return sdf.format(date)
        }
    }
}
