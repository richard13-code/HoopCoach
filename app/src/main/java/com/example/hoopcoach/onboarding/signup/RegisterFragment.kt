package com.example.hoopcoach.onboarding.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.hoopcoach.R
import com.example.hoopcoach.core.FragmentCommunicator
import com.example.hoopcoach.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RegisterViewModel>()
    private lateinit var communicator: FragmentCommunicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupValidation()
        setupClickListeners()

        communicator = requireActivity() as FragmentCommunicator

        return binding.root
    }

    private fun setupValidation() {
        // 1. Empezamos con el botón de "Siguiente" desactivado
        binding.btnNextRegister.isEnabled = false

        // 2. Escuchamos cambios en cada campo para validar al momento
        binding.ResEmailTiet.addTextChangedListener { validateFields() }
        binding.ResfPasswordTiet.addTextChangedListener { validateFields() }
        binding.ResConfPasswordTiet.addTextChangedListener { validateFields() }
    }

    private fun validateFields() {
        val email = binding.ResEmailTiet.text.toString().trim()
        val pass = binding.ResfPasswordTiet.text.toString().trim()
        val confirm = binding.ResConfPasswordTiet.text.toString().trim()

        binding.ResEmailTiet.error = viewModel.validateEmail(email)
        binding.ResfPasswordTiet.error = viewModel.validatePassword(pass)
        binding.ResConfPasswordTiet.error = viewModel.validateConfirmPassword(pass, confirm)

        // El botón de "Siguiente" se activa solo si el correo y las contraseñas están bien
        binding.btnNextRegister.isEnabled = viewModel.isRegisterFormValid(email, pass, confirm)
    }

    private fun setupClickListeners() {
        binding.btnNextRegister.setOnClickListener {
            val email = binding.ResEmailTiet.text.toString().trim()
            val password = binding.ResfPasswordTiet.text.toString().trim()

            val bundle = Bundle().apply {
                putString("EMAIL", email)
                putString("PASSWORD", password)
            }
            findNavController().navigate(
                R.id.action_registerFragment_to_personalInfoFragment,
                bundle
            )
        }

    }
}