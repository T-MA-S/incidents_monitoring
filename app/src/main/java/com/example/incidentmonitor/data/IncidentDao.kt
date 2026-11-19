package com.example.incidentmonitor.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {

    @Query("SELECT * FROM incidents ORDER BY id DESC")
    fun getAll(): Flow<List<IncidentEntity>>

    @Query("SELECT COUNT(*) FROM incidents")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(incident: IncidentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incidents: List<IncidentEntity>)

    @Update
    suspend fun update(incident: IncidentEntity)

    @Delete
    suspend fun delete(incident: IncidentEntity)
}
