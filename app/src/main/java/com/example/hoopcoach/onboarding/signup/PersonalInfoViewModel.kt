package com.example.hoopcoach.onboarding.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hoopcoach.core.AuthRepository
import com.example.hoopcoach.core.ResponseService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalInfoViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _registerState = MutableStateFlow<ResponseService<FirebaseUser>?>(null)
    val registerState: StateFlow<ResponseService<FirebaseUser>?> = _registerState.asStateFlow()

    fun completeRegistration(
        email: String, pass: String, name: String,
        lastName: String, middleName: String, phone: String, birthday: String
    ) {
        viewModelScope.launch {
            _registerState.value = ResponseService.Loading

            // PASO 1: Crear cuenta en Firebase Auth
            val authResponse = authRepository.requestSignIn(email, pass)

            if (authResponse is ResponseService.Success) {
                val user = authResponse.data
                val userId = user?.uid ?: ""

                // PASO 2: Guardar datos en Firestore
                val dbResponse = authRepository.saveUserProfile(
                    userId, name, lastName, middleName, phone, birthday
                )

                if (dbResponse is ResponseService.Success) {
                    _registerState.value = ResponseService.Success(user!!)
                } else {
                    _registerState.value = ResponseService.Error("Cuenta creada, pero error en perfil")
                }
            } else {
                _registerState.value = authResponse
            }
        }
    }
}