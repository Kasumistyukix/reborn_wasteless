package com.reborn.wasteless.ui.logging

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.reborn.wasteless.databinding.FragmentLoggingBinding
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TextView
import java.util.Calendar

class LoggingFragment : Fragment() {

    private var _binding: FragmentLoggingBinding? = null
    private val binding get() = _binding!!
    private val vm: LoggingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoggingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Cancel button
        binding.toolbarLoggingNo.setOnClickListener {
            findNavController().popBackStack()
        }

        //Input for date/time
        binding.dateTimeInput.setOnClickListener {
            showDateTimePicker(binding.dateTimeInput)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDateTimePicker(dateTimeText: TextView) {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // This runs AFTER the user selects a date and hits OK on the DatePicker
                // Immediately show the TimePickerDialog right after the date selection
                TimePickerDialog(
                    requireContext(),
                    { _, selectedHour, selectedMinute ->

                        // Combine results into Calendar inst.
                        calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
                        val combinedTimestamp = calendar.timeInMillis

                        // Format the output for the TextView
                        // Example: 08/Dec/2025 03:45 AM
                        val formatted = "%02d/%02d/%04d %02d:%02d".format(
                            selectedDay,
                            selectedMonth + 1, // Month is 0-indexed in Calendar
                            selectedYear,
                            selectedHour,
                            selectedMinute
                        )
                        dateTimeText.text = formatted

                        // Store the combined timestamp in the ViewModel
                        // You need to ensure you can access your viewModel instance here
                        // viewModel.date.value = combinedTimestamp

                    },
                    hour, // Initial hour for TimePicker
                    minute, // Initial minute for TimePicker
                    false // Set to true for 24-hour format
                ).show()
            },
            year,
            month,
            day
        ).show()

        // You would call this function when a button is clicked:
        // binding.pickDateTimeButton.setOnClickListener {
        //     showDateTimePicker(binding.dateTimeTextView)
        // }
    }

    private fun showOverlay() {
        binding.loggingOverlayPanel.visibility = View.VISIBLE
    }

    private fun hideOverlay() {
        binding.loggingOverlayPanel.visibility = View.GONE
    }
}

