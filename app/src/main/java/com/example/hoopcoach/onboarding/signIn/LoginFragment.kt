package com.example.hoopcoach.onboarding.signIn

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
import androidx.navigation.fragment.findNavController
import com.example.hoopcoach.R
import com.example.hoopcoach.core.FragmentCommunicator
import com.example.hoopcoach.core.ResponseService
import com.example.hoopcoach.databinding.FragmentLoginBinding
import com.example.hoopcoach.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SignInViewModel>() //Enlace con el binding y viewmodel

    private lateinit var communicator: FragmentCommunicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        communicator = activity as FragmentCommunicator
        setupValidation()
        setupClickListeners()
        observeState()
        binding.textRecoverPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment2_to_passwordFragment)
        }

        binding.textRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment2_to_registerFragment)
        }
        return binding.root
    }

    private fun setupValidation(){
        binding.signInButton.isEnabled = false

        binding.emailTiet.addTextChangedListener {
            validateFields()
        }
        binding.passwordTiet.addTextChangedListener {
            validateFields()
        }
    }

    private fun validateFields() {
        val email = binding.emailTiet.text.toString().trim()
        val password = binding.passwordTiet.text.toString().trim()

        binding.emailTiet.error = viewModel.validateEmail(email)
        binding.passwordTiet.error = viewModel.validatePassword(password)
        binding.signInButton.isEnabled = viewModel.isLoginFormValid(email, password)
    }

    private fun setupClickListeners() {
        binding.signInButton.setOnClickListener {
            val email = binding.emailTiet.text.toString().trim()
            val password = binding.passwordTiet.text.toString().trim()
            viewModel.requestLogin(email, password)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.signInButton.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            Toast.makeText(requireContext(), "¡Sesión Iniciada!", Toast.LENGTH_LONG).show()
                            val intent = Intent(requireContext(), HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            binding.signInButton.isEnabled = true
                            Snackbar.make(binding.root, state.error,
                                Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }



}