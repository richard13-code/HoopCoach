package com.example.hoopcoach.onboarding.signup

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.hoopcoach.core.FragmentCommunicator
import com.example.hoopcoach.core.ResponseService
import com.example.hoopcoach.databinding.FragmentPersonalInfoBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.getValue

class PersonalInfoFragment : Fragment() {
    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PersonalInfoViewModel>()
    private lateinit var communicator: FragmentCommunicator


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator

        // 1. Recuperar datos del Bundle
        val email = arguments?.getString("EMAIL") ?: ""
        val pass = arguments?.getString("PASSWORD") ?: ""

        setupDatePicker()
        setupClickListeners(email, pass)
        observeState()

        return binding.root
    }

    private fun setupDatePicker() {
        binding.BirthdayTiet.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    requireContext(),
                    { _, year, month, day ->
                        val selectedDate = "$day/${month + 1}/$year"
                        setText(selectedDate)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

    private fun setupClickListeners(email: String, pass: String) {
        binding.btnRegister.setOnClickListener {
            val name = binding.NameTiet.text.toString().trim()
            val lastName = binding.LastNameTiet.text.toString().trim()
            val middleName = binding.MiddleNameTiet.text.toString().trim()
            val phone = binding.phoneTiet.text.toString().trim()
            val birthday = binding.BirthdayTiet.text.toString().trim()

            if (name.isNotEmpty() && lastName.isNotEmpty() && phone.isNotEmpty()) {
                viewModel.completeRegistration(email, pass, name, lastName, middleName, phone, birthday)
            } else {
                Snackbar.make(binding.root, "Llena los campos obligatorios", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> communicator.manageLoader(true)
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            // Navegar a la Home de HoopCoach
                            Toast.makeText(requireContext(), "¡Bienvenido a HoopCoach!", Toast.LENGTH_LONG).show()
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }
}
