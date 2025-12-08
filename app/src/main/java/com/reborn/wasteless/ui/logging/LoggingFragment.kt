package com.reborn.wasteless.ui.logging

import android.app.Activity
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
import java.util.Calendar

class LoggingFragment : Fragment() {

    private var _binding: FragmentLoggingBinding? = null
    private val binding get() = _binding!!
    private val vm: LoggingViewModel by viewModels()

    //ActivityResult launcher
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val uri = result.data?.data
                    if (uri != null) {
                        binding.photoCaptureView.setImageURI(uri)
                        // NEW: stash for upload
                        vm.imageUri.value = uri
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    val errorMsg = ImagePicker.getError(result.data)
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // This single block handles the initial "Autofill" AND any future updates.
        vm.dateTime.observe(viewLifecycleOwner) { timestamp ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp

            binding.dateTimeInput.text = formatCalendarToString(cal)
        }

        //UI features from top to bottom (according to XML)

        //1. Photo Picker
        binding.photoCaptureView.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent -> imagePickerLauncher.launch(intent) }
        }

        //2. Input for date/time, it should autofill onCreate
        binding.dateTimeInput.setOnClickListener {
            showDateTimePicker()
        }

        //Cancel button
        binding.toolbarLoggingNo.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Helper function to ensure date format is consistent
     * between the "Autofill" and the "Picker"
     *
     * @param calendar The instance of calendar dialog, based on the user's sys
     */
    private fun formatCalendarToString(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is 0-indexed
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Format the output for the TextView
        // Example: 08/Dec/2025 03:45 AM
        return "%02d/%02d/%04d %02d:%02d".format(day, month, year, hour, minute)
    }

    /**
     * Function for showing the date picker -> followed by the time picker based on android's dialog lib
     * Call this function when DateTimeButton is clicked
     *
     * No params
     *
     */
    private fun showDateTimePicker() {
        val currentTimeStamp = vm.dateTime.value ?: System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeStamp

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->

                // Show the TimePickerDialog right after the date selection
                TimePickerDialog(
                    requireContext(),
                    { _, selectedHour, selectedMinute ->

                        // Combine results into Calendar inst.
                        calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)

                        // Store the combined timestamp in the ViewModel, so no necessary conversion is needed later
                        vm.dateTime.value = calendar.timeInMillis

                    },
                    calendar.get(Calendar.HOUR_OF_DAY), // Initial hour for TimePicker
                    calendar.get(Calendar.MINUTE), // Initial minute for TimePicker
                    false // Set to true for 24-hour format
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showOverlay() {
        binding.loggingOverlayPanel.visibility = View.VISIBLE
    }

    private fun hideOverlay() {
        binding.loggingOverlayPanel.visibility = View.GONE
    }
}

