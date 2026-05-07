package com.example.hoopcoach.core.repostories

import com.example.hoopcoach.core.ResponseService
import com.example.hoopcoach.onboarding.personal.model.UserProfile

interface UserService {
    suspend fun saveUserInfo(userProfile: UserProfile): ResponseService<Unit>
}