package net.cyberplanete.meslieuxfavoris_kotlin.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.cyberplanete.meslieuxfavoris_kotlin.models.MesLieuxFavorisEntity

@Dao
interface MesLieuxFavorisDAO {

    @Insert
    suspend fun insert(mesLieuxFavorisEntity: MesLieuxFavorisEntity)

    @Update
    suspend fun update(mesLieuxFavorisEntity: MesLieuxFavorisEntity)

    @Delete
    suspend fun delete(mesLieuxFavorisEntity: MesLieuxFavorisEntity)

    @Query("select * from 'meslieuxfavoristable'")
    fun fetchAllMesLieuxFavoris(): Flow<List<MesLieuxFavorisEntity>>

    @Query("select * from 'meslieuxfavoristable' where date=: date")
    fun fetchMesLieuxFavorisByDate(date:String): Flow<MesLieuxFavorisEntity>


}