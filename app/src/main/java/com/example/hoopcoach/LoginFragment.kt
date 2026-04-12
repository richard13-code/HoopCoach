package com.example.hoopcoach

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hoopcoach.databinding.FragmentLoginBinding
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment() {
    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        setupValidation()
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

        val isEmailValid = isValidEmail(email)
        val isPasswordValid = password.length >= 8

        binding.emailTiet.error = if (email.isNotEmpty() || isEmailValid) null else "Correo inválido"
        binding.passwordTiet.error = if (password.isNotEmpty() || isPasswordValid) null else "Mínimo 8 caracteres"

        binding.signInButton.isEnabled = email.isNotEmpty() && password.isNotEmpty() && isEmailValid && isPasswordValid
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


}