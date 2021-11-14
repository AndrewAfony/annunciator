package com.example.annunciator.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"
private const val REQUEST_KEY = "requestKey"

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        fun newInstance(date: Date, key: String): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
                putString(REQUEST_KEY, key)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = arguments?.getSerializable(ARG_DATE) as Date

        val calendar = Calendar.getInstance()
        calendar.time = date

        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog (requireContext(), this, initialYear, initialMonth, initialDay)

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        val resultDate : Date = GregorianCalendar(year, month, dayOfMonth).time

        parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf("bundleKey" to resultDate))
    }


}