package com.example.hoopcoach.onboarding.personal

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.hoopcoach.core.FragmentCommunicator
import com.example.hoopcoach.core.ResponseService
import com.example.hoopcoach.databinding.FragmentPersonalInfoBinding
import com.example.hoopcoach.home.HomeActivity
import com.example.hoopcoach.onboarding.personal.PersonalInfoViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar

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
        setupValidation()
        setupDatePicker()
        setupClickListeners()
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

    private fun setupValidation() {
        binding.btnRegister.isEnabled = false
        binding.NameTiet.addTextChangedListener { validateAndEnable() }
        binding.LastNameTiet.addTextChangedListener { validateAndEnable() }
        binding.userNameTiet.addTextChangedListener { validateAndEnable() }
        binding.phoneTiet.addTextChangedListener { validateAndEnable() }
        binding.BirthdayTiet.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val firstName = binding.NameTiet.text.toString().trim()
        val lastName = binding.LastNameTiet.text.toString().trim()
        val username = binding.userNameTiet.text.toString().trim()
        val phone = binding.phoneTiet.text.toString().trim()
        val birthDate = binding.BirthdayTiet.text.toString().trim()

        binding.NameTiet.error = viewModel.validateFirstName(firstName)
        binding.LastNameTiet.error = viewModel.validateLastName(lastName)
        binding.userNameTiet.error = viewModel.validateUsername(username)
        binding.phoneTiet.error = viewModel.validatePhone(phone)
        binding.BirthdayTiet.error = viewModel.validateBirthDate(birthDate)

        binding.btnRegister.isEnabled =
            viewModel.isFormValid(firstName, lastName, username, phone, birthDate)
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Snackbar.make(binding.root, "Sesión inválida", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.saveProfile(
                uid = uid,
                firstName = binding.NameTiet.text.toString().trim(),
                lastName = binding.LastNameTiet.text.toString().trim(),
                username = binding.userNameTiet.text.toString().trim(),
                phone = binding.phoneTiet.text.toString().trim(),
                birthDate = binding.BirthdayTiet.text.toString().trim()
            )
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.btnRegister.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            val intent = Intent(requireContext(), HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            Toast.makeText(requireContext(), "¡Bienvenido a HoopCoach!", Toast.LENGTH_LONG).show()
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            binding.btnRegister.isEnabled = true
                            Snackbar.make(binding.root, state.error, Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }
}