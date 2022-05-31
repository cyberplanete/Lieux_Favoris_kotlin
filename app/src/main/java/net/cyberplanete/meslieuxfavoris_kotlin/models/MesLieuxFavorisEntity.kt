package net.cyberplanete.meslieuxfavoris_kotlin.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MesLieuxFavorisTable")
data class MesLieuxFavorisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val image: String,
    val description: String,
    val date: String,
    val localisation: String,
    val latitude: Double,
    val longitude: Double
)
