package com.example.hoopcoach.core.network

import com.example.hoopcoach.core.ResponseService
import com.example.hoopcoach.core.model.Drill

interface DrillService {
    suspend fun getDrills (limit: Int= 20): ResponseService<List<Drill>>
}