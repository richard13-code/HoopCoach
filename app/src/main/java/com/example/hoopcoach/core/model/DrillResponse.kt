package com.example.hoopcoach.core.model

import com.google.gson.annotations.SerializedName

data class DrillResponse(
    @SerializedName("results") val results: List<Drill>
)

data class Drill(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("portada") val cover: String,
    @SerializedName("duration_minutes") val durationMinutes: Int,
    @SerializedName("description") val description: String,
    @SerializedName("category") val category: String, // Ej: "Tiro", "Dribbling", "Defensa"
    @SerializedName("difficulty") val difficulty: String, // Ej: "Principiante", "Avanzado"
    @SerializedName("media_url") val mediaUrl: String, // Para cargar un video o imagen del ejercicio
    @SerializedName("creator_id") val creatorId: String
)

