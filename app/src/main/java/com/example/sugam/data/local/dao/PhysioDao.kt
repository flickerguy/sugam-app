package com.example.sugam.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sugam.data.local.entity.PhysioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhysioDao {

    @Query("SELECT * FROM physios ORDER BY name ASC")
    fun getAllPhysios(): Flow<List<PhysioEntity>>

    @Insert
    suspend fun insertPhysio(physio: PhysioEntity)

    @Update
    suspend fun updatePhysio(physio: PhysioEntity)

    @Delete
    suspend fun deletePhysio(physio: PhysioEntity)
}