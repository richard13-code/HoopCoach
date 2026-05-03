package com.example.hoopcoach.core

import com.google.firebase.auth.FirebaseUser

interface Authentication {

    suspend fun requestLogin(email: String, password: String): ResponseService<FirebaseUser>
    suspend fun requestSignIn(email: String, password: String): ResponseService<FirebaseUser>
    suspend fun saveUserProfile(userId: String, name: String, lastName: String,
                                middleName: String, phone: String, birthday: String): ResponseService<Boolean>

}
