package com.example.hoopcoach.home.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.hoopcoach.core.ResponseService
import com.example.hoopcoach.databinding.FragmentAccountBinding
import com.example.hoopcoach.onboarding.MainActivity
import com.example.hoopcoach.onboarding.personal.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AccountViewModel>()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeState()
        
        // Solicitar los datos del usuario actual
        viewModel.fetchUserData()
    }

    private fun setupClickListeners() {
        binding.btnCerrarSesion.setOnClickListener {
            logout()
        }
        
        // Listeners adicionales (ejemplo)
        binding.btnEditPhoto.setOnClickListener {
            Toast.makeText(requireContext(), "Función de edición próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfileState.collect { response ->
                    when (response) {
                        is ResponseService.Loading -> {
                            // Aquí podrías mostrar un shimmer o progreso si lo deseas
                        }
                        is ResponseService.Success -> {
                            updateUI(response.data)
                        }
                        is ResponseService.Error -> {
                            Toast.makeText(requireContext(), response.error, Toast.LENGTH_SHORT).show()
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    private fun updateUI(user: UserProfile) {
        // Nombre de usuario y correo (de Auth)
        binding.tvUserName.text = user.userName
        binding.tvUserEmail.text = auth.currentUser?.email ?: "Sin correo"
        
        // Información personal
        binding.tvDisplayFullName.text = "${user.firstName} ${user.lastName}"
        binding.tvDisplayPhone.text = user.phone
        
        // Inicial del Avatar
        if (user.firstName.isNotEmpty()) {
            binding.tvAvatarInitial.text = user.firstName.take(1).uppercase()
        }
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
