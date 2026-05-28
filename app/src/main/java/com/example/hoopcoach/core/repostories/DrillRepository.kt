package com.example.hoopcoach.core.repostories

import com.example.hoopcoach.core.ResponseService
import com.example.hoopcoach.core.model.Drill
import com.example.hoopcoach.core.network.ApiClient
import com.example.hoopcoach.core.network.DrillService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DrillRepository: DrillService {

    private val api = ApiClient.DrillApi

    override suspend fun getDrills(limit: Int): ResponseService<List<Drill>> =
        withContext(Dispatchers.IO){
            try {
                val response = api.getDrills(
                    format = "json", // O el formato que requiera tu API
                    limit = limit,
                    offset = 0       // Valor por defecto
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        ResponseService.Success(body.results)
                    } else {
                        ResponseService.Error("Respuesta vacía del servidor")
                    }
                } else {
                    ResponseService.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                ResponseService.Error(
                    "No se pudieron cargar los drills: ${e.localizedMessage}"
                )
            }
    }
}